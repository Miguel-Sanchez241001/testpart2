package pe.bn.com.sate.ope.transversal.dto.sate;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaccion {

	private String numeroExpediente;
	private String numeroTarjeta;
	private String monedaCuenta;
	private Date fechaOperacion;
	private Date fechaPosteo;
	private String operacion;
	private String comercio;
	private Double monto;
	private String monedaTransaccion;
	private String numeroAutorizacion;
	private String estado;

 

}
