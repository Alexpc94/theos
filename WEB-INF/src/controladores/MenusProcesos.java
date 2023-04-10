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

import model.domain.Menus;
import model.manager.MenusProcesosManager;
import utils.Jencryption;
import utils.VarGlobales;

@Controller
public class MenusProcesos {

	@Autowired
	MenusProcesosManager menusProcesosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"menusProcesos16.html"})
	public String menusProcesos16(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("16",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
					
			//recupera lista de personal
			List<Menus> lisMenus =this.menusProcesosManager.listarMenus100();
			
			model.addAttribute("opcion", crypt.encrypt("MenusProcesos")  );
			model.addAttribute("opcionPriv", crypt.encrypt("MenusProcesosPrivilegio")  );

			model.addAttribute("xMenus", lisMenus );
		}
		
		model.addAttribute("file1", comodoTpl+"/menusProcesos/menusProcesos.vm");	
		return "marco";
	}
	
	@RequestMapping({"procesosDet16.html"})
	public String procesosDet16(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String codm=request.getParameter("codm");
		String estado=request.getParameter("estado");
		String xlogin=(String) session.getAttribute("s_login");
		
//		System.out.println(" codr="+codm+" estado="+estado+" codm="+crypt.decrypt(codm));
		List<?> lisMenus=null;
		List<?> lisMenusPrivs=null;
		//recupera los roles de la base de datos
		if (estado.equals("1")){
			//recupera los menus de la base de datos
			lisMenus = this.menusProcesosManager.listarProcesosAsignados(Integer.parseInt(crypt.decrypt(codm)));
			lisMenusPrivs = this.menusProcesosManager.listarProcesosTodosPrivs(Integer.parseInt(crypt.decrypt(codm)),xlogin);
		}else{
			//recupera los menus de la base de datos
			lisMenus = this.menusProcesosManager.listarProcesosTodos(Integer.parseInt(crypt.decrypt(codm)));
			lisMenusPrivs = this.menusProcesosManager.listarProcesosTodosPrivs(Integer.parseInt(crypt.decrypt(codm)),xlogin);			
		}

		model.addAttribute("xProcesos", lisMenus );
		model.addAttribute("xProcesosPrivs", lisMenusPrivs );
		
		return "menusProcesos/procesosDet";
	}
	
	
	@RequestMapping({"menusProcesosServices16.html"})
	public String menusProcesosServices16(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
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
//System.out.println(" opcion ="+ crypt.decrypt(op));
			if (op==null) {op="0";}
			else{ op=crypt.decrypt(op);}//descifra 
			
			//ADICIONAR O BORRA menpro
			if (op.equals("MenusProcesos")){				
				String xcodm=request.getParameter("xcodm");
				String xcodp=request.getParameter("xcodp");
				
				xcodm=crypt.decrypt(xcodm);
//System.out.println(" llego codm="+xcodm+" xcodp="+xcodp);
				
				String error = this.menusProcesosManager.setAsignacionMenusProcesos(Integer.parseInt(xcodm), Integer.parseInt(xcodp));
//System.out.println("  error:"+error);
				
			}
			
			//ADICIONAR O BORRA MEPRIV
			if (op.equals("MenusProcesosPrivilegio")){				
				String xcodm=request.getParameter("xcodm");
				String xcodp=request.getParameter("xcodp");
				String xletra=request.getParameter("xletra");
				
				xcodm=crypt.decrypt(xcodm);
//System.out.println(" llego codm="+xcodm+" xcodp="+xcodp);
				
				String error = this.menusProcesosManager.setAsignacionMenusProcesosPrivs(Integer.parseInt(xcodm), Integer.parseInt(xcodp), xletra);
//System.out.println("  error:"+error);
				
			}
			
		}
		
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
