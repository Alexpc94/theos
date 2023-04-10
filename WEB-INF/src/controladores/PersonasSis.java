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

import model.domain.PersonalSis;
import model.manager.AccesoManager;
import model.manager.PersonalSisManager;
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
public class PersonasSis {
	
	@Autowired
	PersonalSisManager personalSisManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"personalSismon.html"})
	public String personalSismon(Model model,HttpServletRequest request)  throws IOException  {
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
			

			//Recuperando los menus, roles y procesos, falta recuperar privilegios
			//tomar en cuenta que los privilegios son enviados al template de roles
			VarGlobales var=new VarGlobales("11",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
			
			//PARAMETROS mensajes
/*
			String xmen=request.getParameter("m"); //mensaje
			String xtex=request.getParameter("t"); //texto del mensaje
			if (xmen==null){xmen="0";xtex="0";}
			else {
				xmen=crypt.decrypt(xmen);
				xtex=crypt.decrypt(xtex);
			}
			model.addAttribute("mensaje", xmen);//OK
			model.addAttribute("menTexto", xtex);//OK
*/			
			//PARAMAMETROS 
			String xestado=request.getParameter("estado");
			if (xestado==null){ xestado="1";}
			int xest1=0, xest2=1;
			if (xestado.equals("1")){xest1=1;xest2=1;}
			if (xestado.equals("0")){xest1=0;xest2=0;}
			model.addAttribute("estado", Integer.parseInt(xestado));
			
			//recupera los roles de la base de datos
			List<?> lispersonal = this.personalSisManager.listarPersonal(xest1,xest2);
			model.addAttribute("xPersonal", lispersonal );	
			
			
//System.out.println("xpersonal="+lispersonal.size()+" lisAreas="+lisAreas.size()+" lisRutas="+lisRutas.size());			
			

			model.addAttribute("opAdd", crypt.encrypt("addPersonalSis"));
			model.addAttribute("opMod", crypt.encrypt("modPersonalSis"));
			model.addAttribute("opDel", crypt.encrypt("delPersonalSis"));
			model.addAttribute("opHab", crypt.encrypt("habPersonalSis"));
			model.addAttribute("opAddusu", crypt.encrypt("addUsuPersonal"));
			model.addAttribute("opModUsu", crypt.encrypt("modUsuPersonal"));
		}
		
		model.addAttribute("file1", comodoTpl+"/personalsis/personalsismon.vm");	
		return "marco";
	}
	
	
	
	
	@RequestMapping({"listaPersonalSis_det_11.html"})
	public String listarpersonalSis_11(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("11",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
		System.out.println(" LISTA PERSONAL 12::");
		System.out.println(" estado="+xestado);
		
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }
//		List<Productos> xlistarProductos = this.productosCManager.listarProductos(Integer.parseInt(xcodtipoPadre),xest1,xest2);
	
		//recupera los roles de la base de datos
		List<?> lispersonal = this.personalSisManager.listarPersonal(xest1,xest2);
		model.addAttribute("xPersonal", lispersonal );	
		
		return "personalsis/lisPersonalsis";
	}	
	
	@RequestMapping({"personaSisServices.html"})
	public String personalSisServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			//ADICIONAR PERSONAL
			if (op.equals("addPersonalSis")){				
				String ci=request.getParameter("a_ci");
				String nombre=request.getParameter("a_nombre");
				String ap=request.getParameter("a_ap");
				String am=request.getParameter("a_am");
				String telef=request.getParameter("a_telef");
				String celular=request.getParameter("a_cell");
				String email=request.getParameter("a_email");
				String genero=request.getParameter("a_genero");
				String ecivil=request.getParameter("a_ecivilRadios");
				String fnac=request.getParameter("a_fnac");

				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}

				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fnac);
				
System.out.println(" llego ="+ci+" nombre="+nombre+" ap="+ap+" am="+am+" telf="+telef+" cell="+celular+" email="+email+" genero="+genero+" ecivil="+ecivil+" fnac="+fechaNac);
//System.out.println(" llego ="+ci+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);		
				String error = this.personalSisManager.setAddPersonalSis(ci,nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),telef,celular,email,genero.toUpperCase(),ecivil.toUpperCase(),fechaNac);		
//System.out.println(" error ="+error);							
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//MODIFICAR PERSONA
			if (op.equals("modPersonalSis")){				
				String ci=request.getParameter("m_ci");
				String codper=request.getParameter("m_codper");
				String nombre=request.getParameter("m_nombre");
				String ap=request.getParameter("m_ap");
				String am=request.getParameter("m_am");
				String telef=request.getParameter("m_telef");
				String celular=request.getParameter("m_cell");
				String email=request.getParameter("m_email");
				String genero=request.getParameter("m_genero");
				String ecivil=request.getParameter("m_ecivil");
				String fechanac=request.getParameter("m_fechanac");
				
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fechanac);

//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" telef="+telef+" email="+email+" direc="+direc+" "+areas+" "+ecivil);
				
				String error = this.personalSisManager.setModPersonalSis(ci,Integer.parseInt(codper),nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),telef,celular,email,genero.toUpperCase(),ecivil.toUpperCase(),fechaNac);
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//ADICIONAR USUARIO
			if (op.equals("addUsuPersonal")){				
				String clave=request.getParameter("Usu_login");
				String codper=request.getParameter("Usu_codper");
				String login=request.getParameter("Usu_log");
				System.out.println(" llego ="+login+" "+clave+" "+codper);
				
				String error = this.personalSisManager.setAddUsuario(clave,Integer.parseInt(codper),login);
				
				System.out.println("CREAR USER... error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
				
			}
			
			//MODIFICAR USUARIO
			if (op.equals("modUsuPersonal")){				
				String codper=request.getParameter("Musu_codper");
				String clave=request.getParameter("Musu_login");
				String login=request.getParameter("Musu_log");
				System.out.println(" llego ="+ codper+clave+login);
				
				String error = this.personalSisManager.setModUsuario(Integer.parseInt(codper),clave,login);
				
//				System.out.println("MODIFICAR CLAVE... error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}	
			
			//ELIMINAR PERSONAL
			if (op.equals("delPersonalSis")){				
				String codper=request.getParameter("d_codper");
				
				String error = this.personalSisManager.setDelPersonalSis(Integer.parseInt(codper));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//HABILITAR PERSONAL
			if (op.equals("habPersonalSis")){				
				String codper=request.getParameter("h_codper");
				
				String error = this.personalSisManager.setHabPersonalSis(Integer.parseInt(codper));
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
		}
				
		model.addAttribute("file1", comodoTpl+"/personalsis/personalsismon.vm");	
		return "marco";
	}
	
	@RequestMapping("personalSisReportes01.html")
	  public void personasSisReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
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
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personalSis/personalSis01.jasper", "personalSis");	        
	  }
}
