package pe.bn.com.sate.ope.transversal.util.enums;


public enum MotivosBloqueoWS {

	NORMAL("N","Activar"),
	PERDIDA("E", "Perdida"),
	FRAUDE("F", "Fraude"),
	MAL_REALCE("Q","Mal realce"),
	ALERTA("T","Alerta"),
	ROBO("X","Robo"),
	TEMPORAL("K", "Bloqueo Temporal de Cuenta");
	
//	

	private String id;
	private String descripcion;

	private MotivosBloqueoWS(String id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static String descripcionMotivoBloqueoWS(String codigo){
		String descripcion = "Ninguno";
		for(MotivosBloqueoWS motivosBloqueoTarjeta:values()){
			if(codigo.equals(motivosBloqueoTarjeta.getId())){
				descripcion = motivosBloqueoTarjeta.getDescripcion();
			}
		}
		return descripcion;
	}
	
	
}
