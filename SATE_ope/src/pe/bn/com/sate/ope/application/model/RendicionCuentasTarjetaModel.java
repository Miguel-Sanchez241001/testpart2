package pe.bn.com.sate.ope.application.model;

 
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.CuentaTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;
 

 @Data
public class RendicionCuentasTarjetaModel {
	
	
	private String numeroDocumento;
	private List<TipoBusqueda> listaTipoBusqueda;
	private String tipoBusqueda;	
	private boolean busquedaRealizada;	
	private List<String> listaTipoBusquedaPor;
	private String tipoBusquedaPor;
	private DatosTarjetaCliente datosTarjetaCliente;
	
	private List<CuentaTarjeta> listaCuentasTarjeta;

	private  Tarjeta   tarjetaSeleccionada;
	public RendicionCuentasTarjetaModel() {
 		tipoBusqueda = TipoBusqueda.NUM_TARJETA.getId();	
		listaTipoBusquedaPor = new ArrayList<>();
        listaTipoBusquedaPor.add("Por Documento");
        listaTipoBusquedaPor.add("Por Tarjeta");		
		datosTarjetaCliente = new DatosTarjetaCliente();
		
 	}
	
	public void inicializarFormulario() {
		datosTarjetaCliente = new DatosTarjetaCliente();
		busquedaRealizada = false;
		numeroDocumento = null;
		tipoBusqueda = null;
		listaCuentasTarjeta = null;
 		
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
}
