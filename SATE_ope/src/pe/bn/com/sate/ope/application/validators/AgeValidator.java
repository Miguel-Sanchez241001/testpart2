package pe.bn.com.sate.ope.application.validators;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.validator.FacesValidator;
 
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@FacesValidator("ageValidator")
public class AgeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return; // Si no hay valor, no validar
        }

        Date dateOfBirth = (Date) value;
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        // Calcular la edad
        int age = today.getYear() - birthDate.getYear();
        if (birthDate.plusYears(age).isAfter(today)) {
            age--; // Ajustar si aún no cumplió años este año
        }

        // Validar si la edad es menor a 18 años
        if (age < 18) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Debe ser mayor de 18 años para continuar.", null));
        }
    }
}