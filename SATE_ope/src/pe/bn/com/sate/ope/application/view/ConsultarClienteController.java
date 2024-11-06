package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarClienteModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceBnTablasException;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.BuscarTarjetaFacade;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.AgenciaService;
import pe.bn.com.sate.ope.infrastructure.service.external.UbigeoService;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.mc.ModificacionClientes;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Ubigeo;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosCliente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaMovimientosExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionClientes;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Controller("consultarClienteController")
@Scope("view")
public class ConsultarClienteController implements Serializable {

	private final static Logger logger = Logger
			.getLogger(ConsultarClienteController.class);

	private static final long serialVersionUID = 1L;

	private ConsultarClienteModel consultarClienteModel;

	private @Autowired
	TarjetaService tarjetaService;

	private @Autowired
	ClienteService clienteService;

	private @Autowired
	AgenciaService agenciaService;

	private @Autowired
	UbigeoService ubigeoService;
	
	private @Autowired 
	FWMCProcesos fwmcProcesos;

//	@Autowired
//	private ConsultarClienteFacade consultarClienteFacade;
	
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

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
		System.out.println("LLEGO A consultarCliente");
		
		System.out.println("tipo doc:"+consultarClienteModel.getTipoBusqueda());
		System.out.println("num doc"+consultarClienteModel.getNumDocumento());
		
		DTOConsultaDatosCliente datosCliente = new  DTOConsultaDatosCliente();
		
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
							
				consultarClienteModel.getDatosTarjetaCliente().getCliente().setTelefonoCasa(StringsUtils.quitarCeroIzquierdaString(datosCliente.getTelCliente().trim()));
			}else{
				System.out.println("else");
				System.out.println("descrip:"+datosCliente.getDescRespuesta().replace(".", ","));
				consultarClienteModel.setBusquedaRealizada(false);
				UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						datosCliente.getDescRespuesta(),
						datosCliente.getDescRespuesta());
				UsefulWebApplication
				.actualizarComponente("formConsultarCliente:pgResultado");
			
			}
			System.out.println("---------");
			
		} catch (ExternalServiceMCProcesosException este) {
			System.out.println("ExternalServiceMCProcesosException");
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
					// TODO Auto-generated catch block
			System.out.println("InternalExcepcion");
					e.printStackTrace();
		}
		
		
		
	}
	
	public void actualizarDatosCliente() throws ExternalServiceMCProcesosException {
		
		
		System.out.println("LLEGO A actualizarDatosCliente");
		
//		consultarClienteModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
//				consultarClienteModel.getTipoBusqueda(), consultarClienteModel.getNumDocumento(), "B"));
				
		String tipoDoc = consultarClienteModel.getDatosTarjetaCliente().getCliente().getTipoDocumento();
		String numDoc = consultarClienteModel.getDatosTarjetaCliente().getCliente().getNroDocumento();		
		String nombres = consultarClienteModel.getDatosTarjetaCliente().getCliente().getNombres();
		String apellidos = consultarClienteModel.getDatosTarjetaCliente().getCliente().getApCompleto();
		String email = consultarClienteModel.getDatosTarjetaCliente().getTarjeta().getEmail();
		String telefono = consultarClienteModel.getDatosTarjetaCliente().getCliente().getTelefonoCasa();
		
		
		Tarjeta datos = new Tarjeta();
		
		tarjetaService.buscarPrimeraTarjetaCliente(tipoDoc, numDoc);
		
		String celular = datos.getNumeroCelular();		
		
		DTOModificacionClientes response = new DTOModificacionClientes();
		
		try {
			response = fwmcProcesos.actualizarCliente(tipoDoc, numDoc, nombres, apellidos, email, telefono, celular);
			System.out.println("cod respuesta"+response.getCodRespuesta());
			System.out.println("des respuesta"+response.getDescRespuesta());
			
			//String codRespuesta = "0000";
			String codRespuesta = response.getCodRespuesta();
			
			if (codRespuesta.equals("0000")) { 
				
				//aqui va tu codigo miguel
				
				clienteService.actualizarClienteBD(tipoDoc, numDoc, telefono, email);
				
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
						//ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
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
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
		
		
		
	}

}
