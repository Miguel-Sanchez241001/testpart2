package pe.bn.com.sate.ope.application.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.configuration.security.UsuarioSeguridad;
@Getter
@Setter
@ToString
public class SeguridadModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private UsuarioSeguridad usuario;

	public SeguridadModel(UsuarioSeguridad usuario){
		this.usuario = usuario;
	}

 

	
	
}
