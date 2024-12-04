package pe.bn.com.sate.ope.application.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.FacesValidator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@FacesValidator("fechaValidator")
public class FechaValidator implements Validator {

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
        String fechaStr = (String) value;

        // Validar formato correcto
        try {
            @SuppressWarnings("unused")
			Date fecha = dateFormat.parse(fechaStr);
            String[] partesFecha = fechaStr.split("/");

            int dia = Integer.parseInt(partesFecha[0]);
            int mes = Integer.parseInt(partesFecha[1]);
            int anio = Integer.parseInt(partesFecha[2]);

            // Validar día, mes y año
            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || anio < 2000) {
                throw new IllegalArgumentException("Fecha inválida.");
            }
        } catch (ParseException | IllegalArgumentException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fecha inválida", "La fecha debe estar en el formato dd/MM/yyyy y ser válida.");
            throw new javax.faces.validator.ValidatorException(msg);
        }
    }
}
