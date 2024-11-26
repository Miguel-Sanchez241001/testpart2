package pe.bn.com.sate.ope.transversal.dto.wsrestsate;

import lombok.Data;

@Data
public class MovimientoData {
    private String fechaTxn;
    private String descripcionTxn;
    private String monOriginalTxn;
    private String montoTxn;
    private String sigMontoTxn;
    private String operacionTxn;
    private String codAutTxn;
    private String numTarjetaTxn;
}

