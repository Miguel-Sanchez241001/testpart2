package pe.bn.com.sate.ope.application.view;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.Setter;
 import pe.bn.com.sate.ope.application.model.RendicionCuentasTarjetaModel;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
  

@Getter
@Setter
@Controller("rendicionCuentasTarjetaController")
@Scope("view")
public class RendicionCuentasTarjetaController {
	private final static Logger logger = Logger.getLogger(RendicionCuentasTarjetaController.class);

	private RendicionCuentasTarjetaModel rendicionCuentasTarjetaModel;
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		rendicionCuentasTarjetaModel = new RendicionCuentasTarjetaModel();
		
	}
	
	
	
	public void buscarTipoBusqueda() {
		if (rendicionCuentasTarjetaModel.getTipoBusquedaPor().equals("Por Documento")) {
			rendicionCuentasTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
			rendicionCuentasTarjetaModel.setBusquedaRealizada(false);
			rendicionCuentasTarjetaModel.setNumeroDocumento(null);
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:numDocumento");
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:pgResultadoFin");

		} else if (rendicionCuentasTarjetaModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
			rendicionCuentasTarjetaModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
			rendicionCuentasTarjetaModel.setBusquedaRealizada(false);
			rendicionCuentasTarjetaModel.setNumeroDocumento(null);
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:numDocumento");
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:pgResultadoFin");

		} else {
			rendicionCuentasTarjetaModel.setListaTipoBusqueda(null);
			rendicionCuentasTarjetaModel.setBusquedaRealizada(false);
			rendicionCuentasTarjetaModel.setNumeroDocumento(null);
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:numDocumento");
			UsefulWebApplication.actualizarComponente("formRendicionCuenta:pgResultadoFin");
		}
	}
}
