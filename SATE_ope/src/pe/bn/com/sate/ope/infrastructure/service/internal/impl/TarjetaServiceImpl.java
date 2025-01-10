package pe.bn.com.sate.ope.infrastructure.service.internal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.persistence.mapper.internal.ClienteMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.EmpresaMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.ParametroMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.TarjetaMapper;
import pe.bn.com.sate.ope.transversal.configuration.security.SecurityContextFacade;
import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.SolicitudTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.TarjetaResumen;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosTarjeta;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.constantes.ExceptionConstants;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoWS;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Service
public class TarjetaServiceImpl implements TarjetaService {

	private final static String FLAG_CAMBIO_CLAVE = "1";

	private final static Logger logger = Logger.getLogger(TarjetaServiceImpl.class);

	private @Autowired ClienteMapper clienteMapper;

	private @Autowired TarjetaMapper tarjetaMapper;

	private @Autowired EmpresaMapper empresaMapper;
	private @Autowired ParametroMapper parametroMapper;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	// @Transactional
	public void registrarSolicitudTarjeta(Tarjeta tarjeta, Cliente cliente) {
		try {
			logger.info("Inicio metodo registrarSolicitudTarjeta ");

			if (verificarTarjetasDisponiblesPorTipoTar(cliente.getTipoDocumento(), cliente.getNroDocumento(),
					SecurityContextFacade.getAuthenticatedUser().getRuc())) {
				if (cliente.getId() == null) {

					clienteMapper.registrarCliente(cliente);
					logger.info("Exito registro Cliente");
				} else {
					clienteMapper.actualizarCliente(cliente);
					logger.info("Exito actualizacion Cliente");

				}

				tarjeta.setIdEmpresa(empresaMapper
						.buscarEmpresaPorRUC(SecurityContextFacade.getAuthenticatedUser().getRuc()).getId());

				tarjeta.setIdUsu(SecurityContextFacade.getAuthenticatedUser().getId());
				cliente = clienteMapper.buscarCliente(cliente.getTipoDocumento(), cliente.getNroDocumento());
				logger.info("Exito trayendo cliente ");

				tarjeta.setIdCli(cliente.getId());
				tarjeta.setFechaCreacion(new Date());
				tarjeta.setFlagActualizarContacto("0");
				tarjetaMapper.registrarTarjeta(tarjeta);
				logger.info("Exito registro Tarjeta");
				tarjeta = tarjetaMapper.buscarTarjeta(tarjeta.getIdEmpresa(), tarjeta.getIdUsu(), cliente.getId());

				EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
				estadoTarjeta.setIdTarjeta(tarjeta.getId());
				estadoTarjeta.setEstado(TipoEstadoTarjeta.SOLICITUD_TARJETA_REGISTRADA.getCod());
				estadoTarjeta.setFechaRegistro(new Date());
				estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());

				tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);
				logger.info("Exito registro Estado Tarjeta");

			} else {
				throw new InternalServiceException(
						"Este cliente ya tiene una solicitud pendiente o una tarjeta activada");
			}
			logger.info("Fin metodo registrarSolicitudTarjeta ");

		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	private boolean verificarTarjetasDisponiblesPorTipoTar(String tipoDocumento, String nroDocuemnto, String ruc) {
		List<Tarjeta> tarjetasDelCliente = tarjetaMapper.buscarTarjetaPorTipoDocumento(tipoDocumento, nroDocuemnto,
				ruc);
		int countTarjetasActivadas = 0;
		for (Tarjeta tarjetaExistente : tarjetasDelCliente) {
			// Verificar si el estado de la tarjeta es activada, bloqueada o cancelada
			boolean tarjetaActiva = TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod().equals(tarjetaExistente.getEstado())
					|| TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod().equals(tarjetaExistente.getEstado())
					|| TipoEstadoTarjeta.TARJETA_CANCELADA.getCod().equals(tarjetaExistente.getEstado());
			if (tarjetaActiva) {
				countTarjetasActivadas++;
			}

		}
		// TODO MAXIMO DE TARJETAS DISPONIBLES POR ENTIDAD
		if (countTarjetasActivadas >= 3) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<SolicitudTarjeta> buscarTodosSolicitudTarjetaPendientes() {
		try {
			return tarjetaMapper.buscarTodosSolicitudesTarjetaPendientes(
					TipoEstadoTarjeta.SOLICITUD_TARJETA_REGISTRADA.getCod(),
					UsefulWebApplication.obtenerUsuario().getRuc());
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void aprobarSolicitudTarjeta(List<SolicitudTarjeta> solicitudTarjetas) {
		try {
			for (SolicitudTarjeta solicitudTarjeta : solicitudTarjetas) {
				EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
				estadoTarjeta.setIdTarjeta(solicitudTarjeta.getId());
				estadoTarjeta.setEstado(TipoEstadoTarjeta.SOLICITUD_TARJETA_AUTORIZADA.getCod());
				estadoTarjeta.setFechaRegistro(new Date());
				estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
				tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);
			}
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void rechazarSolicitudTarjeta(List<SolicitudTarjeta> solicitudTarjetas) {
		try {
			for (SolicitudTarjeta solicitudTarjeta : solicitudTarjetas) {
				EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
				estadoTarjeta.setIdTarjeta(solicitudTarjeta.getId());
				estadoTarjeta.setEstado(TipoEstadoTarjeta.SOLICITUD_TARJETA_CANCELADA.getCod());
				estadoTarjeta.setFechaRegistro(new Date());
				estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
				tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);
			}
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public Tarjeta buscarTarjetaPorNumeroTarjeta(String numTarjeta) {
		try {
			return tarjetaMapper.buscarTarjetaPorNumeroTarjeta(numTarjeta,
					UsefulWebApplication.obtenerUsuario().getRuc());
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public List<Tarjeta> buscarTarjetaPorTipoDocumento(String tipoDocumento, String numDocumento) {
		try {
			return tarjetaMapper.buscarTarjetaPorTipoDocumento(tipoDocumento, numDocumento,
					UsefulWebApplication.obtenerUsuario().getRuc());
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public DatosTarjetaCliente buscarDatosTarjetasCliente(String tipoBusqueda, String numDocumento) {
		try {
			String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();
			DatosTarjetaCliente datosTarjetaCliente = new DatosTarjetaCliente();
			Cliente cliente = null;
			Tarjeta tarjeta = null;
			List<Tarjeta> tarjetas = null;
			if (tipoBusqueda.equals(TipoBusqueda.NUM_TARJETA.getId())) {
				cliente = clienteMapper.buscarClientePorNumTajeta(numDocumento);
				tarjeta = tarjetaMapper.buscarTarjetaValidosParaBloqueoPorNumTar(numDocumento, rucUsuario);
				datosTarjetaCliente.setCliente(cliente);
				datosTarjetaCliente.setTarjeta(tarjeta);
				if ( tarjeta == null ) {
					throw new Exception("El Numero de tarjeta no existe");
				}
			} else if (tipoBusqueda.equals(TipoBusqueda.DNI.getId())
					|| tipoBusqueda.equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				cliente = clienteMapper.buscarCliente(tipoBusqueda, numDocumento);
				datosTarjetaCliente.setCliente(cliente);

				tarjetas = tarjetaMapper.buscarTarjetaValidosParaBloqueoPorDocumento(tipoBusqueda, numDocumento,
						rucUsuario);

				datosTarjetaCliente.setTarjetas(tarjetas);
				if ( tarjetas == null    ) {
					throw new Exception("No hay lista de tarjetas para el cliente");
				}
			}

			if (cliente == null   ) {
				throw new Exception("El cliente no existe ");
			}
			
			return datosTarjetaCliente;
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void actualizarEstadoTarjeta(EstadoTarjeta estadoTarjeta) {
		try {
			tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public List<TarjetaResumen> obtenerListaTarjetas(String cuentaCorriente, String fechaInicio, String fechaFin) {
		try {
			return tarjetaMapper.obtenerListaTarjetas(cuentaCorriente, fechaInicio, fechaFin);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}

	}

	@Override
	public void actualizarContacto(Tarjeta tarjeta) {
		try {
			tarjeta.setFlagActualizarContacto("1");
			tarjetaMapper.actualizarContacto(tarjeta);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void actualizarSaldos(Tarjeta tarjeta) {
		try {
			tarjetaMapper.actualizarSaldos(tarjeta);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void bloquearTarjetaPorRobo(EstadoTarjeta estadoTarjeta, Long idTarjeta, Long idCliente) {
		try {
			Tarjeta nuevaTarjeta = tarjetaMapper.buscarTarjetaPorId(idTarjeta);
			logger.info("nuevaTarjeta:" + nuevaTarjeta.toString());
			tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);
			nuevaTarjeta.setIdEmpresa(
					empresaMapper.buscarEmpresaPorRUC(SecurityContextFacade.getAuthenticatedUser().getRuc()).getId());
			nuevaTarjeta.setIdUsu(SecurityContextFacade.getAuthenticatedUser().getId());
			nuevaTarjeta.setFechaCreacion(new Date());
			tarjetaMapper.registrarTarjeta(nuevaTarjeta);

			nuevaTarjeta = tarjetaMapper.buscarTarjeta(nuevaTarjeta.getIdEmpresa(), nuevaTarjeta.getIdUsu(),
					nuevaTarjeta.getIdCli());

			EstadoTarjeta estadoTarjetaCreacion = new EstadoTarjeta();
			estadoTarjetaCreacion.setIdTarjeta(nuevaTarjeta.getId());
			estadoTarjetaCreacion.setEstado(TipoEstadoTarjeta.SOLICITUD_TARJETA_REGISTRADA.getCod());
			estadoTarjetaCreacion.setFechaRegistro(new Date());
			estadoTarjetaCreacion.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
			tarjetaMapper.registrarEstadoTarjeta(estadoTarjetaCreacion);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void actualizarEstadoCuenta(Tarjeta tarjeta) {
		try {
			tarjeta.setFlagActualizarEstadoCuenta(FLAG_CAMBIO_CLAVE);
			tarjetaMapper.actualizarestadoCuenta(tarjeta);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public String verificarSolicitudes(String tipoDocumento, String nroDocumento) {
		String ruc = SecurityContextFacade.getAuthenticatedUser().getRuc();

		// Buscar solicitudes en estado "registrada"
		List<SolicitudTarjeta> solicitudesRegistradas = tarjetaMapper
				.buscarTodosSolicitudesTarjetaPendientes(TipoEstadoTarjeta.SOLICITUD_TARJETA_REGISTRADA.getCod(), ruc);

		// Buscar solicitudes en estado "autorizada"
		List<SolicitudTarjeta> solicitudesAutorizadas = tarjetaMapper
				.buscarTodosSolicitudesTarjetaPendientes(TipoEstadoTarjeta.SOLICITUD_TARJETA_AUTORIZADA.getCod(), ruc);

		// Buscar solicitudes en estado "enviada"
		List<SolicitudTarjeta> solicitudesEnviadas = tarjetaMapper
				.buscarTodosSolicitudesTarjetaPendientes(TipoEstadoTarjeta.SOLICITUD_TARJETA_ENVIADA.getCod(), ruc);

		// Verificar si el tipo y número de documento están en alguna de las listas
		if (existeEnLista(solicitudesRegistradas, tipoDocumento, nroDocumento)) {
			return "Este cliente ya tiene una solicitud pendiente de autorizar";
		}
		if (existeEnLista(solicitudesAutorizadas, tipoDocumento, nroDocumento)) {
			return "Este cliente ya tiene una solicitud autorizada pendiente de envio";
		}
		if (existeEnLista(solicitudesEnviadas, tipoDocumento, nroDocumento)) {
			return "Este cliente ya tiene una solicitud enviada en espera";
		}

		// Si no está en ninguna lista, devuelve null
		return null;
	}

	@Override
	public String verificarTarjetasDisponible(String tipoDocumento, String nroDocuemnto, Tarjeta tarjeta) {
		String ruc = SecurityContextFacade.getAuthenticatedUser().getRuc();
		TipoTarjetaNegocio typeTarActual = TipoTarjetaNegocio.fromCodigoYDiseno(tarjeta.getTipoTarjeta(),
				tarjeta.getDiseno());

		List<Tarjeta> tarjetasDelCliente = tarjetaMapper.buscarTarjetaPorTipoDocumento(tipoDocumento, nroDocuemnto,
				ruc);

		if (existeEnListaTarjetas(typeTarActual, tarjetasDelCliente)) {
			return ExceptionConstants.TARJETA_NO_VALID;
		}

		return null;
	}

	private boolean existeEnListaTarjetas(TipoTarjetaNegocio typeTarActual, List<Tarjeta> tarjetasDelCliente) {

		if (typeTarActual == null) {
			return false;
		}

		for (Tarjeta tarjetaExistente : tarjetasDelCliente) {
			TipoTarjetaNegocio tipoTarjetaExistente = TipoTarjetaNegocio
					.fromCodigoYDiseno(tarjetaExistente.getTipoTarjeta(), tarjetaExistente.getDiseno());
			boolean tarjetaActiva = TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod().equals(tarjetaExistente.getEstado())
					|| TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod().equals(tarjetaExistente.getEstado())
					|| TipoEstadoTarjeta.TARJETA_CANCELADA.getCod().equals(tarjetaExistente.getEstado());

			// Comparar si coinciden el tipo de tarjeta y el diseño
			if (typeTarActual.getDiseno().equals(tipoTarjetaExistente.getDiseno()) && tarjetaActiva) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Verifica si el tipo y número de documento están en la lista de solicitudes.
	 *
	 * @param solicitudes
	 *            La lista de solicitudes a verificar.
	 * @param tipoDocumento
	 *            El tipo de documento a buscar.
	 * @param nroDocumento
	 *            El número de documento a buscar.
	 * @return true si el documento está en la lista, de lo contrario false.
	 */
	private boolean existeEnLista(List<SolicitudTarjeta> solicitudes, String tipoDocumento, String nroDocumento) {
		for (SolicitudTarjeta solicitud : solicitudes) {
			if (solicitud.getTipoDocumento().equals(tipoDocumento)
					&& solicitud.getNumDocumento().equals(nroDocumento)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Tarjeta buscarPrimeraTarjetaCliente(String tipoDocumento, String numeroDocumento) {
		try {
			return tarjetaMapper.buscarPrimeraTarjetaCliente(tipoDocumento, numeroDocumento);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}

	}

	@Override
	public Tarjeta buscarTarjetaId(Long idtar) {
		try {
			return tarjetaMapper.buscarTarjetaPorIdValidosParaBloqueo(idtar);
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public long consultarExisteTarjetaRUC(String numTarjeta, String ruc) {
		long num = tarjetaMapper.consultarExisteTarjetaRUC(numTarjeta, ruc);

		return num;
	}

	@Override
	public long consultarExisteTipNumDocRUC(String tipoDocumento, String numDocumento, String ruc) {
		long num = tarjetaMapper.consultarExisteTipNumDocRUC(tipoDocumento, numDocumento, ruc);

		return num;
	}

	@Override
	public List<TipoTarjetaNegocio> consultaTipoTarjetaNegocio(String bim) throws InternalServiceException {

		try {
			String ENCARGO, CAJACHICA, VIATICO;
			CAJACHICA = parametroMapper.buscarParametro("15", "0").getValor().split("-")[1];
			VIATICO = parametroMapper.buscarParametro("15", "2").getValor().split("-")[1];
			ENCARGO = parametroMapper.buscarParametro("15", "3").getValor().split("-")[1];
			List<TipoTarjetaNegocio> listaBase;
			if (bim.equals(ConstantesGenerales.BIM_BLACK)) {
				listaBase = TipoTarjetaNegocio.buscarTipoTarjetaBLACK();
			} else if (bim.equals(ConstantesGenerales.BIM_CORPORATE)) {
				listaBase = TipoTarjetaNegocio.buscarTipoTarjetaCORP();
			} else {
				throw new Exception("bim no valido");
			}
			List<TipoTarjetaNegocio> listaFiltrada = new ArrayList<>();
			for (TipoTarjetaNegocio tarjeta : listaBase) {
				if ((tarjeta.getDescripcion().equalsIgnoreCase("ENCARGO") && ENCARGO.equalsIgnoreCase("SI"))
						&& tarjeta.getCodigo().equalsIgnoreCase(ConstantesGenerales.BIM_CORPORATE)
						|| (tarjeta.getDescripcion().equalsIgnoreCase("CAJA CHICA") && CAJACHICA.equalsIgnoreCase("SI"))
						|| (tarjeta.getDescripcion().equalsIgnoreCase("VIATICO") && VIATICO.equalsIgnoreCase("SI"))) {
					listaFiltrada.add(tarjeta);
				}
			}

			return listaFiltrada;
		} catch (Exception ex) {
			throw new InternalServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public Tarjeta verificarEstadoTarjeta(Tarjeta tarjeta, DTOConsultaDatosTarjeta data) {
 
	    String codEstadoActivada = TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod();
	    String codNormal = MotivosBloqueoWS.NORMAL.getId();
	    
	    boolean codBloqueoEsNormal = codNormal.equals(data.getCodBloqueo());
	    boolean tarjetaEstaActivada = codEstadoActivada.equals(tarjeta.getEstado());

 
	    if (codBloqueoEsNormal && tarjetaEstaActivada) {
	        return tarjeta;
	    }

 
	    if (!codBloqueoEsNormal && !tarjetaEstaActivada) {
	        return tarjeta;
	    }

	  
	    if (codBloqueoEsNormal && !tarjetaEstaActivada) {
	        EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
	        estadoTarjeta.setIdTarjeta(tarjeta.getId());
	        estadoTarjeta.setEstado(codEstadoActivada);
	        estadoTarjeta.setMotivo(codNormal);
	        estadoTarjeta.setFechaRegistro(new Date());
	        estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
	        estadoTarjeta.setCodAutorizacion(data.getIdTransaccion());
	        
	        tarjetaMapper.registrarEstadoTarjeta(estadoTarjeta);

	        Tarjeta tarjetaResponse = tarjetaMapper.buscarTarjetaPorIdValidosParaBloqueo(tarjeta.getId());
	        tarjetaResponse.setNumTarjeta(StringsUtils.quitarCeroIzquierdaString(tarjetaResponse.getNumTarjeta()));
	        return tarjetaResponse;
	    }

 	    return tarjeta;
	}


}
