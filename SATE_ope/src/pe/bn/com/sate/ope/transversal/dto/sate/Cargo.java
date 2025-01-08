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
public class Cargo {
	private Date fechaCorte;
	private Date fechaProceso;
	private String numeroCuenta;
	private Double montoCargado;
	private Double montoPendiente;
	private Double saldoInicial;
	private Double saldoFinal;

	 

}
