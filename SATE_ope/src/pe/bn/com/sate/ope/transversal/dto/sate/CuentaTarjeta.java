package pe.bn.com.sate.ope.transversal.dto.sate;

import java.util.Date;

import lombok.Data;

@Data
public class CuentaTarjeta {
    private Date fechaAutorizacion;       // Fecha Autorización
    private Date fechaSalida;            // Fecha Salida
    private Date fechaRetorno;           // Fecha Retorno
    private Integer duracion;                // Duración (en días)
    private double montoLineaAsignado;   // Monto Línea Asignado S/.
    private double montoLineaUtilizado;  // Monto Línea Utilizado S/.
    private String numeroAutorizacion;   // N° Autorización
    private double montoLineaDevuelta;   // Monto Línea Devuelta S/.
}
