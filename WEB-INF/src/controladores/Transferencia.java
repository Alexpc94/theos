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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import model.domain.TransferenciaT;
import model.domain.Traspasocio;
import model.manager.TransferenciaManager;
import model.manager.TraspaSocioManager;
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
public class Transferencia {

	@Autowired
	TransferenciaManager transferenciaManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"transferenciamon_203.html"})
	public String transferenciamon_203(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		Utilitarios u=new Utilitarios();

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
			VarGlobales var=new VarGlobales("201",crypt.decrypt(cd)); //params: (proceso, menu)
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
			List<TransferenciaT> lisTransfer = this.transferenciaManager.listar(xest1,xest2);
			model.addAttribute("xTras", lisTransfer );
			
			model.addAttribute("opAdd", crypt.encrypt("addTransferencia"));
			model.addAttribute("opDel", crypt.encrypt("delTransferencia")); 
			
			model.addAttribute("op_json", crypt.encrypt("lista_Socios"));
			model.addAttribute("op_json_des", crypt.encrypt("lista_Socios_deshabilitados"));

			model.addAttribute("xfecha",u.getFecha());//Nueva fechas
		}
		
		model.addAttribute("file1", comodoTpl+"/transferencia/transferenciamon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaTransferencia_det_203.html"})
	public String listaTraspasocio_det_201(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
		Utilitarios u=new Utilitarios();
		
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("203",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }

		model.addAttribute("xfecha",u.getFecha());//Nueva fechas
		
		//recupera los roles de la base de datos
		List<TransferenciaT> lisTransfer = this.transferenciaManager.listar(xest1,xest2);
		model.addAttribute("xTras", lisTransfer );
		
		return "transferencia/transferenciaDet";
	}
	
	@RequestMapping({"transferenciaServices_203.html"})
	public String transferenciaServices_203(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			//LISTA DE SOCIOS 
			if (op.equals("lista_Socios")){
//System.out.println("PROCESSING lista benficiario llegoo"+op);
				List<TransferenciaT> xlistarsocios = this.transferenciaManager.listaSocios();
				JSONArray sgAU =new  JSONArray();
				for(int i=0;i<xlistarsocios.size();i++){
					TransferenciaT pt = (TransferenciaT) xlistarsocios.get(i);
//					System.out.println(" socio::"+pt.getDatosPersona());
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.getDatosPersona());
					sgOU.put("value", pt.codper_padre);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}
			
			//LISTA DE SOCIOS  
			if (op.equals("lista_Socios_deshabilitados")){
//System.out.println("PROCESSING lista benficiario llegoo"+op);
				List<TransferenciaT> xlistarsocios = this.transferenciaManager.listaSociosDeshabilitados();
				JSONArray sgAU =new  JSONArray();
				for(int i=0;i<xlistarsocios.size();i++){
					TransferenciaT pt = (TransferenciaT) xlistarsocios.get(i);
//					System.out.println(" socio::"+pt.getDatosPersona());
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.getDatosPersona());
					sgOU.put("value", pt.codper_padre);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}
			
			//ADICIONAR NUEVA TRANSFERENCIA
			if (op.equals("addTransferencia")){				
//System.out.println(" PROCESSING ADDTRANSFERENCIA");				
				String xcodper1=request.getParameter("a_socio_val");
				String xcodper2=request.getParameter("a_nuevosocio_val");
				String xfecha=request.getParameter("a_fecha");
				String xsocio=request.getParameter("a_socio");
				String xnuevosocio=request.getParameter("a_nuevosocio");
				String xobser=request.getParameter("a_obser");
				String xinterespagar=request.getParameter("a_montot");
//System.out.println(" SOCIO ANTIGUO ="+xcodper1+" SOCIO NUEVO::"+xcodper2+" xfecha="+xfecha+ " xlogin="+xlogin+" socio="+xsocio+" socio nuevo="+xnuevosocio+" obser="+xobser );
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaTransac = dateformat.parse(xfecha);
				String error = this.transferenciaManager.setAddTransferencia(fechaTransac,Integer.parseInt(xcodper1),Integer.parseInt(xcodper2),xlogin,xobser);
				
//System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());					
				return "util/json";				
			}
		
			//ELIMINAR DEPARTAMENTOS
			if (op.equals("delTransferencia")){	
//System.out.println("DELETE TRANFERENCIA....");			
				String codtra=request.getParameter("d_codtra");
				String codper=request.getParameter("d_codper");
				String codper2=request.getParameter("d_codper2");
				
				String error = this.transferenciaManager.setDelTransferencia(Integer.parseInt(codtra),Integer.parseInt(codper),Integer.parseInt(codper2),xlogin);				
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
/*
	  @RequestMapping("dptosReportes01.html")
	  public void dptosReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		  String xestado=req.getParameter("xest") ;
		  HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");
//System.out.println(xestado);
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
*/
}
