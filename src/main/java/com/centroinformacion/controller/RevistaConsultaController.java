package com.centroinformacion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.centroinformacion.entity.Revista;
import com.centroinformacion.service.RevistaService;
import com.centroinformacion.util.AppSettings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@RestController
@RequestMapping("/url/consultaRevista")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class RevistaConsultaController {

    @Autowired
    private RevistaService revistaService;

    @GetMapping("/consultaRevistaPorParametros")
    @ResponseBody
    public ResponseEntity<?> consultaRevistaPorParametros(
            @RequestParam(name = "nombre", required = true, defaultValue = "") String nombre,
            @RequestParam(name = "frecuencia", required = true, defaultValue = "") String frecuencia,
            @RequestParam(name = "fecDesde", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecDesde,
            @RequestParam(name = "fecHasta", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecHasta,
            @RequestParam(name = "estado", required = true, defaultValue = "") int estado,
            @RequestParam(name = "idPais", required = false, defaultValue = "-1") int idPais,
            @RequestParam(name = "idTipo", required = false, defaultValue = "-1") int idTipo) {
        List<Revista> lstSalida = revistaService.listaConsultaCompleja(
                "%" + nombre + "%", "%" + frecuencia + "%", fecDesde, fecHasta, estado, idPais, idTipo);

        return ResponseEntity.ok(lstSalida);
    }

    // excel
    private static String[] HEADERs = {"CÓDIGO", "NOMBRE", "FRECUENCIA", "FECHA CREACIÓN", "ESTADO", "PAÍS", "TIPO"};
    private static String SHEET = "Listado de Revista";
    private static String TITLE = "Listado de Revista - Autor: Jorge Jacinto";
    private static int[] HEADER_WITH = {3000, 10000, 6000, 6000, 6000, 6000, 6000};

    @PostMapping("/reporteRevistaExcel")
    public void reporteExcel(
    		
    		 @RequestParam(name = "nombre", required = true, defaultValue = "") String nombre,
             @RequestParam(name = "frecuencia", required = true, defaultValue = "") String frecuencia,
             @RequestParam(name = "fecDesde", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecDesde,
             @RequestParam(name = "fecHasta", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecHasta,
             @RequestParam(name = "estado", required = true, defaultValue = "") int estado,
             @RequestParam(name = "idPais", required = false, defaultValue = "-1") int idPais,
             @RequestParam(name = "idTipo", required = false, defaultValue = "-1") int idTipo,
    		HttpServletRequest request, HttpServletResponse response) {

        try (Workbook excel = new XSSFWorkbook()) {
            // Se crea la hoja de Excel
            Sheet hoja = excel.createSheet(SHEET);
            // Agrupar
            hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADER_WITH.length - 1));

            // Se establece el ancho de las columnas
            for (int i = 0; i < HEADER_WITH.length; i++) {
                hoja.setColumnWidth(i, HEADER_WITH[i]);
            }

            // Fuente
            Font fuente = excel.createFont();
            fuente.setFontHeightInPoints((short) 10);
            fuente.setFontName("Arial");
            fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex());

         // Estilo
         			CellStyle estiloCeldaCentrado = excel.createCellStyle();
         			estiloCeldaCentrado.setWrapText(true);
         			estiloCeldaCentrado.setAlignment(HorizontalAlignment.CENTER);
         			estiloCeldaCentrado.setVerticalAlignment(VerticalAlignment.CENTER);
         			estiloCeldaCentrado.setFont(fuente);
         			estiloCeldaCentrado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
         			estiloCeldaCentrado.setFillPattern(FillPatternType.SOLID_FOREGROUND);


         	        // Estilo para datos
         	        CellStyle estiloDatosCentrado = excel.createCellStyle();
         	        estiloDatosCentrado.setAlignment(HorizontalAlignment.CENTER);
         	        estiloDatosCentrado.setVerticalAlignment(VerticalAlignment.CENTER);
         	        estiloDatosCentrado.setBorderBottom(BorderStyle.THIN);
         	        estiloDatosCentrado.setBorderTop(BorderStyle.THIN);
         	        estiloDatosCentrado.setBorderLeft(BorderStyle.THIN);
         	        estiloDatosCentrado.setBorderRight(BorderStyle.THIN);
         	        
         	        CellStyle estiloDatosIzquierdo = excel.createCellStyle();
         	        estiloDatosIzquierdo.setAlignment(HorizontalAlignment.LEFT);
         	        estiloDatosIzquierdo.setVerticalAlignment(VerticalAlignment.CENTER);
         	        estiloDatosIzquierdo.setBorderBottom(BorderStyle.THIN);
         	        estiloDatosIzquierdo.setBorderTop(BorderStyle.THIN);
         	        estiloDatosIzquierdo.setBorderLeft(BorderStyle.THIN);
         	        estiloDatosIzquierdo.setBorderRight(BorderStyle.THIN);
            // Fila 0
            Row fila1 = hoja.createRow(0);
            Cell celAuxs = fila1.createCell(0);
            celAuxs.setCellStyle(estiloCeldaCentrado);
            celAuxs.setCellValue(TITLE);

            // Fila 1
            Row fila2 = hoja.createRow(1);
            Cell celAuxs2 = fila2.createCell(0);
            celAuxs2.setCellValue("");
            // Fila 2
            Row fila3 = hoja.createRow(2);
            for (int i = 0; i < HEADERs.length; i++) {
                Cell celda1 = fila3.createCell(i);
                celda1.setCellStyle(estiloCeldaCentrado);
                celda1.setCellValue(HEADERs[i]);
            }

            // formato para fecha
            
           //este no puesto que aqui lista todos :>> List<Revista> lstSalida = revistaService.listaTodos();
            // Fila 3....n
            //consulta

			List<Revista> lstSalida = revistaService.listaConsultaCompleja("%" + nombre + "%", "%" + frecuencia + "%",
					fecDesde, fecHasta, estado, idPais, idTipo);
			//Filas de datos
            int rowIdx = 3;
            for (Revista obj : lstSalida) {
                Row row = hoja.createRow(rowIdx++);

                Cell cel0 = row.createCell(0);
                cel0.setCellValue(obj.getIdRevista());

                Cell cel1 = row.createCell(1);
                cel1.setCellValue(obj.getNombre());

                Cell cel2 = row.createCell(2);
                cel2.setCellValue(obj.getFrecuencia());

                Cell cel3 = row.createCell(3);
                cel3.setCellValue(obj.getFechaCreacion());

                Cell cel4 = row.createCell(4);
                cel4.setCellValue(obj.getEstado() == 1 ? AppSettings.ACTIVO_DESC : AppSettings.INACTIVO_DESC);

                Cell cel5 = row.createCell(5);
                cel5.setCellValue(obj.getPais().getNombre());

                Cell cel6 = row.createCell(6);
                cel6.setCellValue(obj.getTipoRevista().getDescripcion());
            }

            // Tipo de archivo y nombre de archivo
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-disposition", "attachment; filename=ReporteRevista.xlsx");

            OutputStream outStream = response.getOutputStream();
            excel.write(outStream);
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    //==================================PDF====================================================
    @PostMapping("/reporteRevistaPDF")
    public void reportePDF(
            @RequestParam(name = "nombre", required = true, defaultValue = "") String nombre,
            @RequestParam(name = "frecuencia", required = true, defaultValue = "") String frecuencia,
            @RequestParam(name = "fecDesde", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecDesde,
            @RequestParam(name = "fecHasta", required = true, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecHasta,
            @RequestParam(name = "estado", required = true, defaultValue = "") int estado,
            @RequestParam(name = "idPais", required = false, defaultValue = "-1") int idPais,
            @RequestParam(name = "idTipo", required = false, defaultValue = "-1") int idTipo,
            HttpServletRequest request, HttpServletResponse response) {

        try {
            // PASO 1: Obtener datos para el reporte desde el servicio
            List<Revista> lstSalida = revistaService.listaConsultaCompleja(
                    "%" + nombre + "%", "%" + frecuencia + "%", fecDesde, fecHasta, estado, idPais, idTipo);
            
            // Convertir la lista de objetos Revista en una fuente de datos compatible con JasperReports
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lstSalida);

            // PASO 2: Cargar el diseño del reporte (archivo .jasper)// Aquí se obtiene la ubicación física del archivo
            String fileReporte = request.getServletContext().getRealPath("/reporteRevista.jasper");
            

            // PASO 3: Definir parámetros adicionales para el reporte 
            Map<String, Object> params = new HashMap<String, Object>();

            // Cargar el archivo JasperReport desde el archivo .jasper
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream(new File(fileReporte)));

            // PASO 4: Llenar el reporte con datos y parámetros para generar JasperPrint
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            // PASO 5: Configurar la respuesta HTTP para el archivo PDF
            response.setContentType("application/pdf"); 
            response.addHeader("Content-disposition", "attachment; filename=ReporteRevista.pdf"); 

            // PASO 6: Escribir el archivo PDF en el flujo de salida de la respuesta HTTP
            OutputStream outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

            // La generación del PDF se realiza directamente en la respuesta HTTP del navegador del usuario

        } catch (Exception e) {
            e.printStackTrace();  // Manejo básico de errores: imprimir la traza de la excepción
        }
    }


    

}
