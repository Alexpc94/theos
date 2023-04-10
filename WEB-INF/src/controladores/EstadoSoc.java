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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import model.manager.AccesoManager;
import model.manager.EstadoSocManager;
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
public class EstadoSoc {

	@Autowired
	EstadoSocManager estadosocManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"EstadoSocmon.html"})
	public String estadoSocmon(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		Utilitarios u=new Utilitarios();
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
			VarGlobales var=new VarGlobales("4",crypt.decrypt(cd)); //params: (proceso, menu)
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
			
			
			
			//recupera los roles de la base de datos
			List<?> lisEstSoc= this.estadosocManager.listar(1,1);
			model.addAttribute("xEstSoc", lisEstSoc);
			
			model.addAttribute("opMod", crypt.encrypt("modEstSoc"));
			model.addAttribute("opAdd", crypt.encrypt("addEstSoc"));
			
		}
		
		model.addAttribute("xfecha", u.getFecha());//ENVIA LA FECHA ACTUAL DEL SISTEMA
		model.addAttribute("file1", comodoTpl+"/EstSoc/estadoSocmon.vm");	
		return "marco";
	}
	// AQUI SE LLAMA A LOS ESTADOS DE LOS SOCIOS
		@RequestMapping({"detalle_costo4.html"})
		public String detalle_estado12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
			String xcodes=request.getParameter("xcodes");
			String xnombre=request.getParameter("xnombre");
			System.out.println(xcodes);
			//recupera los roles de la base de datos
			List<?> liscostos_estados = this.estadosocManager.listarCosto_estado(Integer.parseInt(xcodes));
			model.addAttribute("xCostos_estados", liscostos_estados );
			
			model.addAttribute("xcodes", xcodes );
			model.addAttribute("xnombre", xnombre );
			return "EstSoc/CostosDet";	
		}
	@RequestMapping({"listaEstSoc_det_4.html"})
	public String listaEstSoc_det_4(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("4",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);

		//LISTA MENUS - IDEM. listaMenusMON	
		List<?> lisEstSoc= this.estadosocManager.listar(1,1);
		model.addAttribute("xEstSoc", lisEstSoc);
		
		return "EstSoc/lisEstSoc";
	}
	
	@RequestMapping({"estSocServices.html"})
	public String estSocServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			
			
			//MODIFICAR Costo
			if (op.equals("modEstSoc")){				
				String costo=request.getParameter("m_costo");
				String codes=request.getParameter("m_codes");
				String codest=request.getParameter("m_codest");
				String fecha=request.getParameter("m_fecha");
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaF = dateformat.parse(fecha);
				
				System.out.println(" llego="+costo+" "+ codes+" "+xlogin);
				String error = this.estadosocManager.setModEstSoc(Double.parseDouble(costo),Integer.parseInt(codes),Integer.parseInt(codest),fechaF,xlogin);

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
				
			}
			
			//ADICIONAR Costo
			if (op.equals("addEstSoc")){				
				String costo=request.getParameter("a_costo");
				String codes=request.getParameter("a_codes");
				String fecha=request.getParameter("a_fecha");
				int sw= 1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaF = dateformat.parse(fecha);
				
				System.out.println(" llego="+costo+" "+ codes+" "+xlogin+" "+sw);
				String error = this.estadosocManager.setAddEstSoc(Double.parseDouble(costo),Integer.parseInt(codes),fechaF,xlogin,sw);

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
		
		model.addAttribute("file1", comodoTpl+"/EstSoc/estadoSocmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("estadoSocReportes01.html")
	  public void estadoSocReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
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
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/estadoSoc/estadoSoc01.jasper", "Estado Socios");	        
	  }

}
