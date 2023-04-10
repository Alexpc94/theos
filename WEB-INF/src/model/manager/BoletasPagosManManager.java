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
	public class BoletasPagosManManager {
		
		private JdbcTemplate jdbcTemplate;
		
		@Autowired
		public void setDataSource(DataSource dataSource2){
			jdbcTemplate = new JdbcTemplate(dataSource2);
		}
		
		//ES PARA PAGOS BOLETAS
		public List<Personal> listarSocios(){
			String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
					+"		from personal p  "
					+"		where 	(p.estado=1)and(p.benef=0)and  "
					+"			EXISTS (   "
					+"				   select b.*  "
					+"				   from boletas b, estado es   "
					+"				   where b.codestado=es.codestado and es.codper=p.codper and b.saldo > 0 and b.estado=1   "
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
		
		public List<Mpagos> listaMaestroPagos(int xest1, int xest2, Date xfini, Date xffin){			
			String xsql="   select DISTINCT m.codpag,m.estado,m.contador,m.fecha,m.concepto,m.monto,p.nombre,p.ap,p.am, m.nrofac,m.monadicional  "
						+"	from mpagos m, dpagos d, boletas b, estado e, personal p"
						+"	where (m.estado between ? and ?)and(m.fecha between ? and ?)and(m.tipotran='Boletas')and(m.codpag=d.codpag)and(d.codestado=b.codestado)and  "
						+"	      (d.mes=b.mes)and(d.anio=b.anio)and(b.codestado=e.codestado)and(e.codper=p.codper)and(m.facturaman=1)  "
						+"  order by m.fecha DESC, m.contador DESC   ";
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
		                    return per;
				        }
				    },new Object[] {xest1,xest2, xfini, xffin});
			return per;		
		}
		
		public List<Boletas> listaBoletasApagar(int xcodper){			
			String xsql="   select b.boleta_id, b.codestado,b.mes,b.anio,b.contador,b.saldo,d.ci,d.cliente,ec.nombre  "
						+"	from  estado e, boletas b, datosfac d, estadosoc ec  "
						+"	where (e.codper=?)and(e.estado=1)and  "
						+"	      (e.codestado=b.codestado)and(b.estado=1)and(b.saldo>0.01)and "
						+"	      (e.codper=d.codper)and(e.codes=ec.codes) "
						+"	order by 4,3  ";

			List per = this.jdbcTemplate.query(
				    xsql,
				    new RowMapper() {
				        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				        	Boletas per = new Boletas();
				        	per.setBoleta_id(rs.getString("boleta_id"));
				        	per.setCodestado(rs.getInt("codestado"));
		                    per.setMes(rs.getInt("mes"));
		                    per.setAnio(rs.getInt("anio"));
		                    per.setContador(rs.getInt("contador"));
		                    per.setSaldo(rs.getFloat("saldo"));
		                    per.setCi(rs.getString("ci"));
		                    per.setCliente(rs.getString("cliente"));
		                    per.setNombre(rs.getString("nombre"));
		                    return per;
				        }
				    },new Object[] {xcodper});
			return per;		
		}
		
		public List<Boletas> listaDetalle_Boletas_pagados(String xcodpag){			
			String xsql="   select d.codpag,d.codestado,d.mes,d.anio,d.monto,m.obs as obser,m.monadicional,m.monto as montotal "
						+"	from dpagos d, mpagos m "
						+"	where (d.codpag like ?)and(d.codpag=m.codpag) "
						+"	order by d.anio,d.mes ";
			xcodpag="%"+xcodpag+"%";
			
			List per = this.jdbcTemplate.query(
				    xsql,
				    new RowMapper() {
				        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				        	Dpagos per = new Dpagos();
				        	per.setCodpag(rs.getString("codpag"));
				        	per.setCodestado(rs.getInt("codestado"));
		                    per.setMes(rs.getInt("mes"));
		                    per.setAnio(rs.getInt("anio"));
		                    per.setMonto(rs.getFloat("monto"));
		                    per.setObser(rs.getString("obser"));
		                    per.setMontotal(rs.getFloat("montotal"));
		                    per.setMonadicional(rs.getFloat("monadicional"));
		                    return per;
				        }
				    },new Object[] {xcodpag});
			return per;		
		}
		
		public String setAddMpagos(Date xfecha, String xlogin, String xobs,double xtotal,String xestsoc1,String xdetalle,int xnrofac,float xadicional,int misFilas){
			String xsql="select adicionarMpagos_manual(?,?,?,?,?,?,?,?,?)";
			return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xfecha,xlogin, xobs, xtotal,xestsoc1,xdetalle,xnrofac,xadicional,misFilas});
		}
		public String setDelMpagos(String xcodpag, int xnroFac){
			String xsql="select eliminarMpagos_manual(?,?)";
			return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xnroFac});
		}
		public String setAddDetallepagos(String xcodpag,int xcodestado,int xmes,int xanio, float xmonto, int xcontador){
			String xsql="select adicionardetallepagos_manual(?,?,?,?,?,?)";
			return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodpag,xcodestado,xmes,xanio,xmonto,xcontador});
		}
	
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
		
		
	}