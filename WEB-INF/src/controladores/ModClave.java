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
import model.manager.PersonalSisManager;
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
public class ModClave {

	@Autowired
	//GenerarBoletasManager generarBoletasManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	PersonalSisManager personalSisManager;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"modclave.html"})
	public String modclave70(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		
		String xusuario=(String) session.getAttribute("s_usuario");
		String xlogin=(String) session.getAttribute("s_login");
	//	System.out.println(xusuario+" "+xlogin);
		
		
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
			VarGlobales var=new VarGlobales("70",crypt.decrypt("1")); //params: (proceso, menu)
			model=var.getDatos(request,model);
			
			//RECUPERO LOS DATOS GENERALES PARA GENERAR LAS BOLETAS
		//	General xbol = this.generarBoletasManager.getGeneralUno();		
		//	model.addAttribute("xmesTexto", xbol.getMesFormat() );
		//	System.out.println(xusuario+" "+xlogin);
			model.addAttribute("Musu_nombres", xusuario );
			model.addAttribute("Musu_log", xlogin );
			
			model.addAttribute("opModUsu", crypt.encrypt("modUsuPersonal"));
			
		}		
		model.addAttribute("file1", comodoTpl+"/modclave/modclavemon.vm");	
		return "marco";
	}
	
	
	
	@RequestMapping({"claveServices.html"})
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
			
			//MODIFICAR USUARIO
			if (op.equals("modUsuPersonal")){				
				
				String clave=request.getParameter("Musu_login");
				
				System.out.println(" llego =" +clave+xlogin);
				
				String error = this.personalSisManager.setModUsuarioClave(clave,xlogin);
				
//				System.out.println("MODIFICAR CLAVE... error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}	
						
		}

		model.addAttribute("file1", comodoTpl+"/modclave/modclavemon.vm");	
		return "marco";
	}

}
