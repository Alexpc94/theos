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
import model.manager.PagarFacturasManager;
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
public class PagarFacturas {

	@Autowired
	PagarFacturasManager pagarfacturasManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Value("${comodo.qr}")
	private String comodoQr;
	
	@Autowired
	private DataSource dataSource;
					  
	@RequestMapping({"pagarBoletas110.html"})
	public String pagarBoletas110(Model model,HttpServletRequest request)  throws IOException, ParseException  {
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
			VarGlobales var=new VarGlobales("110",crypt.decrypt(cd));
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
			
			List<?> lisMpagos = this.pagarfacturasManager.listaMaestroPagos(xest1,xest2,fechaini,fechafin);
			model.addAttribute("xMpagos", lisMpagos);
			
			

			model.addAttribute("opReporte", crypt.encrypt("Factura"));
			
			model.addAttribute("op_json", crypt.encrypt("lista_socios"));
			model.addAttribute("opAdd", crypt.encrypt("addPagoBoletas"));
//			model.addAttribute("opAdd", "addPagoBoletas");
			model.addAttribute("opDel", crypt.encrypt("delPagoBoletas"));
		}
		
		model.addAttribute("xfechaini",xfecha);
		model.addAttribute("xfechafin",xfecha);
		model.addAttribute("xfecha",xfecha);
		
		model.addAttribute("file1", comodoTpl+"/pagarFacturas/pagarBoletasmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listarBoletas110.html"})
	public String listarBoletas110(Model model,HttpServletRequest request)  throws IOException  {
		HttpSession session=request.getSession(true);
		
		String xcodper=request.getParameter("xcodper");
		
//System.out.println(" xcodper="+xcodper);
		
		//recupera los roles de la base de datos
		List<?> lisboletas = this.pagarfacturasManager.listaBoletasApagar(Integer.parseInt(xcodper));
		model.addAttribute("xBoletas", lisboletas);
		
		return "pagarFacturas/lista_boletas";
	}

	@RequestMapping({"detalle_Pagos_boletas110.html"})
	public String detalle_Pagos_boletas110(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xcodpag=request.getParameter("xcodpag");
		String xnombre=request.getParameter("xnombre");
		
		//recupera los bebeficiarios de la base de datos

		List<?> lisboletas_pagadas = this.pagarfacturasManager.listaDetalle_Boletas_pagados(xcodpag);
		model.addAttribute("xBoletas_pagadas", lisboletas_pagadas);
		
		model.addAttribute("xcodpag", xcodpag );
		model.addAttribute("xnombre", xnombre );
		return "pagarFacturas/detalleBoletasPagadas";	
	}
	
	@RequestMapping({"adicionarFilas110.html"})
	public String adicionarFilas45(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xaction=request.getParameter("action");
		String xdatos=request.getParameter("xdatos");
		int xrow=Integer.parseInt(request.getParameter("row"));
//System.out.println("action="+xaction+" row="+xrow+" xdatos="+xdatos);
		//RECUPERA LOS CONCEPTOS
		List<?> lisboletas = this.pagarfacturasManager.listaConceptos();
		model.addAttribute("xConceptos", lisboletas);
				
		model.addAttribute("row", xrow + 1);
		model.addAttribute("xdatos", xdatos);
		
		return "pagarFacturas/addRowAdmin";	
	}
	
	@RequestMapping({"pagarBoletasDet110.html"})
	public String pagarBoletasDet110(Model model,HttpServletRequest request)  throws IOException, ParseException  {
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}

		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("110",crypt.decrypt(cd));
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
			
		List<?> lisMpagos = this.pagarfacturasManager.listaMaestroPagos(xest1,xest2,fechaini,fechafin);
		model.addAttribute("xMpagos", lisMpagos);
		
		model.addAttribute("opReporte", crypt.encrypt("Factura"));
		
		return "pagarFacturas/pagarBoletasDet";
	}

	@RequestMapping({"pagarBoletas110Services.html"})
    @Transactional()
	public String boletaspagosServices(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException, NotFoundException, WriterException  {
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
System.out.println("LLEGOOO ::::"+op);
			//LISTA DE LUGAR JSON
			if (op.equals("lista_socios")){
				List<Personal> xlistarsocios = this.pagarfacturasManager.listarSocios();
				JSONArray sgAU =new  JSONArray();
System.out.println("llegoo"+op);
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
			
			//ELIMIANR PAGOS Y FACTURA
			if (op.equals("delPagoBoletas")){				
				String codpag=request.getParameter("d_codpag");
				String nrofactura=request.getParameter("d_factura");
//System.out.println(" xcodpag="+crypt.decrypt(codpag)+" nrofact="+crypt.decrypt(nrofactura));				
				String error = this.pagarfacturasManager.setDelMpagos(crypt.decrypt(codpag),Integer.parseInt(crypt.decrypt(nrofactura)));

//				System.out.println(" error ="+error);
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";				
			}
			
			//ADICIONAR PAGO DE BOLETAS
			if (op.equals("addPagoBoletas")){				
				String xfecha=request.getParameter("a_fecha");
				String xcliente=request.getParameter("a_xcliente");
				String xnit=request.getParameter("a_cedula");
				String xobs=request.getParameter("a_obser");
				String xfilas=request.getParameter("filas");
				float xmonadicional=0;//cuando se tenga que usar, se dejo abierto
				String xcodpag="";
System.out.println("GUARDANDO MPAGOS....OAM...................................................................................");
System.out.println("fecha="+xfecha+" xcliente="+xcliente+" xnit="+xnit+" xfila="+xfilas+" xobser="+xobs);
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaPago = dateformat.parse(xfecha);
				
				//calcula el monto total del PAGO
				double xtotal=0;
				int zanio1=0,zanio2=0;
				int zsw=0;
//				String xestadosoc="",xestsoc1="";
				int xmisFilas=0;
				for (int i=1;i<=Integer.parseInt(xfilas);i++){
					zsw=0;
					String estadoFila=request.getParameter("estadoFila_"+i);
					if (estadoFila.equals("1")){
							String xconcepto=request.getParameter("concepto_"+i);
							String xobser=request.getParameter("obser_"+i);
							String ximporte=request.getParameter("imp_"+i);
							
							xmisFilas=xmisFilas + 1;
							xtotal=xtotal + Float.parseFloat(ximporte);
							//GENERA EL DETALLE DE PAGO/ACUMULA ESTADO SOCIO
System.out.println(" xconcepto="+xconcepto+" xobser="+xobser+" ximporte="+ximporte);							
					}					
				}
System.out.println(" xtotal="+xtotal+" xmisfilas="+xmisFilas);
				
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
System.out.println(" fecha de registro::"+fechareg+" dia="+dia+" mes="+mes+" anio="+anio+" letra="+montoText);
System.out.println(" heber"+fechaPago+xlogin+xobs+xtotal+xcliente.toUpperCase()+xnit+dia+mes+anio+ montoText);
			String error="";
			try{
 				//GUARDA DATOS EN MPAGOS				
				error = this.pagarfacturasManager.setAddMpagos(fechaPago,xlogin,xobs,xtotal,xcliente.toUpperCase(),xnit,dia,mes,anio, montoText,xmonadicional,xmisFilas);
				String[] parts = error.split("@");
				error = parts[0];
				if (error.equals("0")){
							xcodpag = parts[1];  //obtiene el CODPAG despues de guardar el MPAGOS
							String xauto= parts[2];
							String xllave= parts[3];
			//				String xllave="zZ7Z]xssKqkEf_6K9uH(EcV+%x+u[Cca9T%+_$kiLjT8(zr3T9b5Fx2xG-D+_EBS";
							String xnrofac= parts[4];
							String xnitemp= parts[5];
			System.out.println(" xnrofac="+xnrofac+" xauto="+xauto+" xllave="+xllave+"  xnitemp="+xnitemp);
			System.out.println("HABER YA PASO..1.");
							//OBTIENE EL CODIGO DE CONTROL
							
							CodigoControl7 cc=new CodigoControl7();
							cc.setNumeroAutorizacion(xauto);
							cc.setNumeroFactura(Integer.parseInt(xnrofac));
							cc.setNitci(xnit);
							cc.setFechaTransaccion(fechaPago);
							cc.setMonto((int)Math.round(xtotal));
							cc.setLlaveDosificacion(xllave);
							String xcodControl=cc.obtener();
			
System.out.println(" xnrofac="+xnrofac+" xauto="+xauto+" xllave="+xllave+" CODIGO CONTROL::"+xcodControl);
							
								String error2=""; 

								int ban=0;
								for (int i=1;i<=Integer.parseInt(xfilas);i++){					
									String estadoFila=request.getParameter("estadoFila_"+i);
									if (estadoFila.equals("1")){
										String xconcepto=request.getParameter("concepto_"+i);
										String xobser=request.getParameter("obser_"+i);
										String ximporte=request.getParameter("imp_"+i);
System.out.println(" xconcepto="+xconcepto+" xobser="+xobser+" ximporte="+ximporte);
										ban=ban+1;
										//GUARDA DATOS EN DPAGOS
//										public String setAddDetallepagos(String xcodpag,int xcodc,String xdetalle,float ximporte, String xcodControl, int xcontador){
										error2 = this.pagarfacturasManager.setAddDetallepagos(xcodpag,Integer.parseInt(xconcepto),xobser,Float.parseFloat(ximporte),xcodControl,ban);
//System.out.println(misDatos+" error2="+error2+" xmes="+xmes+" xanio="+xanio);
									}					
								}										
								
							//INICIO GENERAR CODIGO QR OAM
								String xdatosFAC=xnitemp+"|"+xnrofac+"|"+xauto+"|"+xfecha+"|"+xtotal+"|"+xtotal+"|"+xcodControl+"|"+xnit+"|0|0|0|0";
								GeneradorQR ge=new GeneradorQR();
								ge.generar(xdatosFAC, comodoQr+xcodControl+".png");
							//FIN GENERAR CODIGO QR				
System.out.println(" error ="+error+" xcodpag="+xcodpag+" xusuario="+xusuario);
System.out.println(" para QR="+xdatosFAC);
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
			
			//ELIMIANR LUGAR
			if (op.equals("delPagoBoletas")){				
				String codl=request.getParameter("d_codl");
				
				String error ="0";
//System.out.println(" error ="+error);				
			}
		}
		
		//Recuperando los menus, roles y procesos, falta recuperar privilegios
//		VarGlobales var=new VarGlobales();
//		model=var.getDatos(request,model);
		
		model.addAttribute("file1", comodoTpl+"/lugar/lugarmon.vm");	
		return "marco";
	}

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx	
	  @RequestMapping("reportePagarBoletas110110.html")
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

					String xsal=rep.generadorReportesTres(res, req, this.dataSource.getConnection(), params, "reports/facturas/facturatodo.jasper", "factura","Copia","Contabilidad");
				}
				
			}	 
	  }

	
}



