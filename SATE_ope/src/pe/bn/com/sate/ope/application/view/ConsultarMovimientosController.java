package pe.bn.com.sate.ope.application.view;

import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarMovimientosModel;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.persistence.mapper.internal.AsignacionMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.CargoMapper;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaMovimientosExpediente;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjetaNegocio;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.*;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;



@Controller("consultarMovimientosController")
@Scope("view")
public class ConsultarMovimientosController {

	private final static Logger logger = Logger.getLogger(ConsultarMovimientosController.class);

	private ConsultarMovimientosModel consultarMovimientosModel;

	private @Autowired FWMCProcesos fwmcProcesos;

	private @Autowired TarjetaService tarjetaService;
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		consultarMovimientosModel = new ConsultarMovimientosModel();
	}

	public void buscarMovimientos() {
		consultarMovimientosModel.limpiarMovimientosTarjeta();
		try {
			consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
					consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta(), "B"));

			try {
				
				if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
					fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea()

					);
					

				} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
						|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
					
					fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getFechaTerminoLinea()
					);					
				}
				
			} catch (InternalExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		}
	}

	public void seleccionarAsignacion() {
		consultarMovimientosModel.limpiarMovimientosTarjeta();
		try {
			consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
					consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta(), "B"));
			
			System.out.println("consultarMovimientosModel.getTipoBusqueda():::"+consultarMovimientosModel.getTipoBusqueda());

			// consultarMovimientosModel.setMovimientosTarjeta(fwmcProcesos
			// .consultarMovimientosPorTarjeta(consultarMovimientosModel
			// .getNumeroTarjeta()));

			// aui debe llegar el valor del pop-up
			DTOConsultaMovimientosExpediente dato = null;
			List<MovimientoTarjetaExpediente> listDato;
			
			try {				
				if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
					dato=fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea()
					);
					
				} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
						|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
					
					dato=fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getFechaTerminoLinea()
					);					
				}	
				
				if (dato.getCodRespuesta().equals("0000")) {        		
	        		
					String diseno = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getDiseno();
					String tipTarj = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoTarjeta();
					
					String tipoTarjeta = TipoTarjetaNegocio.descripcionTipotarjeta(tipTarj, diseno);
					System.out.println("tipoTarjeta:"+tipoTarjeta);
					
					listDato=fwmcProcesos.listaMovTarjExp(dato,tipoTarjeta);
													
					consultarMovimientosModel.setMovimientosTarjetaExp(listDato);
					
					UsefulWebApplication.actualizarComponente("msgs");
					UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultado");
	        		        		
	        	}else{
	        		UsefulWebApplication
					.mostrarMensajeJSF(
							ConstantesGenerales.SEVERITY_ERROR,
							dato.getDescRespuesta(),
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			
	        	}
				
				
				
				
				
			} catch (InternalExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formCambiarEstadoTarjeta:pgResultado");
		}
	}

	public ConsultarMovimientosModel getConsultarMovimientosModel() {
		return consultarMovimientosModel;
	}

	public void setConsultarMovimientosModel(ConsultarMovimientosModel consultarMovimientosModel) {
		this.consultarMovimientosModel = consultarMovimientosModel;
	}

	public void buscarAsignaciones() {
		try {
			List<Asignacion> asignaciones = null;
			if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				asignaciones = reporteResumenFacade
						.obtenerAsignacionesPorTarjeta(consultarMovimientosModel.getNumeroTarjeta());

			} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {
				asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumento(
						consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta());
			}
			consultarMovimientosModel.setAsignacionesTotal(asignaciones);
			// MOSTRAR MODAL COMPONENTE
			UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
			// formulario del componente
			UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
		} catch (InternalExcepcion se) {
			UsefulWebApplication.mostrarMensajeJSF(
					ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(se.getMessage());
		}

	}
	
//	public void exportToExcel() {
//        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
//            // Crear hoja en el archivo Excel
//            Sheet sheet = workbook.createSheet("Datos");
//
//            // Crear fila de encabezados
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("ID");
//            headerRow.createCell(1).setCellValue("Nombre");
//
//            // Llenar las filas con los datos del DataTable
//            int rowNum = 1;
//            for (Item item : items) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(item.getId());
//                row.createCell(1).setCellValue(item.getNombre());
//            }
//
//            // Preparar la respuesta HTTP para la descarga
//            FacesContext facesContext = FacesContext.getCurrentInstance();
//            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setHeader("Content-Disposition", "attachment; filename=datos.xlsx");
//
//            // Escribir el archivo Excel en la respuesta
//            OutputStream outputStream = response.getOutputStream();
//            workbook.write(outputStream);
//            outputStream.flush();
//
//            // Completar la respuesta para detener el ciclo JSF
//            facesContext.responseComplete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
	
//	public List<Item> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Item> items) {
//        this.items = items;
//    }
//	

}