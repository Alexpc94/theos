package controladores;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import model.domain.General;
//import model.manager.GenerarBoletasManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import utils.GenerarReportes;
import utils.Jencryption;
import utils.Utilitarios;
import utils.VarGlobales;

@Controller
public class Tester {

	@Autowired
	//GenerarBoletasManager generarBoletasManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"tester20.html"})
	public String tester20(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		
		String xusuario=(String) session.getAttribute("s_usuario");
		//verifica si el usuario se autentifico
		if (xusuario == null){
			//usuario quiso ingresar sin logearse
			return "/acceso/acceso";
		}else{
			Jencryption crypt=new Jencryption();
			
			String cd=request.getParameter("cd");
			if (cd==null){ 				
				cd=(String) session.getAttribute("s_menuActivo");
			}else{
				session.setAttribute("s_menuActivo", cd );
			}
			
			//System.out.println("dato crypting="+cd);
			//Recuperando los menus, roles y procesos, falta recuperar privilegios
			//tomar en cuenta que los privilegios son enviados al template de roles
			VarGlobales var=new VarGlobales("20",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
			
			//RECUPERO LOS DATOS GENERALES PARA GENERAR LAS BOLETAS
		//	General xbol = this.generarBoletasManager.getGeneralUno();		
		//	model.addAttribute("xmesTexto", xbol.getMesFormat() );
		//	model.addAttribute("xmes", xbol.getMes() );
		//	model.addAttribute("xanio", xbol.getAnio() );
			
			model.addAttribute("opAdd", crypt.encrypt("addTester"));
		}		
		model.addAttribute("file1", comodoTpl+"/tester/testermon.vm");	
		return "marco";
	}
	
	
	
	@RequestMapping({"testerServices.html"})
	public String generarBoletasServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		String xlogin=(String) session.getAttribute("s_login");
		//verifica si el usuario se autentifico
		if (xusuario == null){
			//usuario quiso ingresar sin logearse
			return "/acceso/acceso";
		}else{
			
			//opcion que llega de los formularios
			String op=request.getParameter("opcion");
			if (op==null) {op="0";}
			else{ op=crypt.decrypt(op);}//descifra 
			
			//ADICIONAR DEPARTAMENTOS
			if (op.equals("addTester")){	
				String nroautorizacion=request.getParameter("xnroautorizacion");
				String nrofac=request.getParameter("xnrofac");
				String cinit=request.getParameter("xcinit");
				String fecha=request.getParameter("xfecha");
				String monto=request.getParameter("xmonto");
				String llave=request.getParameter("xllave");
				
				System.out.println("llego="+" nroautorizacion "+ nroautorizacion+" nrofac "+nrofac+" cinit "+cinit+" fecha "+fecha+" monto "+monto+" llave "+llave);
				
				//String error = this.generarBoletasManager.setAddBoletas(Integer.parseInt(xmes),Integer.parseInt(xanio),xlogin);
				//System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				//object.addProperty("error", error);
				object.addProperty("error", 1);
				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
						
		}

		model.addAttribute("file1", comodoTpl+"/tester/testermon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("TesterReportes01.html")
	  public void dptosReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/dptos/dptos01.jasper", "tester");	        
	  }

}
