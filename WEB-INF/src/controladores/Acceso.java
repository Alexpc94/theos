package controladores;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import model.manager.AccesoManager;
import net.sf.jasperreports.engine.JRException;
import utils.GenerarReportes;

@Controller
public class Acceso {

	@Autowired
	AccesoManager accesoManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"index.html"})
	public String vista1(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		session.removeAttribute("s_login");
		session.removeAttribute("s_usuario");
		session.removeAttribute("s_roles");
		session.removeAttribute("s_menus");
		session.removeAttribute("s_procesos");
		session.removeAttribute("s_rolActivoID");
		session.removeAttribute("s_menuActivo");
		

		return "/acceso/acceso";
	}
	
	@RequestMapping({"valida.html"})
	public String valida(Model model,HttpServletRequest request, HttpServletResponse response)  throws IOException  {
		String xuser=request.getParameter("xuser");
		String xclave=request.getParameter("xclave");
		
		String xres = this.accesoManager.getLogin(xuser,xclave);
		System.out.println(xres);
		//System.out.println("xuser="+xuser+" xclave="+xclave+" cant="+xcant);
		if (!xres.equals("0")){
			// Debo sesionar usuarios, roles, privilegios
//System.out.println("llego:"+xres);
			String[] parts = new String[10];
			parts = xres.split(">");
//System.out.println("tamaño del Vector..!->"+parts.length);	
			//completa los nulos
			for (int i=parts.length; i<5;i++)
				parts[i]="-";
//System.out.println("otro tamaño del Vector..!->"+parts.length);				
			String xusuario = parts[0]; // usuario
			String xroles = parts[1]; // roles
			String xmenus = parts[2]; // menus
			String xprocesos = parts[3]; // procesos
			String xprivilegios = parts[4]; // privilegios
			String xalertas = parts[5]; // alertas, si es que existen datos para alertar
//System.out.println(" privs="+xprivilegios);			
			HttpSession session=request.getSession(true);
			session.setAttribute("s_login", xuser );
			session.setAttribute("s_usuario", xusuario );
			session.setAttribute("s_roles", xroles );
			session.setAttribute("s_menus", xmenus );
			session.setAttribute("s_procesos", xprocesos );
			session.setAttribute("s_privilegios", xprivilegios );
			session.setAttribute("s_alertas", xalertas );
//System.out.println("LLEGO HASTA AQUI..!");			
			//source: Usuarios
			response.sendRedirect("index2.html");
		}else{
			String xval="1";
			if ((xuser==null)||(xuser.equals(""))) { xval="0"; }
			model.addAttribute("mensaje", xval );
		}
		
		return "/acceso/acceso";
	}
	
	@RequestMapping({"listaAlertas_2.html"})
	public String listaAlertas_2(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		List<?> xlistaMayores = this.accesoManager.listarMayores21();
		List<?> xlistaVarios = this.accesoManager.listarAlertaVarios();
		model.addAttribute("xlistaMayores", xlistaMayores);
		model.addAttribute("xlistaVarios", xlistaVarios);
		return "acceso/listaAlertas";	
	}
	
	@RequestMapping({"birthlist_2.html"})
	public String birthlist_2(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		List<?> xlistaCumpleanios = this.accesoManager.listarCumpleanios();
		model.addAttribute("xlistaCumpleanios", xlistaCumpleanios);
		return "acceso/listaCumpleaños";	
	}
	
	@RequestMapping("repo_benef_mayores_02.html")
	public void repo_benef_mayores_02(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		HttpSession session=req.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		  
		 Map<String,Object> params = new HashMap<>();
		 params.put("responsable", xusuario);
//System.out.println("llegamos hasta aqui.. reportes de beneficiarios..");			  
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/alertas/alertaBenef.jasper", "alerta");	        
	}
	
	@RequestMapping("repo_otros_02.html")
	public void repo_otros_02(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		HttpSession session=req.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		  
		 Map<String,Object> params = new HashMap<>();
		 params.put("responsable", xusuario);
//System.out.println("llegamos hasta aqui.. reportes OTROS..");			  
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/alertas/alertaOtros.jasper", "alertaOtros");	        
	}
		
//NOTA: LAS ALERTAS ESTAN EN Usuarios.java
	
}
