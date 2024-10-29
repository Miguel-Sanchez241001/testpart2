package pe.bn.com.sate.ope.transversal.dto.sate;

import java.util.Date;

public class MovimientoTarjetaExpediente {

	private String id;
	private Date fechaTxn;
	private String descripcionTxn;
	private String monOriginalTxn;
	private String montoTxn;
	
	private String sigMontoTxn;
	
	private String operacionTxn; 
	private String codAutTxn;
	
	private String numTarjetaTxn;
	
	private String tipoTarjeta;

	public MovimientoTarjetaExpediente() {
	
	}

	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public Date getFechaTxn() {
		return fechaTxn;
	}



	public void setFechaTxn(Date fechaTxn) {
		this.fechaTxn = fechaTxn;
	}



	public String getDescripcionTxn() {
		return descripcionTxn;
	}

	public void setDescripcionTxn(String descripcionTxn) {
		this.descripcionTxn = descripcionTxn;
	}

	public String getMonOriginalTxn() {
		return monOriginalTxn;
	}

	public void setMonOriginalTxn(String monOriginalTxn) {
		this.monOriginalTxn = monOriginalTxn;
	}

	public String getMontoTxn() {
		return montoTxn;
	}

	public void setMontoTxn(String montoTxn) {
		this.montoTxn = montoTxn;
	}

	public String getSigMontoTxn() {
		return sigMontoTxn;
	}

	public void setSigMontoTxn(String sigMontoTxn) {
		this.sigMontoTxn = sigMontoTxn;
	}

	public String getOperacionTxn() {
		return operacionTxn;
	}

	public void setOperacionTxn(String operacionTxn) {
		this.operacionTxn = operacionTxn;
	}

	public String getCodAutTxn() {
		return codAutTxn;
	}

	public void setCodAutTxn(String codAutTxn) {
		this.codAutTxn = codAutTxn;
	}

	public String getNumTarjetaTxn() {
		return numTarjetaTxn;
	}

	public void setNumTarjetaTxn(String numTarjetaTxn) {
		this.numTarjetaTxn = numTarjetaTxn;
	}



	public String getTipoTarjeta() {
		return tipoTarjeta;
	}



	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	
	
}
