package pe.bn.com.sate.ope.application.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.Setter;
import pe.bn.com.sate.ope.application.model.ReporteResumenModel;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;

@Getter
@Setter
@Controller("reporteResumenController")
@Scope("view")
public class ReporteResumenController {

	private final static Logger logger = Logger.getLogger(ReporteResumenController.class);

	private ReporteResumenModel reporteResumenModel;

	@Autowired
	private ReporteResumenFacade reporteResumenFacade;

	@PostConstruct
	public void init() {
		reporteResumenModel = new ReporteResumenModel();
	}

	public void buscarReporte() {
		try {
			if (reporteResumenModel.validarFechasCorte()) {
				LimpiarListasResumen();
				switch (reporteResumenModel.getTipoReporteSeleccionado()) {
				case 1:
					reporteResumenModel.setListaTarjetas(reporteResumenFacade.obtenerListaTarjetas(
							reporteResumenModel.getFechaCorteInicialSeleccionada(),
							reporteResumenModel.getFechaCorteFinalSeleccionada()));
					break;
				case 2:
					reporteResumenModel.setListaTransacciones(reporteResumenFacade.obtenerListaTransacciones(
							reporteResumenModel.getFechaCorteInicialSeleccionada(),
							reporteResumenModel.getFechaCorteFinalSeleccionada()));
					break;
				case 3:
					reporteResumenModel.setListaCargos(reporteResumenFacade.obtenerListaCargos(
							reporteResumenModel.getFechaCorteInicialSeleccionada(),
							reporteResumenModel.getFechaCorteFinalSeleccionada()));
					break;
				}

				if ((reporteResumenModel.getListaTarjetas() == null || reporteResumenModel.getListaTarjetas().isEmpty())
						&& (reporteResumenModel.getListaTransacciones() == null
								|| reporteResumenModel.getListaTransacciones().isEmpty())
						&& (reporteResumenModel.getListaCargos() == null
								|| reporteResumenModel.getListaCargos().isEmpty()))
					UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "",
							"No se encontró ningún registro en el rango de fechas seleccionadas.");
			} else {
				LimpiarListasResumen();
				UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR, "",
						"Fecha inicial debe estar antes o ser igual que la fecha final.");
			}
		} catch (InternalServiceException ise) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(ise.getMessage());
		}
	}

	private void LimpiarListasResumen() {
		reporteResumenModel.setListaTarjetas(null);
		reporteResumenModel.setListaTransacciones(null);
		reporteResumenModel.setListaCargos(null);
	}

	public void customizeXLS(Object document) {
		
		String titulo = "";
		String nombreHoja = "";
		
		switch (reporteResumenModel.getTipoReporteSeleccionado()) {
		case 1:
			titulo = "Reporte de Tarjeta";
			nombreHoja = "Lista Tarjeta";
			break;
		case 2:
			titulo = "Reporte de Transacciones";
			nombreHoja = "Lista Transacciones";
			break;
		case 3:
			titulo = "Reporte de Cargos";
			nombreHoja = "Lista Cargos";
			break;
		}
		
		// Casting del documento a un HSSFWorkbook
		HSSFWorkbook workbook = (HSSFWorkbook) document;
		workbook.setSheetName(0, nombreHoja);
		Sheet sheet = workbook.getSheetAt(0); // Acceder a la primera hoja

		sheet.shiftRows(0, sheet.getLastRowNum(), 1); // Mover todo hacia abajo desde la fila 0

		// Crear la nueva fila al inicio para el título
		Row titleRow = sheet.createRow(0); // Ahora la fila 0 está vacía
		Cell titleCell = titleRow.createCell(0); // Primera celda

		// Agregar el texto del título
		titleCell.setCellValue(titulo);

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

		CellStyle labelStyle = workbook.createCellStyle();
		Font labelFont = workbook.createFont();
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // Negrita
		labelFont.setColor(HSSFColor.WHITE.index);

		labelStyle.setFont(labelFont);
		labelStyle.setFillForegroundColor(HSSFColor.DARK_RED.index);
		labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		CellStyle columnStyle = workbook.createCellStyle();
		Font columnFont = workbook.createFont();
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
		int lastColumn = sheet.getRow(2).getLastCellNum() - 1; // Última columna en la fila de datos
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastColumn)); // Combinar columnas

		// Agregar datos al final
		int lastRowIndex = sheet.getLastRowNum() + 2; // Espacio después de los datos

		// Estilo para etiquetas

		Row additionalDatFecha = sheet.createRow(lastRowIndex); // Fila para datos adicionales

		// Celda: Dato (Etiqueta)
		Cell dateLabelCell = additionalDatFecha.createCell(0);
		dateLabelCell.setCellValue("Fecha y Hora:");
		dateLabelCell.setCellStyle(labelStyle);

		// Celda: Valor
		Cell dateValueCell = additionalDatFecha.createCell(1);
		dateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		Row additionalDataRow = sheet.createRow(lastRowIndex + 1); // Fila para datos adicionales

		// Celda: Dato (Etiqueta)
		Cell userLabelCell = additionalDataRow.createCell(0);
		userLabelCell.setCellValue("Generado por:");
		userLabelCell.setCellStyle(labelStyle);

		// Celda: Valor
		Cell userValueCell = additionalDataRow.createCell(1);
		userValueCell.setCellValue(UsefulWebApplication.obtenerUsuario().getUsername());

		// Ajustar automáticamente el ancho de las columnas
		for (int i = 0; i <= lastColumn; i++) {
			sheet.autoSizeColumn(i);
		}
	}
}
