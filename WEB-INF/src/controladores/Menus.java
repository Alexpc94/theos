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

import model.manager.AccesoManager;
import model.manager.MenusManager;
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
import utils.VarGlobales;

@Controller
public class Menus {

	@Autowired
	MenusManager menusManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"menusmon.html"})
	public String menusmon(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("7",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
			
			//PARAMETROS mensajes
			String xmen=request.getParameter("m"); //mensaje
			String xtex=request.getParameter("t"); //texto del mensaje
			if (xmen==null){xmen="0";xtex="0";}
			else {
				xmen=crypt.decrypt(xmen);
				xtex=crypt.decrypt(xtex);
			}
			model.addAttribute("mensaje", xmen);//OK
			model.addAttribute("menTexto", xtex);//OK
			
			//PARAMAMETROS 
			String xestado=request.getParameter("estado");
			if (xestado==null){ xestado="1";}
			int xest1=0, xest2=1;
			if (xestado.equals("1")){xest1=1;xest2=1;}
			if (xestado.equals("0")){xest1=0;xest2=0;}
			model.addAttribute("estado", Integer.parseInt(xestado));
			
			//recupera los roles de la base de datos
			List<?> lisMenus = this.menusManager.listar(xest1,xest2);
			model.addAttribute("xMenus", lisMenus );
			model.addAttribute("opAdd", crypt.encrypt("addMenus"));
			model.addAttribute("opMod", crypt.encrypt("modMenus"));
			model.addAttribute("opHab", crypt.encrypt("habMenus"));
			model.addAttribute("opDel", crypt.encrypt("delMenus"));

		}
		
		model.addAttribute("file1", comodoTpl+"/menus/menusmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaMenus_det_7.html"})
	public String listaMenus_det_7(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("7",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }

		//LISTA MENUS - IDEM. listaMenusMON	
		List<?> lisMenus = this.menusManager.listar(xest1,xest2);
		model.addAttribute("xMenus", lisMenus );
		
		return "menus/lisMenus";
	}
	
	@RequestMapping({"menusServices.html"})
	public String menusServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		//verifica si el usuario se autentifico
		if (xusuario == null){
			//usuario quiso ingresar sin logearse
			return "/acceso/acceso";
		}else{
			
			//opcion que llega de los formularios
			String op=request.getParameter("opcion");
			if (op==null) {op="0";}
			else{ op=crypt.decrypt(op);}//descifra 
			
			//ADICIONAR MENUS
			if (op.equals("addMenus")){				
				String menuname=request.getParameter("menuname");
				System.out.println(" llego ="+menuname);
				
				String error = this.menusManager.setAddMenus(menuname.toUpperCase());
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//MODIFICAR MENUS
			if (op.equals("modMenus")){				
				String menuname=request.getParameter("m_nombre");
				String codm=request.getParameter("m_codm");
				
				String error = this.menusManager.setModMenus(menuname.toUpperCase(),Integer.parseInt(codm));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
				
			}
			//HABILITAR MENUS
			if (op.equals("habMenus")){				
				String codm=request.getParameter("h_codm");
				
				String error = this.menusManager.setHabMenus(Integer.parseInt(codm));
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}

			//ELIMIANR MENUS
			if (op.equals("delMenus")){				
				String codm=request.getParameter("d_codm");
				
				String error = this.menusManager.setDelMenus(Integer.parseInt(codm));
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//VarGlobales var=new VarGlobales("6");
		//model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/menus/menusmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("menusReportes01.html")
	  public void menusReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		  String xestado=req.getParameter("xest") ;
		  HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");
		  System.out.println(xestado);
		  int xest1=0, xest2=1;
		  if(xestado.equals("1")){
			  xest1=1; xest2=1;
		  }
		  if(xestado.equals("0")){
			  xest1=0; xest2=0;
		  }
		  System.out.println(xest1+" "+xest2);
		  Map<String,Object> params = new HashMap<>();

			params.put("xest1", xest1);
			params.put("xest2", xest2);
			params.put("responsable", xusuario);
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/menus/menus01.jasper", "menus");	        
	  }

}
