package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class GenerarReportes {
	
	//GENERAR PDF
	public String generadorReportes(HttpServletResponse res, 
									HttpServletRequest req, 
									Connection conn, 
									Map<String,Object> params,
									String xpath,
									String fileSalida
									) throws IOException {		
		HttpSession sesion = req.getSession();
		String rutaCompleta = sesion.getServletContext().getRealPath(xpath);
		
	   
		
		byte[] bytes;
		try {
			bytes = JasperRunManager.runReportToPdf(rutaCompleta,params,conn);
			res.reset();
			res.setContentType("application/pdf");		    					
			res.setHeader ("Content-disposition", "inline; filename="+fileSalida+"_"+Math.random()+".pdf");
			res.setHeader ("Cache-Control", "max-age=30");
			res.setHeader ("Pragma", "No-cache");
			res.setDateHeader ("Expires", 0);
			res.setContentLength(bytes.length);
		    ServletOutputStream ouputStream = res.getOutputStream();
		    
		    ouputStream.write(bytes, 0, bytes.length);
		    ouputStream.flush();
		    ouputStream.close();

		    //si todo va bien retorna -  
		    //if (true) return "1";
		} catch (JRException e) {		    					
			e.printStackTrace();
			System.err.println("Exception caught:" + e.getMessage());
		}
		
		return "1";
	}

	//String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMes.jasper", "Socios");
	//GENERAR EXCEL
	public void generadorReportesToExcel(
			HttpServletResponse res, 
			HttpServletRequest req, 
			Connection conn, 
			Map<String,Object> params,
			String xpath,
			String fileSalida
			) throws IOException, JRException {
	
		File reportFile = new File(req.getRealPath(xpath));	
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath (), params, conn); 

		// Mostrando el documento
//		res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		ServletOutputStream servletOutputStream = res.getOutputStream();
		res.setHeader("Content-disposition", "attachment; filename=" + "reports/"+fileSalida+ ".xls");
		res.setContentType("application/vnd.ms-excel");
		//httpServletResponse.setContentLength(arrayOutputStream.toByteArray().length);

		JRXlsExporter exporterXLS = new JRXlsExporter();

		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporterXLS.exportReport();

		servletOutputStream.flush();
        servletOutputStream.close();
		
//		FacesContext.getCurrentInstance().responseComplete();
		
	}
	
	//genera archivo de texto XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxx
		//u.generaTextFile222(request, response, reportFile, parametros, sqlMap, "/", "Ventas_"+mfin+zgestion+"_"+znit);
	//GENERAR TEXTO
		public void generaTextFile222(
				HttpServletResponse res, 
				HttpServletRequest req, 
				Connection conn, 
				Map<String,Object> params,
				String xpath,
				String fileSalida
				) throws IOException, JRException {
					File reportFile = new File(req.getRealPath(xpath));	
					try {  
			            //used for synchronization between database and source file.  
			            JasperPrint print = JasperFillManager.fillReport(reportFile.getPath (), params, conn);  
			          
			            //desde aqui
			            JRCsvExporter exporter = new JRCsvExporter();
			            exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, "|");
			            ByteArrayOutputStream baos = new ByteArrayOutputStream();
			            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,"ISO-8859-1");
			            
			            exporter.setParameter(JRCsvExporterParameter.RECORD_DELIMITER, "\r\n"); 
			            
			            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			           // exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);//prueba
			            exporter.exportReport();
			            byte[] output = baos.toByteArray();
			            res.setContentType("text/plain");
			            res.setContentLength(output.length);
//			            ServletOutputStream ouputStream = response.getOutputStream();
			            
			          //File f=new File(request.getRealPath(Velocity.getProperty("ceramica.reportes") +pathName+fileName+".txt"));
			          //FileInputStream fin = new FileInputStream(f);
			            ServletOutputStream outStream = res.getOutputStream();
			            // SET THE MIME TYPE.
			            res.setContentType("application/text");
			            // set content dispostion to attachment in with file name.
			            // case the open/save dialog needs to appear.
			            res.setHeader("Content-Disposition", "attachment;filename="+fileSalida+".txt");

			            //byte[] buffer = new byte[1024];
			          //int n = 0;
			          //while ((n = fin.read(output)) != -1) {
			            //outStream.write(output, 0, n);
			            //System.out.println(output);
			          //}
			           //hasta aqui 
			            outStream.write(output);
			            outStream.flush();
		            //fin.close();
			            outStream.close();

			            
			            /*
			            ouputStream.write(output);
			            ouputStream.flush();
			            ouputStream.close();
			          	*/	            
			            
			        } catch (JRException e) {  
			            e.printStackTrace();  
			        }
			}	
	
	public String generadorReportesTres(HttpServletResponse res, 
										HttpServletRequest req, 
										Connection conn, 
										Map<String,Object> params,
										String xpath,
										String fileSalida,
										String copia2,
										String copia3
			) throws IOException, DocumentException {		
			HttpSession sesion = req.getSession();
			String rutaCompleta = sesion.getServletContext().getRealPath(xpath);
			
			File reportFile = new File(req.getRealPath(xpath));		    				
			byte[] bytes, bytes2, bytes3;
			try {
				bytes = JasperRunManager.runReportToPdf(reportFile.getPath (),params,conn);
				params.put("tipo", copia2);
				bytes2 = JasperRunManager.runReportToPdf(reportFile.getPath (),params,conn);
				params.put("tipo", copia3);
				bytes3 = JasperRunManager.runReportToPdf(reportFile.getPath (),params,conn);
			/*<Concatenamos los dos reportes>*/
				PdfReader reader1 = new PdfReader(bytes);
				PdfReader reader2 = new PdfReader(bytes2);
				PdfReader reader3 = new PdfReader(bytes3);
				PdfCopyFields copy = new PdfCopyFields(new FileOutputStream(req.getRealPath("reports/uno.pdf")));
				copy.addDocument(reader1);
				copy.addDocument(reader2);
				copy.addDocument(reader3);
				copy.close();
			/*<Concatenamos los dos reportes>*/
				res.setContentType("application/pdf");		    					
				res.setHeader ("Content-disposition", "inline; filename=uno.pdf");
				res.setHeader ("Cache-Control", "max-age=30");
				res.setHeader ("Pragma", "No-cache");
				res.setDateHeader ("Expires", 0);
					    	
				/*<Se lee el Archivo pdf que creamos>*/
					FileInputStream archivo = new FileInputStream(req.getRealPath("reports/uno.pdf")); 
				/*<Se crea una variable de salida para el navegador>*/
					ServletOutputStream outs = res.getOutputStream();			    				
				/*<Se lee el contenido del Archivo PDF y se envia a la variable de salida>*/
					int bit = 1256;
					int i = 0;
			
					while ((bit) >= 0) {
						bit = archivo.read();
						outs.write(bit);
					}			    					
				/*<Forzamos a enviar los datos al navegador>*/
					outs.flush(); 
				/*<Se cierra la variable de salida y el archivo PDF>*/
					outs.close();
					archivo.close();
				
			} catch (JRException e) {		    					
				e.printStackTrace();
				System.err.println("1.-Exception caught:" + e.getMessage());
			}			
			return "1";
	}
	
	/*
	//String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMes.jasper", "Socios");
	//es la otra alternativa y funciona bien.
	public void generadorReportesToExcel_dos(
			HttpServletResponse res, 
			HttpServletRequest req, 
			Connection conn, 
			Map<String,Object> params,
			String xpath,
			String fileSalida
			) throws IOException, JRException {
		
		try {  
			File reportFile = new File(req.getRealPath(xpath));	
            //used for synchronization between database and source file.  
            JasperPrint print = JasperFillManager.fillReport(reportFile.getPath (), params, conn);  
            //define and initialize jasperreport engine's exporter for .pdf  
            JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();  
            //parameter used for the destined file.  
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, req.getRealPath("reports/"+fileSalida+".pdf"));
            
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);  
            //export to .pdf  
            exporter.exportReport();  
  		    		    
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
            
            JRXlsExporter exporterXLS = new JRXlsExporter();  

            exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.FALSE);  
            exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,Boolean.TRUE);  
            exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,Boolean.FALSE);  
            exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);
            
            exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true);

            //parameter used for the destined file.   
            exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME,req.getRealPath("reports/"+fileSalida+".xls"));		    		            
            exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);  
            //export to .xls  
            exporterXLS.exportReport();
            
            File f=new File(req.getRealPath("reports/"+fileSalida+".xls"));
            FileInputStream fin = new FileInputStream(f);
            ServletOutputStream outStream = res.getOutputStream();
            // SET THE MIME TYPE.
            res.setContentType("application/vnd.ms-excel");
            // set content dispostion to attachment in with file name.
            // case the open/save dialog needs to appear.
            res.setHeader("Content-Disposition", "attachment;filename="+fileSalida+".xls");
            byte[] buffer = new byte[1024];
	            int n = 0;
	            while ((n = fin.read(buffer)) != -1) {
	            outStream.write(buffer, 0, n);
            }
            outStream.flush();
            fin.close();
            outStream.close();		    		            
        } catch (JRException e) {  
            e.printStackTrace();  
        }				
	}
	*/
	
}
