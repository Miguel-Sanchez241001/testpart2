package pe.bn.com.sate.ope.application.model;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;
import pe.bn.com.sate.ope.transversal.dto.sate.Tarjeta;
import pe.bn.com.sate.ope.transversal.dto.tablas.Agencia;
import pe.bn.com.sate.ope.transversal.dto.tablas.Ubigeo;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.EstadoCivil;
import pe.bn.com.sate.ope.transversal.util.enums.OperadorMovil;
import pe.bn.com.sate.ope.transversal.util.enums.TipoDocumento;
import pe.bn.com.sate.ope.transversal.util.enums.TipoMoneda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Getter
@Setter
@ToString
public class SolicitarTarjetaModel {
	/* Variables del formulario datos del cliente */
	private String tipoDocumentoSeleccionado;
	private String numDocumentoSeleccionado;
	private List<TipoDocumento> listaTipoDocumento;
	private List<OperadorMovil> listaOperadorMovil;
	private Cliente clienteSeleccionado;
	private List<TipoMoneda> listaTipoMoneda;

	/* Variables del formulario datos de la tarjeta */
	private List<TipoTarjetaNegocio> listaTipoTarjetaNegocio;
	private List<TipoTarjeta> listaTipoTarjeta;
	private List<Ubigeo> departamentos;
	private List<Ubigeo> provincias;
	private List<Ubigeo> distritos;
	private List<Agencia> agenciasBN;
	private Agencia agenciaSeleccionada;
	private boolean esEntregaBN;

	private boolean esEntregaUE;
	private boolean esEntregaReferencia;
	private Tarjeta tarjeta;
	private TipoTarjetaNegocio tipoTarjetaNegocioSeleccionada;
	private TipoTarjeta tipoTarjetaSeleccionada;
	private int pasoActual;
	private List<EstadoCivil> listaEstadoCivil;
	private boolean personaExiste;
	
	public SolicitarTarjetaModel() {
		inicializarFormulario();
	}

	public boolean validarDNI() {
		if (clienteSeleccionado != null
				&& clienteSeleccionado.getNroDocumento() != null
				&& clienteSeleccionado.getNroDocumento().equals(
						numDocumentoSeleccionado)) {
			return true;
		} else {
			return false;
		}
	}

	public int obtenerDocumentoLength() {
		return TipoDocumento.obtenerLength(tipoDocumentoSeleccionado);
	}

	public String obtenerDocumentoValidatorMessage() {
		return "El "
				+ TipoDocumento
						.tipoDocumentoBducLetras(tipoDocumentoSeleccionado)
				+ " debe tener "
				+ TipoDocumento.obtenerLength(tipoDocumentoSeleccionado)
				+ " dígitos";
	}

	public String obtenerDocumentoRequiredMessage() {
		return "Ingrese un número de "
				+ TipoDocumento
						.tipoDocumentoBducLetras(tipoDocumentoSeleccionado);
	}









	

	







	
















	



	public void inicializarFormulario() {
		tarjeta = new Tarjeta();
		tarjeta.setTipoMoneda(TipoMoneda.MONEDA_SOLES.getId());
		tarjeta.setEntregaUbicacion("4");
		
		clienteSeleccionado = new Cliente();
		listaOperadorMovil = Arrays.asList(OperadorMovil.values());
		listaTipoDocumento = Arrays.asList(TipoDocumento.values());
		listaTipoMoneda = Arrays.asList(TipoMoneda.values());
		personaExiste = true;
		listaEstadoCivil = Arrays.asList(EstadoCivil.values());
		tipoDocumentoSeleccionado = TipoDocumento.DNI.getCodigoBduc();
		numDocumentoSeleccionado = null;

		esEntregaBN = true;
		esEntregaUE = false;
		esEntregaReferencia = false;
		provincias = null;
		distritos = null;
		agenciasBN = null;
		agenciaSeleccionada = null;
		tipoTarjetaNegocioSeleccionada = null;
		pasoActual = 0;
	}
	public void reiniciarDatosCliente() {
		clienteSeleccionado = new Cliente();
		listaOperadorMovil = Arrays.asList(OperadorMovil.values());
		listaTipoDocumento = Arrays.asList(TipoDocumento.values());
		listaTipoMoneda = Arrays.asList(TipoMoneda.values());
		personaExiste = true;
		listaEstadoCivil = Arrays.asList(EstadoCivil.values());
 		numDocumentoSeleccionado = null;
	}
	public void generarUbigeoPorUbicacion() {

		if (tarjeta.getEntregaUbicacion().equals(
				ConstantesGenerales.ENTREGA_AGENCIA_BN)) {
			tarjeta.setEntregaUbigeo(tarjeta.getEntregaDepartamento().concat(tarjeta.getEntregaProvincia().concat(tarjeta.getEntregaDistrito())));
			tarjeta.setEntregaAgenciaBN(agenciaSeleccionada.getCodAgencia());
			tarjeta.setNombreAgenciaBN(agenciaSeleccionada.getDescripcion());
			tarjeta.setEntregaDireccion(agenciaSeleccionada.getDireccion());
		}
	}












	public boolean esTipoDocumentoDNI() {
		if (tipoDocumentoSeleccionado != null && tipoDocumentoSeleccionado.equals(TipoDocumento.DNI.getCodigoBduc()))
			return true;
		else
			return false;
	}
	
	public boolean esTipoDocumento() {
		if (tipoDocumentoSeleccionado != null )
			return true;
		else
			return false;
	}


}
