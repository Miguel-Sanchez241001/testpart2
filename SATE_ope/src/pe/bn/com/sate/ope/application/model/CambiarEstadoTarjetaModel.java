package pe.bn.com.sate.ope.application.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.EstadoTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Agencia;
import pe.bn.com.sate.ope.transversal.dto.tablas.Ubigeo;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.CodDocumentoWebservice;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoCuenta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.MotivosBloqueoWS;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Getter
@Setter
@ToString
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
		datosTarjetaCliente = new DatosTarjetaCliente();
		listaTipoBusquedaPor = new ArrayList<>();
		listaTipoBusquedaPor.add("Por Documento");
		listaTipoBusquedaPor.add("Por Tarjeta");
		tipoBloqueoSeleccionado = "T";

	}

	public String descripcionTipoDocumento(String codigo) {
		return CodDocumentoWebservice.descripcionCodDocumentoWebservice(codigo);
	}

	


	public String  descricionTarSinCeros(String numtar) {
		return StringsUtils.quitarCeroIzquierdaString(numtar);
	}
	
	public String descripcionEstadoTarjeta(String codigo) {
		return TipoEstadoTarjeta.descripcionTipoEstadoTarjeta(codigo);
	}
	public String descripcionTipoTarjeta(String codigo, String diseno) {
		return TipoTarjetaNegocio.descripcionTipotarjeta(codigo, diseno);
	}
	public String descripcionTipoTarjetaWS(String codigo) {
		return TipoTarjeta.descripcionTipotarjeta(codigo);
	}
	public String descripcionNcombreCompleto() {
		Cliente cleinteTemp = datosTarjetaCliente.getCliente();
		return cleinteTemp.getNombres()+ " " +  cleinteTemp.getApCompleto();
	}
	
	
	
	public String descripcionMotivoBloqueoWS(String codigo) {
		return ConstantesGenerales.obtenerDescripcionBloqueo(codigo);
	}

	public String descripcionMotivoBloqueotarjeta(String codigo) {
		return MotivosBloqueoTarjeta.descripcionMotivoBloqueoTarjeta(codigo);
	}

	public String descripcionNumeroTarjeta(String numTarjeta) {
		return UsefulWebApplication.formatoNumTarjeta(numTarjeta);
	}
	
	public void iniciarEstadoTarjeta() {
		estadoTarjeta = new EstadoTarjeta();
 
			estadoTarjeta.setFechaRegistro(new Date());
			estadoTarjeta.setEstado(TipoEstadoTarjeta.TARJETA_BLOQUEADA.getCod());
			estadoTarjeta.setMotivo("");
			estadoTarjeta.setUsuarioRegistro(UsefulWebApplication.obtenerUsuario().getUsername());
			estadoTarjeta.setIdTarjeta(datosTarjetaCliente.getTarjeta().getId());
		 
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
		tipoBloqueoSeleccionado = "T";

	}

	public int obtenerBusquedaLength() {

		return TipoBusqueda.obtenerLength(tipoBusqueda);
	}

	public String obtenerBusquedaValidatorMessage() {
		return "El " + TipoBusqueda.tipoBusquedaLetras(tipoBusqueda) + " debe  tener "
				+ TipoBusqueda.obtenerLength(tipoBusqueda) + " dígitos";
	}

	public String obtenerBusquedaRequiredMessage() {
		return "Ingrese un número de " + TipoBusqueda.tipoBusquedaLetras(tipoBusqueda);
	}

	public String valorBoton() {

		return "Bloquear";

	}

	public String obtenerMotivo(String id, String descripcion) {
		return (id + "-" + descripcion);
	}

	public String obtenerEstadoCuenta(String id, String descripcion) {
		return (id + "-" + descripcion);
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

}
