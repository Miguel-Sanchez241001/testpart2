package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.CambiarEstadoTarjetaModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceBnTablasException;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.AgenciaService;
import pe.bn.com.sate.ope.infrastructure.service.external.UbigeoService;
import pe.bn.com.sate.ope.infrastructure.service.internal.EmpresaService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.configuration.security.SecurityContextFacade;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.Empresa;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.ModificacionTarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Agencia;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionTarjeta;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesWS;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoCuenta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Controller("cambiarEstadoTarjetaController")
@Scope("view")
public class CambiarEstadoTarjetaController implements Serializable {

	private final static Logger logger = Logger
			.getLogger(CambiarEstadoTarjetaController.class);

	private static final long serialVersionUID = 1L;

	private CambiarEstadoTarjetaModel cambiarEstadoTarjetaModel;

	private @Autowired
	TarjetaService tarjetaService;

	private @Autowired
	FWMCProcesos fwmcProcesos;

	private @Autowired
	Parametros parametros;
	
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
	}

	public void buscarTarjeta() {
		//MGL
		UsefulWebApplication.mostrarDialogo("statusDialog");
		try {
			if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(
					TipoBusqueda.NUM_TARJETA.getId())) {
				
				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(cambiarEstadoTarjetaModel.getNumDocumento(), 19);
				
				
				cambiarEstadoTarjetaModel.setDatosTarjetaCliente(tarjetaService
						.buscarDatosTarjetasCliente(
								cambiarEstadoTarjetaModel.getTipoBusqueda(),
								tarjeta19,
								"C"));

				if (cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
						.getTarjeta() != null) {
					// if (cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
					// .getTarjeta().getEstado()
					// .equals(TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod())
					// || (cambiarEstadoTarjetaModel
					// .getDatosTarjetaCliente()
					// .getTarjeta()
					// .getEstado()
					// .equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA
					// .getCod()) && cambiarEstadoTarjetaModel
					// .getDatosTarjetaCliente()
					// .getTarjeta()
					// .getMotivoBloqueo()
					// .equals(MotivosBloqueoTarjeta.NO_RECLAMADA
					// .getId()))) {
					cambiarEstadoTarjetaModel.setBusquedaRealizada(true);

					EstadoTarjeta estadoTarjeta = new EstadoTarjeta();
					estadoTarjeta.setEstado(cambiarEstadoTarjetaModel
							.getDatosTarjetaCliente().getTarjeta().getEstado());
					estadoTarjeta.setFechaRegistro(cambiarEstadoTarjetaModel
							.getDatosTarjetaCliente().getTarjeta()
							.getFechaBloqueo());
					estadoTarjeta.setMotivo(cambiarEstadoTarjetaModel
							.getDatosTarjetaCliente().getTarjeta()
							.getMotivoBloqueo());
					estadoTarjeta.setUsuarioRegistro(cambiarEstadoTarjetaModel
							.getDatosTarjetaCliente().getTarjeta()
							.getUsuarioBloqueo());
					estadoTarjeta.setEstadoCuenta(cambiarEstadoTarjetaModel
							.getDatosTarjetaCliente().getTarjeta()
							.getEstadoCuenta());

					cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
							.setEstadoTarjeta(estadoTarjeta);

					cambiarEstadoTarjetaModel
							.setMotivosBloqueoTarjetas(Arrays.asList(MotivosBloqueoTarjeta
									.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
											.getDatosTarjetaCliente()
											.getTarjeta().getEstado())));
					
//					/*MGL - el valor de estado de cuenta viene en null
//					 * se le indico a pract-miguel que revise el valor de la tarjeta
//					 * por ahora le mando n*/
//					cambiarEstadoTarjetaModel
//					.getDatosTarjetaCliente()
//					.getTarjeta().setEstadoCuenta("N");
					
					cambiarEstadoTarjetaModel
							.setMotivosBloqueoCuenta(Arrays.asList(MotivosBloqueoCuenta
									.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
											.getDatosTarjetaCliente()
											.getTarjeta().getEstadoCuenta())));

					cambiarEstadoTarjetaModel
							.setBusquedaBloqueoTarjeta(cambiarEstadoTarjetaModel
									.getDatosTarjetaCliente()
									.getTarjeta()
									.getEstado()
									.equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA
											.getCod()));

					UsefulWebApplication.ocultarDialogo("statusDialog");
					UsefulWebApplication
							.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
					// } else {
					// UsefulWebApplication.mostrarMensajeJSF(3, "",
					// "La tarjeta tiene el estado :"
					// + cambiarEstadoTarjetaModel
					// .getDatosTarjetaCliente()
					// .getTarjeta().getEstado());
					// UsefulWebApplication.actualizarComponente("msgs");
					// }
				} else {
					UsefulWebApplication.ocultarDialogo("statusDialog");
					UsefulWebApplication.mostrarMensajeJSF(3, "",
							"El número de tarjeta no existe");

					UsefulWebApplication.actualizarComponente("msgs");

					cambiarEstadoTarjetaModel.setBusquedaRealizada(false);

					cambiarEstadoTarjetaModel.setBusquedaBloqueoTarjeta(false);
					UsefulWebApplication
							.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				}

			} else if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				
				cambiarEstadoTarjetaModel.setDatosTarjetaCliente(tarjetaService
						.buscarDatosTarjetasCliente(
								cambiarEstadoTarjetaModel.getTipoBusqueda(),
								cambiarEstadoTarjetaModel.getNumDocumento(),
								"C"));

				cambiarEstadoTarjetaModel.setBusquedaRealizada(false);

				cambiarEstadoTarjetaModel.setBusquedaBloqueoTarjeta(false);

				if (cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
						.getCliente() == null) {
					UsefulWebApplication
							.mostrarMensajeJSF(3, "",
									"No existe TarjetaHabiente con ese tipo y número de documento.");
					UsefulWebApplication.actualizarComponente("msgs");
				} else {
					
					
//					UsefulWebApplication.ejecutar("wvSeleccionarTajeta.show()");
//					UsefulWebApplication
//							.actualizarComponente("formSeleccionarTarjeta");
//					UsefulWebApplication
//							.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
//					
					

					cambiarEstadoTarjetaModel.setBusquedaRealizada(true);
					
//					cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setTarjeta(							
//							cambiarEstadoTarjetaModel.getTarjetaSeleccionada());
					
					cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setTarjeta(
							cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas().get(0));
					
//					System.out.println("Motivo:"
//							+ cambiarEstadoTarjetaModel.getTarjetaSeleccionada()
//									.getMotivoBloqueo());
				
					cambiarEstadoTarjetaModel.setMotivosBloqueoTarjetas(Arrays
							.asList(MotivosBloqueoTarjeta
									.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
											.getDatosTarjetaCliente()
											.getTarjeta().getEstado())));
					
//					/*MGL - el valor de estado de cuenta viene en null
//					 * se le indico a pract-miguel que revise el valor de la tarjeta
//					 * por ahora le mando n*/
//					
//					cambiarEstadoTarjetaModel
//					.getDatosTarjetaCliente()
//					.getTarjeta().setEstadoCuenta("N");
					
					cambiarEstadoTarjetaModel.setMotivosBloqueoCuenta(Arrays
							.asList(MotivosBloqueoCuenta
									.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
											.getDatosTarjetaCliente().getTarjeta()
											.getEstadoCuenta())));
					UsefulWebApplication.ocultarDialogo("statusDialog");
					UsefulWebApplication
					.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				}

			}
		} catch (InternalServiceException ise) {
			UsefulWebApplication.ocultarDialogo("statusDialog");
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(ise.getMessage());
		}

	}

	public void seleccionarTarjeta() {
		cambiarEstadoTarjetaModel.setBusquedaRealizada(true);
		cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setTarjeta(
				cambiarEstadoTarjetaModel.getTarjetaSeleccionada());
		System.out.println("Motivo:"
				+ cambiarEstadoTarjetaModel.getTarjetaSeleccionada()
						.getMotivoBloqueo());

		/*cambiarEstadoTarjetaModel.setMotivosBloqueoTarjetas(Arrays
				.asList(MotivosBloqueoTarjeta
						.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
								.getTarjetaSeleccionada().getMotivoBloqueo())));*/
		cambiarEstadoTarjetaModel.setMotivosBloqueoTarjetas(Arrays
				.asList(MotivosBloqueoTarjeta
						.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
								.getDatosTarjetaCliente()
								.getTarjeta().getEstado())));
		
		cambiarEstadoTarjetaModel.setMotivosBloqueoCuenta(Arrays
				.asList(MotivosBloqueoCuenta
						.motivosBloqueoPorIdMotivo(cambiarEstadoTarjetaModel
								.getDatosTarjetaCliente().getTarjeta()
								.getEstadoCuenta())));
	}

	public void cambiarEstadoTarjeta() {

		try {
			DTOModificacionTarjeta modificacionTarjeta = new DTOModificacionTarjeta();
			 
			cambiarEstadoTarjetaModel.iniciarEstadoTarjeta();
			
			String tipoMoneda = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda().trim();
			System.out.println("tipoMoneda:"+tipoMoneda);	
			String numTarjeta = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta().trim();
			System.out.println("numTarjeta:"+numTarjeta);
	       
			if (cambiarEstadoTarjetaModel.getTipoBloqueoSeleccionado().equals("T")) {
				
				String motivo = cambiarEstadoTarjetaModel.getMotivoSeleccionado();
				char guion = '-';
			    int posicion = motivo.indexOf(guion);
			       
			    String codMotivo = motivo.substring(0,posicion).trim();
			    String desMotivo = motivo.substring(posicion + 1).trim();
			    
			    System.out.println("codMotivo:"+codMotivo);
			    System.out.println("desMotivo:"+desMotivo);				
				
				System.out.println(cambiarEstadoTarjetaModel.getEstadoTarjeta().toString());
				
				//if (cambiarEstadoTarjetaModel.getEstadoTarjeta().getMotivo().equals(MotivosBloqueoTarjeta.ROBO.getId())) {
				if (codMotivo.equals(MotivosBloqueoTarjeta.ROBO.getId())) {
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
				        	//modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda,numTarjeta,codMotivo,desMotivo);
						 	modificacionTarjeta.setCodRespuesta("0000");
				        	 System.out.println("codRespuesta:"+modificacionTarjeta.getCodRespuesta());
							 //System.out.println("desRespuesta:"+modificacionTarjeta.getDescRespuesta());
				        	
				        	if (modificacionTarjeta.getCodRespuesta().equals("0000")) {        		
				        		
				        		tarjetaService.actualizarEstadoTarjeta(cambiarEstadoTarjetaModel.getEstadoTarjeta());
				        		
				        		cambiarEstadoTarjetaModel.inicializarFormulario();
				        		
				        		UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "","Se cambio de estado exitosamente");
				        		UsefulWebApplication.actualizarComponente("msgs");
				        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				        			        		
				        	}else{
				        		UsefulWebApplication
								.mostrarMensajeJSF(
										ConstantesGenerales.SEVERITY_ERROR,
										modificacionTarjeta.getDescRespuesta(),
										ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
						
				        	}
				       } catch (ExternalServiceMCProcesosException este) {				
							UsefulWebApplication
							.mostrarMensajeJSF(
									ConstantesGenerales.SEVERITY_ERROR,
									ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
									ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
							logger.error(este.getMessage());
							UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
//						} catch (InternalExcepcion e) {						
//							// TODO Auto-generated catch block
//							UsefulWebApplication.mostrarMensajeJSF(
//									ConstantesGenerales.SEVERITY_ERROR,
//									ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
//									ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
//							logger.error(e.getMessage());
//							UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
						}
					
					
//					tarjetaService.actualizarEstadoTarjeta(cambiarEstadoTarjetaModel.getEstadoTarjeta());
//
//					cambiarEstadoTarjetaModel.inicializarFormulario();
//					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "","Se cambio de estado exitosamente");
//					UsefulWebApplication.actualizarComponente("msgs");
//					UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
				}
			} else {
				
				/*ini MGL*/
				
				 try {
					 
					 
					 String motivo = cambiarEstadoTarjetaModel.getEstadoCuentaSeleccionado();
						char guion = '-';
					    int posicion = motivo.indexOf(guion);
					       
					    String codMotivo = motivo.substring(0,posicion).trim();
					    String desMotivo = motivo.substring(posicion + 1).trim();
					    
					    System.out.println("codMotivo:"+codMotivo);
					    System.out.println("desMotivo:"+desMotivo);				
					 
					   // modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda,numTarjeta,codMotivo,desMotivo);
					    modificacionTarjeta.setCodRespuesta("0000");
					    System.out.println("codRespuesta:"+modificacionTarjeta.getCodRespuesta());
					    System.out.println("desRespuesta:"+modificacionTarjeta.getDescRespuesta());
					    
			        	if (modificacionTarjeta.getCodRespuesta().equals("0000")) {        		
			        		
							cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().setEstadoCuenta(cambiarEstadoTarjetaModel.getEstadoCuentaSeleccionado());
			        		
			        		tarjetaService.actualizarEstadoCuenta(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta());
			        		
			        		cambiarEstadoTarjetaModel.inicializarFormulario();
			        		
			        		UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "","Se cambio de estado exitosamente");
			        		UsefulWebApplication.actualizarComponente("msgs");
			        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
			        		
			        		
			        			        		
			        	}else{
			        		UsefulWebApplication
							.mostrarMensajeJSF(
									ConstantesGenerales.SEVERITY_ERROR,
									modificacionTarjeta.getDescRespuesta(),
									ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
					
			        	}
			       } catch (ExternalServiceMCProcesosException este) {				
						UsefulWebApplication
						.mostrarMensajeJSF(
								ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
								ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
						logger.error(este.getMessage());
						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
//					} catch (InternalExcepcion e) {						
//						// TODO Auto-generated catch block
//						UsefulWebApplication.mostrarMensajeJSF(
//								ConstantesGenerales.SEVERITY_ERROR,
//								ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
//								ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
//						logger.error(e.getMessage());
//						UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
					}
				
				/*FIN MGL*/
				

//				cambiarEstadoTarjetaModel.inicializarFormulario();
//				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "","Se cambio de estado exitosamente");
//				UsefulWebApplication.actualizarComponente("msgs");
//				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
			}
		 } catch (ExternalServiceMCProcesosException este) {				
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				logger.error(este.getMessage());
				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
			}
		
		
		
		
	}

	public void bloquearTarjetaRobo() {
		
			String tipoMoneda = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda().trim();
			System.out.println("tipoMoneda:"+tipoMoneda);	
			String numTarjeta = cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta().trim();
			System.out.println("numTarjeta:"+numTarjeta);
		
			DTOModificacionTarjeta modificacionTarjeta = new DTOModificacionTarjeta();
			
			System.out.println("idtarjeta:"
					+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
							.getTarjeta().getId());
			System.out.println("idtarjeta:"
					+ cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
							.getCliente().getId());
			
			String motivo = cambiarEstadoTarjetaModel.getMotivoSeleccionado();
			char guion = '-';
		    int posicion = motivo.indexOf(guion);
		       
		    String codMotivo = motivo.substring(0,posicion).trim();
		    String desMotivo = motivo.substring(posicion + 1).trim();
		    
		    System.out.println("codMotivo:"+codMotivo);
		    System.out.println("desMotivo:"+desMotivo);				
			
			
		    try {
		    	
	        	//modificacionTarjeta = fwmcProcesos.modificacionTarjeta(tipoMoneda,numTarjeta,codMotivo,desMotivo);
		    	 modificacionTarjeta.setCodRespuesta("0000");
	        	System.out.println("codRespuesta:"+modificacionTarjeta.getCodRespuesta());
	        	//System.out.println("desRespuesta:"+modificacionTarjeta.getDescRespuesta());
	        	
	        	if (modificacionTarjeta.getCodRespuesta().equals("0000")) { 
	        		
	        		System.out.println();
	        		
	        		tarjetaService.bloquearTarjetaPorRobo(
	    					cambiarEstadoTarjetaModel.getEstadoTarjeta(),
	    					cambiarEstadoTarjetaModel.getDatosTarjetaCliente()
	    							.getTarjeta().getId(), cambiarEstadoTarjetaModel
	    							.getDatosTarjetaCliente().getCliente().getId());
	        		
	        		cambiarEstadoTarjetaModel.inicializarFormulario();
	        		
	        		UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_INFO, "","Se cambio de estado exitosamente");
	        		UsefulWebApplication.actualizarComponente("msgs");
	        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
	        			        		
	        	}else{
	        		UsefulWebApplication
					.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							modificacionTarjeta.getDescRespuesta(),
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
	        		UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
			
	        	}
	       } catch (ExternalServiceMCProcesosException este) {				
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				logger.error(este.getMessage());
				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
//			} catch (InternalExcepcion e) {						
//				// TODO Auto-generated catch block
//				UsefulWebApplication.mostrarMensajeJSF(
//						ConstantesGenerales.SEVERITY_ERROR,
//						ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
//						ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
//				logger.error(e.getMessage());
//				UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
			}	
			
	}

	public CambiarEstadoTarjetaModel getCambiarEstadoTarjetaModel() {
		return cambiarEstadoTarjetaModel;
	}

	public void setCambiarEstadoTarjetaModel(
			CambiarEstadoTarjetaModel cambiarEstadoTarjetaModel) {
		this.cambiarEstadoTarjetaModel = cambiarEstadoTarjetaModel;
	}
	
	/************************************/
	
	public void buscarAsignaciones() {
		try {
			List<Asignacion> asignaciones = null;
			if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
												
				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(cambiarEstadoTarjetaModel.getNumDocumento(), 19);
				
				
				if(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas() == null ||
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas().isEmpty()
						){
					System.out.println("entro if");
					
					UsefulWebApplication.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_TARJETAS_BLOQUEADAS ,
							ConstantesGenerales.ERROR_MENSAJE_TARJETAS_BLOQUEADAS);
					
				}else{
					System.out.println("entro else");
				
							
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjetaSimple(tarjeta19);	
				
				if(asignaciones.isEmpty() && asignaciones.size()==0){
					cambiarEstadoTarjetaModel.inicializarFormulario();
					
					UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA);
					
					UsefulWebApplication.actualizarComponente("msgs");
					UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta");
					
					
				}else{		
					
					cambiarEstadoTarjetaModel.setBusquedaRealizada(true);
					cambiarEstadoTarjetaModel.setAsignacionesTotal(asignaciones);
					// MOSTRAR MODAL COMPONENTE
					UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
					// formulario del componente
					UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
				}
			}

			} else if (cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| cambiarEstadoTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				
						
				cambiarEstadoTarjetaModel.setDatosTarjetaCliente(tarjetaService
						.buscarDatosTarjetasCliente(
								cambiarEstadoTarjetaModel.getTipoBusqueda(),
								cambiarEstadoTarjetaModel.getNumDocumento(),
								"C"));
				
				System.out.println(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas().size());
				
				if(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas() == null ||
						cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas().isEmpty()
						){
					System.out.println("entro if");
					
					UsefulWebApplication.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_TARJETAS_BLOQUEADAS ,
							ConstantesGenerales.ERROR_MENSAJE_TARJETAS_BLOQUEADAS);
					
				}else{
					System.out.println("entro else");
					
					asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumentoSimple(
							cambiarEstadoTarjetaModel.getTipoBusqueda(), cambiarEstadoTarjetaModel.getNumDocumento());
					
									
					if(asignaciones.isEmpty() && asignaciones.size()==0){
						cambiarEstadoTarjetaModel.inicializarFormulario();
						UsefulWebApplication.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO,
						ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO);
						
						
					}else{	
						
						//cambiarEstadoTarjetaModel.getDatosTarjetaCliente().setTarjeta(cambiarEstadoTarjetaModel.getDatosTarjetaCliente().getTarjetas().get(0));
						
						cambiarEstadoTarjetaModel.setBusquedaRealizada(true);
						cambiarEstadoTarjetaModel.setAsignacionesTotal(asignaciones);
						// MOSTRAR MODAL COMPONENTE
						UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
						// formulario del componente
						UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
					}
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
		System.out.println("hollllaaa");
		//buscarTarjeta();
	}
	
	public void buscarTipoBusqueda() {
	  	  if (cambiarEstadoTarjetaModel.getTipoBusquedaPor().equals("Por Documento")) {
	  		cambiarEstadoTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
	        } else if (cambiarEstadoTarjetaModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
	        	cambiarEstadoTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
	        } else {
	        	cambiarEstadoTarjetaModel.setListaTipoBusqueda(null);
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
                    .buscarEmpresaPorRUC(SecurityContextFacade
                        .getAuthenticatedUser().getRuc());
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaDireccion(
                    empresa.getDireccion());
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaUbigeo(
                    empresa.getUbigeo());
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaAgenciaBN("0000");
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(
                    empresa.getReferencia());
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
            UsefulWebApplication.mostrarMensajeJSF(
                ConstantesGenerales.SEVERITY_ERROR,
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
                cambiarEstadoTarjetaModel.setAgenciasBN(agenciaService
                    .buscarAgenciasPorUbigeo(departamento, provincia, distrito));
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaAgenciaBN(null);
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
            } catch (ExternalServiceBnTablasException este) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
                logger.error(este.getMessage());
            } catch (ServiceException es) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
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
                .buscarAgenciaPorCodAgencia(cambiarEstadoTarjetaModel
                    .getAgenciaSeleccionada().getCodAgencia());
            cambiarEstadoTarjetaModel.getTarjeta().setEntregaDireccion(
                agencia == null ? "No hay dirección registrada" : agencia
                    .getDireccion());
            logger.info("[SolicitarTarjetaController] fin metodo buscarDatosAgencia");
        } catch (ExternalServiceBnTablasException este) {
            UsefulWebApplication.mostrarMensajeJSF(
                ConstantesGenerales.SEVERITY_ERROR,
                ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
                ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
            logger.error(este.getMessage());
        } catch (ServiceException es) {
            UsefulWebApplication.mostrarMensajeJSF(
                ConstantesGenerales.SEVERITY_ERROR,
                ConstantesGenerales.ERROR_PERSISTENCE_GENERAL,
                ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
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
                cambiarEstadoTarjetaModel.setProvincias(ubigeoService
                    .buscarProvinciasPorDepartamento(departamento));
                cambiarEstadoTarjetaModel.setDistritos(null);
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaProvincia(null);
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
                cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
            } catch (ExternalServiceBnTablasException este) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
                logger.error(este.getMessage());
            } catch (ServiceException es) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
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
                cambiarEstadoTarjetaModel.setDistritos(ubigeoService
                    .buscarDistritosPorProvincia(departamento, provincia));
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaDistrito(null);
                cambiarEstadoTarjetaModel.setAgenciaSeleccionada(null);
                cambiarEstadoTarjetaModel.getTarjeta().setEntregaReferencia(null);
            } catch (ExternalServiceBnTablasException este) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS,
                    ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_BN_TABLAS);
                logger.error(este.getMessage());
            } catch (ServiceException es) {
                UsefulWebApplication.mostrarMensajeJSF(
                    ConstantesGenerales.SEVERITY_ERROR,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL,
                    ConstantesGenerales.ERROR_PERSISTENCE_GENERAL);
                logger.error(es.getMessage());
            }
        }
        logger.info("[SolicitarTarjetaController] Fin metodo buscarDistritos");
    }

}
