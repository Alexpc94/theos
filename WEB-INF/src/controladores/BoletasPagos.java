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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import model.domain.Personal;
import model.manager.AccesoManager;
import model.manager.BoletasPagosManager;
import model.manager.LugarManager;
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
public class BoletasPagos {

	@Autowired
	BoletasPagosManager boletaspagosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"boletasPagos101.html"})
	public String boletasPagos101(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("101",crypt.decrypt(cd));
			model=var.getDatos(request,model);
			
			//PARAMETROS mensajes
			String xmen=request.getParameter("m");
			String xtex=request.getParameter("t");
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
			
			
//			List<?> lisdpto = this.lugarManager.listardpto();
//			model.addAttribute("xDpto", lisdpto);
			
			model.addAttribute("opAdd", crypt.encrypt("addBoleta"));
			model.addAttribute("opAdd2", crypt.encrypt("addBoletas"));
			model.addAttribute("opDel", crypt.encrypt("delBoleta"));
			
			model.addAttribute("op_json", crypt.encrypt("lista_socios"));
			
///			model.addAttribute("opMod", crypt.encrypt("modLugar"));
///			model.addAttribute("opHab", crypt.encrypt("habLugar"));

		}
		
		model.addAttribute("file1", comodoTpl+"/boletaspagos/boletaspagosmon.vm");	
		return "marco";
	}
	

	@RequestMapping({"boletaspagosDet101.html"})
	public String boletaspagosDet101(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("101",crypt.decrypt(cd));
		model=var.getDatos(request,model);
		
		String xcodper=request.getParameter("xcodper");
		
		//PARAMAMETROS 
		String xestado=request.getParameter("xestado");
		if (xestado==null){ xestado="1";}
		int xest1=0, xest2=1;
		if (xestado.equals("1")){xest1=1;xest2=1;}
		if (xestado.equals("0")){xest1=0;xest2=0;}
		model.addAttribute("estado", Integer.parseInt(xestado));
		
		System.out.println(" xcodper="+xcodper);
		
		//recupera los roles de la base de datos
		List<?> lisboletas = this.boletaspagosManager.listarBoletas(Integer.parseInt(xcodper),xest1,xest2);
		model.addAttribute("xBoletas", lisboletas);
		
		List<?> lisAdicional = this.boletaspagosManager.listarAdicionales(Integer.parseInt(xcodper));
		model.addAttribute("xadicional", lisAdicional);
		
		return "boletaspagos/boletaspagosDet";
	}


	@RequestMapping({"boletaspagosServices.html"})
	public String boletaspagosServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String xlogin=(String) session.getAttribute("s_login");
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

			//LISTA DE LUGAR JSON
			if (op.equals("lista_socios")){
				List<Personal> xlistarsocios = this.boletaspagosManager.listar_boletas_Socios();
				JSONArray sgAU =new  JSONArray();
System.out.println("llegoo"+op);
				for(int i=0;i<xlistarsocios.size();i++){
					Personal pt = (Personal) xlistarsocios.get(i);
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.getDatosPersona());
					sgOU.put("value", pt.codper);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}
			
			//ADICIONAR UNA BOLETA
			if (op.equals("addBoleta")){				
				String xcodper=request.getParameter("a_codperVal");
				String xmes=request.getParameter("a_mes");
				String xanio=request.getParameter("a_anio");
				String xmonto=request.getParameter("a_monto");
				String xobser=request.getParameter("a_obser");
				
System.out.println("xcodper="+xcodper+" xmes="+xmes+" xanio="+xanio+" xmonto="+xmonto+" xobser="+xobser);
				
				String error = this.boletaspagosManager.setadicionaUnaBoleta(Integer.parseInt(xcodper),Integer.parseInt(xmes),Integer.parseInt(xanio),Float.parseFloat(xmonto),xobser,xlogin);
							
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				
				model.addAttribute("datos",object.toString());	
				
				return "util/json";	
			}
			
			//ADICIONAR MUCHAS MUCHAS MUCHAS BOLETA
			if (op.equals("addBoletas")){				
				String xcodper=request.getParameter("a_codperVal2");
				String xmes1=request.getParameter("a_mes2");
				String xanio1=request.getParameter("a_anio2");
				String xmes2=request.getParameter("a_mes3");
				String xanio2=request.getParameter("a_anio3");
				String xmonto=request.getParameter("a_monto2");
				String xobser=request.getParameter("a_obser2");
				
System.out.println("xcodper="+xcodper+" xmes1="+xmes1+" xanio1="+xanio1+" xmonto="+xmonto+" xobser="+xobser+" xmes2="+xmes2+" xanio2="+xanio2);
				
				String error = this.boletaspagosManager.setadicionaMUCHASBoleta(Integer.parseInt(xcodper),Integer.parseInt(xmes1),Integer.parseInt(xanio1),Float.parseFloat(xmonto),xobser,xlogin,Integer.parseInt(xmes2),Integer.parseInt(xanio2));
							
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				
				model.addAttribute("datos",object.toString());	
				
				return "util/json";	
			}
/*			
			//MODIFICAR LUGAR
			if (op.equals("modLugar")){				
				String codl=request.getParameter("m_codl");
				String lugarname=request.getParameter("m_nombre");
				String codd=request.getParameter("m_codd");
				
				System.out.println(" llego ="+ codl + lugarname + codd );
				
				String error = this.lugarManager.setModLugar(Integer.parseInt(codl),lugarname.toUpperCase(),Integer.parseInt(codd));
				
				System.out.println(" error ="+error);
				String mensaje="0", texto="0";
				if (error.equals("0")){
					mensaje=crypt.encrypt("1");
					texto=crypt.encrypt("EL LUGAR SE MODIFICO SATISFACTORIAMENTE..!");
				}else{
					mensaje=crypt.encrypt("2");
					texto=crypt.encrypt("ERROR, NO SE PUEDO MODIFICAR LOS DATOS..!");
				}
				
				//reenvio a la pantalla monitor roles
				response.sendRedirect("lugarmon.html?m="+mensaje+"&t="+texto);
				
				//recupera los roles de la base de datos
			}
*/		
			//ELIMIANR BOLETAS
			if (op.equals("delBoleta")){				
				String xcodbol=request.getParameter("b_codbolVal");
				String xobser=request.getParameter("b_obser");
//public String setDelBoletas(String xcodbol,String xlogin,String xobser){
				String error = this.boletaspagosManager.setDelBoletas(xcodbol,xlogin,xobser);
				
				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				
				model.addAttribute("datos",object.toString());	
				
				return "util/json";					
			}


		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
//		VarGlobales var=new VarGlobales();
//		model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/lugar/lugarmon.vm");	
		return "marco";
	}

	
	@RequestMapping("reporteboletasPagos101.html")
	  public void rolesReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/lugar/lugar01.jasper", "lugar");	        
	  }
	
}

