package pe.bn.com.sate.ope.transversal.util.enums;

public enum OperacionMovimientoMC {

	CREDITO("C", "Consumo"), DEBITO("D", "Disp. Efecto");

	String tipo;
	String descripcion;

	OperacionMovimientoMC(String tipo, String descripcion) {
		this.tipo = tipo;
		this.descripcion = descripcion;
	}

	public static String enLetras(String tipo) {
		if (tipo != null)
			for (OperacionMovimientoMC operacionMovimientoMC : OperacionMovimientoMC.values()) {
				if (tipo.equals(operacionMovimientoMC.getTipo()))
					return operacionMovimientoMC.getDescripcion();
			}
		return "-";
	}

	public String getTipo() {
		return tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

}