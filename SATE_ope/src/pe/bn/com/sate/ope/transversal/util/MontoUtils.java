package pe.bn.com.sate.ope.transversal.util;

import java.text.DecimalFormat;

public class MontoUtils {

    private static final DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");

    public static String formatearMonto(String monto) {
        try {
            // Elimina ceros iniciales, si los hay
            int valorNumerico = Integer.parseInt(monto.trim());

            // Retorna el monto en formato S/ #,##0.00
            return "S/ " + formatoMoneda.format(valorNumerico);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El monto proporcionado no es válido: " + monto, ex);
        }
    }

 
}
