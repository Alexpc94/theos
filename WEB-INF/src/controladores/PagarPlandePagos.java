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

//import net.glxn.qrgen.QRCode;
//import net.glxn.qrgen.image.ImageType;

import model.domain.Personal;
import model.manager.PagarplanpagosManager;
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
public class PagarPlandePagos {

	@Autowired
	PagarplanpagosManager pagarplanpagosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Value("${comodo.qr}")
	private String comodoQr;
	
	@Autowired
	private DataSource dataSource;
					  
	@RequestMapping({"pagarplanpagosmon_106.html"})
	public String pagarplanpagosmon_106(Model model,HttpServletRequest request)  throws IOException, ParseException  {
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
			VarGlobales var=new VarGlobales("106",crypt.decrypt(cd));
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
			
			List<?> lisMpagos = this.pagarplanpagosManager.listaMaestroPagos(xest1,xest2,fechaini,fechafin);
			model.addAttribute("xMpagos", lisMpagos);
			
			model.addAttribute("opReporte", crypt.encrypt("Factura"));
			
			model.addAttribute("op_json", crypt.encrypt("lista_socios"));
			model.addAttribute("opAdd", crypt.encrypt("addPagoPlandePagos"));
			model.addAttribute("opDel", crypt.encrypt("delPagoPlandePagos")); // delPagoBoletas
		}
		
		model.addAttribute("xfechaini",xfecha);
		model.addAttribute("xfechafin",xfecha);
		model.addAttribute("xfecha",xfecha);
		
		model.addAttribute("file1", comodoTpl+"/pagarplanpagos/pagarplanpagosmon.vm");	
		return "marco";
	}

	
	@RequestMapping({"listarPagos_106.html"})
	public String listarPagos_106(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		
		String xcodigo=request.getParameter("xcodper");//llega con este formato:  codper@concepto
		
		String[] parts = xcodigo.split("@");
		String xcodper = parts[0]; // extrae codper
		String xconcepto = parts[1]; // extrae concepto
//System.out.println(" codigo="+xcodigo+" xcodper="+xcodper+" xconcepto="+xconcepto);
		
		//recupera los roles de la base de datos
		List<?> lisPlanp = this.pagarplanpagosManager.listaPlanPagosApagar(Integer.parseInt(xcodper),xconcepto);
		model.addAttribute("xPlanpagos", lisPlanp);
		
		return "pagarplanpagos/lista_planp";
	}
	

	@RequestMapping({"detalle_Pagos_planp_106.html"})
	public String detalle_Pagos_boletas102(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodpag=request.getParameter("xcodpag");
		String xnombre=request.getParameter("xnombre");
		String xtipoplan=request.getParameter("xtipoplan");
		
		if (xtipoplan.equals("ACCION")){
			//recupera los bebeficiarios de la base de datos
			List<?> lisboletas_pagadas = this.pagarplanpagosManager.listaDetalle_planp_pagados(xcodpag);
			model.addAttribute("xplan_pagadas", lisboletas_pagadas);
		}
		if (xtipoplan.equals("REACTIVAR")){
			//recupera los bebeficiarios de la base de datos
			List<?> lisboletas_pagadas = this.pagarplanpagosManager.listaDetalle_planp_pagados_reac(xcodpag);
			model.addAttribute("xplan_pagadas", lisboletas_pagadas);
		}
		model.addAttribute("xcodpag", xcodpag );
		model.addAttribute("xnombre", xnombre );
		return "pagarplanpagos/detallePagos";	
	}

	@RequestMapping({"pagarplanpagosDet_106.html"})
	public String pagarplanpagosDet_106(Model model,HttpServletRequest request)  throws IOException, ParseException  {
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}

		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("106",crypt.decrypt(cd));
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
		
		List<?> lisMpagos = this.pagarplanpagosManager.listaMaestroPagos(xest1,xest2,fechaini,fechafin);
		model.addAttribute("xMpagos", lisMpagos);
		
		model.addAttribute("opReporte", crypt.encrypt("Factura"));
		
		return "pagarplanpagos/pagarplanpagosDet";
	}


	@RequestMapping({"plandepagosServices_106.html"})
    @Transactional()
	public String plandepagosServices_106(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException, NotFoundException, WriterException  {
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

			//LISTA SOCIOS QUE TIENEN ACCIONES Y REACTIVACION JSON
			if (op.equals("lista_socios")){
				List<Personal> xlistarsocios = this.pagarplanpagosManager.listarSocios();
				JSONArray sgAU =new  JSONArray();
//System.out.println("llegoo"+op);
				for(int i=0;i<xlistarsocios.size();i++){
					Personal pt = (Personal) xlistarsocios.get(i);
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.getDatosPersona()+"  -  "+pt.concepto);
					sgOU.put("value", pt.codper+"@"+pt.concepto);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}
			
			//ELIMIANR PAGOS Y FACTURA
			if (op.equals("delPagoPlandePagos")){				
				String codpag=request.getParameter("d_codpag");
				String nrofactura="0";//request.getParameter("d_factura");
				
				String error = this.pagarplanpagosManager.setDelMpagos(crypt.decrypt(codpag),0,xlogin);

//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}

			
			//ADICIONAR PAGO DE BOLETAS  addPagoBoletas
			if (op.equals("addPagoPlandePagos")){				
				String xcoda=request.getParameter("xcoda");
				String xcodmp=request.getParameter("xcodmp");
				String xfecha=request.getParameter("a_fecha");
				String xfactura=request.getParameter("a_factura");
				String xdescuento=request.getParameter("a_descuento");
				String xcodigo=request.getParameter("xsocio_val");
				String xcliente=request.getParameter("xcliente");
				String xnit=request.getParameter("xnit");
				String xobs=request.getParameter("a_obser");
				String xfilas=request.getParameter("filas");
				String xcodpag="";
				
				String[] partes = xcodigo.split("@");
				String xsocioVal = partes[0]; // extrae codper
				String xconcepto = partes[1]; // extrae concepto
				
//System.out.println("GUARDANDO MPAGOS PLAN DE PAGOS.......................................................................................");
//System.out.println("xfactura="+xfactura+" descuento"+xdescuento+"fecha="+xfecha+" xsocio="+xsocioVal+" xcliente="+xcliente+" xnit="+xnit+" xfila="+xfilas+" xobser="+xobs+" xcodmp="+xcodmp+" xcoda="+xcoda);
				
				

				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaPago = dateformat.parse(xfecha);
				
				//calcula el monto total del PAGO
				double xtotal=0;
				String xdetalle="";
				int zanio1=0,zanio2=0;
				int zsw=0;
				String xestadosoc="",xestsoc1="";
				for (int i=1;i<=Integer.parseInt(xfilas);i++){
					zsw=0;
					String misDatos=request.getParameter("datos_"+i);
					if (!misDatos.equals("0")){
							//${reg.codestado}0@ ${reg.mes}1@${reg.anio}2 @${reg.saldo}3 @${reg.nombre}4 @${reg.contador}5
							String[] pa = misDatos.split("@");
							String xfechapag= pa[0];
							String xmonto = pa[1];
							
							xtotal=xtotal + Float.parseFloat(xmonto);
							//GENERA EL DETALLE DE PAGO
							if (xdetalle.equals("")){
								xdetalle=xdetalle+xfechapag;
							}else{
								xdetalle=xdetalle+" - "+xfechapag;
							}
					}					
				}
//				xdetalle=xdetalle+"/"+String.valueOf(zanio1);
				String xmon=util.decimalFormat("#####0.00", xtotal);
//System.out.println(" XDETALLE="+xdetalle);				
				//TRANSFORMAR MONTO FLOAT A TEXTO
				Numero_a_Letra letra=new Numero_a_Letra("BOLIVIANOS");
				String montoText=letra.Convertir(xmon,true);
				
				//NECESITO PASAR TAMBIENCOMO PARAMETRO EL MES,DIA,ANIO
 				String fechareg=util.getFecha(); 					
 				int mes=Integer.parseInt(xfecha.substring(3,5));
 				int dia=Integer.parseInt(xfecha.substring(0,2));
 				int anio=Integer.parseInt(xfecha.substring(6,10));
 				
//System.out.println(" fecha de registro::"+fechareg+" dia="+dia+" mes="+mes+" anio="+anio+" letra="+montoText+" coda="+xcoda+" xcodmp="+xcodmp);
//System.out.println(" heber="+fechaPago+" xlogin="+xlogin+" obs="+xobs+" xtotal="+xtotal+"  - "+dia+mes+anio+" - "+ montoText+" - "+xestsoc1+" - "+xdetalle);
			String error="";
			try{
 				//GUARDA DATOS EN MPAGOS				
				error = this.pagarplanpagosManager.setAddMpagos(fechaPago,xlogin,xobs,xtotal,"xcliente","xnit",dia,mes,anio, montoText,"ACCION.",xdetalle,xcoda,xcodmp,Integer.parseInt(xfactura),Float.parseFloat(xdescuento));

				String[] parts = error.split("@");
				error = parts[0];
				
//System.out.println(" error="+error);

				if (error.equals("0")){
							xcodpag = parts[1];  //obtiene el CODPAG despues de guardar el MPAGOS
/*							
							String xauto= parts[2];
							String xllave= parts[3];
							String xnrofac= parts[4];
							String xnitemp= parts[5];
*/							
//			System.out.println(" xnrofac="+xnrofac+" xauto="+xauto+" xllave="+xllave+"  xnitemp="+xnitemp);
//			System.out.println("HABER YA PASO..1.");
							//OBTIENE EL CODIGO DE CONTROL
/*							
							CodigoControl7 cc=new CodigoControl7();
							cc.setNumeroAutorizacion(xauto);
							cc.setNumeroFactura(Integer.parseInt(xnrofac));
							cc.setNitci(xnit);
							cc.setFechaTransaccion(fechaPago);
							cc.setMonto((int)Math.round(xtotal));
							cc.setLlaveDosificacion(xllave);
							String xcodControl=cc.obtener();
*/			
//System.out.println(" xnrofac="+xnrofac+" xauto="+xauto+" xllave="+xllave+" codcontrol="+xcodControl+" xfila="+xfilas);
							
								String error2=""; 

								int ban=1;
								for (int i=1;i<=Integer.parseInt(xfilas);i++){					
										String misDatos=request.getParameter("datos_"+i);
										if (!misDatos.equals("0")){
											String[] pa = misDatos.split("@");
											String xfechapag= pa[0];
											String xmonto = pa[1];
											String xcoda2 = pa[2];
											String xcodmp2 = pa[3];
											String xcoddp = pa[4];
//	System.out.println(i+".-  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxxxx  ");
//	System.out.println(i+".- see this:: xfechapag="+xfechapag+" xmonto="+xmonto+"xcoda2="+xcoda2+" xcodmp2="+xcodmp2+" xcoddp="+xcoddp+" xcodpag="+xcodpag);										 
//	System.out.println("fecha::"+xfechapag+" xmonto::"+xmonto+" xcoda::"+xcoda+" xcodmp::"+xcodmp+" xcoddp::"+xcoddp);								
											//GUARDA DATOS EN DPAGOS																
											error2 = this.pagarplanpagosManager.setAddDetallepagos(xcodpag,Float.parseFloat(xmonto),xcoda2,Integer.parseInt(xcoddp),xcodmp2,ban);//,xcodControl);
											ban++;
//	System.out.println(misDatos+" error2="+error2);
										}					
								}										
							
							//INICIO GENERAR CODIGO QR OAM
//								String xdatosFAC=xnitemp+"|"+xnrofac+"|"+xauto+"|"+xfecha+"|"+xtotal+"|"+xtotal+"|"+xcodControl+"|"+xnit+"|0|0|0|0";
//								GeneradorQR ge=new GeneradorQR();
//								ge.generar(xdatosFAC, comodoQr+xcodControl+".png");
							//FIN GENERAR CODIGO QR				
//System.out.println(" error ="+error+" xcodpag="+xcodpag+" xusuario="+xusuario);
//System.out.println(" para QR="+xdatosFAC);
				}//si error es igual a CERO
		    }catch (Exception e){
	    		System.out.println("ERROR AL GUARDAR DATOS error="+error);
	    		error="1";//quiere decir que hubo ERROR
	    	}
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				object.addProperty("codpag", crypt.encrypt(xcodpag));

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
	  @RequestMapping("reportePagarPlanPagos106.html")
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
//System.out.println(" opcion ="+op);
				//LISTA DE LUGAR JSON
				if (op.equals("Factura")){
					String xcodpag=req.getParameter("codpag") ;
					xcodpag=crypt.decrypt(xcodpag);
//System.out.println(" xcodpag="+xcodpag+" comodoQR="+comodoQr);		 
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

	
}


