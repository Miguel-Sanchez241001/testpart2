package pe.bn.com.sate.ope.application.model;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.dto.sate.Rol;
import pe.bn.com.sate.ope.transversal.dto.sate.Usuario;
import pe.bn.com.sate.ope.transversal.util.enums.OperadorMovil;
import pe.bn.com.sate.ope.transversal.util.enums.TipoDocumento;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstado;

@Getter
@Setter
@ToString
public class ModificarUsuarioModel {

	private String tipoDocumentoSeleccionado;
	private String numDocumentoSeleccionado;
	private List<TipoDocumento> listaTipoDocumento;
	private List<OperadorMovil> listaOperadorMovil;
	private Usuario usuarioSeleccionado;

	private List<Rol> roles;
	private Long rolSeleccionado;
	private List<TipoEstado> estadosUsuario;
	private String estadoSeleccionado;

	private boolean personaExiste;

	public ModificarUsuarioModel() {
		usuarioSeleccionado = new Usuario();
		listaOperadorMovil = Arrays.asList(OperadorMovil.values());
		listaTipoDocumento = Arrays.asList(TipoDocumento.values());
		estadosUsuario = Arrays.asList(TipoEstado.values());
		personaExiste = false;
		tipoDocumentoSeleccionado = TipoDocumento.DNI.getCodigoBduc();
	}

	public void limpiarNumeroDocumento() {
		numDocumentoSeleccionado = "";
	}

 

	public void inicializarFormularioUsuario() {
		usuarioSeleccionado = new Usuario();
		tipoDocumentoSeleccionado = null;
		numDocumentoSeleccionado = null;
		rolSeleccionado = null;
		personaExiste = false;
	}

 

 

	public boolean esTipoDocumentoDNI() {
		if (tipoDocumentoSeleccionado != null
				&& tipoDocumentoSeleccionado.equals(TipoDocumento.DNI
						.getCodigoBduc()))
			return true;
		else
			return false;
	}

	public int obtenerDocumentoLength() {
		return TipoDocumento.obtenerLength(tipoDocumentoSeleccionado);
	}

	public String obtenerDocumentoValidatorMessage() {
		return "El "
				+ TipoDocumento
						.tipoDocumentoBducLetras(tipoDocumentoSeleccionado)
				+ " debe  tener "
				+ TipoDocumento.obtenerLength(tipoDocumentoSeleccionado)
				+ " dígitos";
	}

	public String obtenerDocumentoRequiredMessage() {
		return "Ingrese un número de "
				+ TipoDocumento
						.tipoDocumentoBducLetras(tipoDocumentoSeleccionado);
	}

}
