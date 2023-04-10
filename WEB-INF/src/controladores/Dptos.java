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
import model.manager.DptosManager;
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
public class Dptos {

	@Autowired
	DptosManager dptosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"dptosmon.html"})
	public String dptosmon(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("9",crypt.decrypt(cd)); //params: (proceso, menu)
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
			List<?> lisDptos = this.dptosManager.listar(xest1,xest2);
			model.addAttribute("xDptos", lisDptos );
			model.addAttribute("opAdd", crypt.encrypt("addDptos"));
			model.addAttribute("opMod", crypt.encrypt("modDptos"));
			model.addAttribute("opHab", crypt.encrypt("habDptos"));
			model.addAttribute("opDel", crypt.encrypt("delDptos"));

		}
		
		model.addAttribute("file1", comodoTpl+"/dptos/dptosmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaDptos_det_12.html"})
	public String listaDptos_det_12(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("9",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }

		//LISTA MENUS - IDEM. listaMenusMON	
		List<?> lisDptos = this.dptosManager.listar(xest1,xest2);
		model.addAttribute("xDptos", lisDptos );
		
		return "dptos/lisDptos";
	}
	
	@RequestMapping({"dptosServices.html"})
	public String dptosServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
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
			
			//ADICIONAR DEPARTAMENTOS
			if (op.equals("addDptos")){				
				String dptoname=request.getParameter("dptoname");
				System.out.println(" llego ="+dptoname);
				
				String error = this.dptosManager.setAddDptos(dptoname.toUpperCase());
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//MODIFICAR DEPARTAMENTOS
			if (op.equals("modDptos")){				
				String dptoname=request.getParameter("m_nombre");
				String codd=request.getParameter("m_codd");
				
				String error = this.dptosManager.setModDptos(dptoname.toUpperCase(),Integer.parseInt(codd));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
				
			}
			//HABILITAR DEPARTAMENTOS
			if (op.equals("habDptos")){				
				String codd=request.getParameter("h_codd");
				
				String error = this.dptosManager.setHabDptos(Integer.parseInt(codd));
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}

			//ELIMIANR DEPARTAMENTOS
			if (op.equals("delDptos")){				
				String codd=request.getParameter("d_codd");
				
				String error = this.dptosManager.setDelDptos(Integer.parseInt(codd));
				
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
		
		model.addAttribute("file1", comodoTpl+"/dptos/dptosmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("dptosReportes01.html")
	  public void dptosReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
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
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/dptos/dptos01.jasper", "departamentos");	        
	  }

}
