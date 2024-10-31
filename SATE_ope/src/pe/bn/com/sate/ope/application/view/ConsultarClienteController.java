package pe.bn.com.sate.ope.application.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarClienteModel;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.AgenciaService;
import pe.bn.com.sate.ope.infrastructure.service.external.UbigeoService;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;

@Controller("consultarClienteController")
@Scope("view")
public class ConsultarClienteController implements Serializable {

	private final static Logger logger = Logger
			.getLogger(ConsultarClienteController.class);

	private static final long serialVersionUID = 1L;

	private ConsultarClienteModel consultarClienteModel;

	private @Autowired
	TarjetaService tarjetaService;

	private @Autowired
	ClienteService clienteService;

	private @Autowired
	AgenciaService agenciaService;

	private @Autowired
	UbigeoService ubigeoService;

//	@Autowired
//	private ConsultarClienteFacade consultarClienteFacade;
	
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		consultarClienteModel = new ConsultarClienteModel();
	}

	public void seleccionarTarjeta() {}

	public void buscarTarjeta() {}

	public void cancelarTarjeta() {}

	public void actualizarDatosContactoTarjetaHabiente() {}

	public ConsultarClienteModel getConsultarClienteModel() {
		return consultarClienteModel;
	}

	public void setConsultarClienteModel(ConsultarClienteModel consultarClienteModel) {
		this.consultarClienteModel = consultarClienteModel;
	}
	
	
	public void buscarAsignaciones() {}
	
	public void seleccionarAsignacion() {
		
		System.out.println("LLEGO A seleccionarAsignacion");
		buscarTarjeta();
		
		
	}

}
