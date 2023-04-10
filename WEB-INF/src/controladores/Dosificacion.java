package controladores;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.velocity.runtime.directive.Parse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import model.manager.AccesoManager;
import model.manager.DosificacionManager;
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
public class Dosificacion {

	@Autowired
	DosificacionManager dosificacionManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"Dosificacionmon.html"})
	public String dosificacionmon(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("17",crypt.decrypt(cd)); //params: (proceso, menu)
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
			List<?> lisdosificacion = this.dosificacionManager.listar(xest1,xest2);
			model.addAttribute("xdosificacion", lisdosificacion);
			model.addAttribute("opAdd", crypt.encrypt("addDosificacion"));
			model.addAttribute("opMod", crypt.encrypt("modDosificacion"));
			model.addAttribute("opHab", crypt.encrypt("habDosificacion"));
			model.addAttribute("opDel", crypt.encrypt("delDosificacion"));

		}
		
		model.addAttribute("file1", comodoTpl+"/dosificacion/dosificacionmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaDosificacion_det_17.html"})
	public String listadosificacion_det_17(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("17",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }

		//LISTA MENUS - IDEM. listaMenusMON	
		List<?> lisdosificacion = this.dosificacionManager.listar(xest1,xest2);
		model.addAttribute("xdosificacion", lisdosificacion);
		
		return "dosificacion/lisDosificacion";
	}
	
	@RequestMapping({"dosificacionServices.html"})
	public String tcambioServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			//ADICIONAR DOSIFICACION
			if (op.equals("addDosificacion")){				
				String nrotram=request.getParameter("a_nrotram");
				String autorizacion=request.getParameter("a_autorizacion");
				String fec=request.getParameter("a_fechalimite");
				String numfac=request.getParameter("a_numfac");
				String llave=request.getParameter("a_llave");
				String ley=request.getParameter("a_ley");
				int sw=1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechalimite = dateformat.parse(fec);
				

				System.out.println(" llego ="+xlogin+" "+nrotram+" "+autorizacion+" "+fec+" "+numfac+" "+llave+" "+ley+" ");
				String error = this.dosificacionManager.setAddDosificacion(nrotram,autorizacion,fechalimite,Integer.parseInt(numfac),llave,sw,ley,xlogin);
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//MODIFICAR DOSIFICACION
			if (op.equals("modDosificacion")){				
				String antnrotram=request.getParameter("ma_nrotram");
				String nrotram=request.getParameter("m_nrotram");
				String autorizacion=request.getParameter("m_autorizacion");
				String fec=request.getParameter("m_fechalimite");
				String numfac=request.getParameter("m_numfac");
				String llave=request.getParameter("m_llave");
				String ley=request.getParameter("m_ley");
				String login=request.getParameter("m_login");
			
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechalimite = dateformat.parse(fec);
				
				//System.out.println(" llego ="+login+" "+nrotram+" "+autorizacion+" "+fec+" "+numfac+" "+llave+" "+ley+" ");
				String error = this.dosificacionManager.setModDosificacion(antnrotram,nrotram,autorizacion,fechalimite,Integer.parseInt(numfac),llave,ley,login);

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
		
		model.addAttribute("file1", comodoTpl+"/dosificacion/dosificacionmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("dosificacionReportes01.html")
	  public void dosificacionReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/dosificaion/dosificacion01.jasper", "dosificacion");	        
	  }

}
