package pe.bn.com.sate.ope.transversal.dto.sate;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatosTarjetaCliente {

	private Cliente cliente;
	private Tarjeta tarjeta;
	private EstadoTarjeta estadoTarjeta;

	private List<Tarjeta> tarjetas;

	public DatosTarjetaCliente() {
		cliente = new Cliente();
		tarjeta = new Tarjeta();
		estadoTarjeta = new EstadoTarjeta();
	}

 
	
	

}
