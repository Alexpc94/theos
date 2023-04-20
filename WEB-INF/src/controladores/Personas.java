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
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import model.domain.Personal;
import model.domain.Dirdom;
import model.domain.Dirtrab;
import model.domain.General;
import model.domain.Permiso;
import model.domain.Datosant;
import model.manager.AccesoManager;

import model.manager.PersonasManager;

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
public class Personas {
	
	@Autowired
	PersonasManager personasManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"personalmon.html"})
	public String personalmon(Model model,HttpServletRequest request)  throws IOException  {
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
			
			//Recuperando los menus, roles y procesos, falta recuperar privilegios
			//tomar en cuenta que los privilegios son enviados al template de roles
			VarGlobales var=new VarGlobales("12",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
					
			//PARAMAMETROS 
			String xestado=request.getParameter("estado");
/*			
System.out.println("estado=");			
			if (xestado==null){ xestado="1";}
			int xest1=0, xest2=1;
			if (xestado.equals("1")){xest1=1;xest2=1;}
			if (xestado.equals("0")){xest1=0;xest2=0;}
			model.addAttribute("estado", Integer.parseInt(xestado));
			model.addAttribute("activos", 1);
*/			
			//recupera los roles de la base de datos
			List<?> lispersonal = this.personasManager.listarPersonal(1,1,1);
			model.addAttribute("xPersonal", lispersonal );	
			List<?> lislugar = this.personasManager.listarlugar(1,1);
			model.addAttribute("xlugar", lislugar );
			
			model.addAttribute("activos", 1);
			
//System.out.println("xpersonal="+lispersonal.size()+" lisAreas="+lisAreas.size()+" lisRutas="+lisRutas.size());			
			
			model.addAttribute("opAdd", crypt.encrypt("addPersonal"));
			model.addAttribute("opMod", crypt.encrypt("modPersonal"));
			model.addAttribute("opDel", crypt.encrypt("delPersonal"));
			model.addAttribute("opHab", crypt.encrypt("habPersonal"));
			model.addAttribute("opAddD", crypt.encrypt("addDom"));
			model.addAttribute("opModD", crypt.encrypt("modDom"));
			model.addAttribute("opDelD", crypt.encrypt("delDom"));
			model.addAttribute("opAddT", crypt.encrypt("addTrab"));
			model.addAttribute("opModT", crypt.encrypt("modTrab"));
			model.addAttribute("opDelT", crypt.encrypt("delTrab"));
			model.addAttribute("opAddE", crypt.encrypt("addEstado"));
			model.addAttribute("opModE", crypt.encrypt("modEstado"));
			model.addAttribute("opAddB", crypt.encrypt("addBeneficiario"));
			model.addAttribute("opModB", crypt.encrypt("modBeneficiario"));
			model.addAttribute("opHabB", crypt.encrypt("habBen"));
			model.addAttribute("opDelB", crypt.encrypt("delBen"));
			model.addAttribute("opAddusu", crypt.encrypt("addUsuPersonal"));
			model.addAttribute("opModUsu", crypt.encrypt("modUsuPersonal"));
			model.addAttribute("opModf", crypt.encrypt("modFacPersonal"));
			model.addAttribute("opAddAnt", crypt.encrypt("addAntiguedad"));
			model.addAttribute("opdelAnt", crypt.encrypt("delAntiguedad"));
			
			model.addAttribute("opActivo", crypt.encrypt("activoPersonal"));
			model.addAttribute("opDesactivo", crypt.encrypt("desactivoPersonal"));
			
			model.addAttribute("xfecha", u.getFecha());//ENVIA LA FECHA ACTUAL DEL SISTEMA
		}
		model.addAttribute("file1", comodoTpl+"/personal/personalmon.vm");	
		return "marco";
	}
	
	// AQUI SE LLAMA A LOS ESTADOS DE LOS SOCIOS
	@RequestMapping({"detalle_lista_estado12.html"})
	public String detalle_lista_estado12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodper=request.getParameter("xcodper");		
		String xestadosoc=request.getParameter("xestadosoc");
		String xcodes=request.getParameter("xcodes");
//System.out.println("---------xcodes="+xcodes);		
		List<?> lisestsoc = null;
		if (xestadosoc.equals("-")){
			lisestsoc = this.personasManager.listarestadosoc333(1,1);
		} else {
			lisestsoc = this.personasManager.listarestadosoc222(1,1,Integer.parseInt(xcodper));
		}

		model.addAttribute("xestsoc", lisestsoc );
		model.addAttribute("xcodes", xcodes);

		return "personal/listaEstados";	
	}
	
	// AQUI SE LLAMA A LOS ESTADOS DE LOS SOCIOS
	@RequestMapping({"detalle_lista_estado_mod12.html"})
	public String detalle_lista_estado_mod12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodper=request.getParameter("xcodper");	
		String xcodes=request.getParameter("xcodes");	
		
		List<?> lisestsoc = null;
		lisestsoc = this.personasManager.listarestadosoc222(1,1,Integer.parseInt(xcodper));

		model.addAttribute("xestsoc", lisestsoc );	
		model.addAttribute("xcodes", xcodes );
		return "personal/listaEstadosMod";	
	}
	
	@RequestMapping({"tiposDirDet_datos_12.html"})
	public String tiposDirDet_datos_12(Model model,HttpServletRequest request)  throws IOException  {
		
		String xcodper=request.getParameter("xcodper");
	//	System.out.println("llego pppp"+xcodper );
		Dirdom xdom = null;
		//LA CONSULTA DEVUELVE UNA CLASE
		try {
			 xdom = this.personasManager.getDirDom(Integer.parseInt(xcodper) );
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		JsonObject object = new JsonObject();
		int sw=1;
		
		if (xdom != null){
			//DEFINE OBJETO TIPO JSON y luego lo devuelve al AJAX solicitado
			//cargo los datos en JSON
		//	System.out.println("entro");
			object.addProperty("xcodper", xdom.getCodper());
			object.addProperty("xcoddom", xdom.getCoddom());
			object.addProperty("xcodl", xdom.getCodl());
			object.addProperty("xzona", xdom.getZona());
			object.addProperty("xcalle", xdom.getCalle());
			object.addProperty("xnumero", xdom.getNumero());
			object.addProperty("xedificio", xdom.getEdificio());
			object.addProperty("xbloque", xdom.getBloque());
			object.addProperty("xpiso", xdom.getPiso());
			object.addProperty("xtelefono", xdom.getTelefono());
			object.addProperty("xcelular", xdom.getCelular());
			object.addProperty("xemail", xdom.getEmail());
			
		}else{
			sw=0;
			object.addProperty("xcodper", xcodper);
		}
			
		
		
	//	System.out.println(sw);
		object.addProperty("estado", sw);
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}

	@RequestMapping({"tiposTrabDet_datos_12.html"})
	public String tiposTrabDet_datos_12(Model model,HttpServletRequest request)  throws IOException  {
		
		String xcodper=request.getParameter("xcodper");
	//	System.out.println("llego pppp"+xcodper );
		Dirtrab xtrab = null;
		//LA CONSULTA DEVUELVE UNA CLASE
		try {
			 xtrab = this.personasManager.getDirTrab(Integer.parseInt(xcodper) );
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		JsonObject object = new JsonObject();
		int sw=1;
		
		if (xtrab != null){
			//DEFINE OBJETO TIPO JSON y luego lo devuelve al AJAX solicitado
			//cargo los datos en JSON
		//	System.out.println("entro");
			object.addProperty("xcodper", xtrab.getCodper());
			object.addProperty("xcoddom", xtrab.getCoddir());
			object.addProperty("xcodl", xtrab.getCodl());
			object.addProperty("xempresa", xtrab.getEmpresa());
			object.addProperty("xcargo", xtrab.getCargo());
			object.addProperty("xzona", xtrab.getZona());
			object.addProperty("xcalle", xtrab.getCalle());
			object.addProperty("xnumero", xtrab.getNumero());
			object.addProperty("xedificio", xtrab.getEdificio());
			object.addProperty("xbloque", xtrab.getBloque());
			object.addProperty("xpiso", xtrab.getPiso());
			object.addProperty("xtelefono", xtrab.getTelefono());
			object.addProperty("xcelular", xtrab.getCelular());
			object.addProperty("xemail", xtrab.getEmail());
			
		}else{
			sw=0;
			object.addProperty("xcodper", xcodper);
		}
			
		
		
	//	System.out.println(sw);
		object.addProperty("estado", sw);
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}
	
	@RequestMapping({"recuperaNroAccion_12.html"})
	public String recuperaNroAccion_12(Model model,HttpServletRequest request)  throws IOException  {	
		//LA CONSULTA DEVUELVE UNA CLASE
		General xper = this.personasManager.getDatosGeneral();
			
		JsonObject object = new JsonObject();
		int sw=1;		
		object.addProperty("xnroaccion", xper.getNroaccion());
		
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}
	
	@RequestMapping({"recuperaDatosPermiso_12.html"})
	public String recuperaDatosPermiso_12(Model model,HttpServletRequest request)  throws IOException  {	
		String xcodestado=request.getParameter("xcodestado");
	//	System.out.println("llego pppp"+xcodper );
		Permiso xper = null;
		//LA CONSULTA DEVUELVE UNA CLASE
		try {
			 xper = this.personasManager.getDatosDePermiso(Integer.parseInt(xcodestado) );
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		JsonObject object = new JsonObject();
		int sw=1;
		
		if (xper != null){
			object.addProperty("xmesini", xper.getMesini());
			object.addProperty("xanioini", xper.getAnioini());
			object.addProperty("xmesfin", xper.getMesfin());
			object.addProperty("xaniofin", xper.getAniofin());

		}else{
			sw=0;
		}
		
//		System.out.println(sw);
		object.addProperty("estado", sw);
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}

	@RequestMapping({"tiposAntDet_datos_12.html"})
	public String tiposAntDet_datos_12(Model model,HttpServletRequest request)  throws IOException  {
		
		String xcodper=request.getParameter("xcodper");
		String xcodes=request.getParameter("xcodes");
System.out.println("llego xcodes"+xcodes );
		Datosant xant = null;
		//LA CONSULTA DEVUELVE UNA CLASE
		try {
			 xant = this.personasManager.getDirAnt(Integer.parseInt(xcodper) );
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		JsonObject object = new JsonObject();
		int sw=1;
		
		if (xant != null){
			//DEFINE OBJETO TIPO JSON y luego lo devuelve al AJAX solicitado
			//cargo los datos en JSON
		//	System.out.println("entro");
			object.addProperty("xcodper", xant.getCodper());
			object.addProperty("xfecha", xant.getFecharegFormat());
			object.addProperty("xcodes", xant.getCodes());
			
		}else{
			sw=0;
			object.addProperty("xcodper", xcodper);
		}
			
		
		
	//	System.out.println(sw);
		object.addProperty("estado", sw);
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}

	// AQUI SE LLAMA A LOS ESTADOS DE LOS SOCIOS
	@RequestMapping({"detalle_estado12.html"})
	public String detalle_estado12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
		Jencryption crypt=new Jencryption();
		Utilitarios u=new Utilitarios();
		String xcodper=request.getParameter("xcodper");
		String xnombre=request.getParameter("xnombre");
		String xestadosoc=request.getParameter("xestadosocio");
		String xcodigoper=request.getParameter("xcodigoper");
//System.out.println(" xxxxxxcodigoper="+xcodigoper);		
		
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//recupera los roles de la base de datos
		List<?> lispersonal_estados = this.personasManager.listarPersonal_estado(Integer.parseInt(xcodper));
		model.addAttribute("xPersonal_estados", lispersonal_estados );
		Date fecha_ant=null;
		String xfecha="";
		try {
			fecha_ant = this.personasManager.getFechaAntiguedad(Integer.parseInt(xcodper));
			xfecha = u.dateFormat(fecha_ant);			
		}catch (Exception e){
			xfecha="-";
		}	
		
		System.out.println("entro a detalle de Estado del Socio..");

		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("12",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println("fecha anterior="+xfecha);
		/*
		for(int i=0;i<lispersonal_estados.size();i++){
			EstadoSoc pt = (Estadosoc) xlistarVendedor.get(codes);
			JSONObject sgOU =new JSONObject();
			sgOU.put("label", pt.getDatosPersona()+" "+pt.getVendedor());
			sgOU.put("value", pt.codper);					
			sgAU.add(sgOU);					
		}
		*/
		model.addAttribute("xcodper", xcodper );
		model.addAttribute("xnombre", xnombre );
		model.addAttribute("xcodigoper", xcodigoper );
		model.addAttribute("xestadosoc", xestadosoc );
		model.addAttribute("xfecha_ant", xfecha );
		return "personal/estadosperDet";	
	}

	// AQUI SE LLAMA A LOS ESTADOS DE LOS SOCIOS
	@RequestMapping({"detalle_activosper12.html"})
	public String detalle_activosper12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodper=request.getParameter("xcodper");
		String xnombre=request.getParameter("xnombre");
		String xcodigoper=request.getParameter("xcodigoper");
		
		//recupera los roles de la base de datos
		List<?> lispersonal_activos = this.personasManager.listarPersonal_activaciones(Integer.parseInt(xcodper));
		model.addAttribute("xPersonal_activos", lispersonal_activos );
		
		model.addAttribute("xcodper", xcodper );
		model.addAttribute("xnombre", xnombre );
		model.addAttribute("xcodigoper", xcodigoper );
		return "personal/activosperDet";	
	}
	
	// AQUI ESTA LLAMANDO A LOS BENEFICIARIOS	
	@RequestMapping({"detalle_beneficiario12.html"})
	public String detalle_beneficiario12(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodper=request.getParameter("xcodper");
		String xnombre=request.getParameter("xnombre");
		String xcodigoper=request.getParameter("xcodigoper");
		String xnewcodigoper=request.getParameter("xnewcodigoper");
		String xecivil=request.getParameter("xecivil");
		
		//recupera los bebeficiarios de la base de datos
		List<?> lispersonal_beneficiarios = this.personasManager.listarPersonal_beneficiarios(Integer.parseInt(xcodper));
		model.addAttribute("xPersonal_beneficiarios", lispersonal_beneficiarios);
		model.addAttribute("xcodper", xcodper );
		model.addAttribute("xcodigoper", xcodigoper );
		model.addAttribute("xnewcodigoper", xnewcodigoper );
		model.addAttribute("xnombre", xnombre );
		model.addAttribute("xecivil", xecivil );
		return "personal/beneficiariosperDet";	
	}
	
	//detalle de personalMon.vm
	@RequestMapping({"listaPersonal_det_12.html"})
	public String tiposprodDet_2_2_17(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
		String xactivos=request.getParameter("xactivos");
		
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("12",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA PERSONAL 12::");
//		System.out.println(" estado="+xestado);
		
		int xact1=0, xact2=1;
		if (xactivos.equals("1")){ xact1=1; xact2=1; }
		if (xactivos.equals("0")){ xact1=0; xact2=0; }
//		List<Productos> xlistarProductos = this.productosCManager.listarProductos(Integer.parseInt(xcodtipoPadre),xest1,xest2);
	
		//recupera los roles de la base de datos
		List<?> lispersonal = this.personasManager.listarPersonal(xact1,xact2,1);
		model.addAttribute("xPersonal", lispersonal );	
		List<?> lislugar = this.personasManager.listarlugar(1,1);
		model.addAttribute("xlugar", lislugar );
		List<?> lisestsoc = this.personasManager.listarestadosoc(1,1);
		model.addAttribute("xestsoc", lisestsoc );
		
		return "personal/lisPersonal";
	}	
	
	@RequestMapping({"personaServices.html"})
	public String lugarServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		String xlogin=(String) session.getAttribute("s_login");
//		System.out.println(xlogin);
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
			if (op.equals("addPersonal")){				
				String ci=request.getParameter("a_ci");
				String codigoper=request.getParameter("a_codigoper");
				String newcodigoper=request.getParameter("a_newcodigoper");
				String xconyuge="1";//request.getParameter("a_conyuge");//SI ES CONYUGE
				String nombre=request.getParameter("a_nombre");
				String ap=request.getParameter("a_ap");
				String am=request.getParameter("a_am");
				String email=request.getParameter("a_email");
				String telefono=request.getParameter("a_telef");
				String ecivil=request.getParameter("a_ecivilRadios");
				String genero=request.getParameter("a_generoRadios");
				String fnac=request.getParameter("a_fnac");
				String fing=request.getParameter("a_fing");
				String cliente=nombre+" "+ap+" "+am;
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}

				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fnac);
				Date fechaIng = dateformat.parse(fing);
//				System.out.println(cliente);

//System.out.println(" telef="+telefono +"newcodigoper"+ newcodigoper);				
//System.out.println(" llego ="+ci+" nombre="+nombre+" ap="+ap+" am="+am+" telf="+telef+" email="+email+" direc="+direc+" areas="+areas+" ecivil="+ecivil+" fnac="+fechaNac);
//System.out.println(" llego ="+ci+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);		
				String error = this.personasManager.setAddPersonal(ci,Integer.parseInt(codigoper),nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),email,ecivil.toUpperCase(),genero.toUpperCase(),fechaNac,fechaIng,cliente.toUpperCase(),Integer.parseInt(xconyuge),newcodigoper.toUpperCase(),telefono);		
//System.out.println(" error ="+error);							
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//MODIFICAR PERSONA
			if (op.equals("modPersonal")){				
				String ci=request.getParameter("m_ci");
				String xcodigoper=request.getParameter("m_codigoper");
				String xnewcodigoper=request.getParameter("m_newcodigoper");
				String codper=request.getParameter("m_codper");
				String nombre=request.getParameter("m_nombre");
				String ap=request.getParameter("m_ap");
				String am=request.getParameter("m_am");			
				String email=request.getParameter("m_email");
				String telefono=request.getParameter("m_telef");
				String genero=request.getParameter("m_genero");
				String ecivil=request.getParameter("m_ecivil");
				String fechanac=request.getParameter("m_fechanac");
				String fechaing=request.getParameter("m_fechaing");
//				System.out.println(" newcodigoper ="+xnewcodigoper+" telefono "+telefono);
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fechanac);
				Date fechaIng = dateformat.parse(fechaing);

//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" email="+email+"ecivil "+ecivil+"genero "+genero+"fecha "+fechanac+"estsocio ");
				
				String error = this.personasManager.setModPersonal(ci,Integer.parseInt(codper),nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),email,ecivil.toUpperCase(),genero.toUpperCase(),fechaNac,fechaIng,1,Integer.parseInt(xcodigoper),xnewcodigoper.toUpperCase(),telefono);//el uno es por conyuge
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//MODIFICAR PERSONA
			if (op.equals("modFacPersonal")){				
				String ci=request.getParameter("mf_ci");
				String codper=request.getParameter("mf_codper");
				String nombre=request.getParameter("mf_nombres");
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}
				
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre);
				
				String error = this.personasManager.setModFacPersonal(ci,Integer.parseInt(codper),nombre.toUpperCase());
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

			//ADICIONAR ESTADO
			if (op.equals("addEstado")){				
				String codper=request.getParameter("Ae_codper");
				String codes=request.getParameter("Ae_codes");
				String[] res = codes.split("@");
//System.out.println(" Ae_codes="+res[0]);	
				codes=res[0];
				int xpermiso=  Integer.parseInt(res[1]);
				String fest=request.getParameter("ae_fest");
				
				String mesini=request.getParameter("Ae_mesini");
				String anioini=request.getParameter("Ae_anioini");
				String mesfin=request.getParameter("Ae_mesfin");
				String aniofin=request.getParameter("Ae_aniofin");
				int sw=1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaest = dateformat.parse(fest);
//System.out.println(" llego ="+codper+" "+codes+" "+fechaest+" "+ xlogin+" "+mesini+" "+mesfin+" "+anioini+" "+aniofin);
				
				String error = this.personasManager.setAddEstados(Integer.parseInt(codper),Integer.parseInt(codes),fechaest,sw,xlogin,Integer.parseInt(mesini),Integer.parseInt(anioini),Integer.parseInt(mesfin),Integer.parseInt(aniofin),xpermiso);
				
				String xestadosoc="AUSENTE";
				if (codes.equals("100")) {xestadosoc="ACTIVO";}
				if (codes.equals("200")) {xestadosoc="MENOR";}
				if (codes.equals("300")) {xestadosoc="EMERITO";}
				if (codes.equals("400")) {xestadosoc="JOVEN";}
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				object.addProperty("xestsoc", xestadosoc);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//MODIFICAR ESTADO
			if (op.equals("modEstado")){				
				String codper=request.getParameter("me_codper");
				String codestado=request.getParameter("me_codestado");
				
				String Actcodes=request.getParameter("me_Actcodes");
				String fest=request.getParameter("me_fest");
				
				String mesini=request.getParameter("me_mesini");
				String anioini=request.getParameter("me_anioini");
				String mesfin=request.getParameter("me_mesfin");
				String aniofin=request.getParameter("me_aniofin");
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaest = dateformat.parse(fest);
				
				String[] res = Actcodes.split("@");
//System.out.println(" Ae_codes="+res[0]);	
				Actcodes=res[0];
				int xpermiso=  Integer.parseInt(res[1]);
				
//System.out.println("codestdo="+codestado+" codper ="+codper+" actcodes="+Actcodes+" fest="+fest+" mesini="+mesini+" anioini="+anioini+" mesfin="+mesfin+" aniofin="+aniofin);

				String error = this.personasManager.setModEstados(Integer.parseInt(codper),Integer.parseInt(codestado),Integer.parseInt(Actcodes),fechaest,xlogin,Integer.parseInt(mesini),Integer.parseInt(anioini),Integer.parseInt(mesfin),Integer.parseInt(aniofin),xpermiso);
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

			//ADICIONAR antiguedad
			if (op.equals("addAntiguedad")){				
				String codper=request.getParameter("xcodper-add-ant");
				String fest=request.getParameter("a_fant");
				int codes=100;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaest = dateformat.parse(fest);
			//	System.out.println(" llego ="+codper+" "+codes+" "+fechaest+" "+ xlogin);
				
				String error = this.personasManager.setAddAntiguedad(Integer.parseInt(codper),codes,fechaest);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//ELIMINAR Antiguedad
			if (op.equals("delAntiguedad")){				
				String codper=request.getParameter("xcodper-mod-ant");
				String error = this.personasManager.setDelAntiguedad(Integer.parseInt(codper));
				
//				System.out.println(" error ="+error);
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			//ADICIONAR DOMICILIO
			if (op.equals("addDom")){				
				String codper=request.getParameter("xcodper-add-dom");
				String codl=request.getParameter("xcodl-add-dom");
				String zona=request.getParameter("xzona-add-dom");
				String calle=request.getParameter("xcalle-add-dom");
				String numero=request.getParameter("xnumero-add-dom");
				String edificio=request.getParameter("xedificio-add-dom");
				String bloque=request.getParameter("xbloque-add-dom");
				String telefono=request.getParameter("xtelefono-add-dom");
				String celular=request.getParameter("xcelular-add-dom");
				String email=request.getParameter("xemail-add-dom");

				
	
				
//System.out.println(" llego ="+codper+" codl="+codl+" zona="+zona+" calle="+calle+" numero="+numero+" edificio="+edificio+" bloque="+bloque+" telefono="+telefono+" celular="+celular+" email="+email);
//System.out.println(" llego ="+ci+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);		
				String error = this.personasManager.setAddDomicilio(Integer.parseInt(codper),Integer.parseInt(codl),zona.toUpperCase(),calle.toUpperCase(),numero,edificio.toUpperCase(),bloque.toUpperCase(),telefono,celular,email);		
//System.out.println(" error ="+error);							
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			//MODIFICAR DOMICILIO
			if (op.equals("modDom")){				
				String codper=request.getParameter("xcodper-mod-dom");
				String codl=request.getParameter("xcodl-mod-dom");
				String zona=request.getParameter("xzona-mod-dom");
				String calle=request.getParameter("xcalle-mod-dom");
				String numero=request.getParameter("xnumero-mod-dom");
				String edificio=request.getParameter("xedificio-mod-dom");
				String bloque=request.getParameter("xbloque-mod-dom");
				String telefono=request.getParameter("xtelefono-mod-dom");
				String celular=request.getParameter("xcelular-mod-dom");
				String email=request.getParameter("xemail-mod-dom");
				
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
		//		System.out.println(" llego ="+codper+" codl="+codl+" zona="+zona+" calle="+calle+" numero="+numero+" edificio="+edificio+" bloque="+bloque+" telefono="+telefono+" celular="+celular+" email="+email);

				String error = this.personasManager.setModDomicilio(Integer.parseInt(codper),Integer.parseInt(codl),zona.toUpperCase(),calle.toUpperCase(),numero,edificio.toUpperCase(),bloque.toUpperCase(),telefono,celular,email);
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//ADICIONAR TRABAJO
			if (op.equals("addTrab")){				
				String codper=request.getParameter("xcodper-add-trab");
				String codl=request.getParameter("xcodl-add-trab");
				String cargo=request.getParameter("xcargo-add-trab");
				String empresa=request.getParameter("xempresa-add-trab");
				String zona=request.getParameter("xzona-add-trab");
				String calle=request.getParameter("xcalle-add-trab");
				String numero=request.getParameter("xnumero-add-trab");
				String edificio=request.getParameter("xedificio-add-trab");
				String bloque=request.getParameter("xbloque-add-trab");
				String telefono=request.getParameter("xtelefono-add-trab");
				String celular=request.getParameter("xcelular-add-trab");
				String email=request.getParameter("xemail-add-trab");

				
	
				
//System.out.println(" llego ="+codper+" codl="+codl+" zona="+zona+" calle="+calle+" numero="+numero+" edificio="+edificio+" bloque="+bloque+" telefono="+telefono+" celular="+celular+" email="+email);
//System.out.println(" llego ="+ci+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);		
				String error = this.personasManager.setAddTrabajo(Integer.parseInt(codper),Integer.parseInt(codl),empresa.toUpperCase(),cargo.toUpperCase(),zona.toUpperCase(),calle.toUpperCase(),numero,edificio.toUpperCase(),bloque.toUpperCase(),telefono,celular,email);		
//System.out.println(" error ="+error);							
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}

			//MODIFICAR TRABAJO
			if (op.equals("modTrab")){				
				String codper=request.getParameter("xcodper-mod-trab");
				String codl=request.getParameter("xcodl-mod-trab");
				String cargo=request.getParameter("xcargo-mod-trab");
				String empresa=request.getParameter("xempresa-mod-trab");
				String zona=request.getParameter("xzona-mod-trab");
				String calle=request.getParameter("xcalle-mod-trab");
				String numero=request.getParameter("xnumero-mod-trab");
				String edificio=request.getParameter("xedificio-mod-trab");
				String bloque=request.getParameter("xbloque-mod-trab");
				String telefono=request.getParameter("xtelefono-mod-trab");
				String celular=request.getParameter("xcelular-mod-trab");
				String email=request.getParameter("xemail-mod-trab");

				
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
		//		System.out.println(" llego ="+codper+" codl="+codl+"empresa"+empresa+"cargo"+cargo+" zona="+zona+" calle="+calle+" numero="+numero+" edificio="+edificio+" bloque="+bloque+" telefono="+telefono+" celular="+celular+" email="+email);

				String error = this.personasManager.setModTrabajo(Integer.parseInt(codper),Integer.parseInt(codl),empresa.toUpperCase(),cargo.toUpperCase(),zona.toUpperCase(),calle.toUpperCase(),numero,edificio.toUpperCase(),bloque.toUpperCase(),telefono,celular,email);
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			//ADICIONAR BENEFICIARIO
			if (op.equals("addBeneficiario")){				
				String ci=request.getParameter("ab_ci");
				String codigoper=request.getParameter("ab_codigoper");
				//String xconyuge=request.getParameter("ab_conyuge"); //CONYUGE
				String nombre=request.getParameter("ab_nombre");
				String ap=request.getParameter("ab_ap");
				String am=request.getParameter("ab_am");
				String email=request.getParameter("ab_email");
				
				String ecivil=request.getParameter("ab_ecivilRadios");
				String genero=request.getParameter("ab_generoRadios");
				String fnac=request.getParameter("ab_fnac");
				String fing=request.getParameter("ab_fing");
				String padrecodper=request.getParameter("Ab_codper");
				String tipobenef=request.getParameter("ab_tipobenef");
				
				//System.out.println(padrecodper+" "+tipobenef );
				int benef=1;
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}

				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fnac);
				Date fechaIng = dateformat.parse(fing);
				
//System.out.println(" llego ="+ci+" nombre="+nombre+" ap="+ap+" am="+am+" telf="+telef+" email="+email+" direc="+direc+" areas="+areas+" ecivil="+ecivil+" fnac="+fechaNac);
//System.out.println(" llego ="+ci+" "+nombre+" "+ap+" "+am+" "+email+" "+ecivil+" "+genero+" "+fnac+" "+fing+" padrecodper"+padrecodper);		
				String error = this.personasManager.setAddBeneficiario(ci,Integer.parseInt(codigoper),nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),email,ecivil.toUpperCase(),genero.toUpperCase(),fechaNac,fechaIng,Integer.parseInt(padrecodper),benef,Integer.parseInt(tipobenef));		
//System.out.println(" error ="+error);							
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}

			//MODIFICAR BENEFICIARIO
			if (op.equals("modBeneficiario")){				
				String ci=request.getParameter("mb_ci");
				String codper=request.getParameter("mb_codper");
				String xcodigoper=request.getParameter("mb_codigoper");
				String xconyuge=request.getParameter("mb_conyuge"); //CONYUGE
				String nombre=request.getParameter("mb_nombre");
				String ap=request.getParameter("mb_ap");
				String am=request.getParameter("mb_am");			
				String email=request.getParameter("mb_email");
				
				String genero=request.getParameter("mb_genero");
				String ecivil=request.getParameter("mb_ecivil");
				String fechanac=request.getParameter("mb_fechanac");
				String fechaing=request.getParameter("mb_fechaing");
				String tipobenef=request.getParameter("mb_tipobenef");
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" "+telef+" "+email+" "+direc+" "+areas+" "+ecivil);
				if (ci == null) {ci="-";}
				if (ci.equals("")){ci="-";}
				System.out.println(tipobenef );
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaNac = dateformat.parse(fechanac);
				Date fechaIng = dateformat.parse(fechaing);
				String newcodigoper="0";
				String telefono="";
//				System.out.println(" llego ="+ci+" "+codper+" "+nombre+" "+ap+" "+am+" email="+email+"ecivil "+ecivil+"genero "+genero+"fecha "+fechanac+"estsocio ");
				
				String error = this.personasManager.setModPersonal(ci,Integer.parseInt(codper),nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),email,ecivil.toUpperCase(),genero.toUpperCase(),fechaNac,fechaIng,Integer.parseInt(tipobenef),Integer.parseInt(xcodigoper),newcodigoper.toUpperCase(),telefono);
				
//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

			
			//ELIMINAR BENEFICIARIO
			if (op.equals("delBen")){				
				String codper=request.getParameter("db_codper");
				String error = this.personasManager.setDelBeneficiario(Integer.parseInt(codper));
				
//				System.out.println(" error ="+error);
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//HABILITAR BENEFICIARIO
			if (op.equals("habBen")){				
				String codper=request.getParameter("hb_codper");
				String error = this.personasManager.setHabBeneficiario(Integer.parseInt(codper));
				
//				System.out.println(" error ="+error);
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			//ELIMINAR PERSONAL
			if (op.equals("delPersonal")){				
				String codper=request.getParameter("d_codper");
				
				String error = this.personasManager.setDelPersonal(Integer.parseInt(codper));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//ACTIVAR SOCIO
			if (op.equals("activoPersonal")){
//System.out.println("ACTIVAR SOCIO.............");
				String xcodper=request.getParameter("act_a_codper");
				String xfecha=request.getParameter("activo_fecha");
				String xmes=request.getParameter("act_a_mes");
				String xanio=request.getParameter("act_a_anio");

//System.out.println("xcodper="+xcodper+" xfecha="+xfecha+" xmes="+xmes+" xanio="+xanio);				
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date xfechaAct = dateformat.parse(xfecha);
				//public String setActivarSocio(int xcodper,Date xfecha,int xmes,int xanio,String xlogin){
				String error = this.personasManager.setActivarSocio(Integer.parseInt(xcodper),xfechaAct,Integer.parseInt(xmes),Integer.parseInt(xanio),xlogin,1);

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//ACTIVAR SOCIO
			if (op.equals("desactivoPersonal")){
//System.out.println("DESACTIVAR SOCIO.............");
				String xcodper=request.getParameter("act_b_codper");
				String xfecha=request.getParameter("desactivo_fecha");

//System.out.println("xcodper="+xcodper+" xfecha="+xfecha);				
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date xfechaAct = dateformat.parse(xfecha);
				//public String setActivarSocio(int xcodper,Date xfecha,int xmes,int xanio,String xlogin){
				LocalDate fechaActual = LocalDate.now();
		        int mes = fechaActual.getMonthValue();
		        int anio = fechaActual.getYear();
		        //System.out.println(mes+" "+ anio);
				String error = this.personasManager.setActivarSocio(Integer.parseInt(xcodper),xfechaAct,mes,anio,xlogin,2);

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//HABILITAR PERSONAL
			if (op.equals("habPersonal")){				
				String codper=request.getParameter("h_codper");
				
				String error = this.personasManager.setHabPersonal(Integer.parseInt(codper));
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//ELIMINAR TRABAJO
			if (op.equals("delTrab")){				
				String codper=request.getParameter("xcodper-mod-trab");
//				System.out.println("codper"+codper);
				String error = this.personasManager.setDelTrabajo(Integer.parseInt(codper));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
			//ELIMINAR DOMICILIO
			if (op.equals("delDom")){				
				String codper=request.getParameter("xcodper-mod-dom");
//				System.out.println("codper"+codper);
				String error = this.personasManager.setDelDomicilio(Integer.parseInt(codper));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
			
		}
				
		model.addAttribute("file1", comodoTpl+"/personal/personalmon.vm");	
		return "marco";
	}
	
	@RequestMapping("personalReportes01.html")
	  public void personasReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		String xestado=req.getParameter("xest") ;
		String xactivos=req.getParameter("xact") ;
		HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");

		  String zactivos="HABILITADOS/DESHABILITADOS";
		  int xact1=0, xact2=1;
		  if (xactivos.equals("1")){ xact1=1; xact2=1; zactivos="HABILITADOS"; }
		  if (xactivos.equals("0")){ xact1=0; xact2=0; zactivos="DESHABILITADOS";}
//		  System.out.println(xest1+" "+xest2);
		  Map<String,Object> params = new HashMap<>();
		  
			params.put("xest1", xact1);
			params.put("xest2", xact2);
			params.put("xactivos", zactivos);
			params.put("xact",Integer.parseInt(xactivos));
//System.out.println("haber llega?::"+Integer.parseInt(xactivos));
			params.put("responsable", xusuario);
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/personal01.jasper", "personal");
	  }
	@RequestMapping("personalReportes01XLS.html")
	  public void personasReporta01XLS(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		String xestado=req.getParameter("xest") ;
		String xactivos=req.getParameter("xact") ;
		HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");

		  String zactivos="HABILITADOS/DESHABILITADOS";
		  int xact1=0, xact2=1;
		  if (xactivos.equals("1")){ xact1=1; xact2=1; zactivos="HABILITADOS"; }
		  if (xactivos.equals("0")){ xact1=0; xact2=0; zactivos="DESHABILITADOS";}
//		  System.out.println(xest1+" "+xest2);
		  Map<String,Object> params = new HashMap<>();
		  
			params.put("xest1", xact1);
			params.put("xest2", xact2);
			params.put("xactivos", zactivos);
			params.put("xact",Integer.parseInt(xactivos));
//System.out.println("haber llega?::"+Integer.parseInt(xactivos));
			params.put("responsable", xusuario);
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/personal01XLS.jasper", "personalXLS");
	  }
	
	@RequestMapping("sociosReportes01.html")
	  public void sociosReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		
		HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");
		  String xbeneficiario_codper=req.getParameter("xcodper") ;
		  String xbeneficiario_name=req.getParameter("xnombres") ;
		  
//		  System.out.println(xbeneficiario_codper+"  "+xbeneficiario_name);
		  Map<String,Object> params = new HashMap<>();
			params.put("responsable", xusuario);
			params.put("socio_cod", Integer.parseInt(xbeneficiario_codper));
		//	params.put("socio", xbeneficiario_name);
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/socios/socios01.jasper", "socios");	        
	  }
}
