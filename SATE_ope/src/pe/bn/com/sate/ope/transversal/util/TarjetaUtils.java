package pe.bn.com.sate.ope.transversal.util;

import java.util.ArrayList;
import java.util.List;

public class TarjetaUtils {

    // Método para procesar un número de tarjeta
    public static String procesarTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de tarjeta no puede estar vacío.");
        }

        // Eliminar ceros iniciales
        numeroTarjeta = numeroTarjeta.replaceFirst("^0+", "");

        // Validar que tenga 16 dígitos
        if (numeroTarjeta.length() != 16) {
            throw new IllegalArgumentException("El número de tarjeta debe tener 16 dígitos después de eliminar ceros iniciales.");
        }

        // Ocultar los números centrales
        String inicio = numeroTarjeta.substring(0, 4);
        String fin = numeroTarjeta.substring(12);
        return inicio + "****" + fin;
    }

    // Método para procesar una lista de tarjetas
    public static List<String> procesarListaTarjetas(List<String> tarjetas) {
        List<String> tarjetasProcesadas = new ArrayList<>();
        for (String tarjeta : tarjetas) {
            tarjetasProcesadas.add(procesarTarjeta(tarjeta));
        }
        return tarjetasProcesadas;
    }

  
}
