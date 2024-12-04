package pe.bn.com.sate.ope.application.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoWS;
import pe.bn.com.sate.ope.transversal.util.enums.OperadorMovil;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Getter
@Setter
@ToString
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
	
	private String estadoBloqueoWS;
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
		listaTipoTarjeta = Arrays.asList(TipoTarjeta.values());
		tipoTarjetaNegocioSeleccionada= null;
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");
		datosTarjetaCliente = new DatosTarjetaCliente();
		listaOperadorMovil = Arrays.asList(OperadorMovil.values());
		tipoBusqueda = "N";
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
	public String descripcionTipoTarjetaGasto(String tipo) {
		return TipoTarjetaNegocio.descripcionTipotarjeta(tipo);
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
		String mensaje = "";
		if(tipoBusqueda.equals("N")){
			mensaje = "Ingrese "
					+ TipoBusqueda.tipoBusquedaLetras(tipoBusqueda);			
		}else{
			mensaje = "Ingrese un número de "
					+ TipoBusqueda.tipoBusquedaLetras(tipoBusqueda);
		}
		
		return mensaje;
	}
 
 
 

	public String descripcionMotivoBloqueoWS(String codigo) {
		return MotivosBloqueoWS.descripcionMotivoBloqueoWS(codigo);
	}

	

	

	
	
}
