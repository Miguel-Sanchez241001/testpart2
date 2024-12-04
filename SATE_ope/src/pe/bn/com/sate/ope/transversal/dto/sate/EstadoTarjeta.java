package pe.bn.com.sate.ope.transversal.dto.sate;

import java.util.Date;

import lombok.Data;

@Data
public class EstadoTarjeta {

	private Long id;
	private Long idTarjeta;
	private String estado;
	private String estadoCuenta;
	private String motivo;
	private String usuarioRegistro;
	private Date fechaRegistro;
	private String codAutorizacion;


}
