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
import model.domain.Mpagos;
import model.domain.Personal;

//@Service indica que la clase es un bean de la capa de negocio
@Service
public class PagosAdicionalesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	//ES PARA PAGOS BOLETAS
	public List<Personal> listarSocios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
				+"		from personal p, estado e  "
				+"		where 	(p.estado=1)and(p.benef=0)and(p.codper=e.codper)and(e.sw=1)and  "
				+"			EXISTS (   "
				+"				   select b.*  "
				+"				   from boletas b   "
				+"				   where b.codestado=e.codestado and b.estado=1    "
				+"				)   "
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
		String xsql="   select b.boleta_id,b.mes,b.anio,b.monto,b.saldo,b.estado,ec.nombre,b.generado,b.obser,b.login,b.fechareg,b.promocion "
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
	                    per.setPromocion(rs.getInt("promocion"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2,xcodper});
		return per;		
	}
*/
	
	public List<Mpagos> listaMaestroPagos(int xest1, int xest2, Date xfini, Date xffin){			
		String xsql="   select m.codpag,m.estado,m.contador,m.fecha,m.concepto,m.monto, "
					+"	       p.nombre,p.ap,p.am,  " 
					+"	       f.nrofac,f.codigocontrol "
					+"	from mpagos m, bol_adicional d, personal p, factura f  "  
					+"	where (m.estado between ? and ?)and(m.fecha between ? and ?)and(m.tipotran='Adicional')and(m.codpag=d.codpag)and  "  
					+"	      (d.codper=p.codper)and(m.codpag=f.codpag)and(m.facturaman=0) "
					+"	order by m.fecha DESC, m.contador DESC   ";
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
			        	per.setNombre(rs.getString("nombre"));
			        	per.setAp(rs.getString("ap"));
			        	per.setAm(rs.getString("am"));
			        	per.setNrofac(rs.getInt("nrofac"));
			        	per.setCodigocontrol(rs.getString("codigocontrol"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2, xfini, xffin});
		return per;		
	}
	
	public List<Boletas> listaBoletasApagar(int xcodper){			
		String xsql="   select d.ci,d.cliente  "
					+"	from  datosfac d  "
					+"	where (d.codper=?) ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
	                    per.setCi(rs.getString("ci"));
	                    per.setCliente(rs.getString("cliente"));
	                    return per;
			        }
			    },new Object[] {xcodper});
		return per;		
	}

	
	public List<Mpagos> listaDetalle_Boletas_pagados(String xcodpag){			
		String xsql="   select m.obs,m.detalle2,m.monto  "
					+"	from mpagos m  "
					+"	where (m.codpag=?)  "; 
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Mpagos per = new Mpagos();
			        	per.setObs(rs.getString("obs"));
			        	per.setDetalle2(rs.getString("detalle2"));
	                    per.setMonto(rs.getFloat("monto"));
	                    return per;
			        }
			    },new Object[] {xcodpag});
		return per;		
	}

	
	public String setAddMpagos(Date xfecha, String xlogin, String xobs,double xtotal,String xcliente,String xnit,int xdia,int xmes,int xanio,String xmontotext,String xestsoc1,String xdetalle,int xcodper){
		String xsql="select adicionarmpagos_adicional(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xfecha,xlogin, xobs, xtotal, xcliente,xnit,xdia,xmes,xanio,xmontotext,xestsoc1,xdetalle,xcodper});
	}
	public String setAddDetalleAdicional(String xcodpag, String xcodControl){
		String xsql="select adicionardetalleadicional(?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xcodControl});
	}
	
	public String setDelMpagos(String xcodpag, int xnroFac){
		String xsql="select eliminarmpagos_adicionales(?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xnroFac});
	}
/*	
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