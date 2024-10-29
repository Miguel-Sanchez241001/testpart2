package pe.bn.com.sate.ope.application.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pe.bn.com.sate.ope.transversal.util.NumeroALetras;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.CodDocumentoWebservice;
import pe.bn.com.sate.ope.transversal.util.enums.DisposicionEfectivo;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusquedaPN;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

public class ConsultarClienteModel {

	private List<TipoBusquedaPN> listaTipoBusqueda;
	private String tipoBusqueda;
	private String numDocumento;



	public ConsultarClienteModel() {
		listaTipoBusqueda = Arrays.asList(TipoBusquedaPN.values());
		tipoBusqueda = "N";
	}
	
	

	public List<TipoBusquedaPN> getListaTipoBusqueda() {
		return listaTipoBusqueda;
	}

	public void setListaTipoBusqueda(List<TipoBusquedaPN> listaTipoBusqueda) {
		this.listaTipoBusqueda = listaTipoBusqueda;
	}

	public String getTipoBusqueda() {
		return tipoBusqueda;
	}

	public void setTipoBusqueda(String tipoBusqueda) {
		this.tipoBusqueda = tipoBusqueda;
	}

	public String getNumDocumento() {
		return numDocumento;
	}

	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}

	

	public String descripcionTipoDocumento(String codigo) {
		return CodDocumentoWebservice.descripcionCodDocumentoWebservice(codigo);
	}

	public String descripcionTipoTarjeta(String codigo, String diseno) {
		return TipoTarjetaNegocio.descripcionTipotarjeta(codigo, diseno);
	}

	public String descripcionEstadoTarjeta(String codigo) {
		return TipoEstadoTarjeta.descripcionTipoEstadoTarjeta(codigo);
	}

	public String descripcionTipoMoneda(String id) {
		return TipoMoneda.decripcionTipoMoneda(id);
	}

	public String descripcionDisposicionEfectivo(String codigo) {
		return DisposicionEfectivo.descripcionDisposicionEfectivo(codigo);
	}

	public String descripcionNumeroTarjeta(String numTarjeta) {
		return UsefulWebApplication.formatoNumTarjeta(numTarjeta);
	}

	

	public String formatoNroAutorizacion(String nroAutorizacion) {
		return (nroAutorizacion == null || nroAutorizacion.isEmpty()) ? ConstantesGenerales.SIMBOLO_VACIO
				: nroAutorizacion;
	}

	public String formatoFechaAutorizacion(Date fechaAutorizacion) {
		return fechaAutorizacion == null ? ConstantesGenerales.SIMBOLO_VACIO
				: new SimpleDateFormat("dd/MM/yyyy").format(fechaAutorizacion);
	}

	public String formatoUsuarioCreador(String usuarioCreador) {
		return (usuarioCreador == null || usuarioCreador.isEmpty()) ? ConstantesGenerales.SIMBOLO_VACIO
				: usuarioCreador;
	}

	public String formatoFecha(Date fecha) {
		return (fecha == null) ? ConstantesGenerales.SIMBOLO_VACIO
				: new SimpleDateFormat("dd/MM/yyyy").format(fecha);
	}

	public String formatoMonto(Double monto) {
		return (monto == null) ? ConstantesGenerales.SIMBOLO_VACIO
				: NumeroALetras.numeroFormateado(monto);
	}

	public void inicializarFormulario() {		
		numDocumento = null;
		tipoBusqueda = null;
	}

	public int obtenerBusquedaLength() {

		return TipoBusquedaPN.obtenerLength(tipoBusqueda);
	}

	public String obtenerBusquedaValidatorMessage() {
		return "El " + TipoBusquedaPN.tipoBusquedaLetras(tipoBusqueda)
				+ " debe  tener " + TipoBusquedaPN.obtenerLength(tipoBusqueda)
				+ " dígitos";
	}

	public String obtenerBusquedaRequiredMessage() {
		return "Ingrese un número de "
				+ TipoBusquedaPN.tipoBusquedaLetras(tipoBusqueda);
	}
	
	
}
