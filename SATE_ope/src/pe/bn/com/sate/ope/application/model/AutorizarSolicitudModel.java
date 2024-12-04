package pe.bn.com.sate.ope.application.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.transversal.dto.sate.SolicitudTarjeta;
import pe.bn.com.sate.ope.transversal.util.NumeroALetras;
import pe.bn.com.sate.ope.transversal.util.enums.CodDocumentoWebservice;
import pe.bn.com.sate.ope.transversal.util.enums.DisposicionEfectivo;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Getter
@Setter
@ToString
public class AutorizarSolicitudModel {

	private List<SolicitudTarjeta> solicitudesTarjeta;

	private List<SolicitudTarjeta> solicitudesTarjetaSeleccionado;
 
	 
	public boolean validarCantidadSolicitudes() {
		if (solicitudesTarjetaSeleccionado == null
				|| solicitudesTarjetaSeleccionado.size() == 0)
			return false;
		else
			return true;
	}

	public String descripcionTipoDocumento(String codigo) {
		return CodDocumentoWebservice.descripcionCodDocumentoWebservice(codigo);
	}

	public String descripcionTipoTarjeta(String codigo, String diseno) {

		return TipoTarjetaNegocio.descripcionTipotarjeta(codigo, diseno);
	}

	public String descripcionEstadoTarjeta(String codigo) {
		return TipoEstadoTarjeta.descripcionTipoEstadoTarjeta(codigo);
	}

	public String descripcionDisposicionEfectivo(String codigo) {
		return DisposicionEfectivo.descripcionDisposicionEfectivo(codigo);
	}

	public String formatoNumeroSolicitud(Long id) {
		return NumeroALetras.formatoNumero(id, 10);
	}

}
