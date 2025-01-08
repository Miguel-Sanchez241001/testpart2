package pe.bn.com.sate.ope.infrastructure.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pe.bn.com.sate.ope.application.view.ConsultarMovimientosController;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosExpediente;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Component
public class BuscarTarjetaFacade {

	@Autowired
	private TarjetaService tarjetaService;

	@Autowired
	private FWMCProcesos fWMCProcesos;
	private final static Logger logger = Logger.getLogger(BuscarTarjetaFacade.class);
	public void actualizarSaldoTarjeta(
			String numTarjeta,
			String numCuenta, 
			String tipoMoneda, 
			Date fechaExpiracion)
			throws ServiceException, ParseException {
		//devolver como tarjeta		
		DTOConsultaDatosExpediente consDatoExp = new DTOConsultaDatosExpediente();
		//MGL 
		try {
			consDatoExp = fWMCProcesos.consultaDatosPorExpediente(
					
					numCuenta, 
					tipoMoneda, 
					fechaExpiracion);
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			
			
			Tarjeta tarjeta = new Tarjeta();
			tarjeta.setNumTarjeta(numTarjeta);//numTarjeta		
									
			/**/
			if(!consDatoExp.getSaldoFechaApertura().trim().isEmpty()){
				String anio = consDatoExp.getSaldoFechaApertura().substring(0,4);
				String mes = consDatoExp.getSaldoFechaApertura().substring(4,6);
				String dia = consDatoExp.getSaldoFechaApertura().substring(6,8);
				Date fechaApe = new Date();
				try{			    	   
					fechaApe = formato.parse(dia+"/"+mes+"/"+anio);			    	  
				}catch(ParseException e){   
			           e.printStackTrace();
			           // log the exception
			           throw new RuntimeException(e);
			    }				
				tarjeta.setFechaInicioLinea(fechaApe);//saldoTarjeta.getFechaApertura()				
			}	
			
			
			if(!consDatoExp.getSaldoFechaExpiracion().trim().isEmpty()){
				  String fchExpanio =  consDatoExp.getSaldoFechaExpiracion().substring(0,2);			     
			      String fchExpdia =  "01";
			      String fchExpmes  =  consDatoExp.getSaldoFechaExpiracion().substring(2,4);
			      Date fechaExp = new Date();
					try{				    	   
						fechaExp = formato.parse(fchExpdia+"/"+fchExpmes+"/20"+fchExpanio);			    	  
					}catch(ParseException e){   
				           e.printStackTrace();
				           // log the exception
				           throw new RuntimeException(e);
				    }				
				tarjeta.setFechaTerminoLinea(fechaExp);//saldoTarjeta.getFechaExpiracion()
					
			}
					
			tarjeta.setMontoLineaAsignado(Double.parseDouble(consDatoExp.getSaldoLineaCredito()));//saldoTarjeta.getLineaCredito()
			tarjeta.setMontoLineaActual(Double.parseDouble(consDatoExp.getSaldoActual()));//saldoTarjeta.getDispActual()
			tarjeta.setMontoCompraUsado(Double.parseDouble(consDatoExp.getSaldoPagoTotal()));//saldoTarjeta.getPagoTotal()
			
			//tarjeta.setMontoPorProcesar(Double.parseDouble(consDatoExp.getSaldoImporteMora()));//saldoTarjeta.getImporteMora()
			tarjeta.setMontoPorProcesar(Double.parseDouble(consDatoExp.getSaldoMontoProcesar()));

			
			tarjeta.setDispEfectivoUsado(
					Double.parseDouble(consDatoExp.getSaldoEfectivo())	- Double.parseDouble(consDatoExp.getSaldoDispEfectivo()));
			//saldoTarjeta.getEfectivo()	- saldoTarjeta.getDispEfectivo()
			
			tarjetaService.actualizarSaldos(tarjeta);
			
		} catch (InternalExcepcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Tarjeta obtenerDatosCredito(
			String numTarjeta,
			String numCuenta, 
			String tipoMoneda, 
			Date fechaExpiracion)
			throws ServiceException, ParseException {
		
		DTOConsultaDatosExpediente consDatoExp = new DTOConsultaDatosExpediente();
		Tarjeta tarjeta = new Tarjeta();
		
		try {
			consDatoExp = fWMCProcesos.consultaDatosPorExpediente(
					
					numCuenta, 
					tipoMoneda, 
					fechaExpiracion);
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			
			if (consDatoExp.getCodRespuesta().equals("0000")) { 		
			
				tarjeta.setNumTarjeta(numTarjeta);//numTarjeta		
										
				/**/
				if(!consDatoExp.getSaldoFechaApertura().trim().isEmpty()){
					String anio = consDatoExp.getSaldoFechaApertura().substring(0,4);
					String mes = consDatoExp.getSaldoFechaApertura().substring(4,6);
					String dia = consDatoExp.getSaldoFechaApertura().substring(6,8);
					Date fechaApe = new Date();
					try{			    	   
						fechaApe = formato.parse(dia+"/"+mes+"/"+anio);			    	  
					}catch(ParseException e){   
				           e.printStackTrace();
				           // log the exception
				           throw new RuntimeException(e);
				    }				
					tarjeta.setFechaInicioLinea(fechaApe);//saldoTarjeta.getFechaApertura()				
				}	
				
				
				if(!consDatoExp.getSaldoFechaExpiracion().trim().isEmpty()){
					  String fchExpanio =  consDatoExp.getSaldoFechaExpiracion().substring(0,2);			     
				      String fchExpdia =  "01";
				      String fchExpmes  =  consDatoExp.getSaldoFechaExpiracion().substring(2,4);
				      Date fechaExp = new Date();
						try{				    	   
							fechaExp = formato.parse(fchExpdia+"/"+fchExpmes+"/20"+fchExpanio);			    	  
						}catch(ParseException e){   
					           e.printStackTrace();
					           // log the exception
					           throw new RuntimeException(e);
					    }				
					tarjeta.setFechaTerminoLinea(fechaExp);//saldoTarjeta.getFechaExpiracion()
						
				}
						
				tarjeta.setMontoLineaAsignado(Double.parseDouble(consDatoExp.getSaldoLineaCredito()));//saldoTarjeta.getLineaCredito()				
				tarjeta.setMontoLineaActual(Double.parseDouble(consDatoExp.getSaldoDispEfectivo()));//saldoTarjeta.getDispActual()=> consDatoExp.getSaldoActual()
				tarjeta.setMontoCompraUsado(Double.parseDouble(consDatoExp.getSaldoPagoTotal()));//saldoTarjeta.getPagoTotal()
				
				//tarjeta.setMontoPorProcesar(Double.parseDouble(consDatoExp.getSaldoImporteMora()));//saldoTarjeta.getImporteMora()
				tarjeta.setMontoPorProcesar(Double.parseDouble(consDatoExp.getSaldoMontoProcesar()));
	
				
				tarjeta.setDispEfectivoUsado(
						Double.parseDouble(consDatoExp.getSaldoEfectivo())	- Double.parseDouble(consDatoExp.getSaldoDispEfectivo()));
				//saldoTarjeta.getEfectivo()	- saldoTarjeta.getDispEfectivo()
				
				logger.info("****************************");
				logger.info("fechaInicioLinea:"+tarjeta.getFechaInicioLinea());
				logger.info("fechaTerminoLinea:"+tarjeta.getFechaTerminoLinea());
				logger.info("montoLineaAsignado:"+tarjeta.getMontoLineaAsignado());
				logger.info("montoLineaActual:"+tarjeta.getMontoLineaActual());
				logger.info("montoCompraUsado:"+tarjeta.getMontoCompraUsado());
				logger.info("montoPorProcesar:"+tarjeta.getMontoPorProcesar());
				logger.info("dispEfectivoUsado:"+tarjeta.getDispEfectivoUsado());
				logger.info("++++++++++++++++++++++++++++");	
			
			}else{
        		UsefulWebApplication
				.mostrarMensajeJSF(
						ConstantesGenerales.SEVERITY_ERROR,
						consDatoExp.getDescRespuesta(),
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
		
        	}
			
		} catch (InternalExcepcion e) {
			logger.error(e.getMessage());
 			e.printStackTrace();
		}
		
		
		
		return tarjeta;
	}
	
 
	
	
	
}
