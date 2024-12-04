package pe.bn.com.sate.ope.transversal.util.componentes;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("parametros")
public class Parametros {

	private String urlComp;
	private String errorComp;
	private String desErrorComp;

	// PARAMETROS RENIEC
	public String consultaReniec;
	public String sistemaReniec;
	public String user1Reniec;
	public String userReniec;

	// PARAMETROS MC
	public String codigoEmisorMc;
	public String codigoUsuarioMc;
	public String numTerminalMc;
	public String prefijoNumReferenciaMc;
	public String wsUsuarioMc;
	public String wsClaveMc;
	public String wsSoapMc;
	public String wsComercioMc;
	



	// PARAMETROS TIEMPO
	private String sesionExpiradaTiempo;
	private String conexionTiempo;
	private String urlServiceRestAntiguos;

	private String respuestaTiempo;
	private String urlAldeamo;
	private String tokenAldeamo;
	private String correoEmisor;
	
}
