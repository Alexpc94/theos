package model.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import model.domain.Boletas;
import model.domain.Dpagos;
import model.domain.Dplanp;
import model.domain.Mpagos;
import model.domain.Personal;


//@Service indica que la clase es un bean de la capa de negocio
@Service
public class PagarplanpagosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	
	//ES PARA PAGOS BOLETAS
	public List<Personal> listarSocios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am, 'ACCION' as concepto  "   
				+"		from personal p, accion a  "
				+"		where p.codper=a.codper and a.saldo>0.1 and a.estado=1  and  "
				+"		      exists (select * "
				+"		              from mplanp m  " 
				+"		              where m.tipo='ACCION' and m.referencia=a.coda and m.estado=1)  "
				+"		union all  "
				+"		select p.codper,p.nombre,p.ap,p.am, 'REACTIVAR' as concepto  " 
				+"		from personal p, reactivar r  "
				+"		where p.codper=r.codper and r.saldo>0.1 and r.estado=1  "
				+"		order by 3,4,2	";		
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal pro = new Personal();
			        	pro.setCodper(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    pro.setConcepto(rs.getString("concepto"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;		
	}
	
/*
	//ES PARA LISTA DE BOLETAS
	public List<Personal> listar_boletas_Socios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
				+"		from personal p, estado e  "
				+"		where 	(p.estado=1)and(p.benef=0)and(p.codper=e.codper)and(e.sw=1) "
				+"		order by p.ap,p.am,p.nombre  ";				
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal pro = new Personal();
			        	pro.setCodper(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;		
	}
*/	
/*	
	public List<Boletas> listarBoletas(int xcodper,int xest1, int xest2){			
		String xsql="   select b.boleta_id,b.mes,b.anio,b.monto,b.saldo,b.estado,ec.nombre,b.generado,b.obser,b.login,b.fechareg "
					+"	from boletas b, estado e, estadosoc ec  "
					+"	where (b.estado between ? and ?)and(b.codestado=e.codestado)and(e.estado=1)and(e.codper=?)and(e.codes=ec.codes) "
					+"  order by  b.anio, b.mes ";			

		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
	                    per.setBoleta_id(rs.getString("boleta_id"));
	                    per.setObser(rs.getString("obser"));
	                    per.setLogin(rs.getString("login"));
	                    per.setFechareg(rs.getDate("fechareg"));
	                    per.setGenerado(rs.getInt("generado"));
	                    per.setMes(rs.getInt("mes"));
	                    per.setAnio(rs.getInt("anio"));
	                    per.setSaldo(rs.getFloat("saldo"));
	                    per.setMonto(rs.getFloat("monto"));
	                    per.setNombre(rs.getString("nombre"));
	                    per.setEstado(rs.getInt("estado"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2,xcodper});
		return per;		
	}
*/	
	
	public List<Mpagos> listaMaestroPagos(int xest1, int xest2, Date xfini, Date xffin){
		String xsql="   select DISTINCT m.codpag,m.estado,m.contador,m.fecha,m.concepto,m.monto,p.nombre,p.ap,p.am,m.tipotran,m.nrofac,m.descuento  "   
				+"		from mpagos m, dpagosaccion d, accion a, personal p  "
				+"		where (m.estado between ? and ?)and(m.fecha between ? and ?)and(m.tipotran='ACCION')and  "  
				+"			  (m.codpag=d.codpag)and(d.coda=a.coda)and   "
				+"			  (a.codper=p.codper)   "
				+"		union all   "
				+"		select DISTINCT m.codpag,m.estado,m.contador,m.fecha,m.concepto,m.monto,p.nombre,p.ap,p.am, m.tipotran,m.nrofac,m.descuento  "   
				+"		from mpagos m, dpagosreactivar d, reactivar a, personal p  "
				+"		where (m.estado between ? and ?)and(m.fecha between ? and ?)and(m.tipotran='REACTIVAR')and  "  
				+"		      (m.codpag=d.codpag)and(d.codr=a.codr)and  "
				+"		      (a.codper=p.codper)  "     	  
				+"		order by 4 DESC, 3 DESC ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Mpagos per = new Mpagos();
			        	per.setCodpag(rs.getString("codpag"));
			        	per.setFecha(rs.getDate("fecha"));
			        	per.setEstado(rs.getInt("estado"));
			        	per.setContador(rs.getInt("contador"));
			        	per.setConcepto(rs.getString("concepto"));
			        	per.setMonto(rs.getFloat("monto"));
			        	per.setDescuento(rs.getFloat("descuento"));
			        	per.setNrofac(rs.getInt("nrofac"));
			        	per.setNombre(rs.getString("nombre"));
			        	per.setAp(rs.getString("ap"));
			        	per.setAm(rs.getString("am"));
			        	per.setTipotran(rs.getString("tipotran"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2, xfini, xffin,xest1,xest2, xfini, xffin});
		return per;		
	}
		
	public List<Boletas> listaPlanPagosApagar(int xcodper, String xtipo){			
		String xsql="   select m.codper,m.codmp,d.coddp,m.referencia,d.fecha,d.monto,d.saldo,dat.ci,dat.cliente  "
				+"		from  mplanp m, dplanp d, (  "
				+"					select p.codper,d.ci,d.cliente  "
				+"					from personal p LEFT JOIN datosfac d on p.codper=d.codper  "
				+"					where p.codper=?  "
				+"		) as dat  "
				+"		where m.codper=? and m.tipo=? and m.estado=1 and m.sw=1 and m.codmp=d.codmp and m.codper=dat.codper  "
				+"		order by d.fecha  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Dplanp per = new Dplanp();
			        	per.setCodper(rs.getInt("codper"));
			        	per.setCodmp(rs.getString("codmp"));
			        	per.setCoddp(rs.getInt("coddp"));
			        	per.setReferencia(rs.getString("referencia"));
	                    per.setFecha(rs.getDate("fecha"));
	                    per.setMonto(rs.getFloat("monto"));
	                    per.setSaldo(rs.getFloat("saldo"));
	                    per.setCi(rs.getString("ci"));
	                    per.setCliente(rs.getString("cliente"));
	                    return per;
			        }
			    },new Object[] {xcodper,xcodper,xtipo});
		return per;		
	}
	
	public List<Boletas> listaDetalle_planp_pagados(String xcodpag){			
		String xsql="   select d.monto,p.fecha "
				+"		from dpagosAccion d, dplanp p "
				+"		where d.codpag=? and d.coddp=p.coddp and d.codmp=p.codmp "
				+"		order by p.fecha  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Dplanp per = new Dplanp();
			        	per.setMonto(rs.getFloat("monto"));
			        	per.setFecha(rs.getDate("fecha"));
	                    return per;
			        }
			    },new Object[] {xcodpag});
		return per;		
	}
	
	public List<Boletas> listaDetalle_planp_pagados_reac(String xcodpag){			
		String xsql="   select d.monto,p.fecha "
				+"		from dpagosreactivar d, dplanp p "
				+"		where d.codpag=? and d.coddp=p.coddp and d.codmp=p.codmp "
				+"		order by p.fecha  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Dplanp per = new Dplanp();
			        	per.setMonto(rs.getFloat("monto"));
			        	per.setFecha(rs.getDate("fecha"));
	                    return per;
			        }
			    },new Object[] {xcodpag});
		return per;		
	}
	
	public String setAddMpagos(Date xfecha, String xlogin, String xobs,double xtotal,String xcliente,String xnit,int xdia,
				int xmes,int xanio,String xmontotext,String xestsoc1,String xdetalle,String xcoda,String xcodmp,int xfactura,float xdescuento){
		String xsql="select adicionarmpagos_planpagos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";													  	
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xfecha,xlogin, xobs, xtotal, xcliente,xnit,xdia,xmes,xanio,xmontotext,xestsoc1,xdetalle,xcoda,xcodmp,xfactura,xdescuento});
	}
	//xcodpag,Float.parseFloat(xmonto),xcoda,Integer.parseInt(xcoddp),xcodmp,ban
	public String setAddDetallepagos(String xcodpag,float xmonto, String xcoda, int xcoddp,String xcodmp, int xban){
		String xsql="select adddetallepagos_planpagos(?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xmonto,xcoda,xcoddp,xcodmp,xban});
	}
	public String setDelMpagos(String xcodpag, int xnroFac,String xlogin){
		String xsql="select eliminarmpagos_planp(?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xnroFac,xlogin});
	}
/*	
  eliminarmpagos_planp(
	xcodpag text, 
	xnrofac integer,
	xlogin text
	
	public String setadicionaUnaBoleta(int xcodper,int xmes,int xanio,float xmonto,String xobser,String xlogin){
		String xsql="select creaUnaboleta(?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodper,xmes,xanio,xmonto,xobser,xlogin});
	}
	
	public String setadicionaMUCHASBoleta(int xcodper,int xmes1,int xanio1,float xmonto,String xobser,String xlogin,int xmes2,int xanio2){
		String xsql="select creaMuchasboleta(?,?,?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodper,xmes1,xanio1,xmonto,xobser,xlogin,xmes2,xanio2});
	}		
	
	public String setDelBoletas(String xcodbol,String xlogin,String xobser){
		String xsql="select eliminarBoleta(?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodbol,xlogin,xobser});
	}
*/
	
	
}

