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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;

import model.domain.Personal;
import model.manager.AccesoManager;
import model.manager.ReportesSociosManager;
import model.manager.RolesManager;
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
public class ReportesSocios {

	@Autowired
	ReportesSociosManager reportesSociosManager;
	
	@Value("${comodo.tpl}")
	private String comodoTpl;
	
	@Autowired
	private DataSource dataSource;
	
	@RequestMapping({"reportessociosmon103.html"})
	public String reportessociosmon(Model model,HttpServletRequest request)  throws IOException  {
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
			
			VarGlobales var=new VarGlobales("103",crypt.decrypt(cd)); //params: (proceso, menu)
			model=var.getDatos(request,model);
						
			model.addAttribute("opPagos", crypt.encrypt("Pagos_Socio"));
			model.addAttribute("opSaldos", crypt.encrypt("Saldos_Socio_2_anio"));
			model.addAttribute("opPagosMes", crypt.encrypt("Pagos_Mes"));
			model.addAttribute("opPagosMesCB", crypt.encrypt("Pagos_MesCb"));
			model.addAttribute("opSaldoDeudor", crypt.encrypt("Saldo_Deudor"));
			model.addAttribute("opSociosEstados", crypt.encrypt("Socios_Estados"));
			model.addAttribute("opSociosMenores", crypt.encrypt("Socios_Menores"));
			model.addAttribute("opBeneficiarios", crypt.encrypt("Socios_Beneficiarios"));
			model.addAttribute("opKardexPagosSocios", crypt.encrypt("Kardex_Pagos_Socios"));
			
			model.addAttribute("opListaSocios", crypt.encrypt("lista_Socios"));
//			<input type="hidden" name="opcion" id="opcion_json" value="${op_json}" >
			model.addAttribute("opResSaldoDeudor", crypt.encrypt("Resumen_Saldo_Deudor"));
			
			model.addAttribute("xfechaini",xfecha);
			model.addAttribute("xfechafin",xfecha);
			model.addAttribute("xfechaini_K",xfecha);
			model.addAttribute("xfechafin_K",xfecha);
		}
		
		model.addAttribute("file1", comodoTpl+"/reportesSocios/reportessociosmon.vm");	
		return "marco";
	}
	
	  @RequestMapping("serviciosQuerys_103.html")
	  public String serviciosQuerys_103(Model model,HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException, DocumentException, ParseException {
		  Jencryption crypt=new Jencryption();
			HttpSession session=req.getSession(true);
			String xusuario=(String) session.getAttribute("s_usuario");
			String xlogin=(String) session.getAttribute("s_login");
			Utilitarios util=new Utilitarios();
			//verifica si el usuario se autentifico  
			if (xusuario == null){
				res.sendRedirect("index.html");			
			}else{				 
				//opcion que llega de los formularios
				String op=req.getParameter("opcion");
				if (op==null) {op="0";}
				else{ op=crypt.decrypt(op);}//descifra 
//System.out.println(" opcion::"+op);	
				
				//LISTA DE SOCIOS QUE TIENEN BOLETAS
				if (op.equals("lista_Socios")){
					List<Personal> xlistarsocios = this.reportesSociosManager.listarSocios();
					JSONArray sgAU =new  JSONArray();
//System.out.println("llegoo"+op);
					for(int i=0;i<xlistarsocios.size();i++){
						Personal pt = (Personal) xlistarsocios.get(i);
						JSONObject sgOU =new JSONObject();
						sgOU.put("label", pt.getDatosPersona());
						sgOU.put("value", pt.codper);					
						sgAU.add(sgOU);					
					}
//System.out.println(sgAU.toString());					
					model.addAttribute("datos",sgAU.toString());  //listJSONP);				
					return "util/json";
				}						
				
///////////////////////////////////////////////////////////////////////////////////////				
			}	
		  return "marco";	
	  }
	
	  @RequestMapping("reportesServicios103.html")
	  public void reportesServicios103(Model model,HttpServletResponse res, HttpServletRequest req) throws JRException, IOException, SQLException, DocumentException, ParseException {
		  Jencryption crypt=new Jencryption();
			HttpSession session=req.getSession(true);
			String xusuario=(String) session.getAttribute("s_usuario");
			String xlogin=(String) session.getAttribute("s_login");
			Utilitarios util=new Utilitarios();
			//verifica si el usuario se autentifico  
			if (xusuario == null){
				res.sendRedirect("index.html");			
			}else{				 
				//opcion que llega de los formularios
				String op=req.getParameter("opcion");
				if (op==null) {op="0";}
				else{ op=crypt.decrypt(op);}//descifra 
System.out.println(" opcion::"+op);	
					
				//LISTA DE LUGAR JSON
				if (op.equals("Pagos_Socio")){
//					String xcodpag=req.getParameter("codpag") ;
					String xfini=req.getParameter("fechaini");
					String xffin=req.getParameter("fechafin");
//System.out.println(" haber sillego");		 
					DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
					Date fechaini=dateformat.parse(xfini);//Valores de entrada para el filtro de la consulta
					Date fechafin= dateformat.parse(xffin);//Valores de entrada para el filtro de la consulta
					//PARAMETROS
					Map<String,Object> params = new HashMap<>();
					params.put("fechaini", fechaini);
					params.put("fechafin", fechafin);
					params.put("responsable", xusuario);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagos/parteDiarioPagos.jasper", "pagos");
				}
				
				//LISTA DE LUGAR JSON
				if (op.equals("Saldos_Socio_2_anio")){
					String xanio1=req.getParameter("anioini");
					String xtiposaldos=req.getParameter("saldos");
					//0 sin saldos  1=con saldos    2=todos
//System.out.println(" haber sillego="+xanio1+" tipo saldos="+xtiposaldos);		 
//					DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
//					Date fechaini=dateformat.parse(xfini);//Valores de entrada para el filtro de la consulta
//					Date fechafin= dateformat.parse(xffin);//Valores de entrada para el filtro de la consulta
					//PARAMETROS
					float sal1=0,sal2=0;
					if (xtiposaldos.equals("0")){
						sal1=0;sal2=0;
					}
					if (xtiposaldos.equals("1")){
						sal1=1;sal2=99999;
					}
					if (xtiposaldos.equals("2")){
						sal1=0;sal2=99999;
					}

					String xres = this.reportesSociosManager.setSaldos2Anios(Integer.parseInt(xanio1),Integer.parseInt(xanio1)+1,xlogin);
					
					Map<String,Object> params = new HashMap<>();
					params.put("gestion1", xanio1);
					params.put("gestion2", String.valueOf(Integer.parseInt(xanio1)+1));
					params.put("sal1", sal1);
					params.put("sal2", sal2);					
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/saldo2anios.jasper", "saldos");
				}

				//LISTA DE LUGAR JSON  
				if (op.equals("Pagos_Mes")){
					String xanio1=req.getParameter("aniofin");
					String xmes=req.getParameter("mes");
					String xtipodoc=req.getParameter("tipodoc");
					
					int xpro1=Integer.parseInt(req.getParameter("pro_1"));
					int xpro2=Integer.parseInt(req.getParameter("pro_2"));
					int xpro3=Integer.parseInt(req.getParameter("pro_3"));
					int xpro4=Integer.parseInt(req.getParameter("pro_4"));

					int xproa1=Integer.parseInt(req.getParameter("proa_1"));
					int xproa2=Integer.parseInt(req.getParameter("proa_2"));
					int xproa3=Integer.parseInt(req.getParameter("proa_3"));
					int xproa4=Integer.parseInt(req.getParameter("proa_4"));

					String[] xest={"-","ACTIVOS","MENOR","EMERITO","JOVEN","ACTIVO AUSENTE","MENOR AUSENTE","EMERITO AUSENTE","JOVEN AUSENTE"};
							//		0     1        2         3        4          5             6                   7                8
					String xestados="";
					
					if (xpro1==100) { xestados="ACTIVOS"; }
					
//System.out.println("PAGOS_MES pro_1::"+xpro1+" pro_2::"+xpro2+" pro_3::"+xpro3+" pro_4::"+xpro4);
//System.out.println(" proA_1::"+xproa1+" proA_2::"+xproa2+" proA_3::"+xproa3+" proA_4::"+xproa4);

					//0 sin saldos  1=con saldos    2=todos
					//String xestado=xpro1+","+xpro2+","+xpro3+","+xpro4;

//ME QUEDE AQUI TENEMOS QUE REVISAR OJO
String xres = this.reportesSociosManager.setPagosMes(Integer.parseInt(xanio1),Integer.parseInt(xmes),xlogin,xpro1,xpro2,xpro3,xpro4,xproa1+1,xproa2+1,xproa3+1,xproa4+1);
					
					String[] xmeses={"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
					
					if (xpro1 > 0) { xpro1=xpro1 / 100; }
					if (xpro2 > 0) { xpro2=xpro2 / 100; }
					if (xpro3 > 0) { xpro3=xpro3 / 100; }
					if (xpro4 > 0) { xpro4=xpro4 / 100; }
					
					if (xproa1 > 0) { xproa1=(xproa1 / 100)+4; }
					if (xproa2 > 0) { xproa2=(xproa2 / 100)+4; }
					if (xproa3 > 0) { xproa3=(xproa3 / 100)+4; }
					if (xproa4 > 0) { xproa4=(xproa4 / 100)+4; }
//System.out.println("XXXXX pro_1::"+xpro1+" pro_2::"+xpro2+" pro_3::"+xpro3+" pro_4::"+xpro4);
//System.out.println("XXXXX proA_1::"+xproa1+" proA_2::"+xproa2+" proA_3::"+xproa3+" proA_4::"+xproa4);
//System.out.println(xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]+","+xest[xproa1]+","+xest[xproa2]+","+xest[xproa3]+","+xest[xproa4]);					
					
					Map<String,Object> params = new HashMap<>();
					params.put("anio", xanio1);
					params.put("mes", xmeses[Integer.parseInt(xmes)-1]);
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
					params.put("estados", xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]+","+xest[xproa1]+","+xest[xproa2]+","+xest[xproa3]+","+xest[xproa4]);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMes.jasper", "reporte");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesXLS.jasper", "reporte");						
					}
				}
				
				//LISTA DE LUGAR JSON  
				if (op.equals("Pagos_MesCb")){
					System.out.println("entro");
					String xanio1=req.getParameter("aniofin");
					String xmes=req.getParameter("mes");
					String xtipodoc=req.getParameter("tipodoc");
					
					int xpro1=Integer.parseInt(req.getParameter("pro_1"));
					int xpro2=Integer.parseInt(req.getParameter("pro_2"));
					int xpro3=Integer.parseInt(req.getParameter("pro_3"));
					int xpro4=Integer.parseInt(req.getParameter("pro_4"));

					int xproa1=Integer.parseInt(req.getParameter("proa_1"));
					int xproa2=Integer.parseInt(req.getParameter("proa_2"));
					int xproa3=Integer.parseInt(req.getParameter("proa_3"));
					int xproa4=Integer.parseInt(req.getParameter("proa_4"));

					String[] xest={"-","ACTIVOS","MENOR","EMERITO","JOVEN","ACTIVO AUSENTE","MENOR AUSENTE","EMERITO AUSENTE","JOVEN AUSENTE"};
							//		0     1        2         3        4          5             6                   7                8
					String xestados="";
					
					if (xpro1==100) { xestados="ACTIVOS"; }
					
System.out.println("PAGOS_MES pro_1::"+xpro1+" pro_2::"+xpro2+" pro_3::"+xpro3+" pro_4::"+xpro4);
System.out.println(" proA_1::"+xproa1+" proA_2::"+xproa2+" proA_3::"+xproa3+" proA_4::"+xproa4);

					//0 sin saldos  1=con saldos    2=todos
					//String xestado=xpro1+","+xpro2+","+xpro3+","+xpro4;

//ME QUEDE AQUI TENEMOS QUE REVISAR OJO
String xres = this.reportesSociosManager.setPagosMes(Integer.parseInt(xanio1),Integer.parseInt(xmes),xlogin,xpro1,xpro2,xpro3,xpro4,xproa1+1,xproa2+1,xproa3+1,xproa4+1);
					
					String[] xmeses={"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
					
					if (xpro1 > 0) { xpro1=xpro1 / 100; }
					if (xpro2 > 0) { xpro2=xpro2 / 100; }
					if (xpro3 > 0) { xpro3=xpro3 / 100; }
					if (xpro4 > 0) { xpro4=xpro4 / 100; }
					
					if (xproa1 > 0) { xproa1=(xproa1 / 100)+4; }
					if (xproa2 > 0) { xproa2=(xproa2 / 100)+4; }
					if (xproa3 > 0) { xproa3=(xproa3 / 100)+4; }
					if (xproa4 > 0) { xproa4=(xproa4 / 100)+4; }
//System.out.println("XXXXX pro_1::"+xpro1+" pro_2::"+xpro2+" pro_3::"+xpro3+" pro_4::"+xpro4);
//System.out.println("XXXXX proA_1::"+xproa1+" proA_2::"+xproa2+" proA_3::"+xproa3+" proA_4::"+xproa4);
//System.out.println(xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]+","+xest[xproa1]+","+xest[xproa2]+","+xest[xproa3]+","+xest[xproa4]);					
					
					Map<String,Object> params = new HashMap<>();
					params.put("anio", xanio1);
					params.put("mes", xmeses[Integer.parseInt(xmes)-1]);
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
					params.put("estados", xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]+","+xest[xproa1]+","+xest[xproa2]+","+xest[xproa3]+","+xest[xproa4]);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesCB.jasper", "reporte");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesCBXLS.jasper", "reporte");						
					}
				}
				
				//LISTA DE LUGAR JSON     
				if (op.equals("Saldo_Deudor")){
					String xanioini=req.getParameter("anioini");
					String xmesini=req.getParameter("mesini");
					String xanio1=req.getParameter("aniofin");
					String xmes=req.getParameter("mes");
					String xtipodoc=req.getParameter("tipodoc");
					String xfinicio=req.getParameter("xfinicio");//1=SIN  0=CON
					
//System.out.println("SALDO DEUDOR ----> XANIO::"+xanio1+" xmes::"+xmes+" xtipodoc::"+xtipodoc+" anioini="+xanioini+" xmesini="+xmesini+" xfinicio="+xfinicio);	
//'&anioini='+anioini+'&mesini='+mesini

					
					String[] xmeses={"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
					
					String xparam="";
					if (xfinicio.equals("1")){
						xmesini="1";xanioini="1900";
						xparam=" SALDOS TOTALES HASTA::"+xmeses[Integer.parseInt(xmes)-1]+"/"+String.valueOf(xanio1);
					}else{
xparam=" SALDOS  DESDE::"+xmeses[Integer.parseInt(xmesini)-1]+"/"+String.valueOf(xanioini)+"  HASTA::"+xmeses[Integer.parseInt(xmes)-1]+"/"+String.valueOf(xanio1);
					}

					String xres = this.reportesSociosManager.setSaldoDeudor(Integer.parseInt(xanioini),Integer.parseInt(xmesini),Integer.parseInt(xanio1),Integer.parseInt(xmes),xlogin);
					
					Map<String,Object> params = new HashMap<>();
					params.put("anio", xanio1);
					params.put("mes", xparam);// xmeses[Integer.parseInt(xmes)-1]);
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
//					params.put("estados", xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]);

					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/deudasHastaMes.jasper", "deudasHastaMes");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/deudasHastaMesXLS.jasper", "deudasHastaMes");						
					}
				}
				
				//RESUMEN SALDO DEUDOR     
				if (op.equals("Resumen_Saldo_Deudor")){
					String xanioini=req.getParameter("anioini");
					String xmesini=req.getParameter("mesini");
					String xaniofin=req.getParameter("aniofin");
					String xmesfin=req.getParameter("mes");
					String xtipodoc=req.getParameter("tipodoc");
					
System.out.println("RESUMEN SALDO DEUDOR ----> XANIO::"+xaniofin+" xmes::"+xmesfin+" xtipodoc::"+xtipodoc+" anioini="+xanioini+" xmesini="+xmesini);	
//'&anioini='+anioini+'&mesini='+mesini

					
					String[] xmeses={"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
					
String xparam=" SALDOS  DESDE::"+xmeses[Integer.parseInt(xmesini)-1]+"/"+String.valueOf(xanioini)+"  HASTA::"+xmeses[Integer.parseInt(xmesfin)-1]+"/"+String.valueOf(xaniofin);

//String xres = this.reportesSociosManager.setSaldoDeudor(Integer.parseInt(xanioini),Integer.parseInt(xmesini),Integer.parseInt(xanio1),Integer.parseInt(xmes),xlogin);
					
					Map<String,Object> params = new HashMap<>();
					params.put("mesini", (Integer.parseInt(xanioini) * 12)+Integer.parseInt(xmesini));
					params.put("mesfin", (Integer.parseInt(xaniofin) * 12)+Integer.parseInt(xmesfin));
					params.put("mes", xparam);// xmeses[Integer.parseInt(xmes)-1]);
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
//					params.put("estados", xest[xpro1]+","+xest[xpro2]+","+xest[xpro3]+","+xest[xpro4]);

					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/resumenMontoDeudas.jasper", "resumenDeudas");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/resumenMontoDeudasXLS.jasper", "resumenDeudas");				
					}
				}
				
				//LISTA DE SOCIOS Y ESTADOS   
				if (op.equals("Socios_Estados")){
					String xtipodoc=req.getParameter("tipodoc");
					
					int xpro1=Integer.parseInt(req.getParameter("pro_1"));
					int xpro2=Integer.parseInt(req.getParameter("pro_2"));
					int xpro3=Integer.parseInt(req.getParameter("pro_3"));
					int xpro4=Integer.parseInt(req.getParameter("pro_4"));
					
					int xproA1=Integer.parseInt(req.getParameter("proa_1"));
					int xproA2=Integer.parseInt(req.getParameter("proa_2"));
					int xproA3=Integer.parseInt(req.getParameter("proa_3"));
					int xproA4=Integer.parseInt(req.getParameter("proa_4"));
										
					String[] xest={"-","ACTIVOS","MENOR","EMERITO","JOVEN","ACTIVOS AUSENTE","MENOR AUSENTE","EMERITO AUSENTE","JOVEN AUSENTE"};					
					String xestados="";
										
//System.out.println("SOCIOS Y ESTADOS  -->> pro_1::"+xpro1+" pro_2::"+xpro2+" pro_3::"+xpro3+" pro_4::"+xpro4+" xtipoDoc="+xtipodoc);
//System.out.println("SOCIOS Y ESTADOS  -->> proA_1::"+xproA1+" proA_2::"+xproA2+" proA_3::"+xproA3+" proA_4::"+xproA4+" xtipoDoc="+xtipodoc);

					int zpro1=0,zpro2=0,zpro3=0,zpro4=0;
					int zproA1=0,zproA2=0,zproA3=0,zproA4=0;
					
					if (xpro1 > 0) { zpro1=xpro1 / 100; }
					if (xpro2 > 0) { zpro2=xpro2 / 100; }
					if (xpro3 > 0) { zpro3=xpro3 / 100; }
					if (xpro4 > 0) { zpro4=xpro4 / 100; }
					
					if (xproA1 > 0) { zproA1=(xproA1 / 100)+4; }
					if (xproA2 > 0) { zproA2=(xproA2 / 100)+4; }
					if (xproA3 > 0) { zproA3=(xproA3 / 100)+4; }
					if (xproA4 > 0) { zproA4=(xproA4 / 100)+4; }
//System.out.println(xest[zpro1]+","+xest[zpro2]+","+xest[zpro3]+","+xest[zpro4]+xest[zproA1]+","+xest[zproA2]+","+xest[zproA3]+","+xest[zproA4]);										
					Map<String,Object> params = new HashMap<>();
					params.put("pro1", xpro1);
					params.put("pro2", xpro2);
					params.put("pro3", xpro3);
					params.put("pro4", xpro4);
					params.put("proa1", xproA1 +1);
					params.put("proa2", xproA2 +1);
					params.put("proa3", xproA3 +1);
					params.put("proa4", xproA4 +1);
					params.put("xestado", 0);//Integer.parseInt(xestado));
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
					params.put("estados",xest[zpro1]+","+xest[zpro2]+","+xest[zpro3]+","+xest[zpro4]+xest[zproA1]+","+xest[zproA2]+","+xest[zproA3]+","+xest[zproA4]);
					//llamada a la clase GenerarReportes
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/sociosEstados.jasper", "sociosEstados");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/sociosEstadosXLS.jasper", "reporte");						
					}
					
					//PDF
					//String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMes.jasper", "reporte");
					//EXCEL
					//rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesXLS.jasper", "reporte");
					//TXT
					//rep.generaTextFile222(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesTXT.jasper", "reporte");
				}
				
				//LISTA DE SOCIOS MENORES Y ESTADOS  
				if (op.equals("Socios_Menores")){
//System.out.println("Enter option: SOCIO_MENORES");	
					Utilitarios u=new Utilitarios();
					String xtipodoc=req.getParameter("tipodoc");
					
					int xpro0=Integer.parseInt(req.getParameter("pro_0"));					
					int xpro1=Integer.parseInt(req.getParameter("pro_1"));
					int xpro2=Integer.parseInt(req.getParameter("pro_2"));
					
					//PARAMETRO FECHA
					String zfecha=u.getFechaEdad(20);//PARAMETRO PARA 20 AÑOS, por que si tiene 21 años es JOVEN
					DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
					Date xfecha=dateformat.parse(zfecha);//Valores de entrada para el filtro de la consulta
										
					String xestados="MENOR AUSENTE/ MENOR ACTIVO / MENOR PASIVO";
										
//System.out.println("pro_1::"+xpro1+" pro_2::"+xpro2+" pro_0::"+xpro0+" xtipoDoc="+xtipodoc+" FECHAA="+zfecha);					

					int zpro0=0,zpro1=999,zpro2=0; //zpro0=MENOR AUSENTE, zpro1=PASIVO, zpro2=MENOR ACTIVO
					
					if ((xpro1 > 0)&&(xpro2 > 0)&&(xpro0 > 0)) { 
						zpro1=(-1); zpro2=200; zpro0=201;  
					}else{
						xestados="";
						if (xpro0 > 0) { 
							zpro0=201; xestados="MENOR AUSENTE"; 
						}
						if (xpro1 > 0) { 
							zpro1=(-1); 
							if (xestados.length()>0) {
								xestados=xestados+" / MENOR PASIVO";
							}else{
								xestados="MENOR PASIVO";
							}
						}
						if (xpro2 > 0) { 
							zpro2=200;
							if (xestados.length()>0) {
								xestados=xestados+" / MENOR ACTIVO";
							}else{
								xestados="MENOR ACTIVO";
							}
						}	
					}
//System.out.println("zpro_1::"+zpro1+" zpro_2::"+zpro2+" zpro_0::"+zpro0);					
					Map<String,Object> params = new HashMap<>();
					params.put("pro0", zpro0); //MENOR AUSENTE
					params.put("pro1", zpro1);//MENOR PASIVO
					params.put("pro2", zpro2); //MENOR ACTIVO
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);
					params.put("xfecha", xfecha);
					params.put("estados", xestados);
					//llamada a la clase GenerarReportes
					
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/socioMenorEstados.jasper", "socioMenor");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/socioMenorEstadosXLS.jasper", "socioMenor");						
					}
					
					//PDF
					//String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMes.jasper", "reporte");
					//EXCEL
					//rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesXLS.jasper", "reporte");
					//TXT
					//rep.generaTextFile222(res, req, this.dataSource.getConnection(), params, "reports/pagosHastaMesTXT.jasper", "reporte");
				}

				//LISTA DE SOCIOS Y SUS BENEFICIARIOS 
				if (op.equals("Socios_Beneficiarios")){
//System.out.println("Enter option: SOCIO_BENEFICIARIOS");						
					String xtipodoc=req.getParameter("tipodoc");					
					String xfiltro1=req.getParameter("filtro1");
					String xfiltro2=req.getParameter("filtro2");										
//System.out.println("filtro_1::"+xfiltro1+" filtro_2::"+xfiltro2+" xtipoDoc="+xtipodoc);					
					int zpro1=0,zpro2=0;								
					Map<String,Object> params = new HashMap<>();
					params.put("filtro1", "%"+xfiltro1.toUpperCase()+"%");
					params.put("filtro2", "%"+xfiltro2.toUpperCase()+"%");
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);

					//llamada a la clase GenerarReportes					
					GenerarReportes rep=new GenerarReportes();
					if (xtipodoc.equals("P")){
						String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/beneficiarios.jasper", "beneficiarios");
					}else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/beneficiariosXLS.jasper", "beneficiarios");						
					}
				}
				
				//LISTA DE KARDEX DE PAGOS DE SOCIOS   
				if (op.equals("Kardex_Pagos_Socios")){
//System.out.println("Enter option: KARDEX_PAGOS_SOCIOS......");		//si finicio=1=sin limite ; finicio=0=con fecha limite
//alert("xfinicio="+xfinicio+" xsocioVal="+xsocioVal+" mesini="+xmesini+" xmesfin="+xmesfin+" anioini="+xini+" aniofin"+xfin);
					//String xtipodoc=req.getParameter("tipodoc");					
					String xfinicio=req.getParameter("xfinicio");
					String xsocioVal=req.getParameter("xsocioVal");
					String xfechaini=req.getParameter("xfechaini");
					String xfechafin=req.getParameter("xfechafin");		
					
					int isAll=0;
					if (xfinicio.equals("1")){
						isAll=1; //si todo
						xfechaini="01/01/2000";
					}
//System.out.println("xfinicio::"+xfinicio+" xsocioVal::"+xsocioVal+" xfechaini="+xfechaini+" xfechafin="+xfechafin+" isAll="+isAll);
					
					DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
					Date xfini=dateformat.parse(xfechaini);//Valores de entrada para el filtro de la consulta
					Date xffin=dateformat.parse(xfechafin);//Valores de entrada para el filtro de la consulta
					
					String xres = this.reportesSociosManager.setKardexPagosSocios(isAll,Integer.parseInt(xsocioVal),xfini,xffin,xlogin);
//System.out.println("xres::"+xres);
					int zpro1=0,zpro2=0;								
					Map<String,Object> params = new HashMap<>();
					params.put("fechaini", xfini);
					params.put("fechafin", xffin);
					params.put("responsable", xusuario);
					params.put("xlogin", xlogin);

					//llamada a la clase GenerarReportes					
					GenerarReportes rep=new GenerarReportes();
					String xsal=rep.generadorReportes(res, req, this.dataSource.getConnection(), params, "reports/personal/kardexPagosSocios.jasper", "kardexPagosSocios");
					
/*					else{
						rep.generadorReportesToExcel(res, req, this.dataSource.getConnection(), params, "reports/personal/beneficiariosXLS.jasper", "beneficiarios");						
					}
*/					
				}
				
///////////////////////////////////////////////////////////////////////////////////////				
			}	 
	  }
}

