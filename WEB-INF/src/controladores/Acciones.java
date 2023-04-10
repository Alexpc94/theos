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

import model.domain.General;
import model.manager.AccesoManager;
import model.manager.AccionesManager;
import model.manager.TcambioManager;
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
public class Acciones {

	@Autowired
	AccionesManager accionesManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"accionesmon_104.html"})
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
			VarGlobales var=new VarGlobales("104",crypt.decrypt(cd)); //params: (proceso, menu)
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
			
			//PARAMAMETROS  ESTADO
			String xestado=request.getParameter("estado");
			if (xestado==null){ xestado="1";}
			int xest1=0, xest2=1;
			if (xestado.equals("1")){xest1=1;xest2=1;}
			if (xestado.equals("0")){xest1=0;xest2=0;}
			model.addAttribute("estado", Integer.parseInt(xestado));
			
			//PARAMAMETROS SALDO
			String xsaldo=request.getParameter("saldo");
			if (xsaldo==null){ xsaldo="2";}
			float xsal1=0, xsal2=999999;
			if (xsaldo.equals("1")){xsal1=1;xsal2=999999;}
			if (xestado.equals("0")){xsal1=0;xsal2=0;}
			model.addAttribute("saldo", Integer.parseInt(xsaldo));
			
			//LISTA DE ACCIONES
			List<?> lisAcciones = this.accionesManager.listarSociosMon(xest1,xest2,xsal1,xsal2);
			model.addAttribute("xAcciones", lisAcciones);
			
			model.addAttribute("opAdd", crypt.encrypt("addAccion"));
			model.addAttribute("opDel", crypt.encrypt("delAccion"));
			
//			model.addAttribute("opMod", crypt.encrypt("modTcambio"));
//			model.addAttribute("opHab", crypt.encrypt("habTcambio"));
		}
		
		model.addAttribute("xfecha",xfecha);
		
		model.addAttribute("file1", comodoTpl+"/acciones/accionesmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaSocios_104.html"})
	public String listaSocios_104(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
//System.out.println("action="+xaction+" row="+xrow+" xdatos="+xdatos);
		
		List<?> xlistaSocio = this.accionesManager.listarSocios(1,1);
		model.addAttribute("xlistaSocio", xlistaSocio);
	
		return "acciones/listaSocios";	
	}
	
	
	@RequestMapping({"listaAcciones_det_104.html"})
	public String listaAcciones_det_104(Model model,HttpServletRequest request)  throws IOException  {
		Jencryption crypt=new Jencryption();
		String xestado=request.getParameter("xestado");
		String xsaldo=request.getParameter("xsaldo");
	
		HttpSession session=request.getSession(true);
		String cd=(String) session.getAttribute("s_menuActivo");
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		//tomar en cuenta que los privilegios son enviados al template de roles
		VarGlobales var=new VarGlobales("104",crypt.decrypt(cd)); //params: (proceso, menu)
		model=var.getPrivs(request,model); //ojo solo saca privilegio y no los demas
		
//		System.out.println(" LISTA ROLES 6::");
//		System.out.println(" estado="+xestado);
		
		//PARAM ESTADO
		int xest1=0, xest2=1;
		if (xestado.equals("1")){ xest1=1; xest2=1; }
		if (xestado.equals("0")){ xest1=0; xest2=0; }
		
		//PARAM SALDO
		float xsal1=0, xsal2=999999;
		if (xsaldo.equals("1")){xsal1=1;xsal2=999999;}
		if (xsaldo.equals("0")){xsal1=0;xsal2=0;}

		//LISTA DE ACCIONES
		List<?> lisAcciones = this.accionesManager.listarSociosMon(xest1,xest2,xsal1,xsal2);
		model.addAttribute("xAcciones", lisAcciones);
		
		return "acciones/accionesDet";
	}

	//detalle de accion
	@RequestMapping({"detalle_Accion_104.html"})
	public String detalle_Accion_104(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcoda=request.getParameter("xcoda");
		String xnombre=request.getParameter("xnombre");
		
		//recupera los bebeficiarios de la base de datos

		List<?> lisAcciones = this.accionesManager.listaDetalle_Accines(xcoda);
		model.addAttribute("xAcciones", lisAcciones);
		
		model.addAttribute("xcoda", xcoda );
		model.addAttribute("xnombre", xnombre );
		return "acciones/detalleAcciones";	
	}
	
	@RequestMapping({"extrae_Accion_104.html"})
	public String extrae_Accion_104(Model model,HttpServletRequest request)  throws IOException  {	
		//LA CONSULTA DEVUELVE UNA CLASE
		int xnroaccion = this.accionesManager.getNroAccion();
			
		JsonObject object = new JsonObject();
		int sw=1;		
		
//		System.out.println("El nro de accion=="+xnroaccion);
		
		object.addProperty("xnroaccion", xnroaccion);
		
		model.addAttribute("datos",object.toString());//guarda los datos en el atributo "datos" 	
		return "util/json";
	}
	
	@RequestMapping({"accionServices_104.html"})
	public String accionServices_104(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException  {
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
			if (op.equals("addAccion")){				
				String zfecha=request.getParameter("a_fecha");
				String xnro=request.getParameter("a_nro");
				String xsw=request.getParameter("a_sw");
				String xcodper=request.getParameter("a_codperVal");
				String xmonto=request.getParameter("a_monto");
				String xobser=request.getParameter("a_obser");
				
				String xinteres=request.getParameter("a_interes");
				String xnrocuota=request.getParameter("a_nrocuota");
				String xcuota=request.getParameter("a_cuota");
				String xmontot=request.getParameter("a_montot");
				
				
				int sw=1;
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date xfecha = dateformat.parse(zfecha);
				
				if (Integer.parseInt(xsw) == 2 ){ //SI ES ACCION GENERADO  SI ES ==1  ES EDITADO
					xnro="0"; //esto quiere decir que se DEBE GUARDAR EL NUMERO ACCION GENERADO
				}
				
//System.out.println(" llego login="+xlogin+" fecha="+xfecha+" SW="+xsw+" nro="+xnro+" xcodper="+xcodper+" xmonto="+xmonto+" obser="+xobser);
//System.out.println(" xinteres="+xinteres+" xnrocuota="+xnrocuota+" xcuota="+xcuota+" xmontot="+xmontot);
				//setAddAccion(Date fecha,int xnro,int xcodper,float xmonto,String xobser,int xmesactiv,int xanioactiv,String xlogin){
				String error = this.accionesManager.setAddAccion(xfecha,Integer.parseInt(xnro),Integer.parseInt(xcodper),Float.parseFloat(xmonto),xobser,1,1,xlogin,Integer.parseInt(xnrocuota),Float.parseFloat(xinteres),Float.parseFloat(xcuota),Float.parseFloat(xmontot));
				
				System.out.println(" error ="+error);
				
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
			if (op.equals("delAccion")){				
				String xcoda=request.getParameter("b_coda");
				
				String error = this.accionesManager.setDelAccion(xcoda);
				
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
