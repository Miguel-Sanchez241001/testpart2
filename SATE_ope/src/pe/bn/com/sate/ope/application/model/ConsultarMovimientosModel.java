package pe.bn.com.sate.ope.application.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.util.enums.OperacionMovimientoMC;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMontoMC;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaMC;

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
		//listaTipoBusqueda = Arrays.asList(TipoBusqueda.values());
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

	public List<MovimientoTarjeta> getMovimientosTarjeta() {
		return movimientosTarjeta;
	}

	public void setMovimientosTarjeta(List<MovimientoTarjeta> movimientosTarjeta) {
		this.movimientosTarjeta = movimientosTarjeta;
	}
	
	
	public List<MovimientoTarjetaExpediente> getMovimientosTarjetaExp() {
		return movimientosTarjetaExp;
	}

	public void setMovimientosTarjetaExp(List<MovimientoTarjetaExpediente> movimientosTarjetaExp) {
		this.movimientosTarjetaExp = movimientosTarjetaExp;
	}
	
	

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
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

	public List<TipoBusqueda> getListaTipoBusqueda() {
		return listaTipoBusqueda;
	}

	public void setListaTipoBusqueda(List<TipoBusqueda> listaTipoBusqueda) {
		this.listaTipoBusqueda = listaTipoBusqueda;
	}

	public String getTipoBusqueda() {
		return tipoBusqueda;
	}

	public void setTipoBusqueda(String tipoBusqueda) {
		this.tipoBusqueda = tipoBusqueda;
	}

	public DatosTarjetaCliente getDatosTarjetaCliente() {
		return datosTarjetaCliente;
	}

	public void setDatosTarjetaCliente(DatosTarjetaCliente datosTarjetaCliente) {
		this.datosTarjetaCliente = datosTarjetaCliente;
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
	
	
	
	public boolean isBusquedaRealizada() {
		return busquedaRealizada;
	}

	public void setBusquedaRealizada(boolean busquedaRealizada) {
		this.busquedaRealizada = busquedaRealizada;
	}

	public void inicializarFormulario() {
		datosTarjetaCliente = new DatosTarjetaCliente();
		busquedaRealizada = false;
		numeroTarjeta = null;
		tipoBusqueda = null;	
		movimientosTarjetaExp = null;
		
	}

	public List<String> getListaTipoBusquedaPor() {
		return listaTipoBusquedaPor;
	}

	public void setListaTipoBusquedaPor(List<String> listaTipoBusquedaPor) {
		this.listaTipoBusquedaPor = listaTipoBusquedaPor;
	}

	public String getTipoBusquedaPor() {
		return tipoBusquedaPor;
	}

	public void setTipoBusquedaPor(String tipoBusquedaPor) {
		this.tipoBusquedaPor = tipoBusquedaPor;
	}
	

	
	
}