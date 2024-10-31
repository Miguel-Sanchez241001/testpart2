package pe.bn.com.sate.ope.application.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarMovimientosModel;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaMovimientosExpediente;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;



@Controller("consultarMovimientosController")
@Scope("view")
public class ConsultarMovimientosController {

	private final static Logger logger = Logger.getLogger(ConsultarMovimientosController.class);

	private ConsultarMovimientosModel consultarMovimientosModel;

	private @Autowired FWMCProcesos fwmcProcesos;

	private @Autowired TarjetaService tarjetaService;
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		consultarMovimientosModel = new ConsultarMovimientosModel();
	}

	public void buscarMovimientos() {
		consultarMovimientosModel.limpiarMovimientosTarjeta();
		try {
			consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
					consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta(), "B"));

			try {
				
				if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
					fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea()

					);
					

				} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
						|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
					
					fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getFechaTerminoLinea()
					);					
				}
				
			} catch (InternalExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
		}
	}

	public void seleccionarAsignacion() {
		consultarMovimientosModel.limpiarMovimientosTarjeta();
		try {
			
			

			// consultarMovimientosModel.setMovimientosTarjeta(fwmcProcesos
			// .consultarMovimientosPorTarjeta(consultarMovimientosModel
			// .getNumeroTarjeta()));

			// aui debe llegar el valor del pop-up
			DTOConsultaMovimientosExpediente dato = null;
			List<MovimientoTarjetaExpediente> listDato;
			
			String diseno = "";
			String tipTarj = "";
			
			try {				
				if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
					String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(consultarMovimientosModel.getNumeroTarjeta(), 19);
										
					consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
							consultarMovimientosModel.getTipoBusqueda(), tarjeta19, "B"));
					
					System.out.println("consultarMovimientosModel.getTipoBusqueda():::"+consultarMovimientosModel.getTipoBusqueda());
					dato=fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea()
					);
					
					diseno = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getDiseno();
					tipTarj = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoTarjeta();
					
				} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
						|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {																
					
					consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
							consultarMovimientosModel.getTipoBusqueda(),
							consultarMovimientosModel.getNumeroTarjeta(), "B"));
					
					System.out.println("consultarMovimientosModel.getTipoBusqueda():::"+consultarMovimientosModel.getTipoBusqueda());
					
					
					dato=fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getFechaTerminoLinea()
					);	
					
					diseno = consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getDiseno();
					tipTarj = consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoTarjeta();
					
					consultarMovimientosModel.getDatosTarjetaCliente().setTarjeta(consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0));
				}	
				
				System.out.println("diseno:"+diseno);
				System.out.println("tipTarj:"+tipTarj);
				
				if (dato.getCodRespuesta().equals("0000")) { 
					
					String tipoTarjeta = TipoTarjeta.descripcionTipotarjeta(tipTarj);
					System.out.println("tipoTarjeta:"+tipoTarjeta);
					
					listDato=fwmcProcesos.listaMovTarjExp(dato,tipoTarjeta);
													
					consultarMovimientosModel.setMovimientosTarjetaExp(listDato);
					
					UsefulWebApplication.actualizarComponente("msgs");
					UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
					UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:listaTarjetasPanel");
	        		        		
	        	}else{
	        		consultarMovimientosModel.setBusquedaRealizada(false);
	        		consultarMovimientosModel.inicializarFormulario();
	        		UsefulWebApplication
					.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							dato.getDescRespuesta(),
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);	        		
	        		UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:listaTarjetasPanel");
	        		UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
			
	        	}
				
				
				
				
				
			} catch (InternalExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
		}
	}

	public ConsultarMovimientosModel getConsultarMovimientosModel() {
		return consultarMovimientosModel;
	}

	public void setConsultarMovimientosModel(ConsultarMovimientosModel consultarMovimientosModel) {
		this.consultarMovimientosModel = consultarMovimientosModel;
	}

	public void buscarAsignaciones() {
		try {
			List<Asignacion> asignaciones = null;
			if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				
				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(consultarMovimientosModel.getNumeroTarjeta(), 19);
				
			
				
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjetaSimple(tarjeta19);	
				
				if(asignaciones.isEmpty() && asignaciones.size()==0){
					consultarMovimientosModel.inicializarFormulario();
					
					UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA);
					
				}else{				
					consultarMovimientosModel.setBusquedaRealizada(true);
					consultarMovimientosModel.setAsignacionesTotal(asignaciones);
					// MOSTRAR MODAL COMPONENTE
					UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
					// formulario del componente
					UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
				}

			} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				
							
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumentoSimple(
						consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta());
								
				if(asignaciones.isEmpty() && asignaciones.size()==0){
					consultarMovimientosModel.inicializarFormulario();
					UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO,
					ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO);
					
					
				}else{		
					consultarMovimientosModel.setBusquedaRealizada(true);
					consultarMovimientosModel.setAsignacionesTotal(asignaciones);
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
	
	public void buscarTipoBusqueda() {
	  	  if (consultarMovimientosModel.getTipoBusquedaPor().equals("Por Documento")) {
	  		  consultarMovimientosModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
	        } else if (consultarMovimientosModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
	      	  consultarMovimientosModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
	        } else {
	      	  consultarMovimientosModel.setListaTipoBusqueda(null);
	        }
	  }
	


}