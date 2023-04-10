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

import model.manager.PlandePagosManager;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
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
public class PlandePagos {

	@Autowired	
	PlandePagosManager plandepagosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
///	@Value("${comodo.qr}")
///	private String comodoQr;
	
	@Autowired
	private DataSource dataSource;
	
	
	@RequestMapping({"plandepagosmon_105.html"})
	public String plandepagosmon_105(Model model,HttpServletRequest request)  throws IOException, ParseException  {
		HttpSession session=request.getSession(true);
		Utilitarios u=new Utilitarios();
		//calcula la fecha actual del sistema
		String xfecha=u.getFecha();
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
			
			//Recuperando los menus, roles y procesos, falta recuperar privilegios
			VarGlobales var=new VarGlobales("105",crypt.decrypt(cd));
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
			
			List<?> lisPlanPagos = this.plandepagosManager.listarPlanPagosMonitor(xest1,xest2,fechaini,fechafin);
			model.addAttribute("xlisPlanPagos", lisPlanPagos);
						
//			model.addAttribute("op_json_proveedor", crypt.encrypt("lista_Proveedores"));
			model.addAttribute("opAdd-Proforma", crypt.encrypt("addPlanPagos"));
			model.addAttribute("opDel-Proforma", crypt.encrypt("delPlanPagos"));
						
		}
//System.out.println("ESTAMOS EN ventasAdminMon.vm");		
		model.addAttribute("xfechaini",xfecha);
		model.addAttribute("xfechafin",xfecha);
		model.addAttribute("xfecha",xfecha);
///		model.addAttribute("sel","SELECTED");
		
		model.addAttribute("file1", comodoTpl+"/plandepagos/plandepagosmon.vm");	
		return "marco";
	}
	
	@RequestMapping({"listaSociosConDeudas_105.html"})
	public String listaSociosConDeudas_105(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
//System.out.println("action="+xaction+" row="+xrow+" xdatos="+xdatos);
		
		List<?> xlistaSocio = this.plandepagosManager.listarSociosConDeudas();
		model.addAttribute("xlistaSocio", xlistaSocio);
	
		return "plandepagos/listaSocios";	
	}	
	
	@RequestMapping({"adicionarFilas_105.html"})
	public String adicionarFilas_105(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		Utilitarios u=new Utilitarios();
		String xaction=request.getParameter("action");
		String xdatos=request.getParameter("xdatos");
		String xfecha=request.getParameter("xfecha");
		String xcuota=request.getParameter("xcuota");
		int xrow=Integer.parseInt(request.getParameter("row"));
//System.out.println("fecha="+xfecha); //
		
		model.addAttribute("row", xrow);
		model.addAttribute("xdatos", xdatos);
		// return u.decimalFormat("###,##0.00", this.cuota);
	
		String[] parts = xfecha.split("/");
		String part1 = parts[0]; // 123
		String part2 = parts[1]; // 654321
		String part3 = parts[2]; // 654321
		
		model.addAttribute("xcuota", u.decimalFormat("#####0.00", Float.parseFloat(xcuota)));//
		model.addAttribute("fecha_generada", part2+"/"+part1+"/"+part3 );// "01/0"+String.valueOf(xrow)+"/2019");
		
		return "plandepagos/addRowAdmin";	
	}
	
/*	
	@RequestMapping({"m_adicionarFilas_45.html"})
	public String m_adicionarFilas_45(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		String xaction=request.getParameter("action");
		String xdatos=request.getParameter("xdatos");
		int xrow=Integer.parseInt(request.getParameter("row"));
//System.out.println("action="+xaction+" row="+xrow+" xdatos="+xdatos);
		
		model.addAttribute("row", xrow + 1);
		model.addAttribute("xdatos", xdatos);
		
		return "comprasadmin/m_addRowAdmin";	
	}
*/
	
	@RequestMapping({"detalle_lista_un_planpagos_105.html"})
	public String detalle_lista_una_venta27(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException  {
		Utilitarios u=new Utilitarios();
		String xcodmp=request.getParameter("xcodmp");

		List<?> xlisDetalle = this.plandepagosManager.detalleDePlandePagos(xcodmp);
		model.addAttribute("xlisDetalle", xlisDetalle);
		//saca las comisiones que tiene la venta;
		
		return "plandepagos/detalleplanpago";	
	}

	
	@RequestMapping({"plandepagosDet_105.html"})
	public String plandepagosDet_105(Model model,HttpServletRequest request)  throws IOException, ParseException  {
	//jQuery.post('ventasAdminDet27.html',{xestado:xestado,xfini:xfini,xffin:xffin,xroles:xroles},
		HttpSession session=request.getSession(true);
		Jencryption crypt=new Jencryption();
		
		String cd=request.getParameter("cd");
		if (cd==null){ 				
			cd=(String) session.getAttribute("s_menuActivo");
		}else{
			session.setAttribute("s_menuActivo", cd );
		}

		//Recuperando los menus, roles y procesos, falta recuperar privilegios
		VarGlobales var=new VarGlobales("105",crypt.decrypt(cd));
		model=var.getDatos(request,model);
			
		//PARAMAMETROS 
		String xestado=request.getParameter("xestado");
		String xfini=request.getParameter("xfini");
		String xffin=request.getParameter("xffin");
	
		if (xestado==null){ xestado="1";}
		int xest1=0, xest2=1;
		if (xestado.equals("1")){xest1=1;xest2=1;}
		if (xestado.equals("0")){xest1=0;xest2=0;}
		model.addAttribute("estado", Integer.parseInt(xestado));

//System.out.println(" xfini="+xfini+" xffin="+xffin+" estado="+xestado);
		
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaini=dateformat.parse(xfini);//Valores de entrada para el filtro de la consulta
		Date fechafin= dateformat.parse(xffin);//Valores de entrada para el filtro de la consulta
		
		//tipo de documento
		String xtipo=request.getParameter("xtipo");
		
///		List<?> lisMventas = this.comprasAdminManager.listarMonitorCompra(xest1,xest2,fechaini,fechafin);
///		model.addAttribute("xMcompras", lisMventas);
		List<?> lisPlanPagos = this.plandepagosManager.listarPlanPagosMonitor(xest1,xest2,fechaini,fechafin);
		model.addAttribute("xlisPlanPagos", lisPlanPagos);
			
//		model.addAttribute("opReporte", crypt.encrypt("Factura"));
		
		return "plandepagos/plandepagosDet";
	}

/*	
	@RequestMapping({"m_comprasRecuperaDetalle_45.html"})
	public String m_comprasRecuperaDetalle_45(Model model,HttpServletRequest request)  throws IOException, ParseException  {	
		String xcodcom=request.getParameter("xcodcom");
//System.out.println("RECUPERA EL DETALLE PARA LA MODIFICACION..");
//System.out.println("xcodven="+xcodven);		
		List<?> lisDetalleMventas = this.comprasAdminManager.recuperaDetalle_compra(xcodcom);
		model.addAttribute("xDetalleCompra", lisDetalleMventas);
		return "comprasadmin/m_addRowAdmin_recupera";
	}
*/
	
	@RequestMapping({"comprasAdminServices_105.html"})
    @Transactional()
	public String comprasAdminServices_45(Model model,HttpServletRequest request,HttpServletResponse response)  throws IOException, ParseException, NotFoundException, WriterException  {
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
/*		
			//LISTA DE CLIENTES JSON
			if (op.equals("lista_Proveedores")){
				List<Proveedores> xlistarsocios = this.comprasAdminManager.listarProveedores();
				JSONArray sgAU =new  JSONArray();
//System.out.println("llegoo"+op);
				for(int i=0;i<xlistarsocios.size();i++){
					Proveedores pt = (Proveedores) xlistarsocios.get(i);
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.getDatosPersona());
					sgOU.put("value", pt.codpv);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}						
 */
/*			
			//LISTA DE PRODUCTOS
			if (op.equals("lista_productos")){
				//List<Productos> xlistarProductos = this.ventasAdminManager.listarProductos();
				
				List<Productos> xlistarProductos = this.ventasAdminManager.listarProductos_detalle_Vta();
				
				JSONArray sgAU =new  JSONArray();
//System.out.println("llegoo"+op);
				for(int i=0;i<xlistarProductos.size();i++){
					Productos pt = (Productos) xlistarProductos.get(i);
					JSONObject sgOU =new JSONObject();
					sgOU.put("label", pt.DatosProducto_Marcas());
					sgOU.put("value", pt.codpro+"@"+pt.tipo);					
					sgAU.add(sgOU);					
				}
				model.addAttribute("datos",sgAU.toString());  //listJSONP);				
				return "util/json";
			}
*/

			//ADICIONAR PAGO DE BOLETAS
			if (op.equals("addPlanPagos")){	
//System.out.println("GUARDANDO PLAN DE PAGOS............................................................................");				
				String xcodper=request.getParameter("a_codperVal");
				String xcoda=request.getParameter("a_codaVal");
				String xfecha=request.getParameter("a_fecha");
				String xtipoval=request.getParameter("a_tipoVal");
				String xobser=request.getParameter("a_obser");
				String xmontotal=request.getParameter("xmontotal");
				
				String pfecha=request.getParameter("p_fecha");
				String cmeses=request.getParameter("c_meses");
				String ccuota=request.getParameter("c_cuota");
				
				String xfilas=request.getParameter("a_fila");
				
//System.out.println("xcodper:"+xcodper+" xcoda="+xcoda+" xfecha="+xfecha+" tipoVal="+xtipoval+" obser="+xobser+" xmontotal="+xmontotal+" pfecha="+pfecha+" cmeses="+cmeses+" ccuota="+ccuota+" fila="+xfilas);												
				
				String xdatos[][]= new String[5][250];
				int xcantDatos=0;
				float xtotal=0,xdesc=0;
				
				for (int i=1;i<=Integer.parseInt(xfilas);i++){
					xcantDatos++;
		  			xdatos[1][xcantDatos]=request.getParameter("fecha_"+i);//fecha
					xdatos[2][xcantDatos]=request.getParameter("mon_"+i);;//monto
					xtotal=xtotal + Float.valueOf(request.getParameter("mon_"+i)) ;
				}
				
//System.out.println("PLAN DE PAGOS..:");				
				for (int i=1;i<=xcantDatos;i++){
//					System.out.println("fecha="+xdatos[1][i]+" xmonto="+xdatos[2][i]);
				}
				
				DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaPlan = dateformat.parse(xfecha);
				
				String error="0",xcodigo="", xres="";
				try{
	 				//GUARDAR PLAN DE PAGOS
					xres = this.plandepagosManager.setAddPlanPagos(xtipoval,fechaPlan,Float.parseFloat(xmontotal),xlogin,Integer.parseInt(xcodper),xcoda,xobser);
					xcodigo= xres;
//System.out.println("xcodigo=="+xcodigo);

					//GUARDAR DETALLE PLAN DE PAGOS

					for (int i=1;i<=xcantDatos;i++){
//System.out.println("fecha="+xdatos[1][i]+" xmonto="+xdatos[2][i]);
						Date xfechaPago = dateformat.parse(xdatos[1][i]);
						error= this.plandepagosManager.setAddDetallePlanPagos(xcodigo,xfechaPago,Float.parseFloat(xdatos[2][i]));
					}

			    }catch (Exception e){		    		
		    		error="1";//quiere decir que hubo ERROR
		    		System.out.println("ERROR AL GUARDAR DATOS error="+error);
		    	}
				
				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);
				model.addAttribute("datos",object.toString());					
				return "util/json";	
			}


			//ELIMINAR PROFORMA
			if (op.equals("delPlanPagos")){	
//System.out.println("ELIMINANDO PLAN DE PAGOS.......................................................................................");				
				String xcodmp=request.getParameter("hd_codmp");	
//System.out.println("codmp=="+xcodmp+" xlogin=="+xlogin);									
				String error="1", xres="";
				try{
	 				//ELIMINAR PLAN DE PAGOS 
					error = this.plandepagosManager.setDelPlanPagos(xcodmp,xlogin);
//System.out.println(" error="+error);					
			    }catch (Exception e){
		    		System.out.println("ERROR AL GUARDAR DATOS error="+error);
		    		error="1";//quiere decir que hubo ERROR
		    	}

				//enviando a un  JSON  -- es decir se devuelve al AJAX
				JsonObject object = new JsonObject();
				object.addProperty("error", error);

				model.addAttribute("datos",object.toString());	
				
				return "util/json";	
			}
			

		} //fin

		model.addAttribute("file1", comodoTpl+"/ventasadmin/lugarmon.vm");	
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
*/

	
	
	
}  //fin clase

