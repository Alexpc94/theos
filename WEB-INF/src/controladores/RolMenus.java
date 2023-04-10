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

import model.domain.Personal;
import model.domain.Roles;
import model.manager.RolMenusManager;
import model.manager.UsuarioRolesManager;
import utils.Jencryption;
import utils.VarGlobales;

@Controller
public class RolMenus {

	@Autowired
	RolMenusManager rolMenusManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"rolMenus15.html"})
	public String rolMenus15(Model model,HttpServletRequest request)  throws IOException  {
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

			//PARAMAMETROS 
			String xestado=request.getParameter("estado");
			if (xestado==null){ xestado="0";}
			model.addAttribute("estado", Integer.parseInt(xestado));
			
			//tomar en cuenta que los privilegios son enviados al template de roles
			VarGlobales var=new VarGlobales("15",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
					
			//recupera lista de personal
			List<Roles> lisRolesMenu =this.rolMenusManager.listarRoles100();
			
			model.addAttribute("opcion", crypt.encrypt("RolesMenus")  );

			model.addAttribute("xRolesMenus", lisRolesMenu );
//			model.addAttribute("opAdd", crypt.encrypt("addRoles"));
//			model.addAttribute("opMod", crypt.encrypt("modRoles"));
//			model.addAttribute("opDel", crypt.encrypt("delRoles"));

		}
		
		model.addAttribute("file1", comodoTpl+"/rolMenus/rolMenus.vm");	
		return "marco";
	}
	
	@RequestMapping({"menusDet15.html"})
	public String menusDet15(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String codr=request.getParameter("codr");
		String estado=request.getParameter("estado");
		
		System.out.println(" codr="+codr+" estado="+estado);
		List<?> lisMenus;
		//recupera los roles de la base de datos
		if (estado.equals("1")){
			//recupera los menus de la base de datos
			lisMenus = this.rolMenusManager.listarMenusAsignados(Integer.parseInt(crypt.decrypt(codr)));
		}else{
			//recupera los menus de la base de datos
			lisMenus = this.rolMenusManager.listarMenusTodos(Integer.parseInt(crypt.decrypt(codr)));
		}

		model.addAttribute("xMenus", lisMenus );
		
		return "rolMenus/menusDet";
	}
	
	
	@RequestMapping({"rolesMenusServices15.html"})
	public String rolesMenusServices15(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
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
//			System.out.println(" opcion ="+ crypt.decrypt(op));
			if (op==null) {op="0";}
			else{ op=crypt.decrypt(op);}//descifra 
			
			//ADICIONAR ROLES
			if (op.equals("RolesMenus")){				
				String xcodr=request.getParameter("xcodr");
				String xcodm=request.getParameter("xcodm");
				
				xcodr=crypt.decrypt(xcodr);
				System.out.println(" llego codr="+xcodr+" xcodm="+xcodm);
				
				String error = this.rolMenusManager.setAsignacionRolesMenus(Integer.parseInt(xcodr), Integer.parseInt(xcodm));
//				System.out.println("  error:"+error);
				
			}
			
			
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//VarGlobales var=new VarGlobales("6");
		//model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/roles/rolesmon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */
/*
	  @RequestMapping("XXrolesReportes01.html")
	  public void XXrolesReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/roles/roles01.jasper", "roles");	        
	  }
*/
}
