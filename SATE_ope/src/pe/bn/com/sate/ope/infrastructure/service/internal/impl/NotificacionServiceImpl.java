package pe.bn.com.sate.ope.infrastructure.service.internal.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.ArrayOfTns1ReqListMessage;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.DatosCorreo;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.DatosParametro;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.ReqListMessage;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.RequestMessage;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.message.ServiceMessageProxy;
import pe.bn.com.sate.ope.infrastructure.service.internal.NotificacionService;
import pe.bn.com.sate.ope.transversal.dto.aldeamo.EmailRequest;
import pe.bn.com.sate.ope.transversal.dto.aldeamo.Person;
import pe.bn.com.sate.ope.transversal.dto.aldeamo.Recipient;
import pe.bn.com.sate.ope.transversal.dto.sate.AsignacionCorreo;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Usuario;
import pe.bn.com.sate.ope.transversal.util.CertificadoUtil;
import pe.bn.com.sate.ope.transversal.util.Fecha;
import pe.bn.com.sate.ope.transversal.util.MontoUtils;
import pe.bn.com.sate.ope.transversal.util.ServicioWebUtil;
import pe.bn.com.sate.ope.transversal.util.TarjetaUtils;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;

/**
 * Implementación del servicio de notificación para el envío de correos a los
 * usuarios.
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {
	private final Logger logger = Logger.getLogger(NotificacionServiceImpl.class);

	@Autowired
	private Parametros parametros;

	/**
	 * Envía un correo electrónico a un usuario con su clave de acceso.
	 * 
	 * @param usuario
	 *            el usuario al que se le enviará el correo.
	 * @param clave
	 *            la clave que se enviará al usuario.
	 * @throws InternalServiceException
	 *             si ocurre un error durante el envío del correo.
	 */
	@Override
	public void enviarMailUsuarioClave(Usuario usuario, String clave) {
		logger.info("Inicio del método enviarMailUsuarioClave");

		try {
			// Crea una instancia del proxy del servicio de mensajería.
			ServiceMessageProxy serviceMessage = new ServiceMessageProxy();

			// Cambia los tiempos de espera para la conexión y respuesta.
			ServicioWebUtil.cambiarTiempoEspera(parametros.getConexionTiempo(), parametros.getRespuestaTiempo(),
					(BindingProvider) serviceMessage._getDescriptor().getProxy());

			// Crea un mensaje de solicitud para el envío del correo.
			RequestMessage rqMessage = new RequestMessage();
			rqMessage.setCodRequermiento(ConstantesGenerales.COD_REQUERIMIENTO_ENVIO_SMS);

			// Crea un objeto para la lista de requisitos.
			ArrayOfTns1ReqListMessage arr = new ArrayOfTns1ReqListMessage();
			ReqListMessage rlm = new ReqListMessage();

			// Crea y configura los datos del correo.
			DatosCorreo datos = new DatosCorreo();
			datos.setAsunto("Acceso a Tarjeta Empresarial - Banco de la Nacion");
			// TODO CAMBIAR ======= > CORREO ELECTRONICO
			datos.setCorreoDestinatario(usuario.getCorreoLaboral());

			// Configura los parámetros del correo.
			DatosParametro params = new DatosParametro();
			params.setParametro1(usuario.nombreCompleto());
			params.setParametro2(clave);
			params.setParametro3(new Date().toString()); 


			rlm.setDatosCorreo(datos);
			rlm.setDatosParametro(params);
			arr.getItem().add(rlm);
			rqMessage.setReqListMessage(arr);
			serviceMessage.sendMessage(rqMessage);
		} catch (Exception ex) {
			logger.error("Error en enviarMailUsuarioClave: " + ex.getMessage(), ex);
			throw new InternalServiceException(ex.getMessage(), ex);
		} finally {
			logger.info("Fin del método enviarMailUsuarioClave");
		}
	}

	@Override
	public void enviarMailBloqueoTarjeta(String nombreCompleto, Tarjeta tarjeta, String fecha, String hora,
			String codBloqueo) {
		logger.info("Inicio del método enviarMailCambioClave");

		try {

			// Crear el objeto EmailRequest
			EmailRequest emailRequest = new EmailRequest();
			emailRequest.setAttachments(new ArrayList<>());

			LocalDateTime now = LocalDateTime.now();
			String fechaActual = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			String horaActual = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

			// Plantilla HTML
			String body = String.format("<!DOCTYPE html>" + "<html lang=\"es\">" + "<head>"
					+ "    <meta charset=\"UTF-8\">"
					+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
					+ "    <title>Constancia de Bloqueo</title>" + "    <style>" + "        body {"
					+ "            font-family: Arial, sans-serif;" + "            margin: 0;"
					+ "            padding: 0;" + "            background-color: #f9f9f9;" + "        }"
					+ "        .container {" + "            padding: 20px;" + "            background-color: #ffffff;"
					+ "        }" + "        .header {" + "            font-size: 18px;"
					+ "            font-weight: bold;" + "            margin-bottom: 20px;"
					+ "            color: #333333;" + "        }" + "        .content {"
					+ "            font-size: 14px;" + "            color: #555555;" + "            line-height: 1.6;"
					+ "        }" + "        .details-table {" + "            width: 50%%;"
					+ "            border-collapse: collapse;" + "            margin-top: 10px;" + "        }"
					+ "        .details-table td {" + "            padding: 8px 0;" + "        }" + "        .label {"
					+ "            font-weight: bold;" + "            text-align: left;" + "            width: 50%%;"
					+ "        }" + "        .value {" + "            text-align: left;" + "        }"
					+ "        .footer {" + "            margin-top: 20px;" + "            font-size: 12px;"
					+ "            color: #888888;" + "            text-align: left;" + "        }"
					+ "        .footer a {" + "            color: #007bff;" + "            text-decoration: none;"
					+ "        }" + "        .footer a:hover {" + "            text-decoration: underline;"
					+ "        }" + "        .footer img {" + "            margin-top: 10px;"
					+ "            width: 150px;" + "        }" + "    </style>" + "</head>" + "<body>"
					+ "    <div class=\"container\">" + "        <div class=\"header\">" + "            Estimado(a) %s,"
					+ "        </div>" + "        <div class=\"content\">"
					+ "            Mediante el presente, remitimos <span class=\"highlight\">CONSTANCIA DE BLOQUEO DE TARJETA TESORO</span>, porque elegiste el envío electrónico.<br><br>"
					+ "            A continuación, los detalles de tu solicitud de bloqueo:"
					+ "            <table class=\"details-table\">" + "                <tr>"
					+ "                    <td class=\"label\">N° de Tarjeta:</td>"
					+ "                    <td class=\"value\">%s</td>" + "                </tr>"
					+ "                <tr>" + "                    <td class=\"label\">Código de Bloqueo:</td>"
					+ "                    <td class=\"value\">%s</td>" + "                </tr>"
					+ "                <tr>" + "                    <td class=\"label\">Fecha:</td>"
					+ "                    <td class=\"value\">%s</td>" + "                </tr>"
					+ "                <tr>" + "                    <td class=\"label\">Hora:</td>"
					+ "                    <td class=\"value\">%s</td>" + "                </tr>"
					+ "            </table>"
					+ "            Si tienes alguna consulta, llámanos a nuestra Mesa de Ayuda al <strong>440-5305</strong> / <strong>442-4470</strong>, o también a nuestra línea gratuita desde teléfonos fijos <strong>0800-10700</strong>, o ingresa a <a href=\"https://www.bn.com.pe\">www.bn.com.pe</a>."
					+ "        </div>" + "        <div class=\"footer\">" + "            Atentamente,<br>"
					+ "            <strong>Banco de la Nación</strong><br>"

					+ "        </div>" + "    </div>" + "</body>" + "</html>", nombreCompleto, // Nombre completo
					TarjetaUtils.procesarTarjeta(tarjeta.getNumTarjeta()), // Número de tarjeta
					codBloqueo, // Código de bloqueo
					fechaActual, // Fecha
					horaActual);

			emailRequest.setBody(body);

			Person replyTo = new Person();
			replyTo.setName("Banco de la Nacion");
			replyTo.setEmail(parametros.getCorreoEmisor());
			emailRequest.setReplyTo(replyTo);

			Person from = new Person();
			from.setName("Banco de la Nacion");
			from.setEmail(parametros.getCorreoEmisor());
			emailRequest.setFrom(from);

			Recipient recipient = new Recipient();
			recipient.setEmail(tarjeta.getEmail());
			List<Recipient> toList = new ArrayList<>();
			toList.add(recipient);
			emailRequest.setTo(toList);

			emailRequest.setSubject("ASUNTO BLOQUEO TARJETA");
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonPayload = objectMapper.writeValueAsString(emailRequest);
			String urlString = parametros.getUrlAldeamo();
			URL url = new URL(urlString);
			String host = url.getHost();
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			String token = parametros.getTokenAldeamo();
			headers.put("Authorization", "Bearer " + token);
			String response = enviarSolicitudRest(urlString, host, jsonPayload, headers);
			logger.info("Respuesta del servidor: " + response);

		} catch (Exception ex) {
			logger.error("Error en enviarMailCambioClave: " + ex.getMessage(), ex);
			throw new InternalServiceException(ex.getMessage(), ex);
		} finally {
			logger.info("Fin del método enviarMailCambioClave");
		}

	}

	public String enviarSolicitudRest(String urlString, String host, String jsonPayload, Map<String, String> headers)
			throws Exception {
		SSLContext sslContext = CertificadoUtil.getSslContext(host, ConstantesGenerales.certiAldeamo);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		// Configurar la conexión HTTPS
		URL url = new URL(urlString);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setSSLSocketFactory(sslSocketFactory);

		// Configurar el método y las propiedades de la conexión
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setConnectTimeout(15000); // 15 segundos
		connection.setReadTimeout(15000); // 15 segundos

		// Establecer los encabezados de la petición
		connection.setRequestProperty("Content-Type", "application/json");
		for (Map.Entry<String, String> header : headers.entrySet()) {
			connection.setRequestProperty(header.getKey(), header.getValue());
		}

		// Enviar el cuerpo de la solicitud
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = jsonPayload.getBytes("UTF-8");
			os.write(input, 0, input.length);
		}

		// Leer la respuesta
		int statusCode = connection.getResponseCode();

		InputStream is;
		if (statusCode >= 200 && statusCode < 400) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder response = new StringBuilder();
		String responseLine;
		while ((responseLine = br.readLine()) != null) {
			response.append(responseLine.trim());
		}

		// Cerrar la conexión
		connection.disconnect();

		// Devolver la respuesta
		return response.toString();
	}

	@Override
	public void enviarMailAsignacion(AsignacionCorreo asicoreo) {

		try {
			EmailRequest emailRequest = new EmailRequest();
			emailRequest.setAttachments(new ArrayList<>());

			String body = String.format("<!DOCTYPE html>" + "<html lang='es'>" + "<head>" + "    <meta charset='UTF-8'>"
					+ "    <title>Notificación de Asignación</title>" + "    <style>"
					+ "        body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 0; background-color: #ffffff; }"
					+ "        .container { margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 5px; }"
					+ "        .header img { max-width: 100px; margin-bottom: 10px; }"
					+ "        .footer { margin-top: 20px; font-size: 12px; color: #777777;  }"
					+ "        table.details { width: 40%%; border-collapse: collapse; margin-top: 15px; background-color: #ffffff; }"
					+ "        table.details th, table.details td { border: 1px solid #000; padding: 10px; text-align: left; }"
					+ "        table.details th { background-color: #ffffff; color: #000; font-weight: bold; }"
					+ "    </style>" + "</head>" + "<body>" + "    <div class='container'>"
					+ "        <p>Hola <strong>%s</strong>,</p>"
					+ "        <p>Le notificamos que su tarjeta <strong>TESORO</strong> tiene una nueva asignación con los siguientes detalles:</p>"
					+ "        <table class='details'>" + "            <tr>"
					+ "                <th>Fecha de Inicio</th>" + "                <td>%s</td>" + "            </tr>"
					+ "            <tr>" + "                <th>Fecha de Fin</th>" + "                <td>%s</td>"
					+ "            </tr>" + "            <tr>" + "                <th>Línea de Crédito</th>"
					+ "                <td>%s</td>" + "            </tr>" + "        </table>"
					+ "        <p>Si no está enterado de esta asignación, por favor comuníquese con nuestro <strong>Call Center</strong> para realizar el bloqueo correspondiente.</p>"
					+ "        <div class='footer'>"
					+ "            <img src='https://www.bn.com.pe/img/logoBN.png' alt='Logo'>"
					+ "            <p>Para cualquier consulta, comuníquese a los siguientes números:<br>"
					+ "            (01) 123-4567 / 0800-12345</p>"
					+ "            <p>&copy; 2024 TESORO. Todos los derechos reservados.</p>" + "        </div>"
					+ "    </div>" + "</body>" + "</html>", asicoreo.getNombreCompleto(), // Parámetro 1: Nombre
																							// completo
					Fecha.formatearFechaEmail(asicoreo.getInicioLinea()), // Parámetro 2: Fecha de inicio
					Fecha.formatearFechaEmail(asicoreo.getFinLinea()), // Parámetro 3: Fecha de fin
					MontoUtils.formatearMonto(asicoreo.getLinea()) // Parámetro 4: Monto
			);

			// Configuración del email
			emailRequest.setBody(body);

			Person replyTo = new Person();
			replyTo.setName("Banco de la Nación");
			replyTo.setEmail(parametros.getCorreoEmisor());
			emailRequest.setReplyTo(replyTo);

			Person from = new Person();
			from.setName("Banco de la Nación");
			from.setEmail(parametros.getCorreoEmisor());
			emailRequest.setFrom(from);

			Recipient recipient = new Recipient();
			recipient.setEmail(asicoreo.getCorreo());
			List<Recipient> toList = new ArrayList<>();
			toList.add(recipient);
			emailRequest.setTo(toList);

			emailRequest.setSubject("Notificación de Asignación de Tarjeta TESORO");

			// Conversión a JSON y envío
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonPayload = objectMapper.writeValueAsString(emailRequest);
			String urlString = parametros.getUrlAldeamo();
			URL url = new URL(urlString);
			String host = url.getHost();

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			String token = parametros.getTokenAldeamo();
			headers.put("Authorization", "Bearer " + token);

			String response = enviarSolicitudRest(urlString, host, jsonPayload, headers);
			logger.info("Respuesta del servidor: " + response);

		} catch (Exception ex) {
			logger.error("Error en enviarMailAsignacion: " + ex.getMessage(), ex);
			throw new InternalServiceException(ex.getMessage(), ex);
		}

	}

}