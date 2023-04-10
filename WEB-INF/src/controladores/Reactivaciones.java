package controladores;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import org.apache.velocity.app.Velocity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;

import model.domain.Personal;
import model.manager.ReactivacionesManager;

//import net.glxn.qrgen.QRCode;
//import net.glxn.qrgen.image.ImageType;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
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
import utils.CodigoControl7;
import utils.GeneradorQR;
import utils.GenerarReportes;
import utils.Jencryption;
import utils.Numero_a_Letra;
import utils.Utilitarios;
import utils.VarGlobales;

@Controller
public class Reactivaciones {

	@Autowired
	ReactivacionesManager reactivacionesManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Value("${comodo.qr}")
	private String comodoQr;
	
	@Autowired
	private DataSource dataSource;
	

	@RequestMapping({"reactivacionesmon_108.html"})
	public String reactivacionesmon_108(Model model,HttpServletRequest request)  throws IOException, ParseException  {
		HttpSession session=request.getSession(true);
		Utilitarios u=new Utilitarios();
		String xfecha=u.getFecha();
		String xusuario=(String) session.getAttribute("s_usuario");
		//calcula la fecha actual del sistema
		//String xfecha=u.getFecha();

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
			VarGlobales var=new VarGlobales("108",crypt.decrypt(cd));
			model=var.getDatos(request,model);
					
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
			
			DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaini=dateformat.parse(xfecha);//Valores de entrada para el filtro de la consulta
			Date fechafin=dateformat.parse(xfecha);//Valores de entrada para el filtro de la consulta
			
			List<?> lisMpagos = this.reactivacionesManager.listaRactivarMon(xest1,xest2,fechaini,fechafin);
			model.addAttribute("xMpagos", lisMpagos);
			
			

//			model.addAttribute("opReporte", crypt.encrypt("Factura"));
			
			model.addAttribute("op_json", crypt.encrypt("lista_socios"));
			model.addAttribute("opAdd", crypt.encrypt("addReactivacion"));
			model.addAttribute("opDel", crypt.encrypt("delReactivaciones"));
		}
		
		model.addAttribute("xfechaini",xfecha);
		model.addAttribute("xfechafin",xfecha);
		model.addAttribute("xfecha",xfecha);
		
		model.addAttribute("file1", comodoTpl+"/reactivaciones/reactivarmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listarBoletas_108.html"})
	public String listarBoletas102(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		
		String xcodper=request.getParameter("xcodper");
		
//System.out.println(" xcodper="+xcodper);
		
		//recupera los roles de la base de datos
		List<?> lisboletas = this.reactivacionesManager.listaBoletasApagar(Integer.parseInt(xcodper));
		model.addAttribute("xBoletas", lisboletas);
		
		return "reactivaciones/lista_boletas";
	}

	@RequestMapping({"detalle_reactivacion_108.html"})
	public String detalle_reactivacion_108(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodr=request.getParameter("xcodr");
		String xnombre=request.getParameter("xnombre");
		String xobser=request.getParameter("xobser");
		
System.out.println("this is codr="+xcodr);

		List<?> lisboletas_pagadas = this.reactivacionesManager.listaDetalle_Reactivaciones(xcodr);
		model.addAttribute("xBoletas_pagadas", lisboletas_pagadas);
		
		model.addAttribute("xcodr", xcodr );
		model.addAttribute("xnombre", xnombre );
		model.addAttribute("xobser", xobser);
		return "reactivaciones/detalleReactivaciones";	
	}
	
	@RequestMapping({"reactivarDet108.html"})
	public String reactivarDet108(Model model,HttpServletRequest request)  throws IOException, ParseException  {
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}

		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("108",crypt.decrypt(cd));
		model=var.getDatos(request,model);
			
		//PARAMAMETROS 
		String xestado=request.getParameter("xestado");
		String xfini=request.getParameter("xfinicial");
		String xffin=request.getParameter("xffinal");
	
		if (xestado==null){ xestado="1";}
		int xest1=0, xest2=1;
		if (xestado.equals("1")){xest1=1;xest2=1;}
		if (xestado.equals("0")){xest1=0;xest2=0;}
		model.addAttribute("estado", Integer.parseInt(xestado));
		
//System.out.println(" xfini="+xfini+" xffin="+xffin+" estado="+xestado);
		
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaini=dateformat.parse(xfini);//Valores de entrada para el filtro de la consulta
		Date fechafin= dateformat.parse(xffin);//Valores de entrada para el filtro de la consulta
			
		List<?> lisMpagos = this.reactivacionesManager.listaRactivarMon(xest1,xest2,fechaini,fechafin);
		model.addAttribute("xMpagos", lisMpagos);
		
		model.addAttribute("opReporte", crypt.encrypt("Factura"));
		
		return "reactivaciones/reactivarDet";
	}

	@RequestMapping({"reactivacionServices_108.html"})
    @Transactional()
	public String reactivacionServices_108(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException, NotFoundException, WriterException  {
		Jencryption crypt=new Jencryption();
		HttpSession session=request.getSession(true);
		String xusuario=(String) session.getAttribute("s_usuario");
		String xlogin=(String) session.getAttribute("s_login");
		Utilitarios util=new Utilitarios();
		//verifica si el usuario se autentifico  oam
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
				List<Personal> xlistarsocios = this.reactivacionesManager.listarSocios();
				JSONArray sgAU =new  JSONArray();
//System.out.println("llegoo"+op);
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

			//ELIMIANR PAGOS Y FACTURA delPagoBoletas
			if (op.equals("delReactivaciones")){	
System.out.println(" ELIMINANDO REACTIVACIONES ............................................................................ ");				
				String xcodr=request.getParameter("d_codrVal");

				String error = this.reactivacionesManager.setDelMpagos(xcodr);

				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

			//ADICIONAR REVALIDACION
			if (op.equals("addReactivacion")){				
				String xfecha=request.getParameter("a_fecha");
				String xsocioVal=request.getParameter("xsocio_val");
				String xtotalval=request.getParameter("xtotalVal");
				String xmontotal=request.getParameter("xmontotal");
				String xobser=request.getParameter("a_obser");
				String xfilas=request.getParameter("filas");
				String xcodreac="";
System.out.println("GUARDANDO REACTIVACION .......................................................................................");
System.out.println("fecha="+xfecha+" xsocio="+xsocioVal+" xtotalVal="+xtotalval+" xmontotal="+xmontotal+" xfila="+xfilas+" xobser="+xobser);
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaPago = dateformat.parse(xfecha);
				
				//calcula el monto total del PAGO
				float xtotal=0;
				String xestadosoc="",xestsoc1="";
				for (int i=1;i<=Integer.parseInt(xfilas);i++){
					String misDatos=request.getParameter("datos_"+i);
					if (!misDatos.equals("0")){
							//${reg.codestado}0@ ${reg.mes}1@${reg.anio}2 @${reg.saldo}3 @${reg.nombre}4 @${reg.contador}5
							String[] pa = misDatos.split("@");
							String xcodestado= pa[0];
							String xmes = pa[1];
							String xanio = pa[2];
							String xmonto = pa[3];
							String xestsoc2 = pa[4];
							
							xtotal=xtotal + Float.parseFloat(xmonto);							
					}					
					
				}
				
			String error="",error2="";
			try{
 				//GUARDA DATOS REACTIVACIONES		
				error = this.reactivacionesManager.setAddReactivaciones(fechaPago,xobser,xtotal,Float.parseFloat(xmontotal),Integer.parseInt(xsocioVal),xlogin);
				String[] parts = error.split("@");
				error = parts[0];
				xcodreac = parts[1];  //obtiene el CODPAG despues de guardar el MPAGOS

//System.out.println(" error="+error+" xcodreac="+xcodreac);						


								int ban=0;
								for (int i=1;i<=Integer.parseInt(xfilas);i++){					
									String misDatos=request.getParameter("datos_"+i);
									if (!misDatos.equals("0")){
										//"${reg.codestado}@${reg.mes}@${reg.anio}@${reg.monto}">
										ban=ban+1;
										String[] pa = misDatos.split("@");
										String xcodestado= pa[0];
										String xmes = pa[1];
										String xanio = pa[2];
										String xmonto = pa[3];
										String zcontador = pa[5];  
										//GUARDA DATOS EN DPAGOS
										error2 = this.reactivacionesManager.setAddDetalleReactivar(xcodreac,Integer.parseInt(xcodestado),Integer.parseInt(xmes),Integer.parseInt(xanio),Float.parseFloat(xmonto),Integer.parseInt(zcontador));
//System.out.println(misDatos+" codestado="+xcodestado+" xmes="+xmes+" xanio="+xanio+" xmonto="+xmonto+" zcontador="+zcontador);
									}					
								}																	

		    }catch (Exception e){
	    		System.out.println("ERROR AL GUARDAR DATOS error="+error);
	    		error="1";//quiere decir que hubo ERROR
	    	}
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				object.addProperty("codreac", crypt.encrypt(xcodreac));

				model.addAttribute("datos",object.toString());	
				
				return "util/json";	
			}
			
		/*	
			//ELIMIANR LUGAR
			if (op.equals("delPagoBoletas")){				
				String codl=request.getParameter("d_codl");
				
				String error ="0";
//System.out.println(" error ="+error);				
			}
		*/	
			
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
//		VarGlobales var=new VarGlobales();
//		model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/lugar/lugarmon.vm");	
		return "marco";
	}

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*	
	  @RequestMapping("reportePagarBoletas102102.html")
	  public void reportePagarBoletas102(HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException, DocumentException {
		  Jencryption crypt=new Jencryption();
			HttpSession session=req.getSession(true);
			String xusuario=(String) session.getAttribute("s_usuario");
			String xlogin=(String) session.getAttribute("s_login");
			Utilitarios util=new Utilitarios();
			//verifica si el usuario se autentifico  oam
			if (xusuario == null){
				res.sendRedirect("index.html");			
			}else{				 
				//opcion que llega de los formularios
				String op=req.getParameter("opcion");
				if (op==null) {op="0";}
				else{ op=crypt.decrypt(op);}//descifra 
System.out.println(" opcion ="+op);
				//LISTA DE LUGAR JSON
				if (op.equals("Factura")){
					String xcodpag=req.getParameter("codpag") ;
					xcodpag=crypt.decrypt(xcodpag);
System.out.println(" xcodpag="+xcodpag+" comodoQR="+comodoQr);		 
					//PARAMETROS
					Map<String,Object> params = new HashMap<>();
					params.put("pathImg", comodoQr);//solo el path y codeQR lo recupera de la BD
					params.put("tipo", "Original");
					params.put("xcodpag", xcodpag);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();

					String xsal=rep.generadorReportesTres(res, req, this.dataSource.getConnection(), params, "reports/facturas/factura.jasper", "factura","Copia","Contabilidad");
				}
				
			}	 
	  }
*/
	
}



