package pe.bn.com.sate.ope.application.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import pe.bn.com.sate.ope.application.model.RendicionCuentasTarjetaModel;
import pe.bn.com.sate.ope.infrastructure.exception.ExternalServiceMCProcesosException;
import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.ReporteResumenFacade;
import pe.bn.com.sate.ope.infrastructure.service.internal.TarjetaService;
import pe.bn.com.sate.ope.transversal.dto.sate.CuentaTarjeta;
import pe.bn.com.sate.ope.transversal.dto.sate.DatosTarjetaCliente;
import pe.bn.com.sate.ope.transversal.util.StringsUtils;
import pe.bn.com.sate.ope.transversal.util.UsefulWebApplication;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
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
	private @Autowired TarjetaService tarjetaService;
	
	@PostConstruct
	public void init() {
		rendicionCuentasTarjetaModel = new RendicionCuentasTarjetaModel();
		rendicionCuentasTarjetaModel.setListaCuentasTarjeta(new ArrayList<CuentaTarjeta>());
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

	public void seleccionarTarjeta() {
		rendicionCuentasTarjetaModel.getDatosTarjetaCliente()
				.setTarjeta(rendicionCuentasTarjetaModel.getTarjetaSeleccionada());
 		buscarRendicionCuentas();
	}

	public void buscarListaTarjetas() {
		try {
			String tipoTemp = rendicionCuentasTarjetaModel.getTipoBusqueda();
			String numdoc = rendicionCuentasTarjetaModel.getNumeroDocumento();
			if (rendicionCuentasTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				numdoc = StringsUtils.llenarCerosAlaIzquierdaV2(numdoc, 19);
			}
			DatosTarjetaCliente datosTarjetaCliente = tarjetaService.buscarDatosTarjetasCliente(tipoTemp, numdoc);
			rendicionCuentasTarjetaModel.setDatosTarjetaCliente(datosTarjetaCliente);
			rendicionCuentasTarjetaModel.getDatosTarjetaCliente().getCliente().setApCompleto(rendicionCuentasTarjetaModel.getDatosTarjetaCliente().getCliente().getApPaterno() + " " + rendicionCuentasTarjetaModel.getDatosTarjetaCliente().getCliente().getApMaterno());
			if (rendicionCuentasTarjetaModel.getTipoBusqueda().equals(TipoBusqueda.NUM_TARJETA.getId())) {
				buscarRendicionCuentas();
			} else {
				UsefulWebApplication.ejecutar("wvSeleccionarTajeta.show()");
				UsefulWebApplication.actualizarComponente("formSeleccionarTarjetaCuenta");
			}
		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (InternalServiceException e) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					e.getMessage(), e.getMessage());
			logger.error(e.getMessage());
		}
	}

	public void buscarRendicionCuentas() {
 		try {
			List<CuentaTarjeta>  cuentaTarjeta = reporteResumenFacade.obtenerRendicionCuentaTarjeta(rendicionCuentasTarjetaModel.getDatosTarjetaCliente().getTarjeta().getNumTarjeta());			
			rendicionCuentasTarjetaModel.setListaCuentasTarjeta(cuentaTarjeta);
 		} catch (ExternalServiceMCProcesosException este) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC,
					ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_MC);
			logger.error(este.getMessage());
		} catch (InternalServiceException e) {
			UsefulWebApplication.mostrarMensajeJSF(ConstantesGenerales.SEVERITY_ERROR,
					ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL, ConstantesGenerales.ERROR_PERSISTENCE_INTERNAL);
			logger.error(e.getMessage());
		} 
	}

	
public void customizeXLS(Object document) {
		
		String titulo = "Reporte de rendicion de cuentas";
		String nombreHoja = "Lista rendicion de cuentas";
		
	 
		
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