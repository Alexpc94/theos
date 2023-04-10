package utils;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import model.domain.Menus;
import model.domain.Mepriv;
import model.domain.Procesos;
import model.domain.Roles;

public class VarGlobales {
	public String zproceso;
	public String zmenu;
	
	public VarGlobales(String xproceso1, String xmenu1){
		this.zproceso=xproceso1;
		this.zmenu=xmenu1;
	}
	
	public Model getDatos(HttpServletRequest request,Model model) {
		//debo recuperar sessiones usuarios, roles, privilegios
		HttpSession session2 = request.getSession(true);
		String xusuario=(String) session2.getAttribute("s_usuario");
		String xroles=(String) session2.getAttribute("s_roles");
		String xmenus=(String) session2.getAttribute("s_menus");
		String xprocesos=(String) session2.getAttribute("s_procesos");
		String xprivilegios=(String) session2.getAttribute("s_privilegios");
		String xalertas=(String) session2.getAttribute("s_alertas");
					
		Jencryption crypt=new Jencryption();
		
//System.out.println("zproceso::"+zproceso+"  zmenu::"+zmenu);
		
//		System.out.println("wwwwusuario::"+xusuario);
//		System.out.println("roles::"+xroles);
//		System.out.println("menus::"+xmenus);
//		System.out.println("procesos::"+xprocesos);
		
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
	//		System.out.println("fila :"+rol[con]+"-"+rol[con+1]);
				con=con+2;
			}
		}
		
		//rol activo
		String zrolActivoID="0";
		String xrolActivo=(String) session2.getAttribute("s_rolActivoID");
		if (xrolActivo == null){				
			session2.setAttribute("s_rolActivoID", rol[0] );//rol activo Identificador
			//System.out.println("INICIO rolID="+rol[0]+" rolNom="+rol[1]);
			zrolActivoID=rol[0];
		}else{
			zrolActivoID=request.getParameter("zroles");
			if (zrolActivoID==null){
				zrolActivoID=(String) session2.getAttribute("s_rolActivoID");
			}
//System.out.println("OLD rolID="+zrolActivoID);
		}
		//rolActiveID
		
		//TRANSFORMA STRING A CLASE MENUS
		String[] menus = xmenus.split("@");
		con=0;
		//System.out.println("long="+menus.length);
		ArrayList<Menus> listaMenus=new ArrayList<Menus>();
		if (!xmenus.equals("-")){
			while (con < menus.length){
				Menus xmenu = new Menus();
				xmenu.setCodr(Integer.parseInt(menus[con]));
				xmenu.setCodm(Integer.parseInt(menus[con+1]));
				xmenu.setCodmCrypt(crypt.encrypt(menus[con+1]));			
				xmenu.setNombre(menus[con+2]);
				listaMenus.add(xmenu);
				//System.out.println("fila :"+menus[con]+"-"+menus[con+1]+"-"+menus[con+2]+" codmCrypt="+crypt.encrypt(menus[con+1]));
				con=con+3;
			}
		}
		
		//TRANSFORMA STRING A CLASE PROCESOS
		String[] procesos = xprocesos.split("@");
		con=0;
//System.out.println("longint="+menus.length);
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
		
		//TRANSFORMA STRING A CLASE MEPRIV
		String[] privs = xprivilegios.split("@");
		con=0;
		//System.out.println("long="+menus.length);
		//Se inicializa "-" lo que se envia
			model.addAttribute("PA","-");
			model.addAttribute("PB","-");
			model.addAttribute("PM","-");
			model.addAttribute("PH","-");
			model.addAttribute("PR","-");
			model.addAttribute("PP","-");
			model.addAttribute("PW","-");
		ArrayList<Mepriv> listaMepriv=new ArrayList<Mepriv>();
		while (con < privs.length){
			if (zmenu.equals(privs[con])&&(zproceso.equals(privs[con+1]))){ //si es el menu que se selecciono
				Mepriv xpri = new Mepriv();
				xpri.setCodm2(Integer.parseInt(privs[con]));
				xpri.setCodp2(Integer.parseInt(privs[con+1]));
				xpri.setOpcion(privs[con+2]);
				listaMepriv.add(xpri);
				//SELECCIONO LOS PRIVILEGIOS PARA ENVIAR A LA PAGINA
				if (privs[con+2].equals("A")){ model.addAttribute("PA", privs[con+2]); } //A-adicionar
				if (privs[con+2].equals("B")){ model.addAttribute("PB", privs[con+2]); } //B-borrar
				if (privs[con+2].equals("M")){ model.addAttribute("PM", privs[con+2]); } //M-modificar
				if (privs[con+2].equals("H")){ model.addAttribute("PH", privs[con+2]); } //M-Habilitar
				if (privs[con+2].equals("R")){ model.addAttribute("PR", privs[con+2]); } //R-reporte
				if (privs[con+2].equals("P")){ model.addAttribute("PP", privs[con+2]); } //p-pagos
				if (privs[con+2].equals("W")){ model.addAttribute("PW", privs[con+2]); } //p-modifica todos los estados
//				System.out.println("fila :"+privs[con]+"-"+privs[con+1]+"-"+privs[con+2]);
			}
			con=con+3;
		}
		
//		System.out.println("roles:"+listaRoles.size()+"-menus="+listaMenus.size()+"-procesos="+listaProcesos.size()+" rolActivoID="+zrolActivoID);
		model.addAttribute("xusuario", xusuario );
		model.addAttribute("xlistaRoles", listaRoles );
		model.addAttribute("xlistaMenus", listaMenus );
		model.addAttribute("xlistaProcesos", listaProcesos );
		model.addAttribute("xlistaPrivilegios", listaMepriv );
		model.addAttribute("rolActivoID", zrolActivoID);
		model.addAttribute("xalertas", xalertas );
		
		return model;
	}//Constructor

	public Model getPrivs(HttpServletRequest request,Model model) {
		//debo recuperar sessiones usuarios, roles, privilegios
		HttpSession session2 = request.getSession(true);
		String xprivilegios=(String) session2.getAttribute("s_privilegios");
//System.out.println(" privilegios="+xprivilegios+" zproceso="+zproceso+" zmenu="+zmenu);				
		Jencryption crypt=new Jencryption();
		
		//TRANSFORMA STRING A CLASE MEPRIV
		String[] privs = xprivilegios.split("@");
		int con=0;

			model.addAttribute("PA","-");
			model.addAttribute("PB","-");
			model.addAttribute("PM","-");
			model.addAttribute("PH","-");
			model.addAttribute("PR","-");
			model.addAttribute("PP","-");
			model.addAttribute("PW","-");
		ArrayList<Mepriv> listaMepriv=new ArrayList<Mepriv>();
		while (con < privs.length){
			if (zmenu.equals(privs[con])&&(zproceso.equals(privs[con+1]))){ //si es el menu que se selecciono
				Mepriv xpri = new Mepriv();
				xpri.setCodm2(Integer.parseInt(privs[con]));
				xpri.setCodp2(Integer.parseInt(privs[con+1]));
				xpri.setOpcion(privs[con+2]);
				listaMepriv.add(xpri);
				//SELECCIONO LOS PRIVILEGIOS PARA ENVIAR A LA PAGINA
				if (privs[con+2].equals("A")){ model.addAttribute("PA", privs[con+2]); } //A-adicionar
				if (privs[con+2].equals("B")){ model.addAttribute("PB", privs[con+2]); } //B-borrar
				if (privs[con+2].equals("M")){ model.addAttribute("PM", privs[con+2]); } //M-modificar
				if (privs[con+2].equals("H")){ model.addAttribute("PH", privs[con+2]); } //H-Habilitar
				if (privs[con+2].equals("R")){ model.addAttribute("PR", privs[con+2]); } //R-reporte
				if (privs[con+2].equals("P")){ model.addAttribute("PP", privs[con+2]); } //p-pagos
				if (privs[con+2].equals("W")){ model.addAttribute("PW", privs[con+2]); } //p-modifica todos los estados
//				System.out.println("fila :"+privs[con]+"-"+privs[con+1]+"-"+privs[con+2]);
			}
			con=con+3;
		}
		
		return model;
	}//Constructor
	
}
