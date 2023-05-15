package controladores;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import model.domain.Menus;
import model.domain.Procesos;
import model.domain.Roles;
import model.manager.AccesoManager;
import net.sf.jasperreports.engine.JRException;
import utils.GenerarReportes;
import utils.Jencryption;
import utils.Utilitarios;

@Controller
	public class Usuarios {
		
		@Autowired
		AccesoManager accesoManager;
		
		@Value("${comodo.tpl}")
		private String comodoTpl;
		
		@Autowired
		private DataSource dataSource;
		
		@RequestMapping({"index2.html"})
		public String vista1(Model model, HttpServletRequest request)  throws IOException  {
			Jencryption crypt=new Jencryption();
			Utilitarios u=new Utilitarios();
			//debo recuperar sessiones usuarios, roles, privilegios
			HttpSession session = request.getSession(true);
			String xusuario=(String) session.getAttribute("s_usuario");
			String xroles=(String) session.getAttribute("s_roles");
			String xmenus=(String) session.getAttribute("s_menus");
			String xprocesos=(String) session.getAttribute("s_procesos");
			String xalertas=(String) session.getAttribute("s_alertas");
			String xalertas2=(String) session.getAttribute("s_alertas2"); //es de cumpleaños
			if (xalertas2==null)	{
				System.out.println("es nulo;");
//				xalertas2="1";
			}
			//System.out.println("wwwwusuario::"+xusuario);
			//System.out.println("roles::"+xroles);
			//System.out.println("menus::"+xmenus);
			//System.out.println("procesos::"+xprocesos);
			
			//TRANSFORMA STRING ROLES A CLASE ROLES Y LISTA
			String[] rol = xroles.split("@");
			int con=0;
			//System.out.println("long="+rol.length);
			ArrayList<Roles> listaRoles=new ArrayList<Roles>();
			if (!xroles.equals("-")){
				while (con < rol.length){
					Roles xrol = new Roles();
					xrol.setCodr(Integer.parseInt(rol[con]));
					xrol.setNombre(rol[con+1]);
					listaRoles.add(xrol);
//System.out.println("fila :"+rol[con]+"-"+rol[con+1]);
					con=con+2;
				}
			}
			
			//rol activo
			String zrolActivoID="0";
			String xrolActivo=(String) session.getAttribute("s_rolActivoID");
			if (xrolActivo == null){				
				session.setAttribute("s_rolActivoID", rol[0] );//rol activo Identificador
//System.out.println("INICIO rolID="+rol[0]+" rolNom="+rol[1]);
				zrolActivoID=rol[0];
			}else{
				zrolActivoID=request.getParameter("zroles");
				if (zrolActivoID==null){
					zrolActivoID=(String) session.getAttribute("s_rolActivoID");
				}else{
					session.setAttribute("s_rolActivoID", zrolActivoID );//rol activo Identificador
				}
				//System.out.println("OLD rolID="+zrolActivoID);
			}
			//rolActiveID
			model.addAttribute("rolActivoID", zrolActivoID );//ENVIO
			
			//TRANSFORMA STRING A CLASE MENUS
			String[] menus = xmenus.split("@");
			con=0;
//System.out.println(" xmenus="+xmenus+"long="+menus.length);
			ArrayList<Menus> listaMenus=new ArrayList<Menus>();
			if (!xmenus.equals("-")){
				while (con < menus.length){
					Menus xmenu = new Menus();
					xmenu.setCodr(Integer.parseInt(menus[con]));
					xmenu.setCodm(Integer.parseInt(menus[con+1]));
					xmenu.setCodmCrypt(crypt.encrypt(menus[con+1]));
					xmenu.setNombre(menus[con+2]);
					listaMenus.add(xmenu);
					//System.out.println("fila :"+menus[con]+"-"+menus[con+1]+"-"+menus[con+2]);
					con=con+3;
				}
			}
			
			//TRANSFORMA STRING A CLASE PROCESOS
			String[] procesos = xprocesos.split("@");
			con=0;
//System.out.println("long="+menus.length);
			ArrayList<Procesos> listaProcesos=new ArrayList<Procesos>();
			if (!xprocesos.equals("-")){
				while (con < procesos.length){
					Procesos xproc = new Procesos();
					xproc.setCodr(Integer.parseInt(procesos[con]));
					xproc.setCodm(Integer.parseInt(procesos[con+1]));
					xproc.setCodp(Integer.parseInt(procesos[con+2]));
					xproc.setNombre(procesos[con+3]);
					xproc.setLink(procesos[con+4]);
					xproc.setHelp(procesos[con+5]);
					listaProcesos.add(xproc);
					//System.out.println("codr:"+procesos[con]+"-codm="+procesos[con+1]+"-codp="+procesos[con+2]+"-"+procesos[con+3]);
					con=con+6;
				}
			}
			String xfecha=u.getFecha();
			
			model.addAttribute("xlistaRoles", listaRoles );
			model.addAttribute("xlistaMenus", listaMenus );
			model.addAttribute("xlistaProcesos", listaProcesos );
			model.addAttribute("xusuario", xusuario );
			model.addAttribute("xfecha", xfecha );
			model.addAttribute("xalertas", xalertas );
			model.addAttribute("xalertas2", xalertas2 );
		
			model.addAttribute("file1", comodoTpl+"/usuarios/listaUs.vm" );		
			return "marco";
		}
	

		


		
	}
