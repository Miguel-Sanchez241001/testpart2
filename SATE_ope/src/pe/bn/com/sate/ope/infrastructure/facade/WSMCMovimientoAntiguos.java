package pe.bn.com.sate.ope.infrastructure.facade;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.wsrestsate.MovimientoRequest;
import pe.bn.com.sate.ope.transversal.dto.wsrestsate.MovimientoResponse;
import pe.bn.com.sate.ope.transversal.util.Fecha;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;

@Component
public class WSMCMovimientoAntiguos {
	private @Autowired
	Parametros parametros;
	
	
	private final Logger logger = Logger.getLogger(WSMCMovimientoAntiguos.class);
	
	
	
	
	public List<MovimientoTarjetaExpediente> consultaMovimientoPorExpediente(Asignacion asignacionSeleccionada) throws Exception {
	    logger.info("Inicio proceso movimientos antiguos");

	    // Paso 1: Crear cliente para consumir el servicio REST
	    RestTemplate restTemplate = new RestTemplate();
	    String url = parametros.getUrlServiceRestAntiguos();

	    // Crear el request para el servicio
	    MovimientoRequest request = new MovimientoRequest();
	    request.setFechaInicio(Fecha.formatearFechaWS(asignacionSeleccionada.getFechaInicioLinea()));
	    request.setFechaFin(Fecha.formatearFechaWS(asignacionSeleccionada.getFechaFinLinea()));
	    request.setNumCuenta(asignacionSeleccionada.getCuentaExpediente());

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<MovimientoRequest> entity = new HttpEntity<>(request, headers);

	    try {
	        // Realizar la llamada al servicio REST
	        ResponseEntity<MovimientoResponse> response = restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            entity,
	            MovimientoResponse.class
	        );

	        // Paso 2: Verificar el código HTTP de respuesta
	        if (response.getStatusCode() != HttpStatus.OK) {
	            logger.error("Error en la llamada al servicio, código de estado HTTP: "+ response.getStatusCode());
	            throw new ServiceException("Error al consultar movimientos antiguos: " + response.getStatusCode());
	        }

	        MovimientoResponse movimientoResponse = response.getBody();

	        // Paso 3: Verificar el código en la data
	        if (movimientoResponse == null || !"0000".equals(movimientoResponse.getCodigo())) {
	            String mensajeError = movimientoResponse != null ? movimientoResponse.getMensaje() : "Respuesta nula del servicio";
	            logger.error("Error en la data, código: "+ movimientoResponse.getCodigo() + " mensaje: "+mensajeError);
	            throw new ServiceException("Error en la respuesta del servicio: " + mensajeError);
	        }

	        // Paso 4: Mapear los resultados a objetos
	        List<MovimientoTarjetaExpediente> movimientos = movimientoResponse.getData().stream()
	            .map(data -> {
	                MovimientoTarjetaExpediente expediente = new MovimientoTarjetaExpediente();
	                expediente.setId(UUID.randomUUID().toString()); // Crear ID único
	                expediente.setFechaTxn(Fecha.transformarADateMC((data.getFechaTxn()))); // Convertir String a Date
	                expediente.setDescripcionTxn(data.getDescripcionTxn().trim());
	                expediente.setMonOriginalTxn(data.getMonOriginalTxn());
	                expediente.setMontoTxn(data.getMontoTxn());
	                expediente.setSigMontoTxn(data.getSigMontoTxn());
	                expediente.setOperacionTxn(data.getOperacionTxn());
	                expediente.setCodAutTxn(data.getCodAutTxn());
	                expediente.setNumTarjetaTxn(data.getNumTarjetaTxn());
	                expediente.setTipoTarjeta(""); // Asignar valor predeterminado
	                return expediente;
	            })
	            .collect(Collectors.toList());

	        // Paso 5: Retornar la lista de objetos
	        logger.info("Finaliza proceso movimientos antiguos con "+ movimientos.size()+ " movimientos");
	        return movimientos;

	    } catch (Exception e) {
	        logger.error("Error inesperado al consultar movimientos antiguos:"+ e.getMessage());
	        throw new ServiceException("Error inesperado al consultar movimientos antiguos: " + e.getMessage(), e);
	    }
	}

	
	
}
