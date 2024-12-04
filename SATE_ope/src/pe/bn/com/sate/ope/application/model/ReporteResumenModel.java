package pe.bn.com.sate.ope.application.model;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import pe.bn.com.sate.ope.transversal.dto.sate.Cargo;
import pe.bn.com.sate.ope.transversal.dto.sate.TarjetaResumen;
import pe.bn.com.sate.ope.transversal.dto.sate.Transaccion;
import pe.bn.com.sate.ope.transversal.util.Fecha;
import pe.bn.com.sate.ope.transversal.util.enums.TipoDocumento;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstadoTarjeta;
import pe.bn.com.sate.ope.transversal.util.enums.TipoReporteResumen;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;

@Data
public class ReporteResumenModel {

	private List<TipoReporteResumen> listaReporteResumen;
	private List<String> listaFechaCorte;
	private int tipoReporteSeleccionado;
	private String fechaCorteInicialSeleccionada;
	private String fechaCorteFinalSeleccionada;

	private List<TarjetaResumen> listaTarjetas;
	private List<Transaccion> listaTransacciones;
	private List<Cargo> listaCargos;
 	public ReporteResumenModel() {
		listaFechaCorte = Fecha.inicializarListaFechaCorte();
		listaReporteResumen = Arrays.asList(TipoReporteResumen.values());
	}

	public boolean validarFechasCorte() {
		if (Fecha.transformarADate(fechaCorteInicialSeleccionada).after(
				Fecha.transformarADate(fechaCorteFinalSeleccionada)))
			return false;
		else
			return true;
	}

	public String estadoEnLetras(String estado) {
		return TipoEstadoTarjeta.enLetras(estado);
	}

	public String tipoDocumentoLetras(String tipoDocumento) {
		return TipoDocumento.tipoDocumentoBducLetras(tipoDocumento);

	}
	public  String descripcionTipoTar(String codigo,String diseno) {
		return TipoTarjetaNegocio.descripcionTipotarjetaConplete(codigo, diseno) ;

	}
}
