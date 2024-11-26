package pe.bn.com.sate.ope.transversal.dto.wsrestsate;

import java.util.List;

import lombok.Data;

@Data
public class MovimientoResponse {
    private String codigo;
    private String mensaje;
    private List<MovimientoData> data;
}