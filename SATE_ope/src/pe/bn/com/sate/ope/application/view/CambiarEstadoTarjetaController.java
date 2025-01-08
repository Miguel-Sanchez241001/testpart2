package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.Setter;
import pe.bn.com.sate.ope.application.model.CambiarEstadoTarjetaModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceBnTablasException;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.AgenciaService;
import pe.bn.com.sate.ope.infrastructure.service.external.UbigeoService;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.infrastructure.service.internal.EmpresaService;
import pe.bn.com.sate.ope.infrastructure.service.internal.NotificacionService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.infrastructure.service.internal.UsuarioService;
import pe.bn.com.sate.ope.transversal.configuration.security.SecurityContextFacade;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.Empresa;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Agencia;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosTarjeta;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionTarjeta;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoCuenta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Getter
@Setter
@Controller("cambiarEstadoTarjetaController")
@Scope("view")
public class CambiarEstadoTarjetaController implements Serializable {

	private final static Logger logger = Logger.getLogger(CambiarEstadoTarjetaController.class);

	private static final long serialVersionUID = 1L;

	private CambiarEstadoTarjetaModel cambiarEstadoTarjetaModel;

	private @Autowired TarjetaService tarjetaService;

	private @Autowired ClienteService clienteService;

	private @Autowired UsuarioService usuarioService;
	
	private @Autowired NotificacionService notificacionService;

	private @Autowired FWMCProcesos fwmcProcesos;

	private @Autowired Parametros parametros;

	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private UbigeoService ubigeoService;

	@Autowired
	private AgenciaService agenciaService;

	@PostConstruct
	public void init() {
		cambiarEstadoTarjetaModel = new CambiarEstadoTarjetaModel();
		cambiarEstadoTarjetaModel
				.setMotivosBloqueoTarjetas(Arrays.asList(MotivosBloqueoTarjeta.motivosBloqueoPorIdMotivoWS()));

		cambiarEstadoTarjetaModel
				.setMotivosBloqueoCuenta(Arrays.asList(MotivosBloqueoCuenta.motivosBloqueoPorIdMotivoWS()));
	}

	public void buscarListaTarjetas() {
		try {
			String tipoTemp = cambiarEstadoTarjetaModel.getTipoBusqueda();
			String numdoc = cambiarEstadoTarjetaModel.getNumDocumento();
			if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				numdoc = StringsUtils.llenarCerosAlaIzquierdaV2(numdoc, 19);
			}
			DatosTarjetaCliente datosTarjetaCliente = tarjetaService.buscarDatosTarjetasCliente(tipoTemp, numdoc);
			cambiarEstadoTarjetaModel.setDatosTarjetaCliente(datosTarjetaCliente);
			cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente()
			.setApCompleto(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getApPaterno() + " "
					+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno());
			if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				buscarTarjeta();
			} else {

				UsefulWebApplication.ejecutar("wvSeleccionarTajeta.show()");

				UsefulWebApplication.actualizarComponente("formSeleccionarTarjeta");

			}
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (InternalServiceException e) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					e.getMessage(), e.getMessage());
			logger.error(e.getMessage());
		}
	}

	public void buscarTarjeta() {
		// MGL
		UsefulWebApplication.mostrarDialogo("statusDialog");
		try {

		

			String numtarjeta = StringsUtils.quitarCeroIzquierdaString(
					cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta());
			cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().setNumTarjeta(numtarjeta);

			DTOConsultaDatosTarjeta dato = new DTOConsultaDatosTarjeta();

			try {

				dato = fwmcProcesos.informacionDeTarjeta(
						StringsUtils.quitarCeroIzquierdaString(
								cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta()),
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getFechaVencimientoTar());

				cambiarEstadoTarjetaModel.setEstadoBloqueoWS(dato.getCodBloqueo());
				Tarjeta tarjetaResponse = tarjetaService.verificarEstadoTarjeta(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta(),dato);
				cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setTarjeta(tarjetaResponse);
				cambiarEstadoTarjetaModel.setBusquedaRealizada(true);
			} catch (ExternalServiceMCProcesosException este) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				logger.error(este.getMessage());
			} catch (InternalExcepcion e) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
				logger.error(e.getMessage());
			}

			EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
			estadoTarjeta.setEstado(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getEstado());
			estadoTarjeta.setFechaRegistro(
					cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getFechaBloqueo());
			estadoTarjeta.setMotivo(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getMotivoBloqueo());
			estadoTarjeta.setUsuarioRegistro(
					cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getUsuarioBloqueo());
			estadoTarjeta
					.setEstadoCuenta(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getEstadoCuenta());

			cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setEstadoTarjeta(estadoTarjeta);

			cambiarEstadoTarjetaModel.setBusquedaBloqueoTarjeta(cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
					.getTarjeta().getEstado().equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod()));

			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (InternalServiceException e) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(e.getMessage());
		} finally {
			UsefulWebApplication.ocultarDialogo("statusDialog");
		}

	}

	public void seleccionarTarjeta() {
		cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
				.setTarjeta(cambiarEstadoTarjetaModel.getTarjetaSeleccionada());
		logger.info("Motivo:" + cambiarEstadoTarjetaModel.getTarjetaSeleccionada().getMotivoBloqueo());
		buscarTarjeta();
	}

	public void cambiarEstadoTarjeta() {

		try {

			cambiarEstadoTarjetaModel.iniciarEstadoTarjeta();

			String tipoMoneda = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda().trim();
			logger.info("tipoMoneda:" + tipoMoneda);
			String numTarjeta = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta().trim();
			logger.info("numTarjeta:" + numTarjeta);

			if (cambiarEstadoTarjetaModel.getTipoBloqueoSeleccionado().equals("T")) {

				String motivo = cambiarEstadoTarjetaModel.getMotivoSeleccionado();
				char guion = '-';
				int posicion = motivo.indexOf(guion);

				String codMotivo = motivo.substring(0, posicion).trim();
				String desMotivo = motivo.substring(posicion + 1).trim();

				logger.info("codMotivo:" + codMotivo);
				logger.info("desMotivo:" + desMotivo);

				logger.info(cambiarEstadoTarjetaModel.getEstadoTarjeta().toString());

				if (codMotivo.equals(MotivosBloqueoTarjeta.ROBO.getId())
						|| codMotivo.equals(MotivosBloqueoTarjeta.PERDIDA.getId())) {

					cambiarEstadoTarjetaModel.inicializarFormularioEntrega();
					try {
						cambiarEstadoTarjetaModel.setDepartamentos(ubigeoService.buscarDepartamentos());
					} catch (InternalServiceException ise) {
						logger.error(ise.getMessage());
					} catch (ServiceException se) {
						logger.error(se.getMessage());
					}
					this.mostrarOpcionPorTipoUbicacion();

					UsefulWebApplication.ejecutar("dgSolicitarTarjeta.show()");
					UsefulWebApplication.actualizarComponente("formSolicitarTarjeta");

				} else {

					try {

						final DTOModificacionTarjeta modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda,
								numTarjeta, codMotivo, desMotivo);
						logger.info("codRespuesta:" + modificacionTarjeta.getCodRespuesta());
						logger.info("desRespuesta:" + modificacionTarjeta.getDescRespuesta());
						ExecutorService executorService = Executors.newSingleThreadExecutor();

						if (modificacionTarjeta.getCodRespuesta().equals("0000")) {
							String nombreCompleto = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente()
									.getApPaterno() + " "
									+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno()
									+ ", "
									+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getNombres();
							executorService.submit(() -> {
								notificacionService.enviarMailBloqueoTarjeta(nombreCompleto,
										cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta(),
										modificacionTarjeta.getFechaTxnTerminal(),
										modificacionTarjeta.getHoraTxnTerminal(),
										modificacionTarjeta.getIdTransaccion());
							});

							cambiarEstadoTarjetaModel.getEstadoTarjeta().setMotivo(codMotivo);
							cambiarEstadoTarjetaModel.getEstadoTarjeta()
									.setCodAutorizacion(modificacionTarjeta.getIdTransaccion());
							tarjetaService.actualizarEstadoTarjeta(cambiarEstadoTarjetaModel.getEstadoTarjeta());

							cambiarEstadoTarjetaModel.inicializarFormulario();

							UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "",
									"Se cambio de estado exitosamente");
							UsefulWebApplication.actualizarComponente("msgs");
							UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");

						} else {
							UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
									modificacionTarjeta.getDescRespuesta(), modificacionTarjeta.getDescRespuesta());
							UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");

						}
					} catch (ExternalServiceMCProcesosException este) {
						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
								ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
						logger.error(este.getMessage());
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
					} catch (InternalExcepcion e) {
 						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
								ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
						logger.error(e.getMessage());
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
					}
				}
			} else {

				/* ini MGL */

				try {

					String motivo = cambiarEstadoTarjetaModel.getEstadoCuentaSeleccionado();
					char guion = '-';
					int posicion = motivo.indexOf(guion);

					String codMotivo = motivo.substring(0, posicion).trim();
					String desMotivo = motivo.substring(posicion + 1).trim();

					logger.info("codMotivo:" + codMotivo);
					logger.info("desMotivo:" + desMotivo);
 
					final DTOModificacionTarjeta modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda,
							numTarjeta, codMotivo, desMotivo);
 					logger.info("codRespuesta:" + modificacionTarjeta.getCodRespuesta());
					logger.info("desRespuesta:" + modificacionTarjeta.getDescRespuesta());
					ExecutorService executorService = Executors.newSingleThreadExecutor();

					if (modificacionTarjeta.getCodRespuesta().equals("0000")) {

						String nombreCompleto = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente()
								.getApPaterno() + " "
								+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno() + ", "
								+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getNombres();

						executorService.submit(() -> {
							notificacionService.enviarMailBloqueoTarjeta(nombreCompleto,
									cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta(),
									modificacionTarjeta.getFechaTxnTerminal(), modificacionTarjeta.getHoraTxnTerminal(),
									modificacionTarjeta.getIdTransaccion());
						});
 						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEstadoCuenta(codMotivo);
						cambiarEstadoTarjetaModel.getEstadoTarjeta().setMotivo(codMotivo);
						tarjetaService.actualizarEstadoCuenta(
								cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta());

						cambiarEstadoTarjetaModel.inicializarFormulario();

						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "",
								"Se cambio de estado exitosamente");
						UsefulWebApplication.actualizarComponente("msgs");
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta");

					} else {
						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								modificacionTarjeta.getDescRespuesta(), modificacionTarjeta.getDescRespuesta());
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");

					}
				} catch (ExternalServiceMCProcesosException este) {
					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
					logger.error(este.getMessage());
					UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				} catch (InternalExcepcion e) {
					// TODO Auto-generated catch block
					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
							ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
					logger.error(e.getMessage());
					UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				}

				/* FIN MGL */

			}
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		}

	}

	public void bloquearTarjetaRobo() {

		String tipoMoneda = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda().trim();
		logger.info("tipoMoneda:" + tipoMoneda);
		String numTarjeta = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta().trim();
		logger.info("numTarjeta:" + numTarjeta);

		DTOModificacionTarjeta modificacionTarjeta = new DTOModificacionTarjeta();

		logger.info("idtarjeta:" + cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getId());
		logger.info("idtarjeta:" + cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getId());

		String motivo = cambiarEstadoTarjetaModel.getMotivoSeleccionado();
		char guion = '-';
		int posicion = motivo.indexOf(guion);

		String codMotivo = motivo.substring(0, posicion).trim();
		String desMotivo = motivo.substring(posicion + 1).trim();

		logger.info("codMotivo:" + codMotivo);
		logger.info("desMotivo:" + desMotivo);

		try {

			modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda, numTarjeta, codMotivo, desMotivo);
			// modificacionTarjeta.setCodRespuesta("0000");
			logger.info("codRespuesta : " + modificacionTarjeta.getCodRespuesta());
			logger.info("desRespuesta:" + modificacionTarjeta.getDescRespuesta());

			if (modificacionTarjeta.getCodRespuesta().equals("0000")) {

				cambiarEstadoTarjetaModel.getEstadoTarjeta().setMotivo(codMotivo);
				cambiarEstadoTarjetaModel.getEstadoTarjeta().setCodAutorizacion(modificacionTarjeta.getIdTransaccion());
				tarjetaService.bloquearTarjetaPorRobo(cambiarEstadoTarjetaModel.getEstadoTarjeta(),
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getId(),
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getCliente().getId());

				UsefulWebApplication.ocultarDialogo("dgSolicitarTarjeta");

				cambiarEstadoTarjetaModel.inicializarFormulario();

				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "",
						"Se cambio de estado exitosamente");
				UsefulWebApplication.actualizarComponente("msgs");
				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta");

			} else {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						modificacionTarjeta.getDescRespuesta(), modificacionTarjeta.getDescRespuesta());
				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");

			}
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		} catch (InternalExcepcion e) {
			// TODO Auto-generated catch block
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(e.getMessage());
			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		}

	}

	/************************************/

	public void buscarAsignaciones() {
		try {
			List<Asignacion> asignaciones = null;
			if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {

				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(cambiarEstadoTarjetaModel.getNumDocumento(),
						19);

				String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();

				long valor = 0;

				valor = tarjetaService.consultarExisteTarjetaRUC(tarjeta19, rucUsuario);

				if (valor == 0) {

					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_TIP_TARJETA,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_TIP_TARJETA);
				} else {
					asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjetaSimple(tarjeta19, rucUsuario);

					if (asignaciones.isEmpty() && asignaciones.size() == 0) {

						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA);

						UsefulWebApplication.actualizarComponente("msgs");
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta");

					} else {

						// MOSTRAR MODAL COMPONENTE
						UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
						// formulario del componente
						UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
					}
				}

			} else if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {

				String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();

				long valor = 0;

				valor = tarjetaService.consultarExisteTipNumDocRUC(cambiarEstadoTarjetaModel.getTipoBusqueda(),
						cambiarEstadoTarjetaModel.getNumDocumento(), rucUsuario);

				if (valor == 0) {

					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_NUM_DOCUMENTO,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_NUM_DOCUMENTO);
				} else {
					asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumentoSimple(
							cambiarEstadoTarjetaModel.getTipoBusqueda(), cambiarEstadoTarjetaModel.getNumDocumento(),
							rucUsuario);

					if (asignaciones.isEmpty() && asignaciones.size() == 0) {

						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO);

					} else {

						cambiarEstadoTarjetaModel.setBusquedaRealizada(true);

						UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
						UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
					}

				}
			}

		} catch (InternalExcepcion se) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(se.getMessage());
		}

	}

	public void buscarTipoBusqueda() {
		if (cambiarEstadoTarjetaModel.getTipoBusquedaPor().equals("Por Documento")) {
			cambiarEstadoTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
			cambiarEstadoTarjetaModel.setNumDocumento(null);
		} else if (cambiarEstadoTarjetaModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
			cambiarEstadoTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
			cambiarEstadoTarjetaModel.setNumDocumento(null);
		} else {
			cambiarEstadoTarjetaModel.setListaTipoBusqueda(null);
			cambiarEstadoTarjetaModel.setNumDocumento(null);
		}
	}

	/**
	 * Muestra opciones según el tipo de ubicación de entrega seleccionada.
	 */
	public void mostrarOpcionPorTipoUbicacion() {
		try {
			if (cambiarEstadoTarjetaModel.getTarjeta().getEntregaUbicacion()
					.equals(ConstantesGenerales.ENTREGA_AGENCIA_BN)) {
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDireccion(null);
				cambiarEstadoTarjetaModel.setEsEntregaBN(true);
				cambiarEstadoTarjetaModel.setEsEntregaUE(false);
				cambiarEstadoTarjetaModel.setEsEntregaReferencia(true);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDepartamento(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
			} else if (cambiarEstadoTarjetaModel.getTarjeta().getEntregaUbicacion()
					.equals(ConstantesGenerales.ENTREGA_UNIDAD_EJECUTORA)) {

				Empresa empresa = empresaService
						.buscarEmpresaPorRUC(SecurityContextFacade.getAuthenticatedUser().getRuc());
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDireccion(empresa.getDireccion());
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaUbigeo(empresa.getUbigeo());
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaAgenciaBN("0000");
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(empresa.getReferencia());
				cambiarEstadoTarjetaModel.setAgenciasBN(null);

				cambiarEstadoTarjetaModel.setEsEntregaBN(false);
				cambiarEstadoTarjetaModel.setEsEntregaUE(true);
				cambiarEstadoTarjetaModel.setEsEntregaReferencia(true);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDepartamento(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
				cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
			} else {
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDireccion(null);
				cambiarEstadoTarjetaModel.setAgenciasBN(null);

				cambiarEstadoTarjetaModel.setEsEntregaBN(false);
				cambiarEstadoTarjetaModel.setEsEntregaUE(false);
				cambiarEstadoTarjetaModel.setEsEntregaReferencia(false);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDepartamento(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
				cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
			}
		} catch (ExternalServiceBnTablasException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
		}
	}

	/**
	 * Busca agencias según el ubigeo seleccionado.
	 */
	public void buscarAgenciasPorUbigeo() {
		logger.info("[SolicitarTarjetaController] Inicio metodo buscarAgenciasPorUbigeo");
		String provincia = cambiarEstadoTarjetaModel.getTarjeta().getEntregaProvincia();
		String departamento = cambiarEstadoTarjetaModel.getTarjeta().getEntregaDepartamento();
		String distrito = cambiarEstadoTarjetaModel.getTarjeta().getEntregaDistrito();
		logger.info("[SolicitarTarjetaController] valor departamento: " + departamento);
		logger.info("[SolicitarTarjetaController] valor Provincia: " + provincia);
		logger.info("[SolicitarTarjetaController] valor distrito: " + distrito);

		if (distrito == null) {
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaAgenciaBN(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
		} else {
			try {
				cambiarEstadoTarjetaModel
						.setAgenciasBN(agenciaService.buscarAgenciasPorUbigeo(departamento, provincia, distrito));
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaAgenciaBN(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
			} catch (ExternalServiceBnTablasException este) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
				logger.error(este.getMessage());
			} catch (ServiceException es) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_GENERAL, ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
				logger.error(es.getMessage());
			}
		}
		logger.info("[SolicitarTarjetaController] Fin metodo buscarAgenciasPorUbigeo");
	}

	/**
	 * Busca los datos de una agencia según el código de agencia seleccionado.
	 */
	public void buscarDatosAgencia() {
		try {
			logger.info("[SolicitarTarjetaController] Inicio metodo buscarDatosAgencia");
			Agencia agencia = agenciaService
					.buscarAgenciaPorCodAgencia(cambiarEstadoTarjetaModel.getAgenciaSeleccionada().getCodAgencia());
			cambiarEstadoTarjetaModel.getTarjeta()
					.setEntregaDireccion(agencia == null ? "No hay dirección registrada" : agencia.getDireccion());
			logger.info("[SolicitarTarjetaController] fin metodo buscarDatosAgencia");
		} catch (ExternalServiceBnTablasException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
			logger.error(este.getMessage());
		} catch (ServiceException es) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_GENERAL, ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
			logger.error(es.getMessage());
		}
	}

	/**
	 * Busca las provincias según el departamento seleccionado.
	 */
	public void buscarProvincias() {
		logger.info("[SolicitarTarjetaController] Inicio metodo buscarProvincias");
		String departamento = cambiarEstadoTarjetaModel.getTarjeta().getEntregaDepartamento();
		logger.info("[SolicitarTarjetaController] valor departamento: " + departamento);

		if (departamento == null) {
			cambiarEstadoTarjetaModel.setProvincias(null);
			cambiarEstadoTarjetaModel.setDistritos(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
			cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
		} else {
			try {
				cambiarEstadoTarjetaModel.setProvincias(ubigeoService.buscarProvinciasPorDepartamento(departamento));
				cambiarEstadoTarjetaModel.setDistritos(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
				cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
			} catch (ExternalServiceBnTablasException este) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
				logger.error(este.getMessage());
			} catch (ServiceException es) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_GENERAL, ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
				logger.error(es.getMessage());
			}
		}
		logger.info("[SolicitarTarjetaController] Fin metodo buscarProvincias");
	}

	/**
	 * Busca los distritos según la provincia seleccionada.
	 */
	public void buscarDistritos() {
		logger.info("[SolicitarTarjetaController] Inicio metodo buscarDistritos");
		String provincia = cambiarEstadoTarjetaModel.getTarjeta().getEntregaProvincia();
		String departamento = cambiarEstadoTarjetaModel.getTarjeta().getEntregaDepartamento();
		logger.info("[SolicitarTarjetaController] valor Provincia: " + provincia);
		logger.info("[SolicitarTarjetaController] valor departamento: " + departamento);

		if (provincia == null) {
			logger.info("[SolicitarTarjetaController] Provincia nulo");
			cambiarEstadoTarjetaModel.setDistritos(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
			cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
			cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
		} else {
			try {
				cambiarEstadoTarjetaModel
						.setDistritos(ubigeoService.buscarDistritosPorProvincia(departamento, provincia));
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
				cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
				cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
			} catch (ExternalServiceBnTablasException este) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
				logger.error(este.getMessage());
			} catch (ServiceException es) {
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_GENERAL, ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
				logger.error(es.getMessage());
			}
		}
		logger.info("[SolicitarTarjetaController] Fin metodo buscarDistritos");
	}

	
	
	public String obtenerNombreUsuarioBloquea(String usuarioRegistra) {
		if(usuarioRegistra.equals("No username")) {
			return usuarioRegistra;
		}
		try {
		return usuarioService.buscarClienteNumDoc(usuarioRegistra);
		} catch (Exception exe) {
			logger.error(exe.getMessage());
			return "No username";
 	}
	}
}
