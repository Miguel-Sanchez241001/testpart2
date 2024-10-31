package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.BuscarTarjetaModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceBnTablasException;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.BuscarTarjetaFacade;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.AgenciaService;
import pe.bn.com.sate.ope.infrastructure.service.external.UbigeoService;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Ubigeo;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Controller("buscarTarjetaController")
@Scope("view")
public class BuscarTarjetaController implements Serializable {

	private final static Logger logger = Logger
			.getLogger(BuscarTarjetaController.class);

	private static final long serialVersionUID = 1L;

	private BuscarTarjetaModel buscarTarjetaModel;

	private @Autowired
	TarjetaService tarjetaService;

	private @Autowired
	ClienteService clienteService;

	private @Autowired
	AgenciaService agenciaService;

	private @Autowired
	UbigeoService ubigeoService;

	@Autowired
	private BuscarTarjetaFacade buscarTarjetaFacade;
	
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		buscarTarjetaModel = new BuscarTarjetaModel();
		
	}

	public void seleccionarTarjeta() {
		buscarTarjetaModel.setBusquedaRealizada(true);
		
		//MGL
//		buscarTarjetaFacade.actualizarSaldoTarjeta(buscarTarjetaModel
//				.getTarjetaSeleccionada().getNumTarjeta());		
		try {
			try {
				buscarTarjetaFacade.actualizarSaldoTarjeta(	
					buscarTarjetaModel.getTarjetaSeleccionada().getNumTarjeta(),
					buscarTarjetaModel.getTarjetaSeleccionada().getNumeroCuenta(),				
					buscarTarjetaModel.getTarjetaSeleccionada().getTipoMoneda(),
					buscarTarjetaModel.getTarjetaSeleccionada().getFechaTerminoLinea()
						);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}		
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication
			.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		}
		
		
		buscarTarjetaModel.getDatosTarjetaCliente().setTarjeta(buscarTarjetaModel.getTarjetaSeleccionada());
		
	
		buscarTarjetaModel.setTipoOperacionActualizar(TipoEstadoTarjeta
				.esEstadoTarjetaParaActualizarDatosContacto(buscarTarjetaModel
						.getDatosTarjetaCliente().getTarjeta().getEstado()));
		buscarTarjetaModel.setTipoOperacionCancelar(TipoEstadoTarjeta
				.esEstadoTarjetaCancelarTarjeta(buscarTarjetaModel
						.getDatosTarjetaCliente().getTarjeta().getEstado()));
		// buscarTarjetaModel
		// .getDatosTarjetaCliente()
		// .getCliente()
		// .setFlagActualizar(
		// buscarTarjetaModel
		// .getDatosTarjetaCliente()
		// .getTarjeta()
		// .getEstado()
		// .equals(TipoEstadoTarjeta.TARJETA_ACTIVADA
		// .getCod()) ? "1" : "0");

		if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
				.getEntregaUbigeo() != null) {

			Ubigeo departamento = ubigeoService.buscarLocalidad(
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
							.getEntregaUbigeo(), 1);
			Ubigeo provincia = ubigeoService.buscarLocalidad(buscarTarjetaModel
					.getDatosTarjetaCliente().getTarjeta().getEntregaUbigeo(),
					2);
			Ubigeo distrito = ubigeoService.buscarLocalidad(buscarTarjetaModel
					.getDatosTarjetaCliente().getTarjeta().getEntregaUbigeo(),
					3);

			buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
					.setEntregaDepartamento(departamento.getDescripcion());
			buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
					.setEntregaProvincia(provincia.getDescripcion());
			buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
					.setEntregaDistrito(distrito.getDescripcion());
		}

		if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
				.getDescripcionUbicacion() == null) {
			buscarTarjetaModel
					.getDatosTarjetaCliente()
					.getTarjeta()
					.setDescripcionUbicacion(
							agenciaService
									.buscarAgenciaPorCodAgencia(
											buscarTarjetaModel
													.getDatosTarjetaCliente()
													.getTarjeta()
													.getEntregaAgenciaBN())
									.getDescripcion());
		}
	}

	public void buscarTarjeta() {
		//MGL //aqui 1
		try {
				
			if (buscarTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				
				//buscarTarjetaFacade.actualizarSaldoTarjeta(buscarTarjetaModel.getNumDocumento());
				
				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(buscarTarjetaModel.getNumDocumento(), 19);
				
				buscarTarjetaModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
								buscarTarjetaModel.getTipoBusqueda(),
								tarjeta19, "B"));
				/*
				try {
					buscarTarjetaFacade.actualizarSaldoTarjeta(	
					buscarTarjetaModel.getNumDocumento(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea()
							);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}tipoTarjeta	"530927" (id=24990)	

				*/
				Tarjeta tarjetaMC = new Tarjeta();
				try {
					tarjetaMC = buscarTarjetaFacade.obtenerDatosCredito(
					buscarTarjetaModel.getNumDocumento(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea());
					
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setFechaInicioLinea(tarjetaMC.getFechaInicioLinea());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setFechaTerminoLinea(tarjetaMC.getFechaTerminoLinea());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoLineaAsignado(tarjetaMC.getMontoLineaAsignado());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoLineaActual(tarjetaMC.getMontoLineaActual());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoCompraUsado(tarjetaMC.getMontoCompraUsado());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoPorProcesar(tarjetaMC.getMontoPorProcesar());
					buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setDispEfectivoUsado(tarjetaMC.getDispEfectivoUsado());
					
					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				

				if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta() != null) {
					
					if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
							.getEstado().equals(TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod())
							|| buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
									.getEstado().equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod())) {
						
						buscarTarjetaModel.setBusquedaRealizada(true);
						
						//apMaterno	"ZAPATA" (id=29469)	

						
						//buscarTarjetaModel.getdatosTarjetaCliente.getcliente.set
						
						buscarTarjetaModel.getDatosTarjetaCliente().getCliente().setApCompleto(
								buscarTarjetaModel.getDatosTarjetaCliente().getCliente().getApPaterno()+" "+
								buscarTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno()
								);
						
						
						
						
						buscarTarjetaModel.setTipoOperacionActualizar(
								TipoEstadoTarjeta.esEstadoTarjetaParaActualizarDatosContacto(buscarTarjetaModel
												.getDatosTarjetaCliente().getTarjeta().getEstado()));
						
						buscarTarjetaModel.setTipoOperacionCancelar(TipoEstadoTarjeta
										.esEstadoTarjetaCancelarTarjeta(buscarTarjetaModel
												.getDatosTarjetaCliente().getTarjeta().getEstado()));
						// buscarTarjetaModel
						// .getDatosTarjetaCliente()
						// .getCliente()
						// .setFlagActualizar(
						// buscarTarjetaModel
						// .getDatosTarjetaCliente()
						// .getTarjeta()
						// .getEstado()
						// .equals(TipoEstadoTarjeta.TARJETA_ACTIVADA
						// .getCod()) ? "1" : "0");

						System.out.println("EntregaUbigeo:"+ buscarTarjetaModel.getDatosTarjetaCliente()
								.getTarjeta().getEntregaUbigeo());
						String codDepartamento = buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
								.getEntregaUbigeo().substring(0, 2);
						System.out.println("EntregaUbigeo:"	+ buscarTarjetaModel.getDatosTarjetaCliente()
										.getTarjeta().getEntregaUbigeo().substring(0, 2));
						String codProvincia = buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getEntregaUbigeo().substring(2, 4);
						System.out.println("EntregaUbigeo:"
								+ buscarTarjetaModel.getDatosTarjetaCliente()
										.getTarjeta().getEntregaUbigeo()
										.substring(2, 4));
						String codDistrito = buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getEntregaUbigeo().substring(4);
						System.out.println("EntregaUbigeo:"
								+ buscarTarjetaModel.getDatosTarjetaCliente()
										.getTarjeta().getEntregaUbigeo()
										.substring(4));
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaDepartamento(
							ubigeoService.buscarLocalidad(codDepartamento.concat("0000"),1).getDescripcion());
						
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaProvincia(
							ubigeoService.buscarLocalidad(codDepartamento.concat(codProvincia.concat("00")),2).getDescripcion());
						
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaDistrito(
							ubigeoService.buscarLocalidad(codDepartamento.concat(codProvincia
									.concat(codDistrito)),3).getDescripcion());
						
						if (buscarTarjetaModel.getDatosTarjetaCliente()
								.getTarjeta().getDescripcionUbicacion() == null) {
							buscarTarjetaModel
									.getDatosTarjetaCliente()
									.getTarjeta()
									.setDescripcionUbicacion(
											agenciaService
													.buscarAgenciaPorCodAgencia(
															buscarTarjetaModel
																	.getDatosTarjetaCliente()
																	.getTarjeta()
																	.getEntregaAgenciaBN())
													.getDescripcion());
						}

						EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
						estadoTarjeta.setEstado(buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getEstado());
						estadoTarjeta.setFechaRegistro(buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getFechaBloqueo());
						estadoTarjeta.setMotivo(buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getMotivoBloqueo());
						estadoTarjeta.setUsuarioRegistro(buscarTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getUsuarioBloqueo());

						buscarTarjetaModel.getDatosTarjetaCliente()
								.setEstadoTarjeta(estadoTarjeta);

						UsefulWebApplication
								.actualizarComponente("formEmpresa:pgResultado");
					} else {
						UsefulWebApplication.mostrarMensajeJSF(3, "",
								"La tarjeta tiene el estado : "
										+ buscarTarjetaModel
												.getDatosTarjetaCliente()
												.getTarjeta().getEstado());
						UsefulWebApplication.actualizarComponente("msgs");
					}
				} else {
					UsefulWebApplication.mostrarMensajeJSF(3, "",
							"El número de tarjeta no existe");

					UsefulWebApplication.actualizarComponente("msgs");

					buscarTarjetaModel.setBusquedaRealizada(false);

					UsefulWebApplication
							.actualizarComponente("formEmpresa:pgResultado");
				}

			} else if (buscarTarjetaModel.getTipoBusqueda().equals(
					TipoBusqueda.DNI.getId())
					|| buscarTarjetaModel.getTipoBusqueda().equals(
							TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				buscarTarjetaModel.setDatosTarjetaCliente(tarjetaService
						.buscarDatosTarjetasCliente(
								buscarTarjetaModel.getTipoBusqueda(),
								buscarTarjetaModel.getNumDocumento(), "B"));

				buscarTarjetaModel.setBusquedaRealizada(false);

				if (buscarTarjetaModel.getDatosTarjetaCliente().getCliente() == null) {
					UsefulWebApplication
							.mostrarMensajeJSF(3, "",
									"No existe TarjetaHabiente con ese tipo y número de documento.");
					UsefulWebApplication.actualizarComponente("msgs");
				} else {
					
					//seleccionarTarjeta();
					buscarTarjetaModel.getDatosTarjetaCliente().setTarjeta(buscarTarjetaModel.getDatosTarjetaCliente().getTarjetas().get(0));
					Tarjeta tarjetaMC = new Tarjeta();
					try {
						tarjetaMC = buscarTarjetaFacade.obtenerDatosCredito(						
						buscarTarjetaModel.getNumDocumento(),
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea());
						
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setFechaInicioLinea(tarjetaMC.getFechaInicioLinea());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setFechaTerminoLinea(tarjetaMC.getFechaTerminoLinea());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoLineaAsignado(tarjetaMC.getMontoLineaAsignado());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoLineaActual(tarjetaMC.getMontoLineaActual());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoCompraUsado(tarjetaMC.getMontoCompraUsado());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setMontoPorProcesar(tarjetaMC.getMontoPorProcesar());
						buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setDispEfectivoUsado(tarjetaMC.getDispEfectivoUsado());
						
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta() != null) {
						
						if (buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
								.getEstado().equals(TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod())
								|| buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
										.getEstado().equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod())) {
							
							buscarTarjetaModel.setBusquedaRealizada(true);
							buscarTarjetaModel.setTipoOperacionActualizar(
									TipoEstadoTarjeta.esEstadoTarjetaParaActualizarDatosContacto(buscarTarjetaModel
													.getDatosTarjetaCliente().getTarjeta().getEstado()));
							
							buscarTarjetaModel.setTipoOperacionCancelar(TipoEstadoTarjeta
											.esEstadoTarjetaCancelarTarjeta(buscarTarjetaModel
													.getDatosTarjetaCliente().getTarjeta().getEstado()));
							
							buscarTarjetaModel.getDatosTarjetaCliente().getCliente().setApCompleto(
									buscarTarjetaModel.getDatosTarjetaCliente().getCliente().getApPaterno()+" "+
									buscarTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno()
									);
							
							// buscarTarjetaModel
							// .getDatosTarjetaCliente()
							// .getCliente()
							// .setFlagActualizar(
							// buscarTarjetaModel
							// .getDatosTarjetaCliente()
							// .getTarjeta()
							// .getEstado()
							// .equals(TipoEstadoTarjeta.TARJETA_ACTIVADA
							// .getCod()) ? "1" : "0");

							System.out.println("EntregaUbigeo:"+ buscarTarjetaModel.getDatosTarjetaCliente()
									.getTarjeta().getEntregaUbigeo());
							String codDepartamento = buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta()
									.getEntregaUbigeo().substring(0, 2);
							System.out.println("EntregaUbigeo:"	+ buscarTarjetaModel.getDatosTarjetaCliente()
											.getTarjeta().getEntregaUbigeo().substring(0, 2));
							String codProvincia = buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getEntregaUbigeo().substring(2, 4);
							System.out.println("EntregaUbigeo:"
									+ buscarTarjetaModel.getDatosTarjetaCliente()
											.getTarjeta().getEntregaUbigeo()
											.substring(2, 4));
							String codDistrito = buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getEntregaUbigeo().substring(4);
							System.out.println("EntregaUbigeo:"
									+ buscarTarjetaModel.getDatosTarjetaCliente()
											.getTarjeta().getEntregaUbigeo()
											.substring(4));
							buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaDepartamento(
								ubigeoService.buscarLocalidad(codDepartamento.concat("0000"),1).getDescripcion());
							
							buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaProvincia(
								ubigeoService.buscarLocalidad(codDepartamento.concat(codProvincia.concat("00")),2).getDescripcion());
							
							buscarTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEntregaDistrito(
								ubigeoService.buscarLocalidad(codDepartamento.concat(codProvincia
										.concat(codDistrito)),3).getDescripcion());
							
							if (buscarTarjetaModel.getDatosTarjetaCliente()
									.getTarjeta().getDescripcionUbicacion() == null) {
								buscarTarjetaModel
										.getDatosTarjetaCliente()
										.getTarjeta()
										.setDescripcionUbicacion(
												agenciaService
														.buscarAgenciaPorCodAgencia(
																buscarTarjetaModel
																		.getDatosTarjetaCliente()
																		.getTarjeta()
																		.getEntregaAgenciaBN())
														.getDescripcion());
							}

							EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
							estadoTarjeta.setEstado(buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getEstado());
							estadoTarjeta.setFechaRegistro(buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getFechaBloqueo());
							estadoTarjeta.setMotivo(buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getMotivoBloqueo());
							estadoTarjeta.setUsuarioRegistro(buscarTarjetaModel
									.getDatosTarjetaCliente().getTarjeta()
									.getUsuarioBloqueo());

							buscarTarjetaModel.getDatosTarjetaCliente()
									.setEstadoTarjeta(estadoTarjeta);

							UsefulWebApplication
									.actualizarComponente("formEmpresa:pgResultado");
						} else {
							UsefulWebApplication.mostrarMensajeJSF(3, "",
									"La tarjeta tiene el estado : "
											+ buscarTarjetaModel
													.getDatosTarjetaCliente()
													.getTarjeta().getEstado());
							UsefulWebApplication.actualizarComponente("msgs");
						}
					} else {
						UsefulWebApplication.mostrarMensajeJSF(3, "",
								"El número de tarjeta no existe");

						UsefulWebApplication.actualizarComponente("msgs");

						buscarTarjetaModel.setBusquedaRealizada(false);

						UsefulWebApplication
								.actualizarComponente("formEmpresa:pgResultado");
					}
				}
			}
			
		} catch (InternalServiceException ise) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(ise.getMessage());
		} catch (ExternalServiceBnTablasException este) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
			logger.error(este.getMessage());
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication
					.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (ServiceException es) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_GENERAL,
					ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
			logger.error(es.getMessage());
		}
	}

	public void cancelarTarjeta() {
		try {
			buscarTarjetaModel.iniciarEstadoTarjeta();
			tarjetaService.actualizarEstadoTarjeta(buscarTarjetaModel
					.getEstadoTarjeta());

			UsefulWebApplication.mostrarMensajeJSF(1, "",
					"Se canceló la solicitud");
			buscarTarjetaModel.inicializarFormulario();
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication
					.actualizarComponente("formEmpresa:pgResultado");
		} catch (InternalServiceException ise) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(ise.getMessage());
		}
	}

	public void actualizarDatosContactoTarjetaHabiente() {
		try {
			tarjetaService.actualizarContacto(buscarTarjetaModel
					.getDatosTarjetaCliente().getTarjeta());
			clienteService.actualizarCliente(buscarTarjetaModel
					.getDatosTarjetaCliente().getCliente());
			UsefulWebApplication.mostrarMensajeJSF(1, "",
					"Se actualizo los datos de contacto exitosamente");
			buscarTarjetaModel.inicializarFormulario();
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication
					.actualizarComponente("formEmpresa:pgResultado");
		} catch (InternalServiceException ise) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(ise.getMessage());
		}
	}

	public BuscarTarjetaModel getBuscarTarjetaModel() {
		return buscarTarjetaModel;
	}

	public void setBuscarTarjetaModel(BuscarTarjetaModel buscarTarjetaModel) {
		this.buscarTarjetaModel = buscarTarjetaModel;
	}
	
	
	public void buscarAsignaciones() {
		try {
			List<Asignacion> asignaciones = null;
			if (buscarTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				
				String diseño = buscarTarjetaModel.getTipoTarjetaNegocioSeleccionada().getDiseno();
				String tipoTar = buscarTarjetaModel.getTipoTarjetaNegocioSeleccionada().getCodigo();				
				
				System.out.println("diseño de asignacion::"+ diseño);	
				System.out.println("tipoTar de asignacion::"+ tipoTar);
				
				//asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjeta(buscarTarjetaModel.getNumDocumento());

				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(buscarTarjetaModel.getNumDocumento(), 19);
							
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjeta(tarjeta19, diseño,tipoTar);

				if(asignaciones.isEmpty() && asignaciones.size()==0){
					
					buscarTarjetaModel.inicializarFormulario();	
					
					UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA);
				}else{				
					buscarTarjetaModel.setAsignacionesTotal(asignaciones);
					// MOSTRAR MODAL COMPONENTE
					UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
					// formulario del componente
					UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
				}
				
			} else if (buscarTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| buscarTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				
				String diseño = buscarTarjetaModel.getTipoTarjetaNegocioSeleccionada().getDiseno();
				String tipoTar = buscarTarjetaModel.getTipoTarjetaNegocioSeleccionada().getCodigo();				
				
				System.out.println("diseño de asignacion::"+ diseño);	
				System.out.println("tipoTar de asignacion::"+ tipoTar);
				
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumento(
						buscarTarjetaModel.getTipoBusqueda(), buscarTarjetaModel.getNumDocumento(), diseño, tipoTar);
			
				if(asignaciones.isEmpty() && asignaciones.size()==0){
					
					buscarTarjetaModel.inicializarFormulario();				
										
					
					UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO);
				}else{				
					buscarTarjetaModel.setAsignacionesTotal(asignaciones);
					// MOSTRAR MODAL COMPONENTE
					UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
					// formulario del componente
					UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
				}
			}
			
		} catch (InternalExcepcion se) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(se.getMessage());
		}

	}
	
	public void seleccionarAsignacion() {
		
		System.out.println("LLEGO A seleccionarAsignacion");
		buscarTarjeta();
		
		
	}	


	public void buscarTipoTarjetaNegocio() {
    	
  	  if (buscarTarjetaModel.getTipoTarjetaSeleccionada().getCodigoBim().equals(ConstantesGenerales.BIM_BLACK)) {
            buscarTarjetaModel.setListaTipoTarjetaNegocio(TipoTarjetaNegocio.buscarTipoTarjetaBLACK());
        } else if (buscarTarjetaModel.getTipoTarjetaSeleccionada().getCodigoBim().equals(ConstantesGenerales.BIM_CORPORATE)) {
            buscarTarjetaModel.setListaTipoTarjetaNegocio(
            		TipoTarjetaNegocio.buscarTipoTarjetaCORP());
        
        } else {
            buscarTarjetaModel.setListaTipoTarjetaNegocio(null);
        }  	    
	}
	
	public void buscarTipoBusqueda() {
  	  if (buscarTarjetaModel.getTipoBusquedaPor().equals("Por Documento")) {
  		  buscarTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
        } else if (buscarTarjetaModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
      	  buscarTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
        } else {
      	  buscarTarjetaModel.setListaTipoBusqueda(null);
        }
  }
	
}
