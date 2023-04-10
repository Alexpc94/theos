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
import model.domain.Personal;
import model.domain.Promocion;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class PromocionesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	public List<Promocion> listarPromocionesMon(int xest1, int xest2){
		String xsql=" 	select p.codp,p.fecha,p.monto,p.mesini,p.anioini,p.mescondonado,p.mesfin,p.aniofin,p.login,p.estado,p.obser,  "
				+"		per.nombre,per.ap,per.am  "
				+"		from promocion p,personal per  "
				+"		where (p.estado between ? and ?)and(p.codper=per.codper)  "
				+"		order by p.fecha DESC,p.contador DESC ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Promocion est = new Promocion();
			        	est.setCodp(rs.getString("codp"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setMonto(rs.getFloat("monto"));
			        	est.setMesini(rs.getInt("mesini"));
			        	est.setAnioini(rs.getInt("anioini"));
			        	est.setMescondonado(rs.getInt("mescondonado"));
			        	est.setMesfin(rs.getInt("mesfin"));
			        	est.setAniofin(rs.getInt("aniofin"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setLogin(rs.getString("login"));
			        	est.setObser(rs.getString("obser"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        return est;
			        }
			    },new Object[] {xest1,xest2});
		return est;		
	}
	
	public List<Personal> listarSocios(){
		String xsql=" 	select p.codper,e.codestado,p.nombre,p.ap,p.am,s.nombre as estsocio,c.costo  "
				+"		from personal p, estado e, estadosoc s, costoestado c  "
				+"		where p.activo=1 and p.benef=0 and p.codper=e.codper and e.sw=1 and e.codes=s.codes and  "
				+"		      s.codes=c.codes and c.sw=1  "
				+"		order by p.ap,p.am,p.nombre  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal est = new Personal();
			        	est.setCodper(rs.getInt("codper"));
			        	est.setCodestado(rs.getInt("codestado"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        	est.setEstsocio(rs.getString("estsocio"));
			        	est.setCosto(rs.getFloat("costo"));
			        return est;
			        }
			    },new Object[] {});
		return est;		
	}
	
	/*
	public List<Personal> listarSocios(int xest1, int xest2){
		String xsql=" 	select p.codper,p.nombre,p.ap,p.am   "
				+"		from personal p  "
				+"		where   p.estado=1 and   "
				+"				NOT EXISTS (  "
				+"						select a.*  "
				+"						from accion a  "
				+"						where a.codper=p.codper and a.estado=1   "
				+"						)				"
				+"		order by p.ap,p.am,p.nombre  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal est = new Personal();
			        	est.setCodper(rs.getInt("codper"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        return est;
			        }
			    },new Object[] {});
		return est;		
	}
	*/
	
	public List<Boletas> listaDetalle_promociones(String xcodp){			
		String xsql="   select b.mes,b.anio,b.monto,b.saldo  "
				+"		from boletas b  "
				+"		where b.codp=? and b.promocion=1  "
				+"		order by b.anio, b.mes   ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
			        	per.setMes(rs.getInt("mes"));
			        	per.setAnio(rs.getInt("anio"));
			        	per.setMonto(rs.getFloat("monto"));
			        	per.setSaldo(rs.getFloat("saldo"));
			        	return per;
			        }
			    },new Object[] {xcodp});
		return per;		
	}

	public String setAddPromociones(Date xfecha,int xcodper,float xmonto,String xobser,int xmesini,int xanioini,int xcondonado,int xmesfin, int xaniofin,String xlogin){
		return this.jdbcTemplate.queryForObject("select addpromociones(?,?,?,?,?,?,?,?,?,?)", String.class,new Object[] {xfecha,xcodper,xmonto,xobser,xmesini,xanioini,xcondonado,xmesfin,xaniofin,xlogin});
	}
	public String setDelPromociones(String xcodp,String xlogin){
		return this.jdbcTemplate.queryForObject("select delpromociones(?,?)", String.class,new Object[] {xcodp,xlogin});
	}
	
/*

 	public String setModEstSoc(double cuota,int codes){
		return this.jdbcTemplate.queryForObject("select modEstSoc(?,?)", String.class,new Object[] {cuota,codes});
	}
	public String setDelTcambio(int codtc){
		return this.jdbcTemplate.queryForObject("select delTcambio(?)", String.class,new Object[] {codtc});
	}
	public String setHabTcambio(int codtc){
		return this.jdbcTemplate.queryForObject("select habTcambio(?)", String.class,new Object[] {codtc});
	}
	public String setModTcambio(Date fecha,double tc,int codtc){
		return this.jdbcTemplate.queryForObject("select modTcambio(?,?,?)", String.class,new Object[] {fecha,tc,codtc});
	}

*/	
	
	
}
	

