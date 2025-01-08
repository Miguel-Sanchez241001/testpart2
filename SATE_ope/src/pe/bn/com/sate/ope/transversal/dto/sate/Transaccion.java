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

	private String numeroTarjeta;
	private Date fechaOperacion;
	private Date fechaPosteo;
	private String operacion;
	private String comercio;
	private Double monto;
	private String autorizacionPMC;
	private String numeroAutorizacion;
	private String estado;

 

}
