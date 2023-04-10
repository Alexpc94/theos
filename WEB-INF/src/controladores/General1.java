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

import model.domain.General;
import model.manager.AccesoManager;
import model.manager.GeneralManager;
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
public class General1 {

	@Autowired
	GeneralManager generalManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"Generalmon.html"})
	public String generalmon(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("19",crypt.decrypt(cd)); //params: (proceso, menu)
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
			General  lisGeneral = this.generalManager.listargeneral();
			model.addAttribute("xmesTexto", lisGeneral.getMesFormat() );
			model.addAttribute("xcodg", lisGeneral.getCodg() );
			model.addAttribute("xmes", lisGeneral.getMes() );
			model.addAttribute("xanio", lisGeneral.getAnio() );
			model.addAttribute("xboletaid", lisGeneral.getBoleta_id() );
			model.addAttribute("xmpagosid", lisGeneral.getMpagos_id() );
			model.addAttribute("xcompradolarid", lisGeneral.getCompradolar_id() );
			model.addAttribute("xges", lisGeneral.getGes() );
			model.addAttribute("xnit", lisGeneral.getNit() );
			model.addAttribute("xnroaccion", lisGeneral.getNroaccion());
			model.addAttribute("opMod", crypt.encrypt("modGeneral"));
			

		}
		
		model.addAttribute("file1", comodoTpl+"/general/generalmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaGeneral_det_19.html"})
	public String listatcambio_det_19(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("19",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		//LISTA MENUS - IDEM. listaMenusMON	
		General  lisGeneral = this.generalManager.listargeneral();
		model.addAttribute("xmesTexto", lisGeneral.getMesFormat() );
		model.addAttribute("xcodg", lisGeneral.getCodg() );
		model.addAttribute("xmes", lisGeneral.getMes() );
		model.addAttribute("xanio", lisGeneral.getAnio() );
		model.addAttribute("xboletaid", lisGeneral.getBoleta_id() );
		model.addAttribute("xmpagosid", lisGeneral.getMpagos_id() );
		model.addAttribute("xcompradolarid", lisGeneral.getCompradolar_id() );
		model.addAttribute("xges", lisGeneral.getGes() );
		model.addAttribute("xnit", lisGeneral.getNit() );
		model.addAttribute("xnroaccion", lisGeneral.getNroaccion());
		return "general/lisGeneral";
	}
	
	@RequestMapping({"generalServices.html"})
	public String generalServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			
			
			//MODIFICAR TCAMBIO
			if (op.equals("modGeneral")){				
				String mes=request.getParameter("m_mes");
				String anio=request.getParameter("m_anio");
				String ges=request.getParameter("m_ges");
				String boleta_id=request.getParameter("m_boleta_id");
				String mpagos_id=request.getParameter("m_mpagos_id");
				String compradolar_id=request.getParameter("m_compradolar_id");
				String nit=request.getParameter("m_nit");
				String nroaccion=request.getParameter("m_nroaccion");
				
				String error = this.generalManager.setModGeneral(Integer.parseInt(mes),Integer.parseInt(anio),ges,Integer.parseInt(boleta_id),Integer.parseInt(mpagos_id),Integer.parseInt(compradolar_id),nit,Integer.parseInt(nroaccion));

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
		
		model.addAttribute("file1", comodoTpl+"/general/generalmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */

	  @RequestMapping("generalReportes01.html")
	  public void generakReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/general/general01.jasper", "general");	        
	  }

}
