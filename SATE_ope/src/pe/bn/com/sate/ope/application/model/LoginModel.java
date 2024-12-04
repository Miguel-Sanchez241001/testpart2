package pe.bn.com.sate.ope.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.novatronic.captcha.Captcha;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.componentes.Teclado;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoDocumento;

@Getter
@Setter
@ToString
@FacesValidator("loginModelValidator")
public class LoginModel implements Serializable, Validator {

	private static final long serialVersionUID = 1L;

	private String ruc;
	private String tipoDocumentoSeleccionado;
	private String numeroDocumento;

	private List<TipoDocumento> listaTipoDocumento;

	private String usernameDomain;

	private List<String> listaTeclas;
	private Teclado teclado;

	private String captchaTexto;
	private Captcha captcha;

	private String rucRecuperar;
	private String tipoDocumentoRecuperar;
	private String numeroDocumentoRecuperar;
	private String correoRecuperar;

	private String vacio = "";

	public LoginModel() {
		inicializarObjetos();
	}
	
	public void inicializarObjetos(){
		listaTipoDocumento = Arrays.asList(TipoDocumento.values());
		listaTeclas = new ArrayList<String>();

		ruc = "";
		tipoDocumentoSeleccionado = "1";
		numeroDocumento = "";
		captchaTexto = "";

		rucRecuperar = "";
		tipoDocumentoRecuperar = "1";
		numeroDocumentoRecuperar = "";
		correoRecuperar = "";

		teclado = new Teclado();
	}

	public void generarUsernameDomain() {
		usernameDomain = ruc + "-" + tipoDocumentoSeleccionado + "-"
				+ numeroDocumento;
	}

 

	public void setTipoDocumentoSeleccionado(String tipoDocumentoSeleccionado) {
		if (tipoDocumentoSeleccionado.equals("0"))
			this.tipoDocumentoSeleccionado = "";
		else
			this.tipoDocumentoSeleccionado = tipoDocumentoSeleccionado;
		numeroDocumento = "";
	}

	public int obtenerDocumentoLength(String tipoDocumento) {
		return TipoDocumento.obtenerLength(tipoDocumento);
	}

	public String obtenerDocumentoValidatorMessage(String tipoDocumento) {
		return "El " + TipoDocumento.tipoDocumentoBducLetras(tipoDocumento)
				+ " debe  tener " + TipoDocumento.obtenerLength(tipoDocumento)
				+ " dígitos";
	}

	public String obtenerDocumentoRequiredMessage(String tipoDocumento) {
		return "Ingrese un número de "
				+ TipoDocumento.tipoDocumentoBducLetras(tipoDocumento);
	}

 

	public void setTipoDocumentoRecuperar(String tipoDocumentoRecuperar) {
		if (tipoDocumentoRecuperar.equals("0"))
			this.tipoDocumentoRecuperar = "";
		else
			this.tipoDocumentoRecuperar = tipoDocumentoRecuperar;

		this.numeroDocumentoRecuperar = "";
	}

 

 
 

 

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (((String) value).equals("0")) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					"Ingresa un tipo de documento", "");
		}
	}


}
