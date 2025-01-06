package pe.bn.com.sate.ope.transversal.dto.sate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data // Genera automáticamente getters, setters, toString, equals, y hashCode.
@NoArgsConstructor // Constructor vacío.
@AllArgsConstructor // Constructor con todos los parámetros.
public class AsignacionCorreo {
    private String correo;            // B05_EMAIL
    private String linea;             // B04_LINEA
    private Date inicioLinea;         // B04_FECHA_INICIO_LINEA
    private Date finLinea;            // B04_FECHA_FIN_LINEA
    private String nombreCompleto;    // Nombres concatenados
}

