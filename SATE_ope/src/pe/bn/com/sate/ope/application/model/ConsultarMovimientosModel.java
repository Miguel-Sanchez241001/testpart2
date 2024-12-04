package pe.bn.com.sate.ope.application.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.util.enums.OperacionMovimientoMC;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMontoMC;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaMC;

@Getter
@Setter
@ToString
public class ConsultarMovimientosModel {

	private Date fechaInicio;
	private Date fechaFin;
	private String numeroTarjeta;
	private List<TipoBusqueda> listaTipoBusqueda;
	private String tipoBusqueda;
	
	
	private Asignacion asignacionSeleccionada;
	
	private boolean busquedaRealizada;
	
	private List<Asignacion> asignacionesTotal;
	
	
	private List<String> listaTipoBusquedaPor;
	private String tipoBusquedaPor;
	
		
	public Asignacion getAsignacionSeleccionada() {
		return asignacionSeleccionada;
	}

	public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) {
		this.asignacionSeleccionada = asignacionSeleccionada;
	}

	public List<Asignacion> getAsignacionesTotal() {
		return asignacionesTotal;
	}

	public void setAsignacionesTotal(List<Asignacion> asignacionesTotal) {
		this.asignacionesTotal = asignacionesTotal;
	}

	private List<MovimientoTarjeta> movimientosTarjeta;
	
	private List<MovimientoTarjetaExpediente> movimientosTarjetaExp;
	
	private DatosTarjetaCliente datosTarjetaCliente;

	public ConsultarMovimientosModel() {
 		tipoBusqueda = TipoBusqueda.NUM_TARJETA.getId();	
		
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");
		
		datosTarjetaCliente = new DatosTarjetaCliente();
		asignacionesTotal = new ArrayList<>();
	}

	public void limpiarMovimientosTarjeta() {
		if (movimientosTarjeta != null)
			movimientosTarjeta.clear();
	}
	
	public void limpiarMovimientosTarjetaExp() {
		if (movimientosTarjetaExp != null)
			movimientosTarjetaExp.clear();
	}

 
	

 

	public String tipoMontoLetras(String tipoMonto) {
		return TipoMontoMC.enLetras(tipoMonto);
	}
	
	public String operacionMovLetras(String operacion) {
		return OperacionMovimientoMC.enLetras(operacion);
	}

	public String tipoTarjetaLetras(String tipoTarjeta) {
		return TipoTarjetaMC.enLetras(tipoTarjeta);
	}

	public String tipoMonedaLetras(String tipoMoneda) {
		return TipoMoneda.decripcionTipoMoneda(tipoMoneda);
	}

	public boolean existenMovimientosTarjeta() {
		return (movimientosTarjeta != null && movimientosTarjeta.size() > 0) ? true
				: false;
	}
	
	public boolean existenMovimientosTarjetaExp() {
		return (movimientosTarjetaExp != null && movimientosTarjetaExp.size() > 0) ? true
				: false;
	}

 
 
	
	public int obtenerBusquedaLength() {

		return TipoBusqueda.obtenerLength(tipoBusqueda);
	}

	public String obtenerBusquedaValidatorMessage() {
		return "El " + TipoBusqueda.tipoBusquedaLetras(tipoBusqueda)
				+ " debe  tener " + TipoBusqueda.obtenerLength(tipoBusqueda)
				+ " dígitos";
	}
	public String obtenerBusquedaRequiredMessage() {
		return "Ingrese un número de "
				+ TipoBusqueda.tipoBusquedaLetras(tipoBusqueda);
	}
	
 

	public void inicializarFormulario() {
		datosTarjetaCliente = new DatosTarjetaCliente();
		busquedaRealizada = false;
		numeroTarjeta = null;
		tipoBusqueda = null;	
		movimientosTarjetaExp = null;
		
	}
	
	public void inicializarGrilla() {
		datosTarjetaCliente = new DatosTarjetaCliente();
		busquedaRealizada = false;		
		movimientosTarjetaExp = null;
		
	}

 
	
	
}