package pe.bn.com.sate.ope.transversal.configuration;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import pe.bn.com.sate.ope.infrastructure.service.internal.CompService;
import pe.bn.com.sate.ope.infrastructure.service.internal.NotificacionService;
import pe.bn.com.sate.ope.persistence.mapper.internal.AsignacionMapper;
import pe.bn.com.sate.ope.transversal.dto.sate.AsignacionCorreo;
import pe.bn.com.sate.ope.transversal.util.Fecha;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;

@Configuration
@EnableScheduling
public class DynamicCronScheduler {

    private final static Logger log = Logger.getLogger(DynamicCronScheduler.class);

    @Autowired
    private Parametros parametros;

    @Autowired
    private NotificacionService notificacion;

    @Autowired
    private CompService compService;

    @Autowired
    private AsignacionMapper asignacionMapper;

    private ScheduledFuture<?> currentTask;
    private ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    public void initDynamicCron() {
        scheduler = taskScheduler();
        scheduleDynamicCronTask(); // Configura la tarea principal
        scheduleCronVerifier();    // Configura la verificación cada hora
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("dynamic-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    // Configurar la tarea principal
    public void scheduleDynamicCronTask() {
        try {
            compService.asignarParametros();
        } catch (Exception e) {
            log.error("Error asignando parámetros desde compService", e);
        }

        String cronExpresion = parametros.getCromenviocorreo();
        log.info("Configurando tarea dinámica con cron: " + cronExpresion);

        if (currentTask != null) {
            currentTask.cancel(false);
            log.info("Tarea anterior cancelada.");
        }

        currentTask = scheduler.schedule(() -> enviarCorreos(), new CronTrigger(cronExpresion));
    }

    // Verificación de cambios cada hora
    public void scheduleCronVerifier() {
        log.info("Configurando tarea de verificación de cambios cada hora.");
        scheduler.schedule(() -> {
			try {
				verifyCronConfig();
			} catch (InterruptedException e) {
 				e.printStackTrace();
			} catch (ExecutionException e) {
 				e.printStackTrace();
			}
		}, new CronTrigger("0 0 * * * *")); // Cada hora
    }

    public void verifyCronConfig() throws InterruptedException, ExecutionException {
        log.info("Verificando si la configuración del cron ha cambiado...");
        String nuevaExpresion = parametros.getCromenviocorreo();

        if (currentTask == null || !nuevaExpresion.equals((((CronTrigger) currentTask.get()).getExpression()))) {
            log.info("La configuración del cron ha cambiado. Actualizando...");
            scheduleDynamicCronTask();
        } else {
            log.info("La configuración del cron no ha cambiado.");
        }
    }


    public void enviarCorreos() {
        log.info("Inicio job envio de correos asignaciones registradas");
        
        String fechaRegistro = Fecha.obtenerFechaDiaAnterior();
        //String fechaRegistro = "09-09-2024";
        List<AsignacionCorreo> asignaciones = asignacionMapper.obtenerAsignacionesCorreo(fechaRegistro);

        for (AsignacionCorreo asignacionCorreo : asignaciones) {
            notificacion.enviarMailAsignacion(asignacionCorreo);
        }

        log.info("Fin job envio de correos asignaciones registradas");
    }
}
