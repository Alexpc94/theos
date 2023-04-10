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

import model.manager.PromocionesManager;
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
public class Promociones {

	@Autowired
	PromocionesManager promocionesManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"promocionesmon_107.html"})
	public String accionesmon_104(Model model,HttpServletRequest request)  throws IOException  {
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
			VarGlobales var=new VarGlobales("107",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
			
			//PARAMAMETROS  ESTADO
			String xestado=request.getParameter("estado");
			if (xestado==null){ xestado="1";}
			int xest1=0, xest2=1;
			if (xestado.equals("1")){xest1=1;xest2=1;}
			if (xestado.equals("0")){xest1=0;xest2=0;}
			model.addAttribute("estado", Integer.parseInt(xestado));
			
			//LISTA DE PROMOCIONES
			List<?> lisPromociones = this.promocionesManager.listarPromocionesMon(xest1,xest2);
			model.addAttribute("xPromociones", lisPromociones);
			
			model.addAttribute("opAdd", crypt.encrypt("addPromociones"));
			model.addAttribute("opDel", crypt.encrypt("delPromociones"));
			
//			model.addAttribute("opMod", crypt.encrypt("modTcambio"));
//			model.addAttribute("opHab", crypt.encrypt("habTcambio"));
		}
		
		model.addAttribute("xfecha",xfecha);
		
		model.addAttribute("file1", comodoTpl+"/promociones/promocionesmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaSocios_107.html"})
	public String listaSocios_104(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
//System.out.println("action="+xaction+" row="+xrow+" xdatos="+xdatos);
		
		List<?> xlistaSocio = this.promocionesManager.listarSocios();
		model.addAttribute("xlistaSocio", xlistaSocio);
	
		return "promociones/listaSocios";	
	}
	
	
	@RequestMapping({"listaPromociones_det_107.html"})
	public String listaPromociones_det_107(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("107",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas

		//PARAM ESTADO
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }
		
		//LISTA DE PROMOCIONES
		List<?> lisPromociones = this.promocionesManager.listarPromocionesMon(xest1,xest2);
		model.addAttribute("xPromociones", lisPromociones);
		
		return "promociones/promocionesDet";
	}
	
	//detalle de accion
	@RequestMapping({"detalleProgramacion_107.html"})
	public String detalleProgramacion_107(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodp=request.getParameter("xcodp");
		String xnombre=request.getParameter("xnombre");
		String xobser=request.getParameter("xobser");
System.out.println(" xcodp="+xcodp);		
		//recupera los bebeficiarios de la base de datos

		List<?> lisPromociones = this.promocionesManager.listaDetalle_promociones(xcodp);
		model.addAttribute("xPromociones", lisPromociones);
		
		model.addAttribute("xcodp", xcodp );
		model.addAttribute("xnombre", xnombre );
		model.addAttribute("xobser", xobser );
		return "promociones/detallePromociones";	
	}
	
	@RequestMapping({"promocionesServices_107.html"})
	public String promocionesServices_107(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			
			//ADICIONAR ACCIONES
			if (op.equals("addPromociones")){
System.out.println("GUARDANDO ADD PROMOCIONES....................................................................");				
				String zfecha=request.getParameter("a_fecha");
				String xcodper=request.getParameter("a_codperVal");
				String xestado=request.getParameter("a_estadoVal");
				String xcosto=request.getParameter("a_costo");
				String xmesini=request.getParameter("a_mesini");
				String xanioini=request.getParameter("a_anioini");
				String xmesfin=request.getParameter("a_mesfin");
				String xaniofin=request.getParameter("a_aniofin");
				String xcondonado=request.getParameter("a_condonados");			
				String xmonto=request.getParameter("a_monto");
				String xobser=request.getParameter("a_obser");
				
				int sw=1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date xfecha = dateformat.parse(zfecha);
				
//System.out.println(" login="+xlogin+" fecha="+xfecha+" xcodper="+xcodper+" xestado="+xestado+" xmonto="+xmonto+" obser="+xobser);
//System.out.println(" xmesini="+xmesini+" xanioini="+xanioini+" xmesfin="+xmesfin+" xaniofin="+xaniofin+" xcondonado="+xcondonado);
				//	public String setAddPromociones(Date xfecha,int xcodper,float xmonto,String xobser,int xmesini,int xanioini,int xcondonado,int xmesfin, int xaniofin,String xlogin){
				String error = this.promocionesManager.setAddPromociones(xfecha,Integer.parseInt(xcodper),Float.parseFloat(xmonto),xobser,Integer.parseInt(xmesini),Integer.parseInt(xanioini),Integer.parseInt(xcondonado),Integer.parseInt(xmesfin),Integer.parseInt(xaniofin),xlogin);

//System.out.println(" error ="+error);
										
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

/*
			//MODIFICAR TCAMBIO
			if (op.equals("modTcambio")){				
				String fec=request.getParameter("m_fecha");
				String tc=request.getParameter("m_tc");
				String codtc=request.getParameter("m_codtc");
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fecha = dateformat.parse(fec);
				
				String error = this.tcambioManager.setModTcambio(fecha,Double.parseDouble(tc),Integer.parseInt(codtc));

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
				
			}
*/
/*
			//HABILITAR TCAMBIO
			if (op.equals("habTcambio")){				
				String codtc=request.getParameter("h_codtc");
				System.out.println(codtc);
				String error = this.tcambioManager.setHabTcambio(Integer.parseInt(codtc));
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";
			}
*/
			
			//ELIMIANR TCAMBIO
			if (op.equals("delPromociones")){				
				String xcodp=request.getParameter("b_codpVal");
				
				String error = this.promocionesManager.setDelPromociones(xcodp,xlogin);
				
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
		
		model.addAttribute("file1", comodoTpl+"/tcambio/tcambiomon.vm");	
		return "marco";
	}
	
/********************************************************************************************************************************/
/************************     REPORTES   ************************************************************************************** */
/*
	  @RequestMapping("tcambioReportes01.html")
	  public void tcambioReporta01(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException {
		 Map<String,Object> params = new HashMap<>();
		 //llamada a la clase GenerarReportes
		 GenerarReportes rep=new GenerarReportes();
		 String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/tcambio/tcambio01.jasper", "tipo de cambio");	        
	  }
*/
}
