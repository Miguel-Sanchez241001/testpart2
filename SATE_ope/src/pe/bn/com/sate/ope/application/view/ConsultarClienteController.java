package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarClienteModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosCliente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionClientes;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Controller("consultarClienteController")
@Scope("view")
public class ConsultarClienteController implements Serializable {

	private final static Logger logger = Logger
			.getLogger(ConsultarClienteController.class);

	private static final long serialVersionUID = 1L;

	private ConsultarClienteModel consultarClienteModel;

 
	private @Autowired
	ClienteService clienteService;

	private @Autowired 
	FWMCProcesos fwmcProcesos;
 
	@PostConstruct
	public void init() {
		consultarClienteModel = new ConsultarClienteModel();
	}

	public ConsultarClienteModel getConsultarClienteModel() {
		return consultarClienteModel;
	}

	public void setConsultarClienteModel(ConsultarClienteModel consultarClienteModel) {
		this.consultarClienteModel = consultarClienteModel;
	}
	
	
	public void consultarCliente() {
		logger.info("LLEGO A consultarCliente");
		
		logger.info("tipo doc:"+consultarClienteModel.getTipoBusqueda());
		logger.info("num doc"+consultarClienteModel.getNumDocumento());
		
		DTOConsultaDatosCliente datosCliente = new  DTOConsultaDatosCliente();
		
		long valor=0;
		String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();
		
		valor=clienteService.consultarExisteClienteRUC(consultarClienteModel.getTipoBusqueda(), consultarClienteModel.getNumDocumento(), rucUsuario);
		
		if(valor==0){
			
			consultarClienteModel.setBusquedaRealizada(false);
			UsefulWebApplication
			.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					"El cliente no tiene ninguna tarjeta asociada a esta unidad ejecutora.",
					"El cliente no tiene ninguna tarjeta asociada a esta unidad ejecutora.");
			UsefulWebApplication
			.actualizarComponente("formConsultarCliente:pgResultado");
			
		}else{
			
			try {
				
				datosCliente = fwmcProcesos.consultaDatosCliente(consultarClienteModel.getTipoBusqueda(), consultarClienteModel.getNumDocumento());
				if (datosCliente.getCodRespuesta().equals("0000")) { 
					consultarClienteModel.setBusquedaRealizada(true);
					consultarClienteModel.getDatosTarjetaCliente().getCliente().setTipoDocumento(datosCliente.getTipoDocumento().trim());
					
					if(datosCliente.getTipoDocumento().trim().equals(ConstantesGenerales.CODIGO_DNI)){
						consultarClienteModel.getDatosTarjetaCliente().getCliente().setNroDocumento(StringsUtils.formateo_DNI(datosCliente.getNumDocumento()));
					}else{
						consultarClienteModel.getDatosTarjetaCliente().getCliente().setNroDocumento(datosCliente.getNumDocumento());
					}							
					
					consultarClienteModel.getDatosTarjetaCliente().getCliente().setNombres(datosCliente.getNomCliente());
					consultarClienteModel.getDatosTarjetaCliente().getCliente().setApCompleto(datosCliente.getApeCliente());
					consultarClienteModel.getDatosTarjetaCliente().getTarjeta().setEmail(datosCliente.getCorreoCliente().trim());
					
					consultarClienteModel.getDatosTarjetaCliente().getCliente().setNumCelular(clienteService.buscarClienteNumCel(consultarClienteModel.getTipoBusqueda(), consultarClienteModel.getNumDocumento()));				
					
					consultarClienteModel.getDatosTarjetaCliente().getCliente().setTelefonoCasa(StringsUtils.quitarCeroIzquierdaString(datosCliente.getTelCliente().trim()));
						
					
				}else{
					logger.info("else");
					logger.info("descrip:"+datosCliente.getDescRespuesta().replace(".", ","));
					consultarClienteModel.setBusquedaRealizada(false);
					UsefulWebApplication
					.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							"No existe cliente.",
							"No existe cliente.");
					UsefulWebApplication
					.actualizarComponente("formConsultarCliente:pgResultado");
				
				}
				logger.info("---------");
				
			} catch (ExternalServiceMCProcesosException este) {
				logger.info("ExternalServiceMCProcesosException");
				consultarClienteModel.setBusquedaRealizada(false);
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				logger.error(este.getMessage());
				UsefulWebApplication
				.actualizarComponente("formConsultarCliente:pgResultado");
			} catch (InternalExcepcion e) {
				
 				logger.info("InternalExcepcion");
						e.printStackTrace();
			}
		}
		
		
	}
	
	public void actualizarDatosCliente() throws ExternalServiceMCProcesosException {
		
		
		logger.info("LLEGO A actualizarDatosCliente");
				
		String tipoDoc = consultarClienteModel.getDatosTarjetaCliente().getCliente().getTipoDocumento();
		String numDoc = consultarClienteModel.getDatosTarjetaCliente().getCliente().getNroDocumento();		
		String nombres = consultarClienteModel.getDatosTarjetaCliente().getCliente().getNombres();
		String apellidos = consultarClienteModel.getDatosTarjetaCliente().getCliente().getApCompleto();
		String email = consultarClienteModel.getDatosTarjetaCliente().getTarjeta().getEmail();
		String telefono = consultarClienteModel.getDatosTarjetaCliente().getCliente().getTelefonoCasa();
		String celular = consultarClienteModel.getDatosTarjetaCliente().getCliente().getNumCelular();
		
 	
		
		DTOModificacionClientes response = new DTOModificacionClientes();
		
		try {
			response = fwmcProcesos.actualizarCliente(tipoDoc, numDoc, nombres, apellidos, email, telefono, celular);
			logger.info("cod respuesta"+response.getCodRespuesta());
			logger.info("des respuesta"+response.getDescRespuesta());
			
			//String codRespuesta = "0000";
			String codRespuesta = response.getCodRespuesta();
			
			if (codRespuesta.equals("0000")) { 
				
				
				clienteService.actualizarClienteBD(tipoDoc, numDoc, telefono, email,celular);
				
				consultarClienteModel.setBusquedaRealizada(true);
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_INFO,
						ConstantesGenerales.ACTUALIZA_EXITO,
						ConstantesGenerales.ACTUALIZA_EXITO);
			}else{
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						response.getDescRespuesta(),
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
				UsefulWebApplication
				.actualizarComponente("formConsultarCliente:pgResultado");
			
			}
			
			
		} catch (ExternalServiceMCProcesosException este) {
			consultarClienteModel.setBusquedaRealizada(false);
			UsefulWebApplication
			.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (InternalExcepcion e) {
			consultarClienteModel.setBusquedaRealizada(false);
					e.printStackTrace();
		}
		
		
		
	}

}
