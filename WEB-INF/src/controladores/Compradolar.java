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
import com.lowagie.text.DocumentException;

import model.domain.CompraDolar;
import model.manager.AccesoManager;
import model.manager.CompradolarManager;
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
import utils.Utilitarios;

@Controller
public class Compradolar {

	@Autowired
	CompradolarManager compradolarManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"Compradolarmon.html"})
	public String compradolarmon(Model model,HttpServletRequest request)  throws IOException, ParseException   {
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		Utilitarios u=new Utilitarios();
		String xfecha=u.getFecha();
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
			VarGlobales var=new VarGlobales("21",crypt.decrypt(cd)); //params: (proceso, menu)
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
			
			//PARAMETROS PARA SELECT
			String ActivoID=request.getParameter("b_dep");
			if (ActivoID == null) {
				ActivoID="-1";
			}

			model.addAttribute("ActivoID", Integer.parseInt(ActivoID));
			
			DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaini=dateformat.parse(xfecha);//Valores de entrada para el filtro de la consulta
			Date fechafin=dateformat.parse(xfecha);//Valores de entrada para el filtro de la consulta
			//recupera los roles de la base de datos
			List<?> lisCompradolar = this.compradolarManager.listar(xest1,xest2,fechaini,fechafin);
			model.addAttribute("xCompra", lisCompradolar);
			List<?> lisestsoc = this.compradolarManager.listarTcambio();
			model.addAttribute("xestsoc", lisestsoc );
			model.addAttribute("opAdd", crypt.encrypt("addCompradolar"));
			model.addAttribute("opDel", crypt.encrypt("delCompradolar"));
			model.addAttribute("opMod", crypt.encrypt("modTcambio"));
			model.addAttribute("opHab", crypt.encrypt("habTcambio"));
			
			model.addAttribute("opReporte", crypt.encrypt("Recibo"));
			
			model.addAttribute("xfechaini",xfecha);
			model.addAttribute("xfechafin",xfecha);
			model.addAttribute("xfecha",xfecha);
		}
		
		model.addAttribute("file1", comodoTpl+"/compradolar/compradolarmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaCompradolar_det_21.html"})
	public String listacompradolar_det_21(Model model,HttpServletRequest request)  throws IOException, ParseException   {
		Jencryption crypt=new Jencryption();
		
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("21",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		//PARAMAMETROS 
				String xestado=request.getParameter("xestado");
				String xfini=request.getParameter("xfinicial");
				String xffin=request.getParameter("xffinal");
			
				if (xestado==null){ xestado="1";}
				int xest1=0, xest2=1;
				if (xestado.equals("1")){xest1=1;xest2=1;}
				if (xestado.equals("0")){xest1=0;xest2=0;}
				model.addAttribute("estado", Integer.parseInt(xestado));
				
				System.out.println(" xfini="+xfini+" xffin="+xffin+" estado="+xestado);
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaini=dateformat.parse(xfini);//Valores de entrada para el filtro de la consulta
				Date fechafin= dateformat.parse(xffin);//Valores de entrada para el filtro de la consulta
				
		//LISTA MENUS - IDEM. listaMenusMON	
		List<?> lisCompradolar = this.compradolarManager.listar(xest1,xest2,fechaini,fechafin);
		model.addAttribute("xCompra", lisCompradolar);
		List<?> lisestsoc = this.compradolarManager.listarTcambio();
		model.addAttribute("xestsoc", lisestsoc );
		
		model.addAttribute("opReporte", crypt.encrypt("Recibo"));
		
		return "compradolar/lisCompradolar";
	}
	
	@RequestMapping({"compradolarServices.html"})
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
			
			//ADICIONAR COMPRADOLAR
			if (op.equals("addCompradolar")){				
				String montodol=request.getParameter("a_montodol");
				String montobol=request.getParameter("a_montobol");
				String tc=request.getParameter("a_tc");
				String cliente=request.getParameter("a_cliente");
				String fec=request.getParameter("a_fecha");
				
				int sw=1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fecha = dateformat.parse(fec);

				System.out.println(" llego ="+xlogin+" "+fec+" "+tc+" "+montodol+" "+montobol);
				String error = this.compradolarManager.setAddMontodolar(Double.parseDouble(montodol),Double.parseDouble(montobol),Double.parseDouble(tc),cliente.toUpperCase(),fecha,xlogin);
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//ELIMIANR TCAMBIO
			if (op.equals("delCompradolar")){				
				String codcom=request.getParameter("d_codcom");
				
				String error = this.compradolarManager.setDelCompradolar(codcom);
				
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
		
		model.addAttribute("file1", comodoTpl+"/compradolar/compradolarmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx	
	  @RequestMapping("reportePagarBoletas2121.html")
	  public void reportePagarBoletas2121(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException, DocumentException {
		  Jencryption crypt=new Jencryption();
			HttpSession session=req.getSession(true);
			String xusuario=(String) session.getAttribute("s_usuario");
			String xlogin=(String) session.getAttribute("s_login");
			Utilitarios util=new Utilitarios();
			//verifica si el usuario se autentifico  oam
			if (xusuario == null){
				res.sendRedirect("index.html");			
			}else{				 
				//opcion que llega de los formularios
				String op=req.getParameter("opcion");
				if (op==null) {op="0";}
				else{ op=crypt.decrypt(op);}//descifra 

				//LISTA DE LUGAR JSON
				if (op.equals("Recibo")){
					String xcodcom=req.getParameter("codcom") ;
					xcodcom=crypt.decrypt(xcodcom);
System.out.println(" xcodcom="+xcodcom+" opcion="+op);		 
					//PARAMETROS
					Map<String,Object> params = new HashMap<>();
					params.put("codcom", xcodcom);//
					params.put("responsable", xusuario);//
					
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/compradolar/reciboDolar.jasper", "recibo");
				}
				
			}	 
	  }

}
