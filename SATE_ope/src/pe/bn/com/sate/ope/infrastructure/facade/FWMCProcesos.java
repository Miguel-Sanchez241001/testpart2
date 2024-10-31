package pe.bn.com.sate.ope.infrastructure.facade;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.mc.BasicHttpsBinding_IService1Proxy;
import pe.bn.com.sate.ope.persistence.mapper.internal.ParametroMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.TarjetaMapper;
import pe.bn.com.sate.ope.transversal.dto.sate.ModificacionTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.sate.SaldoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.ws.ConsultaMovimientos;
import pe.bn.com.sate.ope.transversal.dto.ws.ConsultaSaldos;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosCliente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosTarjeta;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaMovimientosExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionClientes;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOModificacionTarjeta;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOwservice;
import pe.bn.com.sate.ope.transversal.util.Fecha;
import pe.bn.com.sate.ope.transversal.util.NumeroALetras;
import pe.bn.com.sate.ope.transversal.util.ServicioWebUtil;
import pe.bn.com.sate.ope.transversal.util.SoapClientUtil;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesWS;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Component
public class FWMCProcesos {

	private @Autowired
	ParametroMapper parametroMapper;

	private @Autowired
	TarjetaMapper tarjetaMapper;

	private @Autowired
	Parametros parametros;
	private final Logger logger = Logger.getLogger(FWMCProcesos.class);

	// ANTIGUO
	public List<MovimientoTarjeta> consultarMovimientosPorTarjeta(
			String numTarjeta) throws ServiceException {
		// TODO CAMBIO de if para que envie para pruebas y no verifique
		if (tarjetaMapper.buscarTarjetaPorNumeroTarjeta(numTarjeta,
				UsefulWebApplication.obtenerUsuario().getRuc()) != null) {

			// if (true) {
			// TODO DATOS PARA SERVICIO WS IZIPAY
			String codigoEmisor = parametros.getCodigoEmisorMc();
			String codigoUsuario = parametros.getCodigoUsuarioMc();
			String numTerminal = parametros.getNumTerminalMc();
			String NumReferencia = parametros.getPrefijoNumReferenciaMc()
					+ parametroMapper.obtenerNumeroReferenciaMovimientos();

			String wSUsuario = parametros.getWsUsuarioMc();
			String WSClave = parametros.getWsClaveMc();

			String request = "";
			String response = "";

			List<MovimientoTarjeta> movimientosTarjeta;

			request = "<![CDATA[" + "<Consulta_Movimientos>" + "<CodEmisor>"
					+ codigoEmisor + "</CodEmisor>" + "<CodUsuario>"
					+ codigoUsuario + "</CodUsuario>" + "<NumTerminal>"
					+ numTerminal + "</NumTerminal>" + "<NumReferencia>"
					+ NumReferencia + "</NumReferencia>"
					+ "<NumTarjetaMonedero>" + numTarjeta
					+ "</NumTarjetaMonedero>"
					+ "<FechaExpiracion>0722</FechaExpiracion>"
					+ "<Comercio>4058950</Comercio>" + "<Moneda>604</Moneda>"
					+ "<FechaTxnTerminal>20160621</FechaTxnTerminal>"
					+ "<HoraTxnTerminal>111901</HoraTxnTerminal>"
					+ "<WSUsuario>" + wSUsuario + "</WSUsuario>" + "<WSClave>"
					+ WSClave + "</WSClave>" + "<Reservado></Reservado>"
					+ "</Consulta_Movimientos>" + "]]>";

			logger.info(request);
			try {
				BasicHttpsBinding_IService1Proxy basicHttpBinding_IService1Proxy = new BasicHttpsBinding_IService1Proxy();
				ServicioWebUtil.cambiarTiempoEspera(parametros
						.getConexionTiempo(), parametros.getRespuestaTiempo(),
						(BindingProvider) basicHttpBinding_IService1Proxy
								._getDescriptor().getProxy());

				response = basicHttpBinding_IService1Proxy
						.consultaMovimientos(request);

				logger.info(response.indexOf("<Consulta_Movimientos>"));
				logger.info(response.indexOf("</Consulta_Movimientos>"));
				logger.info(response.substring(
						response.indexOf("<Consulta_Movimientos>"),
						response.indexOf("</soap:Body>")));
				StringReader reader = new StringReader(response.substring(
						response.indexOf("<Consulta_Movimientos>"),
						response.indexOf("</soap:Body>")));
				/*
				 * JAXBContext jc = JAXBContext
				 * .newInstance(ConsultaMovimientos.class); Unmarshaller
				 * unmarshaller = jc.createUnmarshaller();
				 * 
				 * ConsultaMovimientos consultaMovimientos =
				 * (ConsultaMovimientos) unmarshaller .unmarshal(reader);
				 */

				ConsultaMovimientos consultaMovimientos = SoapClientUtil
						.convertirXMLAObjeto(reader, ConsultaMovimientos.class);
				logger.info(consultaMovimientos.toString());

				if (consultaMovimientos.getCodRespuesta().equals("0000")) {
					movimientosTarjeta = consultaMovimientos
							.obtenerMovimientosTarjeta();
					return movimientosTarjeta;
				} else {
					throw new ExternalServiceMCProcesosException(
							consultaMovimientos.getDescRespuesta());
				}
			} catch (Exception ex) {
				throw new ExternalServiceMCProcesosException(
						ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
						ex);
			}
		} else {
			throw new InternalServiceException(
					"Número de tarjeta no encontrada");
		}

	}

	public ModificacionTarjeta modificarTarjeta(String numTarjetaMonedero,
			String codigoBloqueo, String motivoBloqueo) throws JAXBException {

		try {
			String monedaProducto = "1";

			numTarjetaMonedero = "52634178"; // mockeado
			// codigoBloqueo = "Z"; //mockeado
			// motivoBloqueo = "Bloqueo Z"; //mockeado

			String fechaTxnTerminal = "20160301";
			String horaTxnTerminal = "142021";

			String numTerminal = "12345678";// colocar el parametros comp
			String NumReferencia = "ORD000123456789";// colocar el parametros
														// comp
			String wSUsuario = "0944006748";// colocar el parametros comp
			String WSClave = "dRUch4hupAvuduBE";// colocar el parametros comp

			String request = "<![CDATA[" + "<Modificacion_Tarjeta>"
					+ "<CodEmisor>941</CodEmisor>" + // Obligatorio
					"<CodUsuario>CS00000001</CodUsuario>" + // Obligatorio
					"<NumTerminal>"
					+ numTerminal
					+ "</NumTerminal>"
					+ // Obligatorio
					"<NumReferencia>"
					+ NumReferencia
					+ "</NumReferencia>"
					+ // Obligatorio
					"<MonedaProducto>"
					+ monedaProducto
					+ "</MonedaProducto>"
					+ // Obligatorio
					"<CodigoProducto></CodigoProducto>"
					+ "<NumCuentaAsociada></NumCuentaAsociada>"
					+ "<IndAutGenCodCliente></IndAutGenCodCliente>"
					+ "<NumTarjetaMonedero>"
					+ numTarjetaMonedero
					+ "</NumTarjetaMonedero>"
					+ "<TipoTarjeta></TipoTarjeta>"
					+ "<SecuenciaTarjeta></SecuenciaTarjeta>"
					+ "<TipoEmisionTarjeta></TipoEmisionTarjeta>"
					+ "<NombrePlasticoLinea1></NombrePlasticoLinea1>"
					+ "<NombrePlasticoLinea2></NombrePlasticoLinea2>"
					+ "<CodigoBloqueo>"
					+ codigoBloqueo
					+ "</CodigoBloqueo>"
					+ "<MotivoBloqueo>"
					+ motivoBloqueo
					+ "</MotivoBloqueo>"
					+ "<DireccionEnvioTipoVia></DireccionEnvioTipoVia>"
					+ "<DireccionEnvioNombreVia></DireccionEnvioNombreVia>"
					+ "<DireccionEnvioNum></DireccionEnvioNum>"
					+ "<DireccionEnvioNumDpto></DireccionEnvioNumDpto>"
					+ "<DireccionEnvioOficina></DireccionEnvioOficina>"
					+ "<DireccionEnvioPiso></DireccionEnvioPiso>"
					+ "<DireccionEnvioManzana></DireccionEnvioManzana>"
					+ "<DireccionEnvioLote></DireccionEnvioLote>"
					+ "<DireccionEnvioNumInterior></DireccionEnvioNumInterior>"
					+ "<DireccionEnvioSector></DireccionEnvioSector>"
					+ "<DireccionEnvioKilometro></DireccionEnvioKilometro>"
					+ "<DireccionEnvioTipoZona></DireccionEnvioTipoZona>"
					+ "<DireccionEnvioNombreZona></DireccionEnvioNombreZona>"
					+ "<DireccionEnvioReferencia></DireccionEnvioReferencia>"
					+ "<DireccionEnvioUbigeo></DireccionEnvioUbigeo>"
					+ "<IndTipoDireccion></IndTipoDireccion>"
					+ "<SucursalOriginal></SucursalOriginal>"
					+ "<Mandatorio1></Mandatorio1>"
					+ "<Mandatorio2></Mandatorio2>"
					+ "<SucursalActual></SucursalActual>"
					+ "<CodigoPromocion></CodigoPromocion>"
					+ "<FechaTxnTerminal>"
					+ fechaTxnTerminal
					+ "</FechaTxnTerminal>"
					+ "<HoraTxnTerminal>"
					+ horaTxnTerminal
					+ "</HoraTxnTerminal>"
					+ "<WSUsuario>"
					+ wSUsuario
					+ "</WSUsuario>"
					+ "<WSClave>"
					+ WSClave
					+ "</WSClave>"
					+ "<Reservado></Reservado>"
					+ "</Modificacion_Tarjeta>]]>";
			logger.info("request: " + request);
			String response = "";

			BasicHttpsBinding_IService1Proxy basicHttpBinding_IService1Proxy = new BasicHttpsBinding_IService1Proxy();
			response = basicHttpBinding_IService1Proxy
					.modificacionTarjetas(request);

			logger.info(response.indexOf("<Modificacion_Tarjeta>"));
			logger.info(response.indexOf("</Modificacion_Tarjeta>"));
			logger.info(response.substring(
					response.indexOf("<Modificacion_Tarjeta>"),
					response.indexOf("</soap:Body>")));

			StringReader reader = new StringReader(response.substring(
					response.indexOf("<Modificacion_Tarjeta>"),
					response.indexOf("</soap:Body>")));
			ModificacionTarjeta modificacionTarjeta = SoapClientUtil
					.convertirXMLAObjeto(reader, ModificacionTarjeta.class);

			return modificacionTarjeta;
		} catch (Exception ex) {
			throw new ExternalServiceMCProcesosException(ex.getMessage(), ex);
		}
	}

	public SaldoTarjeta consultarSaldosPorTarjeta(String numTarjeta) {

		if (tarjetaMapper.buscarTarjetaPorNumeroTarjeta(numTarjeta,
				UsefulWebApplication.obtenerUsuario().getRuc()) != null) {

			String codigoEmisor = parametros.getCodigoEmisorMc();
			String codigoUsuario = parametros.getCodigoUsuarioMc();
			String numTerminal = parametros.getNumTerminalMc();
			String NumReferencia = parametros.getPrefijoNumReferenciaMc()
					+ parametroMapper.obtenerNumeroReferenciaSaldos();
			String wSUsuario = parametros.getWsUsuarioMc();
			String WSClave = parametros.getWsClaveMc();

			String request = "";
			String response = "";

			request = "<![CDATA[" + "<Consulta_Saldos>" + "<CodEmisor>"
					+ codigoEmisor + "</CodEmisor>" + "<CodUsuario>"
					+ codigoUsuario + "</CodUsuario>" + "<NumTerminal>"
					+ numTerminal + "</NumTerminal>" + "<NumReferencia>"
					+ NumReferencia + "</NumReferencia>"
					+ "<NumTarjetaMonedero>" + numTarjeta
					+ "</NumTarjetaMonedero>"
					+ "<FechaExpiracion>0921</FechaExpiracion>"
					+ "<Comercio>4058950</Comercio>" + "<Moneda>604</Moneda>"
					+ "<FechaTxnTerminal>20181106</FechaTxnTerminal>"
					+ "<HoraTxnTerminal>120000</HoraTxnTerminal>"
					+ "<WSUsuario>" + wSUsuario + "</WSUsuario>" + "<WSClave>"
					+ WSClave + "</WSClave>" + "<Reservado></Reservado>"
					+ "</Consulta_Saldos>" + "]]>";

			logger.info(request);
			try {
				BasicHttpsBinding_IService1Proxy basicHttpBinding_IService1Proxy = new BasicHttpsBinding_IService1Proxy();
				response = basicHttpBinding_IService1Proxy
						.consultaSaldos(request);

				logger.info(response);
				logger.info(response.indexOf("<Consulta_Saldos>"));
				logger.info(response.indexOf("</Consulta_Saldos>"));
				logger.info(response.substring(
						response.indexOf("<Consulta_Saldos>"),
						response.indexOf("</soap:Body>")));

				StringReader reader = new StringReader(response.substring(
						response.indexOf("<Consulta_Saldos>"),
						response.indexOf("</soap:Body>")));
				ConsultaSaldos consultaSaldos = SoapClientUtil
						.convertirXMLAObjeto(reader, ConsultaSaldos.class);

				logger.info(consultaSaldos.toString());

				if (consultaSaldos.getCodRespuesta().equals("0000")) {
					return new SaldoTarjeta(consultaSaldos);
				} else {
					throw new ExternalServiceMCProcesosException(
							consultaSaldos.getDescRespuesta());
				}

			} catch (Exception ex) {
				throw new ExternalServiceMCProcesosException(ex.getMessage(),
						ex);
			}
		} else {
			throw new ExternalServiceMCProcesosException(
					"NÃºmero de tarjeta no encontrada");
		}
	}

	// WS IZIPAY METODOS 2024
	
	//Consulta de Datos de Tarjeta
	public DTOConsultaDatosTarjeta informacionDeTarjeta() throws InternalExcepcion,ExternalServiceMCProcesosException {

		String wsdlUrl = parametros.getWsSoapMc();
	 

		Map<String, String> inputRequest = ConstantesWS
				.getConsultaDatosTarjetaMap();
		inputRequest.put(ConstantesWS.COD_EMISOR, "191");				//971
		inputRequest.put(ConstantesWS.COD_USUARIO, "TT9999");			//TW9999
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "11010101");		//11010101
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "AC2024000119");	//AC2020000322
		inputRequest.put(ConstantesWS.NUM_TARJETA, "5309270100000021");		//000000009
		inputRequest.put(ConstantesWS.FECHA_EXPIRACION, "2601");		//2701
		inputRequest.put(ConstantesWS.COMERCIO, "4058950");				//9999999
		inputRequest.put(ConstantesWS.MONEDA, "604");
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20210831");	//20160224
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "111901");		//172020
		inputRequest.put(ConstantesWS.WS_USUARIO, "4858643428");		//prueba1234
		inputRequest.put(ConstantesWS.WS_CLAVE, "aza877azutht98b8");	//prueba1234567890
		inputRequest.put(ConstantesWS.RESERVADO, "");
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.Consulta_Datos_Tarjeta_XML, inputRequest);
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_Consulta_Datos_Tarjeta);

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;
		DTOConsultaDatosTarjeta responseDTO = null;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						DTOConsultaDatosTarjeta.class);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;

	}

	//Modificación de Tarjeta ok
	public DTOModificacionTarjeta modificacionTarjeta(
			String tipoMoneda, 
			String numTarjeta,
			String codMotivo,
			String desMotivo
			) throws InternalExcepcion {
		//MGL
		/*obtener valores*/
		String wsdlUrl = parametros.getWsSoapMc();		
		String wsdlAS = parametros.getPrefijoNumReferenciaMc();
		
		String codEmisor = parametros.getCodigoEmisorMc();	
		String codUsuario = parametros.getCodigoUsuarioMc();	
		String numTerminal = parametros.getNumTerminalMc();	
		String comercio = parametros.getWsComercioMc();	
		String numReferenciaWS = wsdlAS+NumeroALetras.llenarCerosAlaIzquierda(Long.toString(parametroMapper.obtenerNumeroReferenciaWS()),10);
		
		String usuario = parametros.getWsUsuarioMc();
		String clave = parametros.getWsClaveMc();		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String fechaTerminal = sdf.format(new Date());
	    System.out.println("fechaTerminal:"+fechaTerminal);
	        
	    DateFormat dateFormat = new SimpleDateFormat("HHmmss");
	    String horaTerminal = dateFormat.format(new Date());	       
	    System.out.println("horaTerminal:"+horaTerminal);	
		
	    DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_BLOQUEO_TARJETA);
		Class<DTOModificacionTarjeta> dtoClass = DTOModificacionTarjeta.class;
		DTOModificacionTarjeta responseDTO = null;
	    
		Map<String, String> inputRequest = ConstantesWS
				.getModificacionTarjetaMap();	
		
		String valor = "0005309270300003841";
		
		String numeroTarjeta = StringsUtils.quitarCeroIzquierdaString(valor);
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, codEmisor);
		inputRequest.put(ConstantesWS.COD_USUARIO, codUsuario);
		inputRequest.put(ConstantesWS.NUM_TERMINAL, numTerminal);
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, numReferenciaWS);
		
		inputRequest.put(ConstantesWS.MONEDA_PRODUCTO, ConstantesWS.TIPO_MONEDA);
		inputRequest.put(ConstantesWS.NUM_TARJETA_MONEDERO, numeroTarjeta);
		inputRequest.put(ConstantesWS.CODIGO_BLOQUEO, codMotivo);
		inputRequest.put(ConstantesWS.MOTIVO_BLOQUEO, desMotivo);		
		
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, fechaTerminal);
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, horaTerminal);
		inputRequest.put(ConstantesWS.WS_USUARIO, usuario);
		inputRequest.put(ConstantesWS.WS_CLAVE, clave);
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.MODIFICACION_TARJETA_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());
				
				DTOwservice dto2 = new DTOwservice(ConstantesWS.SOACTION_MODIFICACION_TARJETA);

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto2.getStartTag()),//<Modificacion_Tarjeta>
						resultado.indexOf(dto2.getEndTag()) //</Modificacion_Tarjeta>
								+ dto2.getEndTag().length());
				
				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}
	
	//Consulta de Movimiento por Expediente
	public DTOConsultaMovimientosExpediente consultaMovimientoPorExpediente(
			String numCuenta,
			String tipoMoneda, 
			Date fechaExpiracion) throws InternalExcepcion {
				
		String wsdlUrl = parametros.getWsSoapMc();		
		String wsdlAS = parametros.getPrefijoNumReferenciaMc();
		
		String codEmisor = parametros.getCodigoEmisorMc();	
		String codUsuario = parametros.getCodigoUsuarioMc();	
		String numTerminal = parametros.getNumTerminalMc();	
		String comercio = parametros.getWsComercioMc();	
		String numReferenciaWS = wsdlAS+NumeroALetras.llenarCerosAlaIzquierda(Long.toString(parametroMapper.obtenerNumeroReferenciaWS()),10);
		
		String usuario = parametros.getWsUsuarioMc();
		String clave = parametros.getWsClaveMc();	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String fechaTerminal = sdf.format(new Date());
	    System.out.println("fechaTerminal:"+fechaTerminal);
	        
	    DateFormat dateFormat = new SimpleDateFormat("HHmmss");
	    String horaTerminal = dateFormat.format(new Date());	       
	    System.out.println("horaTerminal:"+horaTerminal);	
		
	    /*fecha de expiacion*/
	    SimpleDateFormat sdfanio = new SimpleDateFormat("yyyy");
	    String fchExpanio =  sdfanio.format(fechaExpiracion).substring(2,4);
	    
	    SimpleDateFormat sdfmes = new SimpleDateFormat("MM");
	    String fchExpmes = sdfmes.format(fechaExpiracion);
	    
	    String fchExp = fchExpanio + fchExpmes;	    
	    String numeroCuenta = StringsUtils.quitarCeroIzquierdaString(numCuenta);
		  	    
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_CONSULTA_MOVIMIENTOS_EXPEDIENTE);
		Class<DTOConsultaMovimientosExpediente> dtoClass = DTOConsultaMovimientosExpediente.class;
		DTOConsultaMovimientosExpediente responseDTO = null;

		Map<String, String> consultaMovimientosExpedienteMap = ConstantesWS
				.getConsultaMovimientosExpedienteMap();
				
		consultaMovimientosExpedienteMap.put(ConstantesWS.COD_EMISOR, codEmisor);
		consultaMovimientosExpedienteMap.put(ConstantesWS.COD_USUARIO, codUsuario);
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_TERMINAL, numTerminal);
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_REFERENCIA, numReferenciaWS);		
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_CUENTA, numeroCuenta);		
		consultaMovimientosExpedienteMap.put(ConstantesWS.FECHA_EXPIRACION, fchExp);
		consultaMovimientosExpedienteMap.put(ConstantesWS.COMERCIO, comercio);
		consultaMovimientosExpedienteMap.put(ConstantesWS.MONEDA, tipoMoneda);
		consultaMovimientosExpedienteMap.put(ConstantesWS.FECHA_TXN_TERMINAL, fechaTerminal);
		consultaMovimientosExpedienteMap.put(ConstantesWS.HORA_TXN_TERMINAL, horaTerminal);
		consultaMovimientosExpedienteMap.put(ConstantesWS.WS_USUARIO, usuario);
		consultaMovimientosExpedienteMap.put(ConstantesWS.WS_CLAVE, clave);
		consultaMovimientosExpedienteMap.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.CONSULTA_MOVIMIENTOS_EXPEDIENTE_XML, consultaMovimientosExpedienteMap);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
				
		return responseDTO;
	}
	
	public List<MovimientoTarjetaExpediente> listaMovTarjExp (DTOConsultaMovimientosExpediente dato, String tipoTarjeta){
		
		List<MovimientoTarjetaExpediente> movimientoTarjetas = new ArrayList<MovimientoTarjetaExpediente>();
		int c = dato.getMoviUltMovimientos() == null || dato.getMoviUltMovimientos().isEmpty() ? 0
				: Integer.parseInt(dato.getMoviUltMovimientos().trim());
		
		if (c > 0) {
			MovimientoTarjetaExpediente movimientoTarjeta1 = new MovimientoTarjetaExpediente();
			
			
			movimientoTarjeta1.setId("1".trim());
			movimientoTarjeta1.setFechaTxn(Fecha.transformarADateMC(dato.getMov1FechaTxn().trim()));
			movimientoTarjeta1.setDescripcionTxn(dato.getMov1DesTxn().trim());
			movimientoTarjeta1.setMonOriginalTxn(dato.getMov1MonOriginalTxn().trim());
			movimientoTarjeta1.setMontoTxn(dato.getMov1MontoTxn().trim());
			movimientoTarjeta1.setSigMontoTxn(dato.getMov1SigMontoTxn().trim());			
			movimientoTarjeta1.setOperacionTxn(dato.getMov1Operacion().trim());
			movimientoTarjeta1.setCodAutTxn(dato.getMov1CodAutorizacion().trim());
			movimientoTarjeta1.setNumTarjetaTxn(dato.getMov1NumTarjeta().trim());
			movimientoTarjeta1.setTipoTarjeta(tipoTarjeta);
			
			movimientoTarjetas.add(movimientoTarjeta1);
		}
		if (c > 1) {
			MovimientoTarjetaExpediente movimientoTarjeta2 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta2.setId("2".trim());
			movimientoTarjeta2.setFechaTxn(Fecha.transformarADateMC(dato.getMov2FechaTxn().trim()));
			movimientoTarjeta2.setDescripcionTxn(dato.getMov2DesTxn().trim());
			movimientoTarjeta2.setMonOriginalTxn(dato.getMov2MonOriginalTxn().trim());
			movimientoTarjeta2.setMontoTxn(dato.getMov2MontoTxn().trim());
			movimientoTarjeta2.setSigMontoTxn(dato.getMov2SigMontoTxn().trim());			
			movimientoTarjeta2.setOperacionTxn(dato.getMov2Operacion().trim());
			movimientoTarjeta2.setCodAutTxn(dato.getMov2CodAutorizacion().trim());
			movimientoTarjeta2.setNumTarjetaTxn(dato.getMov2NumTarjeta().trim());
			movimientoTarjeta2.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta2);
		}
		
		if (c > 2) {
			MovimientoTarjetaExpediente movimientoTarjeta3 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta3.setId("3".trim());
			movimientoTarjeta3.setFechaTxn(Fecha.transformarADateMC(dato.getMov3FechaTxn().trim()));
			movimientoTarjeta3.setDescripcionTxn(dato.getMov3DesTxn().trim());
			movimientoTarjeta3.setMonOriginalTxn(dato.getMov3MonOriginalTxn().trim());
			movimientoTarjeta3.setMontoTxn(dato.getMov3MontoTxn().trim());
			movimientoTarjeta3.setSigMontoTxn(dato.getMov3SigMontoTxn().trim());			
			movimientoTarjeta3.setOperacionTxn(dato.getMov3Operacion().trim());
			movimientoTarjeta3.setCodAutTxn(dato.getMov3CodAutorizacion().trim());
			movimientoTarjeta3.setNumTarjetaTxn(dato.getMov3NumTarjeta().trim());
			movimientoTarjeta3.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta3);
		}

		if (c > 3) {
			MovimientoTarjetaExpediente movimientoTarjeta4 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta4.setId("4".trim());
			movimientoTarjeta4.setFechaTxn(Fecha.transformarADateMC(dato.getMov4FechaTxn().trim()));
			movimientoTarjeta4.setDescripcionTxn(dato.getMov4DesTxn().trim());
			movimientoTarjeta4.setMonOriginalTxn(dato.getMov4MonOriginalTxn().trim());
			movimientoTarjeta4.setMontoTxn(dato.getMov4MontoTxn().trim());
			movimientoTarjeta4.setSigMontoTxn(dato.getMov4SigMontoTxn().trim());			
			movimientoTarjeta4.setOperacionTxn(dato.getMov4Operacion().trim());
			movimientoTarjeta4.setCodAutTxn(dato.getMov4CodAutorizacion().trim());
			movimientoTarjeta4.setNumTarjetaTxn(dato.getMov4NumTarjeta().trim());
			movimientoTarjeta4.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta4);
		}

		if (c > 4) {
			MovimientoTarjetaExpediente movimientoTarjeta5 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta5.setId("5".trim());
			movimientoTarjeta5.setFechaTxn(Fecha.transformarADateMC(dato.getMov5FechaTxn().trim()));
			movimientoTarjeta5.setDescripcionTxn(dato.getMov5DesTxn().trim());
			movimientoTarjeta5.setMonOriginalTxn(dato.getMov5MonOriginalTxn().trim());
			movimientoTarjeta5.setMontoTxn(dato.getMov5MontoTxn().trim());
			movimientoTarjeta5.setSigMontoTxn(dato.getMov5SigMontoTxn().trim());			
			movimientoTarjeta5.setOperacionTxn(dato.getMov5Operacion().trim());
			movimientoTarjeta5.setCodAutTxn(dato.getMov5CodAutorizacion().trim());
			movimientoTarjeta5.setNumTarjetaTxn(dato.getMov5NumTarjeta().trim());
			movimientoTarjeta5.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta5);
		}

		if (c > 5) {
			MovimientoTarjetaExpediente movimientoTarjeta6 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta6.setId("6".trim());
			movimientoTarjeta6.setFechaTxn(Fecha.transformarADateMC(dato.getMov6FechaTxn().trim()));
			movimientoTarjeta6.setDescripcionTxn(dato.getMov6DesTxn().trim());
			movimientoTarjeta6.setMonOriginalTxn(dato.getMov6MonOriginalTxn().trim());
			movimientoTarjeta6.setMontoTxn(dato.getMov6MontoTxn().trim());
			movimientoTarjeta6.setSigMontoTxn(dato.getMov6SigMontoTxn().trim());			
			movimientoTarjeta6.setOperacionTxn(dato.getMov6Operacion().trim());
			movimientoTarjeta6.setCodAutTxn(dato.getMov6CodAutorizacion().trim());
			movimientoTarjeta6.setNumTarjetaTxn(dato.getMov6NumTarjeta().trim());
			movimientoTarjeta6.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta6);
		}

		if (c > 6) {
			MovimientoTarjetaExpediente movimientoTarjeta7 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta7.setId("7".trim());
			movimientoTarjeta7.setFechaTxn(Fecha.transformarADateMC(dato.getMov7FechaTxn().trim()));
			movimientoTarjeta7.setDescripcionTxn(dato.getMov7DesTxn().trim());
			movimientoTarjeta7.setMonOriginalTxn(dato.getMov7MonOriginalTxn().trim());
			movimientoTarjeta7.setMontoTxn(dato.getMov7MontoTxn().trim());
			movimientoTarjeta7.setSigMontoTxn(dato.getMov7SigMontoTxn().trim());			
			movimientoTarjeta7.setOperacionTxn(dato.getMov7Operacion().trim());
			movimientoTarjeta7.setCodAutTxn(dato.getMov7CodAutorizacion().trim());
			movimientoTarjeta7.setNumTarjetaTxn(dato.getMov7NumTarjeta().trim());
			movimientoTarjeta7.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta7);
		}

		if (c > 7) {
			MovimientoTarjetaExpediente movimientoTarjeta8 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta8.setId("8".trim());
			movimientoTarjeta8.setFechaTxn(Fecha.transformarADateMC(dato.getMov8FechaTxn().trim()));
			movimientoTarjeta8.setDescripcionTxn(dato.getMov8DesTxn().trim());
			movimientoTarjeta8.setMonOriginalTxn(dato.getMov8MonOriginalTxn().trim());
			movimientoTarjeta8.setMontoTxn(dato.getMov8MontoTxn().trim());
			movimientoTarjeta8.setSigMontoTxn(dato.getMov8SigMontoTxn().trim());			
			movimientoTarjeta8.setOperacionTxn(dato.getMov8Operacion().trim());
			movimientoTarjeta8.setCodAutTxn(dato.getMov8CodAutorizacion().trim());
			movimientoTarjeta8.setNumTarjetaTxn(dato.getMov8NumTarjeta().trim());
			movimientoTarjeta8.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta8);
		}

		if (c > 8) {
			MovimientoTarjetaExpediente movimientoTarjeta9 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta9.setId("9".trim());
			movimientoTarjeta9.setFechaTxn(Fecha.transformarADateMC(dato.getMov9FechaTxn()));
			movimientoTarjeta9.setDescripcionTxn(dato.getMov9DesTxn());
			movimientoTarjeta9.setMonOriginalTxn(dato.getMov9MonOriginalTxn());
			movimientoTarjeta9.setMontoTxn(dato.getMov9MontoTxn().trim());
			movimientoTarjeta9.setSigMontoTxn(dato.getMov9SigMontoTxn());			
			movimientoTarjeta9.setOperacionTxn(dato.getMov9Operacion());
			movimientoTarjeta9.setCodAutTxn((dato.getMov9CodAutorizacion()!=null)?dato.getMov9CodAutorizacion().trim():"");
			movimientoTarjeta9.setNumTarjetaTxn(dato.getMov9NumTarjeta());
			movimientoTarjeta9.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta9);
		}

		if (c > 9) {
			MovimientoTarjetaExpediente movimientoTarjeta10 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta10.setId("10".trim());
			movimientoTarjeta10.setFechaTxn(Fecha.transformarADateMC(dato.getMov10FechaTxn().trim()));
			movimientoTarjeta10.setDescripcionTxn(dato.getMov10DesTxn().trim());
			movimientoTarjeta10.setMonOriginalTxn(dato.getMov10MonOriginalTxn().trim());
			movimientoTarjeta10.setMontoTxn(dato.getMov10MontoTxn().trim());
			movimientoTarjeta10.setSigMontoTxn(dato.getMov10SigMontoTxn().trim());			
			movimientoTarjeta10.setOperacionTxn(dato.getMov10Operacion().trim());
			movimientoTarjeta10.setCodAutTxn(dato.getMov10CodAutorizacion().trim());
			movimientoTarjeta10.setNumTarjetaTxn(dato.getMov10NumTarjeta().trim());
			movimientoTarjeta10.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta10);
		}

		if (c > 10) {
			MovimientoTarjetaExpediente movimientoTarjeta11 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta11.setId("11".trim());
			movimientoTarjeta11.setFechaTxn(Fecha.transformarADateMC(dato.getMov11FechaTxn().trim()));
			movimientoTarjeta11.setDescripcionTxn(dato.getMov11DesTxn().trim());
			movimientoTarjeta11.setMonOriginalTxn(dato.getMov11MonOriginalTxn().trim());
			movimientoTarjeta11.setMontoTxn(dato.getMov11MontoTxn().trim());
			movimientoTarjeta11.setSigMontoTxn(dato.getMov11SigMontoTxn().trim());			
			movimientoTarjeta11.setOperacionTxn(dato.getMov11Operacion().trim());
			movimientoTarjeta11.setCodAutTxn(dato.getMov11CodAutorizacion().trim());
			movimientoTarjeta11.setNumTarjetaTxn(dato.getMov11NumTarjeta().trim());
			movimientoTarjeta11.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta11);
		}

		if (c > 11) {
			MovimientoTarjetaExpediente movimientoTarjeta12 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta12.setId("12".trim());
			movimientoTarjeta12.setFechaTxn(Fecha.transformarADateMC(dato.getMov12FechaTxn().trim()));
			movimientoTarjeta12.setDescripcionTxn(dato.getMov12DesTxn().trim());
			movimientoTarjeta12.setMonOriginalTxn(dato.getMov12MonOriginalTxn().trim());
			movimientoTarjeta12.setMontoTxn(dato.getMov12MontoTxn().trim());
			movimientoTarjeta12.setSigMontoTxn(dato.getMov12SigMontoTxn().trim());			
			movimientoTarjeta12.setOperacionTxn(dato.getMov12Operacion().trim());
			movimientoTarjeta12.setCodAutTxn(dato.getMov12CodAutorizacion().trim());
			movimientoTarjeta12.setNumTarjetaTxn(dato.getMov12NumTarjeta().trim());
			movimientoTarjeta12.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta12);
		}

		if (c > 12) {
			MovimientoTarjetaExpediente movimientoTarjeta13 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta13.setId("13".trim());
			movimientoTarjeta13.setFechaTxn(Fecha.transformarADateMC(dato.getMov13FechaTxn().trim()));
			movimientoTarjeta13.setDescripcionTxn(dato.getMov13DesTxn().trim());
			movimientoTarjeta13.setMonOriginalTxn(dato.getMov13MonOriginalTxn().trim());
			movimientoTarjeta13.setMontoTxn(dato.getMov13MontoTxn().trim());
			movimientoTarjeta13.setSigMontoTxn(dato.getMov13SigMontoTxn().trim());			
			movimientoTarjeta13.setOperacionTxn(dato.getMov13Operacion().trim());
			movimientoTarjeta13.setCodAutTxn(dato.getMov13CodAutorizacion().trim());
			movimientoTarjeta13.setNumTarjetaTxn(dato.getMov13NumTarjeta().trim());
			movimientoTarjeta13.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta13);
		}

		if (c > 13) {
			MovimientoTarjetaExpediente movimientoTarjeta14 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta14.setId("14".trim());
			movimientoTarjeta14.setFechaTxn(Fecha.transformarADateMC(dato.getMov14FechaTxn().trim()));
			movimientoTarjeta14.setDescripcionTxn(dato.getMov14DesTxn().trim());
			movimientoTarjeta14.setMonOriginalTxn(dato.getMov14MonOriginalTxn().trim());
			movimientoTarjeta14.setMontoTxn(dato.getMov14MontoTxn().trim());
			movimientoTarjeta14.setSigMontoTxn(dato.getMov14SigMontoTxn().trim());			
			movimientoTarjeta14.setOperacionTxn(dato.getMov14Operacion().trim());
			movimientoTarjeta14.setCodAutTxn(dato.getMov14CodAutorizacion().trim());
			movimientoTarjeta14.setNumTarjetaTxn(dato.getMov14NumTarjeta().trim());
			movimientoTarjeta14.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta14);
		}

		if (c > 14) {
			MovimientoTarjetaExpediente movimientoTarjeta15 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta15.setId("15".trim());
			movimientoTarjeta15.setFechaTxn(Fecha.transformarADateMC(dato.getMov15FechaTxn().trim()));
			movimientoTarjeta15.setDescripcionTxn(dato.getMov15DesTxn().trim());
			movimientoTarjeta15.setMonOriginalTxn(dato.getMov15MonOriginalTxn().trim());
			movimientoTarjeta15.setMontoTxn(dato.getMov15MontoTxn().trim());
			movimientoTarjeta15.setSigMontoTxn(dato.getMov15SigMontoTxn().trim());			
			movimientoTarjeta15.setOperacionTxn(dato.getMov15Operacion().trim());
			movimientoTarjeta15.setCodAutTxn(dato.getMov15CodAutorizacion().trim());
			movimientoTarjeta15.setNumTarjetaTxn(dato.getMov15NumTarjeta().trim());
			movimientoTarjeta15.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta15);
		}

		if (c > 15) {
			MovimientoTarjetaExpediente movimientoTarjeta16 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta16.setId("16".trim());
			movimientoTarjeta16.setFechaTxn(Fecha.transformarADateMC(dato.getMov16FechaTxn().trim()));
			movimientoTarjeta16.setDescripcionTxn(dato.getMov16DesTxn().trim());
			movimientoTarjeta16.setMonOriginalTxn(dato.getMov16MonOriginalTxn().trim());
			movimientoTarjeta16.setMontoTxn(dato.getMov16MontoTxn().trim());
			movimientoTarjeta16.setSigMontoTxn(dato.getMov16SigMontoTxn().trim());			
			movimientoTarjeta16.setOperacionTxn(dato.getMov16Operacion().trim());
			movimientoTarjeta16.setCodAutTxn(dato.getMov16CodAutorizacion().trim());
			movimientoTarjeta16.setNumTarjetaTxn(dato.getMov16NumTarjeta().trim());
			movimientoTarjeta16.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta16);
		}

		if (c > 16) {
			MovimientoTarjetaExpediente movimientoTarjeta17 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta17.setId("17".trim());
			movimientoTarjeta17.setFechaTxn(Fecha.transformarADateMC(dato.getMov17FechaTxn().trim()));
			movimientoTarjeta17.setDescripcionTxn(dato.getMov17DesTxn().trim());
			movimientoTarjeta17.setMonOriginalTxn(dato.getMov17MonOriginalTxn().trim());
			movimientoTarjeta17.setMontoTxn(dato.getMov17MontoTxn().trim());
			movimientoTarjeta17.setSigMontoTxn(dato.getMov17SigMontoTxn().trim());			
			movimientoTarjeta17.setOperacionTxn(dato.getMov17Operacion().trim());
			movimientoTarjeta17.setCodAutTxn(dato.getMov17CodAutorizacion().trim());
			movimientoTarjeta17.setNumTarjetaTxn(dato.getMov17NumTarjeta().trim());
			movimientoTarjeta17.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta17);
		}

		if (c > 17) {
			MovimientoTarjetaExpediente movimientoTarjeta18 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta18.setId("18".trim());
			movimientoTarjeta18.setFechaTxn(Fecha.transformarADateMC(dato.getMov18FechaTxn().trim()));
			movimientoTarjeta18.setDescripcionTxn(dato.getMov18DesTxn().trim());
			movimientoTarjeta18.setMonOriginalTxn(dato.getMov18MonOriginalTxn().trim());
			movimientoTarjeta18.setMontoTxn(dato.getMov18MontoTxn().trim());
			movimientoTarjeta18.setSigMontoTxn(dato.getMov18SigMontoTxn().trim());			
			movimientoTarjeta18.setOperacionTxn(dato.getMov18Operacion().trim());
			movimientoTarjeta18.setCodAutTxn(dato.getMov18CodAutorizacion().trim());
			movimientoTarjeta18.setNumTarjetaTxn(dato.getMov18NumTarjeta().trim());
			movimientoTarjeta18.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta18);
		}

		if (c > 18) {
			MovimientoTarjetaExpediente movimientoTarjeta19 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta19.setId("19".trim());
			movimientoTarjeta19.setFechaTxn(Fecha.transformarADateMC(dato.getMov19FechaTxn().trim()));
			movimientoTarjeta19.setDescripcionTxn(dato.getMov19DesTxn().trim());
			movimientoTarjeta19.setMonOriginalTxn(dato.getMov19MonOriginalTxn().trim());
			movimientoTarjeta19.setMontoTxn(dato.getMov19MontoTxn().trim());
			movimientoTarjeta19.setSigMontoTxn(dato.getMov19SigMontoTxn().trim());			
			movimientoTarjeta19.setOperacionTxn(dato.getMov19Operacion().trim());
			movimientoTarjeta19.setCodAutTxn(dato.getMov19CodAutorizacion().trim());
			movimientoTarjeta19.setNumTarjetaTxn(dato.getMov19NumTarjeta().trim());
			movimientoTarjeta19.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta19);
		}

		if (c > 19) {
			MovimientoTarjetaExpediente movimientoTarjeta20 = new MovimientoTarjetaExpediente();
			
			movimientoTarjeta20.setId("20".trim());
			movimientoTarjeta20.setFechaTxn(Fecha.transformarADateMC(dato.getMov20FechaTxn().trim()));
			movimientoTarjeta20.setDescripcionTxn(dato.getMov20DesTxn().trim());
			movimientoTarjeta20.setMonOriginalTxn(dato.getMov20MonOriginalTxn().trim());
			movimientoTarjeta20.setMontoTxn(dato.getMov20MontoTxn().trim());
			movimientoTarjeta20.setSigMontoTxn(dato.getMov20SigMontoTxn().trim());			
			movimientoTarjeta20.setOperacionTxn(dato.getMov20Operacion().trim());
			movimientoTarjeta20.setCodAutTxn(dato.getMov20CodAutorizacion().trim());
			movimientoTarjeta20.setNumTarjetaTxn(dato.getMov20NumTarjeta().trim());
			movimientoTarjeta20.setTipoTarjeta(tipoTarjeta);
			movimientoTarjetas.add(movimientoTarjeta20);
		}
		
		return movimientoTarjetas;
	}

	//Consulta de Datos del expediente ok
	public DTOConsultaDatosExpediente consultaDatosPorExpediente(String numCuenta, String tipoMoneda, Date fechaExpiracion) throws InternalExcepcion {
		//MGL 
		/*obtener valores*/
		String wsdlUrl = parametros.getWsSoapMc();		
		String wsdlAS = parametros.getPrefijoNumReferenciaMc();
		
		String codEmisor = parametros.getCodigoEmisorMc();	
		String codUsuario = parametros.getCodigoUsuarioMc();	
		String numTerminal = parametros.getNumTerminalMc();	
		String comercio = parametros.getWsComercioMc();	
		String numReferenciaWS = wsdlAS+NumeroALetras.llenarCerosAlaIzquierda(Long.toString(parametroMapper.obtenerNumeroReferenciaWS()),10);
		
		String usuario = parametros.getWsUsuarioMc();
		String clave = parametros.getWsClaveMc();		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String fechaTerminal = sdf.format(new Date());
	    System.out.println("fechaTerminal:"+fechaTerminal);
	        
	    DateFormat dateFormat = new SimpleDateFormat("HHmmss");
	    String horaTerminal = dateFormat.format(new Date());	       
	    System.out.println("horaTerminal:"+horaTerminal);
	    
	    /*fecha de expiacion*/
	    SimpleDateFormat sdfanio = new SimpleDateFormat("yyyy");
	    String fchExpanio =  sdfanio.format(fechaExpiracion).substring(2,4);
	    
	    SimpleDateFormat sdfmes = new SimpleDateFormat("MM");
	    String fchExpmes = sdfmes.format(fechaExpiracion);
	    
	    String fchExp = fchExpanio + fchExpmes;
	    
	    
	    String nCuenta = StringsUtils.quitarCeroIzquierdaString(numCuenta);
		/**/
		
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_CONSULTA_DATOS_EXPEDIENTE);
		Class<DTOConsultaDatosExpediente> dtoClass = DTOConsultaDatosExpediente.class;
		DTOConsultaDatosExpediente responseDTO = null;
				
		Map<String, String> inputRequest = ConstantesWS
				.getConsultaDatosExpedienteMap();
		
		inputRequest.put(ConstantesWS.COD_EMISOR, codEmisor);
		inputRequest.put(ConstantesWS.COD_USUARIO, codUsuario);
		inputRequest.put(ConstantesWS.NUM_TERMINAL, numTerminal);
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, numReferenciaWS);
		inputRequest.put(ConstantesWS.NUM_CUENTA, nCuenta);
		inputRequest.put(ConstantesWS.FECHA_EXPIRACION, fchExp);
		inputRequest.put(ConstantesWS.COMERCIO, comercio);
		inputRequest.put(ConstantesWS.MONEDA, tipoMoneda);
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, fechaTerminal);
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, horaTerminal);
		inputRequest.put(ConstantesWS.WS_USUARIO, usuario);
		inputRequest.put(ConstantesWS.WS_CLAVE, clave);
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.CONSULTA_DATOS_EXPEDIENTE_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	//Modificación de Cliente	
	public DTOModificacionClientes modificacionCliente(int idTarjeta, String nuevosDatos) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_MODIFICACION_CLIENTES);
		Class<DTOModificacionClientes> dtoClass = DTOModificacionClientes.class;


		Map<String, String> inputRequest = ConstantesWS
				.getModificacionClienteMap();
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, "941");
		inputRequest.put(ConstantesWS.COD_USUARIO, "CS00000001");
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "12345678");
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "ORD000123456789");		
		inputRequest.put(ConstantesWS.MONEDA_PRODUCTO, "1");
		inputRequest.put(ConstantesWS.TITULAR_APELLIDOS, "Gutierrez Solis");
		inputRequest.put(ConstantesWS.TITULAR_NOMBRE, "Marianela");
		inputRequest.put(ConstantesWS.TITULAR_NUM_CELULAR, "975426854");
		inputRequest.put(ConstantesWS.TITULAR_TELEFONO_DOMICILIO, "2131600");
		inputRequest.put(ConstantesWS.TITULAR_EMAIL	, "MarianelaGS@miempresa.com.pe");	
		inputRequest.put(ConstantesWS.NUM_DEPENDIENTE, "3");
		inputRequest.put(ConstantesWS.TITULAR_PROFESION, "16779");
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		inputRequest.put(ConstantesWS.WS_USUARIO, "0944006748");
		inputRequest.put(ConstantesWS.WS_CLAVE, "dRUch4hupAvuduBE");
		inputRequest.put(ConstantesWS.RESERVADO, "OA09123456");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.MODIFICACION_CLIENTE_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;
		DTOModificacionClientes responseDTO = null;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				
				DTOwservice dto2 = new DTOwservice(ConstantesWS.SOACTION_MODIFICACION_CLIENTE);
				
				String contenidoXML = resultado.substring(
						resultado.indexOf(dto2.getStartTag()),
						resultado.indexOf(dto2.getEndTag())
								+ dto2.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	
	//Consulta de Datos de Cliente
	public DTOConsultaDatosCliente consultaDatosCliente(int expedienteId) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_CONSULTA_DATOS_CLIENTE);
		Class<DTOConsultaDatosCliente> dtoClass = DTOConsultaDatosCliente.class;
		DTOConsultaDatosCliente responseDTO = null;


		Map<String, String> inputRequest = ConstantesWS
				.getConsultaDatosClienteMap();
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, "191");
		inputRequest.put(ConstantesWS.COD_USUARIO, "TT9999");
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "11010101");
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "AC2023000187");
		
		inputRequest.put(ConstantesWS.TIPO_DOCUMENTO, "1");
		inputRequest.put(ConstantesWS.NUM_DOCUMENTO, "000016727214");
				
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		inputRequest.put(ConstantesWS.WS_USUARIO, "4858643428");
		inputRequest.put(ConstantesWS.WS_CLAVE, "aza877azutht98b8");
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.CONSULTA_DATOS_CLIENTE_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}
	
	
	
	
	
	public DTOModificacionTarjeta bloqueoDeTarjeta(int idTarjeta, String motivoBloqueo) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_BLOQUEO_TARJETA);
		Class<DTOModificacionTarjeta> dtoClass = DTOModificacionTarjeta.class;
		DTOModificacionTarjeta responseDTO = null;


		Map<String, String> inputRequest = ConstantesWS
				.getModificacionTarjetaMap();
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, "971");
		inputRequest.put(ConstantesWS.COD_USUARIO, "TW9999");
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "11010101");
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "AC2020000322");
		inputRequest.put(ConstantesWS.ORGANIZACION, "941");
		inputRequest.put(ConstantesWS.NUM_TARJETA, "000000009");
		inputRequest.put(ConstantesWS.FECHA_EXPIRACION, "2701");
		inputRequest.put(ConstantesWS.CODIGO_BLOQUEO, "A3");
		inputRequest.put(ConstantesWS.MOTIVO_BLOQUEO, "Robo");
		inputRequest.put(ConstantesWS.COMERCIO, "9999999");
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		inputRequest.put(ConstantesWS.WS_USUARIO, "prueba1234");
		inputRequest.put(ConstantesWS.WS_CLAVE, "prueba1234567890");
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.MODIFICACION_TARJETA_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	public DTOConsultaMovimientosExpediente consultaDeMovimientoPorExpediente(int expedienteId) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_CONSULTA_MOVIMIENTOS_EXPEDIENTE);
		Class<DTOConsultaMovimientosExpediente> dtoClass = DTOConsultaMovimientosExpediente.class;
		DTOConsultaMovimientosExpediente responseDTO = null;


		Map<String, String> consultaMovimientosExpedienteMap = ConstantesWS
				.getConsultaMovimientosExpedienteMap();
		
		
		consultaMovimientosExpedienteMap.put(ConstantesWS.COD_EMISOR, "941");
		consultaMovimientosExpedienteMap.put(ConstantesWS.COD_USUARIO, "CS00000001");
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_TERMINAL, "12345678");
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_REFERENCIA, "ORD20160224");
		consultaMovimientosExpedienteMap.put(ConstantesWS.ORGANIZACION, "941");
		consultaMovimientosExpedienteMap.put(ConstantesWS.NUM_TARJETA, "526983659");
		consultaMovimientosExpedienteMap.put(ConstantesWS.FECHA_EXPIRACION, "");
		consultaMovimientosExpedienteMap.put(ConstantesWS.COMERCIO, "2999994");
		consultaMovimientosExpedienteMap.put(ConstantesWS.MONEDA, "604");
		consultaMovimientosExpedienteMap.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		consultaMovimientosExpedienteMap.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		consultaMovimientosExpedienteMap.put(ConstantesWS.WS_USUARIO, "0944006748");
		consultaMovimientosExpedienteMap.put(ConstantesWS.WS_CLAVE, "dRUch4hupAvuduBE");
		consultaMovimientosExpedienteMap.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.CONSULTA_MOVIMIENTOS_EXPEDIENTE_XML, consultaMovimientosExpedienteMap);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	public DTOConsultaDatosExpediente consultaDeDatosPorExpediente(int expedienteId) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_CONSULTA_DATOS_EXPEDIENTE);
		Class<DTOConsultaDatosExpediente> dtoClass = DTOConsultaDatosExpediente.class;
		DTOConsultaDatosExpediente responseDTO = null;


		Map<String, String> inputRequest = ConstantesWS
				.getConsultaDatosExpedienteMap();
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, "971");
		inputRequest.put(ConstantesWS.COD_USUARIO, "TW9999");
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "11010101");
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "AC2020000322");
		inputRequest.put(ConstantesWS.ORGANIZACION, "941");
		inputRequest.put(ConstantesWS.NRO_DOCUMENTO, "74851254");
		inputRequest.put(ConstantesWS.CORREO_ELECTRONICO, "prueba@hotmail.com");
		inputRequest.put(ConstantesWS.NRO_CELULAR, "965845214");
		inputRequest.put(ConstantesWS.FECHA_EXPIRACION, "2701");
		inputRequest.put(ConstantesWS.COMERCIO, "9999999");
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		inputRequest.put(ConstantesWS.WS_USUARIO, "prueba1234");
		inputRequest.put(ConstantesWS.WS_CLAVE, "prueba1234567890");
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.CONSULTA_DATOS_EXPEDIENTE_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	
	public DTOModificacionClientes actualizacionDeDatos(int idTarjeta, String nuevosDatos) throws InternalExcepcion {
		String wsdlUrl = parametros.getWsSoapMc();
		DTOwservice dto = new DTOwservice(ConstantesWS.SOACTION_MODIFICACION_CLIENTE);
		Class<DTOModificacionClientes> dtoClass = DTOModificacionClientes.class;


		Map<String, String> inputRequest = ConstantesWS
				.getModificacionClienteMap();
		
		
		inputRequest.put(ConstantesWS.COD_EMISOR, "971");
		inputRequest.put(ConstantesWS.COD_USUARIO, "TW9999");
		inputRequest.put(ConstantesWS.NUM_TERMINAL, "11010101");
		inputRequest.put(ConstantesWS.NUM_REFERENCIA, "AC2020000322");
		inputRequest.put(ConstantesWS.ORGANIZACION, "941");
		inputRequest.put(ConstantesWS.NRO_DOCUMENTO, "74851254");
		inputRequest.put(ConstantesWS.CORREO_ELECTRONICO, "prueba@hotmail.com");
		inputRequest.put(ConstantesWS.NRO_CELULAR, "965845214");
		inputRequest.put(ConstantesWS.FECHA_EXPIRACION, "2701");
		inputRequest.put(ConstantesWS.COMERCIO, "9999999");
		inputRequest.put(ConstantesWS.FECHA_TXN_TERMINAL, "20160224");
		inputRequest.put(ConstantesWS.HORA_TXN_TERMINAL, "172020");
		inputRequest.put(ConstantesWS.WS_USUARIO, "prueba1234");
		inputRequest.put(ConstantesWS.WS_CLAVE, "prueba1234567890");
		inputRequest.put(ConstantesWS.RESERVADO, "");
		
		String soapRequestPrevie = ConstantesWS.generarXml(
				ConstantesWS.MODIFICACION_CLIENTE_XML, inputRequest);
		

		String soapRequest = dto.getSoapTemplate().replace("SOAP_CONTENT", soapRequestPrevie);

		//logger.info("Request generado: " + soapRequest);

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;
		DTOModificacionClientes responseDTO = null;

		while (attempt < maxRetries && !success) {
			attempt++;
			String dataWS = "";
			try {
				dataWS = SoapClientUtil.sendSoapRequest(wsdlUrl,
						dto.getSoapAction(),
						soapRequest);
			} catch (ExternalServiceMCProcesosException e) {
				throw e;
			}

			try {

				logger.info("Respuesta del servidor:");
				logger.info(dataWS);

				Document documentoXML = SoapClientUtil.parseXmlResponse(dataWS
						.toString());
				String resultado = SoapClientUtil.getTextFromElement(
						documentoXML, dto.getResultTag());

				String contenidoXML = resultado.substring(
						resultado.indexOf(dto.getStartTag()),
						resultado.indexOf(dto.getEndTag())
								+ dto.getEndTag().length());

				responseDTO = SoapClientUtil.convertirXMLAObjeto(
						new StringReader(contenidoXML),
						dtoClass);

				logger.info("Objeto de respuesta: " + responseDTO);
				success = true;
				logger.info("Conexión exitosa en el intento " + attempt);
			} catch (InternalExcepcion e) {
				throw e;
			}

		}
		return responseDTO;
	}

	public void conexionTest() throws InternalExcepcion, ExternalServiceMCProcesosException {
	    int maxRetries = 5;
	    boolean success = false;
	    StringBuilder resultadoFallos = new StringBuilder();

	    // Probar cada mï¿½todo secuencialmente 5 veces
	    for (int i = 0; i < 5; i++) {
	        int fallidos = 0;

	        // Probar tryInformacionDeTarjeta
	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                success = tryInformacionDeTarjeta();
	                if (success) break;
	            } catch (ExternalServiceMCProcesosException e) {
	                fallidos++;
	                resultadoFallos.append("tryInformacionDeTarjeta fallï¿½ en el intento ")
	                        .append(attempt).append(": ").append(e.getMessage()).append("\n");
	                logger.error("Error en intento " + attempt + " de tryInformacionDeTarjeta: " + e.getMessage(), e);
	            }
	        }
	        resultadoFallos.append("tryInformacionDeTarjeta intentado ").append(fallidos).append(" veces.\n");

	        //if (success) break; // Salir si se ha tenido ï¿½xito en el mï¿½todo

	        // Probar tryBloqueoDeTarjeta
	        fallidos = 0;
	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                success = tryBloqueoDeTarjeta();
	                if (success) break;
	            } catch (ExternalServiceMCProcesosException e) {
	                fallidos++;
	                resultadoFallos.append("tryBloqueoDeTarjeta fallï¿½ en el intento ")
	                        .append(attempt).append(": ").append(e.getMessage()).append("\n");
	                logger.error("Error en intento " + attempt + " de tryBloqueoDeTarjeta: " + e.getMessage(), e);
	            }
	        }
	        resultadoFallos.append("tryBloqueoDeTarjeta intentado ").append(fallidos).append(" veces.\n");

	        //if (success) break;

	        // Probar tryConsultaDeMovimientoPorExpediente
	        fallidos = 0;
	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                success = tryConsultaDeMovimientoPorExpediente();
	                if (success) break;
	            } catch (ExternalServiceMCProcesosException e) {
	                fallidos++;
	                resultadoFallos.append("tryConsultaDeMovimientoPorExpediente fallï¿½ en el intento ")
	                        .append(attempt).append(": ").append(e.getMessage()).append("\n");
	                logger.error("Error en intento " + attempt + " de tryConsultaDeMovimientoPorExpediente: " + e.getMessage(), e);
	            }
	        }
	        resultadoFallos.append("tryConsultaDeMovimientoPorExpediente intentado ").append(fallidos).append(" veces.\n");

	        //if (success) break;

	        // Probar tryConsultaDeDatosPorExpediente
	        fallidos = 0;
	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                success = tryConsultaDeDatosPorExpediente();
	               if (success) break;
	            } catch (ExternalServiceMCProcesosException e) {
	                fallidos++;
	                resultadoFallos.append("tryConsultaDeDatosPorExpediente fallï¿½ en el intento ")
	                        .append(attempt).append(": ").append(e.getMessage()).append("\n");
	                logger.error("Error en intento " + attempt + " de tryConsultaDeDatosPorExpediente: " + e.getMessage(), e);
	            }
	        }
	        resultadoFallos.append("tryConsultaDeDatosPorExpediente intentado ").append(fallidos).append(" veces.\n");

	        //if (success) break;

	        // Probar tryActualizacionDeDatos
	        fallidos = 0;
	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                success = tryActualizacionDeDatos();
	                if (success) break;
	            } catch (ExternalServiceMCProcesosException e) {
	                fallidos++;
	                resultadoFallos.append("tryActualizacionDeDatos fallï¿½ en el intento ")
	                        .append(attempt).append(": ").append(e.getMessage()).append("\n");
	                logger.error("Error en intento " + attempt + " de tryActualizacionDeDatos: " + e.getMessage(), e);
	            }
	        }
	        resultadoFallos.append("tryActualizacionDeDatos intentado ").append(fallidos).append(" veces.\n");

	        //if (success) break;
	    }

	    if (!success) {
	        logger.error("Todos los mï¿½todos fallaron despuï¿½s de mï¿½ltiples intentos.\n" + resultadoFallos.toString());
	        throw new ExternalServiceMCProcesosException("Todos los intentos fallaron para obtener el DTO:\n" + resultadoFallos.toString());
	    } else {
	        logger.info("Mï¿½todo ejecutado con ï¿½xito despuï¿½s de mï¿½ltiples intentos.");
	    }
	}



	private boolean tryInformacionDeTarjeta() {
	    try {
	        DTOConsultaDatosTarjeta responseDTO = informacionDeTarjeta();
	        logger.info("informacionDeTarjeta ejecutado con ï¿½xito: " + responseDTO);
	        return true;
	    } catch (Exception e) {
	        logger.warn("Fallï¿½ el mï¿½todo informacionDeTarjeta: " + e.getMessage());
	        return false;
	    }
	}

	private boolean tryBloqueoDeTarjeta() {
	    try {
	        DTOModificacionTarjeta responseDTO = bloqueoDeTarjeta(1, "Robo");
	        logger.info("bloqueoDeTarjeta ejecutado con ï¿½xito: " + responseDTO);
	        return true;
	    } catch (Exception e) {
	        logger.warn("Fallï¿½ el mï¿½todo bloqueoDeTarjeta: " + e.getMessage());
	        return false;
	    }
	}

	private boolean tryConsultaDeMovimientoPorExpediente() {
	    try {
	        DTOConsultaMovimientosExpediente responseDTO = consultaDeMovimientoPorExpediente(1);
	        logger.info("consultaDeMovimientoPorExpediente ejecutado con ï¿½xito: " + responseDTO);
	        return true;
	    } catch (Exception e) {
	        logger.warn("Fallï¿½ el mï¿½todo consultaDeMovimientoPorExpediente: " + e.getMessage());
	        return false;
	    }
	}

	private boolean tryConsultaDeDatosPorExpediente() {
	    try {
	        DTOConsultaDatosExpediente responseDTO = consultaDeDatosPorExpediente(1);
	        logger.info("consultaDeDatosPorExpediente ejecutado con ï¿½xito: " + responseDTO);
	        return true;
	    } catch (Exception e) {
	        logger.warn("Fallï¿½ el mï¿½todo consultaDeDatosPorExpediente: " + e.getMessage());
	        return false;
	    }
	}

	private boolean tryActualizacionDeDatos() {
	    try {
	        DTOModificacionClientes responseDTO = actualizacionDeDatos(1, "nuevosDatos");
	        logger.info("actualizacionDeDatos ejecutado con ï¿½xito: " + responseDTO);
	        return true;
	    } catch (Exception e) {
	        logger.warn("Fallï¿½ el mï¿½todo actualizacionDeDatos: " + e.getMessage());
	        return false;
	    }
	}


	
	
}
