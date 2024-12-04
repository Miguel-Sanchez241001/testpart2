package pe.bn.com.sate.ope.application.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("tipoDocumentoValidator")
public class TipoDocumentoValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String tipoDocumento = (String) value;
        if (tipoDocumento == null || tipoDocumento.equals("0") || tipoDocumento.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccione un tipo de documento", null);
            throw new ValidatorException(msg);
        }
    }
}
