package pe.bn.com.sate.ope.application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Agencia;
import pe.bn.com.sate.ope.transversal.dto.tablas.Ubigeo;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.enums.CodDocumentoWebservice;
import pe.bn.com.sate.ope.transversal.util.enums.EstadoCivil;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoCuenta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoWS;

import pe.bn.com.sate.ope.transversal.util.enums.OperadorMovil;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoDocumento;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

public class CambiarEstadoTarjetaModel {

	private List<TipoBusqueda> listaTipoBusqueda;
	private String tipoBusqueda;
	private String numDocumento;

	private DatosTarjetaCliente datosTarjetaCliente;

	private Tarjeta tarjetaSeleccionada;

	private boolean busquedaRealizada;

	private boolean busquedaBloqueoTarjeta;

	private List<MotivosBloqueoTarjeta> motivosBloqueoTarjetas;

	private List<MotivosBloqueoCuenta> motivosBloqueoCuenta;

	private EstadoTarjeta estadoTarjeta;

	private String motivoSeleccionado;

	private String estadoCuentaSeleccionado;

	private String tipoBloqueoSeleccionado;

	private List<String> listaTipoBusquedaPor;
	private String tipoBusquedaPor;
	private List<Asignacion> asignacionesTotal;
	private Asignacion asignacionSeleccionada;
	
	
	/**/
	private List<Ubigeo> departamentos;
	private List<Ubigeo> provincias;
	private List<Ubigeo> distritos;
	private List<Agencia> agenciasBN;
	private Agencia agenciaSeleccionada;
	private boolean esEntregaBN;
	private Tarjeta tarjeta;
	private boolean esEntregaUE;
	private boolean esEntregaReferencia;
	
	private String estadoBloqueoWS;
	
	public CambiarEstadoTarjetaModel() {
		//listaTipoBusqueda = Arrays.asList(TipoBusqueda.values());
		datosTarjetaCliente = new DatosTarjetaCliente();
		// motivosBloqueoTarjetas =
		// Arrays.asList(MotivosBloqueoTarjeta.values());
		
		tipoBusqueda = TipoBusqueda.NUM_TARJETA.getId();	
		
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");
		
//		tipoBusqueda = "N";
		tipoBloqueoSeleccionado = "T";
		asignacionesTotal = new ArrayList<>();
	}

	public String getMotivoSeleccionado() {
		return motivoSeleccionado;
	}

	public void setMotivoSeleccionado(String motivoSeleccionado) {
		this.motivoSeleccionado = motivoSeleccionado;
	}

	public EstadoTarjeta getEstadoTarjeta() {
		return estadoTarjeta;
	}

	public void setEstadoTarjeta(EstadoTarjeta estadoTarjeta) {
		this.estadoTarjeta = estadoTarjeta;
	}

	public List<MotivosBloqueoTarjeta> getMotivosBloqueoTarjetas() {
		return motivosBloqueoTarjetas;
	}

	public void setMotivosBloqueoTarjetas(
			List<MotivosBloqueoTarjeta> motivosBloqueoTarjetas) {
		this.motivosBloqueoTarjetas = motivosBloqueoTarjetas;
	}

	public boolean isBusquedaBloqueoTarjeta() {
		return busquedaBloqueoTarjeta;
	}

	public void setBusquedaBloqueoTarjeta(boolean busquedaBloqueoTarjeta) {
		this.busquedaBloqueoTarjeta = busquedaBloqueoTarjeta;
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

	public Tarjeta getTarjetaSeleccionada() {
		return tarjetaSeleccionada;
	}

	public void setTarjetaSeleccionada(Tarjeta tarjetaSeleccionada) {
		this.tarjetaSeleccionada = tarjetaSeleccionada;
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
	
	
	
	public String descripcionMotivoBloqueoWS(String codigo) {
		return MotivosBloqueoWS.descripcionMotivoBloqueoWS(codigo);
	}
	
	

	public String descripcionMotivoBloqueotarjeta(String codigo) {
		return MotivosBloqueoTarjeta.descripcionMotivoBloqueoTarjeta(codigo);
	}

	public String descripcionNumeroTarjeta(String numTarjeta) {
		return UsefulWebApplication.formatoNumTarjeta(numTarjeta);
	}

	public void iniciarEstadoTarjeta() {
		estadoTarjeta = new EstadoTarjeta();
		if (busquedaBloqueoTarjeta) {
			estadoTarjeta.setFechaRegistro(new Date());
			estadoTarjeta
					.setEstado(TipoEstadoTarjeta.TARJETA_ACTIVADA.getCod());
			estadoTarjeta.setMotivo(""); //motivoSeleccionado
			estadoTarjeta.setUsuarioRegistro(UsefulWebApplication
					.obtenerUsuario().getUsername());
			estadoTarjeta
					.setIdTarjeta(datosTarjetaCliente.getTarjeta().getId());
		} else {
			estadoTarjeta.setFechaRegistro(new Date());
			estadoTarjeta.setEstado(TipoEstadoTarjeta.TARJETA_BLOQUEADA
					.getCod());
			estadoTarjeta.setMotivo(""); //motivoSeleccionado
			estadoTarjeta.setUsuarioRegistro(UsefulWebApplication
					.obtenerUsuario().getUsername());
			estadoTarjeta
					.setIdTarjeta(datosTarjetaCliente.getTarjeta().getId());
		}
	}

	public void inicializarFormulario() {
		datosTarjetaCliente = new DatosTarjetaCliente();
		motivoSeleccionado = null;
		numDocumento = null;
		tipoBusqueda = null;
		busquedaRealizada = false;
		busquedaBloqueoTarjeta = false;
		estadoTarjeta = null;
		estadoCuentaSeleccionado = null;
		tipoBusqueda = "N";
		tipoBloqueoSeleccionado = "T";
		
	
		
		tipoBusqueda = TipoBusqueda.NUM_TARJETA.getId();	
		
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");
		
//		tipoBusqueda = "N";
		tipoBloqueoSeleccionado = "T";
		asignacionesTotal = new ArrayList<>();
		
		

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

	public List<MotivosBloqueoCuenta> getMotivosBloqueoCuenta() {
		return motivosBloqueoCuenta;
	}

	public void setMotivosBloqueoCuenta(
			List<MotivosBloqueoCuenta> motivosBloqueoCuenta) {
		this.motivosBloqueoCuenta = motivosBloqueoCuenta;
	}

	public String getEstadoCuentaSeleccionado() {
		return estadoCuentaSeleccionado;
	}

	public void setEstadoCuentaSeleccionado(String estadoCuentaSeleccionado) {
		this.estadoCuentaSeleccionado = estadoCuentaSeleccionado;
	}

	public String getTipoBloqueoSeleccionado() {
		return tipoBloqueoSeleccionado;
	}

	public void setTipoBloqueoSeleccionado(String tipoBloqueoSeleccionado) {
		this.tipoBloqueoSeleccionado = tipoBloqueoSeleccionado;
	}

	public String valorBoton() {

//		if (tipoBloqueoSeleccionado.equals("T"))
//			if (datosTarjetaCliente.getTarjeta().getEstado()
//					.equals(TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod()))
//				return "Activar";
//			else
//				return "Bloquear";
//		else if (datosTarjetaCliente.getTarjeta().getEstadoCuenta()
//				.equals(MotivosBloqueoCuenta.TEMPORAL.getId()))
//			return "Activar";
//		else
			return "Bloquear";

	}
	
	public String obtenerMotivo(String id,String descripcion){
		return (id+"-"+descripcion);
	}
	
	public String obtenerEstadoCuenta(String id,String descripcion){
		return (id+"-"+descripcion);
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

	public List<Asignacion> getAsignacionesTotal() {
		return asignacionesTotal;
	}

	public void setAsignacionesTotal(List<Asignacion> asignacionesTotal) {
		this.asignacionesTotal = asignacionesTotal;
	}

	public Asignacion getAsignacionSeleccionada() {
		return asignacionSeleccionada;
	}

	public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) {
		this.asignacionSeleccionada = asignacionSeleccionada;
	}

	public List<Ubigeo> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Ubigeo> departamentos) {
		this.departamentos = departamentos;
	}

	public List<Ubigeo> getProvincias() {
		return provincias;
	}

	public void setProvincias(List<Ubigeo> provincias) {
		this.provincias = provincias;
	}

	public List<Ubigeo> getDistritos() {
		return distritos;
	}

	public void setDistritos(List<Ubigeo> distritos) {
		this.distritos = distritos;
	}

	public List<Agencia> getAgenciasBN() {
		return agenciasBN;
	}

	public void setAgenciasBN(List<Agencia> agenciasBN) {
		this.agenciasBN = agenciasBN;
	}

	public Agencia getAgenciaSeleccionada() {
		return agenciaSeleccionada;
	}

	public void setAgenciaSeleccionada(Agencia agenciaSeleccionada) {
		this.agenciaSeleccionada = agenciaSeleccionada;
	}

	public boolean isEsEntregaBN() {
		return esEntregaBN;
	}

	public void setEsEntregaBN(boolean esEntregaBN) {
		this.esEntregaBN = esEntregaBN;
	}

	public Tarjeta getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(Tarjeta tarjeta) {
		this.tarjeta = tarjeta;
	}

	public boolean isEsEntregaUE() {
		return esEntregaUE;
	}

	public void setEsEntregaUE(boolean esEntregaUE) {
		this.esEntregaUE = esEntregaUE;
	}

	public boolean isEsEntregaReferencia() {
		return esEntregaReferencia;
	}

	public void setEsEntregaReferencia(boolean esEntregaReferencia) {
		this.esEntregaReferencia = esEntregaReferencia;
	}
	
	public void inicializarFormularioEntrega() {
		tarjeta = new Tarjeta();
		tarjeta.setTipoMoneda(TipoMoneda.MONEDA_SOLES.getId());
		tarjeta.setEntregaUbicacion("4");
		
	

		esEntregaBN = true;
		esEntregaUE = false;
		esEntregaReferencia = false;
		provincias = null;
		distritos = null;
		agenciasBN = null;
		agenciaSeleccionada = null;
		
	}

	public String getEstadoBloqueoWS() {
		return estadoBloqueoWS;
	}

	public void setEstadoBloqueoWS(String estadoBloqueoWS) {
		this.estadoBloqueoWS = estadoBloqueoWS;
	}
	
	
	
	
}
