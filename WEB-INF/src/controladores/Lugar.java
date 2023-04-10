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
public class Lugar {

	@Autowired
	LugarManager lugarManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"lugarmon.html"})
	public String lugarmon(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("8",crypt.decrypt(cd));
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
			
			
			List<?> lisdpto = this.lugarManager.listardpto();
			model.addAttribute("xDpto", lisdpto);
			
			model.addAttribute("opAdd", crypt.encrypt("addLugar"));
			model.addAttribute("opMod", crypt.encrypt("modLugar"));
			model.addAttribute("opHab", crypt.encrypt("habLugar"));
			model.addAttribute("opDel", crypt.encrypt("delLugar"));
		}
		
		model.addAttribute("file1", comodoTpl+"/lugar/lugarmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"lugarDet8.html"})
	public String lugarDet8(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("8",crypt.decrypt(cd));
		model=var.getDatos(request,model);
		
		String sel=request.getParameter("sel");
		String xestado=request.getParameter("estado");
		
		//VALIDA ESTADO
		if (xestado==null){ xestado="1";}
		int xest1=0, xest2=1;
		if (xestado.equals("1")){xest1=1;xest2=1;}
		if (xestado.equals("0")){xest1=0;xest2=0;}
		
		//VALIDA SEL
		int xsel1=0, xsel2=9999999;
		if (!sel.equals("-1")){xsel1=Integer.parseInt(sel);    xsel2=xsel1;}
		
		System.out.println(" sel="+sel+" estado="+xestado);
		
		//recupera los roles de la base de datos
		List<?> lislugar = this.lugarManager.listarLugares(xest1,xest2,xsel1,xsel2);
		model.addAttribute("xLugar", lislugar);

		return "lugar/lugarDet";
	}
	
	@RequestMapping({"lugarServices.html"})
	public String lugarServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
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
			
			//ADICIONAR LUGAR
			if (op.equals("addLugar")){				
				String lugarname=request.getParameter("lugarname");
				String dpto=request.getParameter("dpto");
				System.out.println(" llego ="+lugarname+" "+dpto);
				
				String error = this.lugarManager.setAddLugar(lugarname.toUpperCase(),Integer.parseInt(dpto));
				
				System.out.println(" error ="+error);
				String mensaje="0", texto="0";
				if (error.equals("0")){
					mensaje=crypt.encrypt("1");
					texto=crypt.encrypt("LOS DATOS SE ADICIONARON SATISFACTORIAMENTE..!");
				}else{
					mensaje=crypt.encrypt("2");
					texto=crypt.encrypt("ERROR, NO SE PUDO ADICIONAR DATOS..!");
				}
				
				//reenvio a la pantalla monitor dptos
				response.sendRedirect("lugarmon.html?m="+mensaje+"&t="+texto);

				//recupera los roles de la base de datos
				/*
				List<?> lisRoles = this.rolesManager.listar();
				model.addAttribute("xRoles", lisRoles );
				model.addAttribute("opAdd", "addRoles" );
				model.addAttribute("opMod", "modRoles" );
				model.addAttribute("opDel", "delRoles" );
				*/
			}
			
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
				/*
				List<?> lisRoles = this.rolesManager.listar();
				model.addAttribute("xRoles", lisRoles );
				model.addAttribute("opAdd", "addRoles" );
				model.addAttribute("opMod", "modRoles" );
				model.addAttribute("opDel", "delRoles" );
				*/
			}
			//HABILITAR LUGAR
			if (op.equals("habLugar")){				
				String codl=request.getParameter("h_codl");
				System.out.println(" llego ="+ codl  );
				String error = this.lugarManager.setHabLugar(Integer.parseInt(codl));
				
				System.out.println(" error ="+error);
				String mensaje="0", texto="0";
				if (error.equals("0")){
					mensaje=crypt.encrypt("1");
					texto=crypt.encrypt("EL LUGAR SE HABILITO SATISFACTORIAMENTE..!");
				}else{
					mensaje=crypt.encrypt("2");
					texto=crypt.encrypt("ERROR, AL HABILITAR EL LUGAR..!");
				}
				
				//reenvio a la pantalla monitor roles
				response.sendRedirect("lugarmon.html?m="+mensaje+"&t="+texto);
				
				//recupera los roles de la base de datos
				/*
				List<?> lisRoles = this.rolesManager.listar();
				model.addAttribute("xRoles", lisRoles );
				model.addAttribute("opAdd", "addRoles" );
				model.addAttribute("opMod", "modRoles" );
				model.addAttribute("opDel", "delRoles" );
				*/
			}

			//ELIMIANR LUGAR
			if (op.equals("delLugar")){				
				String codl=request.getParameter("d_codl");
				
				String error = this.lugarManager.setDelLugar(Integer.parseInt(codl));
				
				System.out.println(" error ="+error);
				String mensaje="0", texto="0";
				if (error.equals("0")){
					mensaje=crypt.encrypt("1");
					texto=crypt.encrypt("EL LUGAR SE ELIMINO SATISFACTORIAMENTE..!");
				}else{
					mensaje=crypt.encrypt("2");
					texto=crypt.encrypt("ERROR, AL ELIMINAR EL LUGAR..!");
				}
				
				//reenvio a la pantalla monitor roles
				response.sendRedirect("lugarmon.html?m="+mensaje+"&t="+texto);
				
			}
			
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
//		VarGlobales var=new VarGlobales();
//		model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/lugar/lugarmon.vm");	
		return "marco";
	}
	
	@RequestMapping("lugarReportes01.html")
	  public void rolesReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 HttpSession session=req.getSession(true);
		  String xusuario=(String) session.getAttribute("s_usuario");
		 String sel=req.getParameter("sel");
		 System.out.println(sel); 
		 int xsel1=0, xsel2=9999999;
		 if (!sel.equals("-1")){xsel1=Integer.parseInt(sel);    xsel2=xsel1;}
		 
		 String xestado=req.getParameter("xest") ; 
		 System.out.println(xestado); 
		  int xest1=0, xest2=1;
		  if (xestado.equals("1")){xest1=1;xest2=1;}
		  if (xestado.equals("0")){xest1=0;xest2=0;}
		  System.out.println(xest1+" "+xest2);
		  Map<String,Object> params = new HashMap<>();

			params.put("xest1", xest1);
			params.put("xest2", xest2);
			params.put("xsel1", xsel1);
			params.put("xsel2", xsel2);
			params.put("responsable", xusuario);
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/lugar/lugar01.jasper", "lugar");	        
	  }
	
}
