package pe.bn.com.sate.ope.infrastructure.service.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ibm.msg.client.commonservices.InternalException;

import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.SolicitudTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.TarjetaResumen;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaDatosTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

public interface TarjetaService {

	public void registrarSolicitudTarjeta(Tarjeta tarjeta, Cliente cliente);

	public List<SolicitudTarjeta> buscarTodosSolicitudTarjetaPendientes();

	public void aprobarSolicitudTarjeta(List<SolicitudTarjeta> solicitudTarjetas);

	public void rechazarSolicitudTarjeta(
			List<SolicitudTarjeta> solicitudTarjetas);

	public Tarjeta buscarTarjetaPorNumeroTarjeta(String numTarjeta);

	public List<Tarjeta> buscarTarjetaPorTipoDocumento(String tipoDocumento,
			String numDocumento);

	public DatosTarjetaCliente buscarDatosTarjetasCliente(String tipoBusqueda,
			String numDocumento );

	public void actualizarEstadoTarjeta(EstadoTarjeta estadoTarjeta);

	public List<TarjetaResumen> obtenerListaTarjetas(String cuentaCorriente, String fechaInicio,
			String fechaFin);

	public void actualizarContacto(Tarjeta tarjeta);
	
	public void actualizarSaldos(Tarjeta tarjeta);
	
	public void actualizarEstadoCuenta(Tarjeta tarjeta);

	public void bloquearTarjetaPorRobo(EstadoTarjeta estadoTarjeta,
			Long idTarjeta, Long idCliente);

	public String verificarSolicitudes(String tipoDocumento, String nroDocuemnto);
	public String verificarTarjetasDisponible(String tipoDocumento, String nroDocuemnto, Tarjeta tarjeta);
	
	
	public Tarjeta 	buscarPrimeraTarjetaCliente(String tipoDocumento, String numeroDocumento);
	
	public Tarjeta 	buscarTarjetaId(Long idtar);
	
	public long consultarExisteTarjetaRUC(String numTarjeta,String ruc);
	public long consultarExisteTipNumDocRUC(String tipoDocumento,String numDocumento,String ruc);
	
	public List<TipoTarjetaNegocio> consultaTipoTarjetaNegocio(String bim) throws InternalException;

	public Tarjeta verificarEstadoTarjeta(Tarjeta tarjeta, DTOConsultaDatosTarjeta data);

 
	
	
}
