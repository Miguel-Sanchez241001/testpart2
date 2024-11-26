package pe.bn.com.sate.ope.transversal.dto.wsrestsate;

import lombok.Data;

@Data
public class MovimientoRequest {
    private String fechaInicio;
    private String fechaFin;
    private String numCuenta;
}
