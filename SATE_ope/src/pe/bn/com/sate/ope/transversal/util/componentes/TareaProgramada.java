package pe.bn.com.sate.ope.transversal.util.componentes;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pe.bn.com.sate.ope.infrastructure.service.internal.NotificacionService;
import pe.bn.com.sate.ope.persistence.mapper.internal.AsignacionMapper;
import pe.bn.com.sate.ope.transversal.dto.sate.AsignacionCorreo;
import pe.bn.com.sate.ope.transversal.util.Fecha;

 
//@Component
public class TareaProgramada {
	
/*	private final static Logger log = Logger.getLogger(TareaProgramada.class);
	@Autowired
	private NotificacionService notificacion;
	@Autowired
	private AsignacionMapper asignacionMapper;
	
	@Scheduled(cron = "0 15 13 * * *") // 12:58 p.m. todos los días
    public void enviarCorreos() {
    	log.info("Inicio job envio de correos asignaciones registradas");
    	//String fechaRegistro = Fecha.obtenerFechaDiaAnterior();
    	String fechaRegistro = "09-09-2024";
    	List<AsignacionCorreo> asi =  asignacionMapper.obtenerAsignacionesCorreo(fechaRegistro);
    	for (AsignacionCorreo asignacionCorreo : asi) {
    		notificacion.enviarMailAsignacion(asignacionCorreo);
		}
    	log.info("Fin job envio de correos asignaciones registradas");

    }*/

}



