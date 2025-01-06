package pe.bn.com.sate.ope.infrastructure.service.internal;

import pe.bn.com.sate.ope.transversal.dto.sate.AsignacionCorreo;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Usuario;

public interface NotificacionService {
	public void enviarMailUsuarioClave(Usuario usuario, String clave);
	public void enviarMailBloqueoTarjeta(String nombreCompleto, Tarjeta tarjeta,String fecha,String hora,String codBloqueo);
	public void enviarMailAsignacion(AsignacionCorreo asicoreo);
}
