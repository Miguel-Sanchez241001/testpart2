package pe.bn.com.sate.ope.application.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.util.NumeroALetras;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.CodDocumentoWebservice;
import pe.bn.com.sate.ope.transversal.util.enums.DisposicionEfectivo;
import pe.bn.com.sate.ope.transversal.util.enums.OperadorMovil;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

public class BuscarTarjetaModel {

	private List<TipoBusqueda> listaTipoBusqueda;
	private String tipoBusqueda;
	private String numDocumento;

	private List<OperadorMovil> listaOperadorMovil;
	private DatosTarjetaCliente datosTarjetaCliente;

	private Tarjeta tarjetaSeleccionada;

	private boolean busquedaRealizada;

	private boolean tipoOperacionCancelar;

	private boolean tipoOperacionActualizar;

	private EstadoTarjeta estadoTarjeta;
	
	private Asignacion asignacionSeleccionada;
	
	private List<Asignacion> asignacionesTotal;
	
	private Tarjeta tarjeta;
	
	private TipoTarjeta tipoTarjetaSeleccionada;
	
	private TipoTarjetaNegocio tipoTarjetaNegocioSeleccionada;
	
	private List<TipoTarjetaNegocio> listaTipoTarjetaNegocio;	
	private List<TipoTarjeta> listaTipoTarjeta;
	
	
	private List<String> listaTipoBusquedaPor;
	private String tipoBusquedaPor;
		

	public BuscarTarjetaModel() {
		//listaTipoBusqueda = Arrays.asList(TipoBusqueda.values());
		listaTipoTarjeta = Arrays.asList(TipoTarjeta.values());
		
		//listabuscarTarjetaTD = Arrays.asList(BuscarTarjetaTD.values());
		tipoTarjetaNegocioSeleccionada= null;
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");
		datosTarjetaCliente = new DatosTarjetaCliente();
		listaOperadorMovil = Arrays.asList(OperadorMovil.values());
		tipoBusqueda = "N";
	}
	
	
	
	public List<TipoTarjeta> getListaTipoTarjeta() {
		return listaTipoTarjeta;
	}

	public void setListaTipoTarjeta(List<TipoTarjeta> listaTipoTarjeta) {
		this.listaTipoTarjeta = listaTipoTarjeta;
	}
	
	public Asignacion getAsignacionSeleccionada() {
		return asignacionSeleccionada;
	}

	public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) {
		this.asignacionSeleccionada = asignacionSeleccionada;
	}

	public EstadoTarjeta getEstadoTarjeta() {
		return estadoTarjeta;
	}

	public void setEstadoTarjeta(EstadoTarjeta estadoTarjeta) {
		this.estadoTarjeta = estadoTarjeta;
	}

	public boolean isTipoOperacionCancelar() {
		return tipoOperacionCancelar;
	}

	public void setTipoOperacionCancelar(boolean tipoOperacionCancelar) {
		this.tipoOperacionCancelar = tipoOperacionCancelar;
	}

	public boolean isTipoOperacionActualizar() {
		return tipoOperacionActualizar;
	}

	public void setTipoOperacionActualizar(boolean tipoOperacionActualizar) {
		this.tipoOperacionActualizar = tipoOperacionActualizar;
	}

	public Tarjeta getTarjetaSeleccionada() {
		return tarjetaSeleccionada;
	}

	public void setTarjetaSeleccionada(Tarjeta tarjetaSeleccionada) {
		this.tarjetaSeleccionada = tarjetaSeleccionada;
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

	public String getNumDocumento() {
		return numDocumento;
	}

	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}

	public DatosTarjetaCliente getDatosTarjetaCliente() {
		return datosTarjetaCliente;
	}

	public void setDatosTarjetaCliente(DatosTarjetaCliente datosTarjetaCliente) {
		this.datosTarjetaCliente = datosTarjetaCliente;
	}

	public boolean isBusquedaRealizada() {
		return busquedaRealizada;
	}

	public void setBusquedaRealizada(boolean busquedaRealizada) {
		this.busquedaRealizada = busquedaRealizada;
	}

	public List<OperadorMovil> getListaOperadorMovil() {
		return listaOperadorMovil;
	}

	public void setListaOperadorMovil(List<OperadorMovil> listaOperadorMovil) {
		this.listaOperadorMovil = listaOperadorMovil;
	}

	public String descripcionTipoDocumento(String codigo) {
		return CodDocumentoWebservice.descripcionCodDocumentoWebservice(codigo);
	}

	public String descripcionTipoTarjeta(String codigo, String diseno) {
		return TipoTarjetaNegocio.descripcionTipotarjeta(codigo, diseno);
	}

	public String descripcionTipoTarjetaBC(String tipo) {
		return TipoTarjeta.descripcionTipotarjeta(tipo);
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
		String tarjeta = StringsUtils.quitarCeroIzquierdaString(numTarjeta);
 		return UsefulWebApplication.formatoNumTarjeta(tarjeta);
	}

	public void iniciarEstadoTarjeta() {
		estadoTarjeta = new EstadoTarjeta();
		estadoTarjeta.setFechaRegistro(new Date());
		estadoTarjeta.setEstado(TipoEstadoTarjeta.SOLICITUD_TARJETA_CANCELADA.getCod());
		estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
		estadoTarjeta.setIdTarjeta(datosTarjetaCliente.getTarjeta().getId());

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
		datosTarjetaCliente = new DatosTarjetaCliente();
		numDocumento = null;
		tipoBusqueda = null;
		busquedaRealizada = false;
		estadoTarjeta = null;
		tarjetaSeleccionada = null;
		tipoOperacionCancelar = false;
		tipoOperacionActualizar = false;
		tipoTarjetaNegocioSeleccionada = null;
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
	
	public List<Asignacion> getAsignacionesTotal() {
		return asignacionesTotal;
	}

	public void setAsignacionesTotal(List<Asignacion> asignacionesTotal) {
		this.asignacionesTotal = asignacionesTotal;
	}

	public Tarjeta getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(Tarjeta tarjeta) {
		this.tarjeta = tarjeta;
	}

	public TipoTarjeta getTipoTarjetaSeleccionada() {
		return tipoTarjetaSeleccionada;
	}

	public void setTipoTarjetaSeleccionada(TipoTarjeta tipoTarjetaSeleccionada) {
		this.tipoTarjetaSeleccionada = tipoTarjetaSeleccionada;
	}

	public TipoTarjetaNegocio getTipoTarjetaNegocioSeleccionada() {
		return tipoTarjetaNegocioSeleccionada;
	}

	public void setTipoTarjetaNegocioSeleccionada(
			TipoTarjetaNegocio tipoTarjetaNegocioSeleccionada) {
		this.tipoTarjetaNegocioSeleccionada = tipoTarjetaNegocioSeleccionada;
	}
	
	public List<TipoTarjetaNegocio> getListaTipoTarjetaNegocio() {
		return listaTipoTarjetaNegocio;
	}

	public void setListaTipoTarjetaNegocio(List<TipoTarjetaNegocio> listaTipoTarjeta) {
		this.listaTipoTarjetaNegocio = listaTipoTarjeta;
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
