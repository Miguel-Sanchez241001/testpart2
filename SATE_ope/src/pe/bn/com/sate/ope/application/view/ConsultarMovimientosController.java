package pe.bn.com.sate.ope.application.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import pe.bn.com.sate.ope.application.model.ConsultarMovimientosModel;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.FWMCProcesos;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.facade.WSMCMovimientoAntiguos;
import pe.bn.com.sate.ope.infrastructure.service.internal.ClienteService;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
import pe.bn.com.sate.ope.transversal.dto.sate.MovimientoTarjetaExpediente;
import pe.bn.com.sate.ope.transversal.dto.ws.DTOConsultaMovimientosExpediente;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoBusqueda;
import pe.bn.com.sate.ope.transversal.util.enums.TipoTarjeta;
import pe.bn.com.sate.ope.transversal.util.excepciones.InternalExcepcion;

@Controller("consultarMovimientosController")
@Scope("view")
public class ConsultarMovimientosController {

	private final static Logger logger = Logger.getLogger(ConsultarMovimientosController.class);
	
	private final static String PASADO = "PASADO";
	private ConsultarMovimientosModel consultarMovimientosModel;

	private @Autowired FWMCProcesos fwmcProcesos;
	private @Autowired WSMCMovimientoAntiguos wsMCMovimientoAntiguos;

	private @Autowired TarjetaService tarjetaService;
	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	private @Autowired ClienteService clienteService;

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
						|| consultarMovimientosModel.getTipoBusqueda()
								.equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {

					fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0).getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjetas().get(0)
									.getFechaTerminoLinea());
				}

			} catch (InternalExcepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
		}
	}

	public void seleccionarAsignacion() {
		consultarMovimientosModel.limpiarMovimientosTarjeta();
		if(consultarMovimientosModel.getAsignacionSeleccionada().getEstado().equals(PASADO)) {
			
			consultarMovimientosModel.getDatosTarjetaCliente().setCliente(clienteService.buscarClientePorId(consultarMovimientosModel.getAsignacionSeleccionada().getIdCliente()));
			consultarMovimientosModel.getDatosTarjetaCliente().setTarjeta(tarjetaService.buscarTarjetaId(consultarMovimientosModel.getAsignacionSeleccionada().getIdTar()));
		 
			try {
				List<MovimientoTarjetaExpediente> listDato = wsMCMovimientoAntiguos.consultaMovimientoPorExpediente(consultarMovimientosModel.getAsignacionSeleccionada());
					String tipoTarjeta = TipoTarjeta.descripcionTipotarjeta(consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoTarjeta());

					if (listDato.size() == 0) {
						consultarMovimientosModel.inicializarGrilla();
						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_WARN,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_MOVIMIENTO_TIPO_TARJETA,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_MOVIMIENTO_TIPO_TARJETA);
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");

					} else {
						listDato.stream().forEach(mov -> mov.setTipoTarjeta(tipoTarjeta));
						
						consultarMovimientosModel.setMovimientosTarjetaExp(listDato);
						UsefulWebApplication.actualizarComponente("msgs");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:listaTarjetasPanel");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgBusqueda");
					}

				
			} catch (Exception e) {
				logger.error(e.getMessage());
				consultarMovimientosModel.inicializarGrilla();
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", e.getMessage());
				UsefulWebApplication.actualizarComponente("msgs");
				UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
			}
		
			
			
		}else {
			searchAsignacionActual();
		}
	
	}

	public ConsultarMovimientosModel getConsultarMovimientosModel() {
		return consultarMovimientosModel;
	}

	public void setConsultarMovimientosModel(ConsultarMovimientosModel consultarMovimientosModel) {
		this.consultarMovimientosModel = consultarMovimientosModel;
	}

	 public void customizeXLS(Object document) {
	        // Casting del documento a un HSSFWorkbook
	        HSSFWorkbook workbook = (HSSFWorkbook) document;
	        workbook.setSheetName(0, "Movimientos de tarjeta tesoro");
	        Sheet sheet = workbook.getSheetAt(0); // Acceder a la primera hoja
	        // Desplazar todas las filas hacia abajo (1 espacio)
	        sheet.shiftRows(0, sheet.getLastRowNum(), 1); // Mover todo hacia abajo desde la fila 0

	        // Crear la nueva fila al inicio para el título
	        Row titleRow = sheet.createRow(0); // Ahora la fila 0 está vacía
	        Cell titleCell = titleRow.createCell(0); // Primera celda

	        // Agregar el texto del título
	        titleCell.setCellValue("Reporte de Movimientos");

	        // Estilo del título
	        CellStyle titleStyle = workbook.createCellStyle();
	        Font titleFont = workbook.createFont();
	        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // Negrita
	        titleFont.setFontHeightInPoints((short) 16); // Tamaño de fuente
	        titleFont.setColor(HSSFColor.WHITE.index);

	        titleStyle.setFont(titleFont);
	        titleStyle.setAlignment(CellStyle.ALIGN_CENTER); // Centrar horizontalmente
	        titleStyle.setFillForegroundColor(HSSFColor.DARK_RED.index);
	        titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        titleCell.setCellStyle(titleStyle);

	        
	        CellStyle columnStyle = workbook.createCellStyle();
	        Font  columnFont = workbook.createFont();
	        columnFont.setColor(HSSFColor.WHITE.index);
	        columnStyle.setFont(columnFont);
	        columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
	        columnStyle.setFillForegroundColor(HSSFColor.DARK_RED.index);
	        columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        
	        Row headerRow = sheet.getRow(1); // La fila 1 tiene los encabezados
	        if (headerRow != null) {
	  

	            // Recorrer todas las celdas de la fila de encabezados
	            for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
	                Cell cell = headerRow.getCell(cellNum);
	                if (cell == null) {
	                    cell = headerRow.createCell(cellNum); // Crear la celda si no existe
	                }
	                cell.setCellStyle(columnStyle); // Aplicar estilo
	            }
	        }
	        
	        
	        
	        
	        // Combinar celdas para que el título ocupe toda la fila
	        int lastColumn = sheet.getRow(1).getLastCellNum() - 1; // Última columna en la fila de datos
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastColumn)); // Combinar columnas

	        // Agregar datos al final
	        int lastRowIndex = sheet.getLastRowNum() + 2; // Espacio después de los datos


	        // Estilo para etiquetas
	        CellStyle labelStyle = workbook.createCellStyle();
	        Font labelFont = workbook.createFont();
	        labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // Negrita
	        labelFont.setColor(HSSFColor.WHITE.index);

	        labelStyle.setFont(labelFont);
	        labelStyle.setFillForegroundColor(HSSFColor.DARK_RED.index);
	        labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        
	        Row additionalDatFecha = sheet.createRow(lastRowIndex); // Fila para datos adicionales
	        
	        // Celda: Dato (Etiqueta)
	        Cell dateLabelCell = additionalDatFecha.createCell(0);
	        dateLabelCell.setCellValue("Fecha y Hora:");
	        dateLabelCell.setCellStyle(labelStyle);

	        // Celda: Valor
	        Cell dateValueCell = additionalDatFecha.createCell(1);
	        dateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
	        
	        Row additionalDataRow = sheet.createRow(lastRowIndex+ 1); // Fila para datos adicionales

	        // Celda: Dato (Etiqueta)
	        Cell userLabelCell = additionalDataRow.createCell(0);
	        userLabelCell.setCellValue("Generado por:");
	        userLabelCell.setCellStyle(labelStyle);

	        // Celda: Valor
	        Cell userValueCell = additionalDataRow.createCell(1);
	        userValueCell.setCellValue(UsefulWebApplication
					.obtenerUsuario().getUsername());

	   

	        Row additionalNumTar = sheet.createRow(lastRowIndex+2); // Fila para datos adicionales

	        // Celda: Dato (Etiqueta)
	        Cell ipLabelCell = additionalNumTar.createCell(0);
	        ipLabelCell.setCellValue("Numero Tarjeta:");
	        ipLabelCell.setCellStyle(labelStyle);

	        // Celda: Valor
	        Cell ipValueCell = additionalNumTar.createCell(1);
	        ipValueCell.setCellValue(consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta());
	      
	        Row additionalclie = sheet.createRow(lastRowIndex+3); // Fila para datos adicionales

	        // Celda: Dato (Etiqueta)
	        Cell clieLabelCell = additionalclie.createCell(0);
	        clieLabelCell.setCellValue("Cliente :");
	        clieLabelCell.setCellStyle(labelStyle);

	        // Celda: Valor
	        Cell clieValueCell = additionalclie.createCell(1);
	        clieValueCell.setCellValue(consultarMovimientosModel.getDatosTarjetaCliente().getCliente().getNombres());

	        // Ajustar automáticamente el ancho de las columnas
	        for (int i = 0; i <= lastColumn; i++) {
	            sheet.autoSizeColumn(i);
	        }
	    }
	
	
	
	
	
	
	
	
	public void buscarAsignaciones() {
		try {

			consultarMovimientosModel.inicializarGrilla();

			List<Asignacion> asignaciones = null;
			if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {

				String tarjeta19 = StringsUtils.llenarCerosAlaIzquierdaV2(consultarMovimientosModel.getNumeroTarjeta(),
						19);

				String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();

				long valor = 0;

				valor = tarjetaService.consultarExisteTarjetaRUC(tarjeta19, rucUsuario);

				if (valor == 0) {

					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_TIP_TARJETA,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_TIP_TARJETA);
				} else {
					asignaciones = reporteResumenFacade.obtenerAsignacionesPorTarjetaSimple(tarjeta19, rucUsuario);

					if (asignaciones.isEmpty() && asignaciones.size() == 0) {
						consultarMovimientosModel.inicializarFormulario();

						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_TARJETA);

					} else {
						consultarMovimientosModel.setBusquedaRealizada(true);
						consultarMovimientosModel.setAsignacionesTotal(asignaciones);
						// MOSTRAR MODAL COMPONENTE
						UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
						// formulario del componente
						UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
					}
				}

			} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
					|| consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {

				String rucUsuario = UsefulWebApplication.obtenerUsuario().getRuc();

				long valor = 0;

				valor = tarjetaService.consultarExisteTipNumDocRUC(consultarMovimientosModel.getTipoBusqueda(),
						consultarMovimientosModel.getNumeroTarjeta(), rucUsuario);

				if (valor == 0) {

					consultarMovimientosModel.inicializarFormulario();
					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_NUM_DOCUMENTO,
							ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TAREMP_NUM_DOCUMENTO);
				} else {
					asignaciones = reporteResumenFacade.obtenerAsignacionesPorDocumentoSimple(
							consultarMovimientosModel.getTipoBusqueda(), consultarMovimientosModel.getNumeroTarjeta(),
							rucUsuario);

					if (asignaciones.isEmpty() && asignaciones.size() == 0) {
						consultarMovimientosModel.inicializarFormulario();
						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_TIPO_NUMDOCUMENTO);

					} else {
						consultarMovimientosModel.setBusquedaRealizada(true);
						consultarMovimientosModel.setAsignacionesTotal(asignaciones);
						// MOSTRAR MODAL COMPONENTE
						UsefulWebApplication.ejecutar("wvSeleccionarAsignacion.show()");
						// formulario del componente
						UsefulWebApplication.actualizarComponente("formSeleccionarAsignacion");
					}
				}

			}

		} catch (InternalExcepcion se) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(se.getMessage());
		}

	}

	public void buscarTipoBusqueda() {
		if (consultarMovimientosModel.getTipoBusquedaPor().equals("Por Documento")) {
			consultarMovimientosModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposDocumento());
			consultarMovimientosModel.setBusquedaRealizada(false);
			consultarMovimientosModel.setNumeroTarjeta(null);
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:numDocumento");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");

		} else if (consultarMovimientosModel.getTipoBusquedaPor().equals("Por Tarjeta")) {
			consultarMovimientosModel.setListaTipoBusqueda(TipoBusqueda.obtenerTiposNumeroTarjeta());
			consultarMovimientosModel.setBusquedaRealizada(false);
			consultarMovimientosModel.setNumeroTarjeta(null);
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:numDocumento");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");

		} else {
			consultarMovimientosModel.setListaTipoBusqueda(null);
			consultarMovimientosModel.setBusquedaRealizada(false);
			consultarMovimientosModel.setNumeroTarjeta(null);
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:numDocumento");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
		}
	}
	
	public void searchAsignacionActual() {
		try {
			DTOConsultaMovimientosExpediente dato = null;
			List<MovimientoTarjetaExpediente> listDato;

			String tipTarj = "";

			try {
				if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {

					// consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
					// consultarMovimientosModel.getTipoBusqueda(), tarjeta19, "B"));

					consultarMovimientosModel.getDatosTarjetaCliente().setCliente(clienteService
							.buscarClientePorId(consultarMovimientosModel.getAsignacionSeleccionada().getIdCliente()));
					consultarMovimientosModel.getDatosTarjetaCliente().setTarjeta(tarjetaService
							.buscarTarjetaId(consultarMovimientosModel.getAsignacionSeleccionada().getIdTar()));

					System.out.println("consultarMovimientosModel.getTipoBusqueda():::"
							+ consultarMovimientosModel.getTipoBusqueda());
					dato = fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea());

					tipTarj = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoTarjeta();

				} else if (consultarMovimientosModel.getTipoBusqueda().equals(TipoBusqueda.DNI.getId())
						|| consultarMovimientosModel.getTipoBusqueda()
								.equals(TipoBusqueda.CARNET_EXTRANJERIA.getId())) {

					// consultarMovimientosModel.setDatosTarjetaCliente(tarjetaService.buscarDatosTarjetasCliente(
					// consultarMovimientosModel.getTipoBusqueda(),
					// consultarMovimientosModel.getNumeroTarjeta(), "B"));

					consultarMovimientosModel.getDatosTarjetaCliente().setCliente(clienteService
							.buscarClientePorId(consultarMovimientosModel.getAsignacionSeleccionada().getIdCliente()));
					consultarMovimientosModel.getDatosTarjetaCliente().setTarjeta(tarjetaService
							.buscarTarjetaId(consultarMovimientosModel.getAsignacionSeleccionada().getIdTar()));

					System.out.println("consultarMovimientosModel.getTipoBusqueda():::"
							+ consultarMovimientosModel.getTipoBusqueda());

					dato = fwmcProcesos.consultaMovimientoPorExpediente(
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getNumeroCuenta(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoMoneda(),
							consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getFechaTerminoLinea());

					tipTarj = consultarMovimientosModel.getDatosTarjetaCliente().getTarjeta().getTipoTarjeta();

				}

				if (dato.getCodRespuesta().equals("0000")) {

					String tipoTarjeta = TipoTarjeta.descripcionTipotarjeta(tipTarj);
					System.out.println("tipoTarjeta:" + tipoTarjeta);

					listDato = fwmcProcesos.listaMovTarjExp(dato, tipoTarjeta);

					if (listDato.size() == 0) {
						consultarMovimientosModel.inicializarGrilla();

						UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_WARN,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_MOVIMIENTO_TIPO_TARJETA,
								ConstantesGenerales.ERROR_MENSAJE_NO_EXISTE_MOVIMIENTO_TIPO_TARJETA);
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");

					} else {
						consultarMovimientosModel.setMovimientosTarjetaExp(listDato);

						UsefulWebApplication.actualizarComponente("msgs");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:listaTarjetasPanel");
						UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgBusqueda");
					}

				} else {
					consultarMovimientosModel.setBusquedaRealizada(false);
					consultarMovimientosModel.inicializarFormulario();
					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, dato.getDescRespuesta(),
							ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
					UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:listaTarjetasPanel");
					UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");

				}

			} catch (InternalExcepcion e) {
				e.printStackTrace();
			}

		} catch (ServiceException se) {
			logger.error(se.getMessage());
			consultarMovimientosModel.inicializarGrilla();
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "", se.getMessage());
			UsefulWebApplication.actualizarComponente("msgs");
			UsefulWebApplication.actualizarComponente("formMovimientoTarjeta:pgResultadoFin");
		}
	}
}