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

import model.domain.Accion;
import model.domain.Boletas;
import model.domain.Dpagos;
import model.domain.General;
import model.domain.Personal;
import model.domain.Tcambio;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class AccionesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	public List<Personal> listarSociosMon(int xest1, int xest2, float xsal1,float xsal2){
		String xsql=" 	select 	x.coda,x.codper,x.nro,x.monto,x.montotal,x.saldo,x.obs,x.fecha,x.estado,x.login,  "
				+"			    y.nombre as estadosoc,x.contador,p.nombre,p.ap,p.am   "
				+"		from  accion x left join (   "
				+"					select a.codper,b.nombre   "
				+"					from estado a left join estadosoc b on a.codes=b.codes  "
				+"					where a.sw=1   "
				+"					  ) y on x.codper=y.codper, personal p  "
				+"		where x.estado between ? and ? and x.saldo between ? and ? and x.codper=p.codper   "
				+"		order by x.fecha DESC, x.contador DESC  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Accion est = new Accion();
			        	est.setCoda(rs.getString("coda"));
			        	est.setCodper(rs.getInt("codper"));
			        	est.setNro(rs.getInt("nro"));
			        	est.setMonto(rs.getFloat("monto"));
			        	est.setMontotal(rs.getFloat("montotal"));
			        	est.setSaldo(rs.getFloat("saldo"));
			        	est.setObs(rs.getString("obs"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setLogin(rs.getString("login"));
			        	est.setEstadosoc(rs.getString("estadosoc"));
			        	est.setContador(rs.getInt("contador"));			        	
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        return est;
			        }
			    },new Object[] {xest1,xest2,xsal1,xsal2});
		return est;		
	}
	
	public List<Personal> listarSocios(int xest1, int xest2){
		String xsql=" 	select p.codper,p.codigoper,p.nombre,p.ap,p.am   "
				+"		from personal p  "
				+"		where   p.estado=1 and p.activo=0 and p.benef_estado=1 and   "
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
			        	est.setCodigoper(rs.getInt("codigoper"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        return est;
			        }
			    },new Object[] {});
		return est;		
	}
/*	
	public General getDatosGeneral() {
        BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(General.class);        
        String xsql=" select nroaccion + 1 "
				+"	  from general  ";			        		
        return (General) jdbcTemplate.queryForObject(xsql,
                new Object[]{},bprm);
    }
*/	
	public int getNroAccion(){
		String xsql="	select nroaccion + 1  "
					+"	from general  ";
		return this.jdbcTemplate.queryForObject(xsql, Integer.class,new Object[] {});
	}
	
	public List<Accion> listaDetalle_Accines(String xcoda){			
		String xsql="   select a.coda,a.nro,a.obs,a.fecha,a.monto,a.cantcuota,a.interes,a.cuota,a.montotal,a.saldo,a.login,a.estado  "
				+"		from accion a   "
				+"		where (a.coda=?)   ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Accion per = new Accion();
			        	per.setCoda(rs.getString("coda"));
			        	per.setNro(rs.getInt("nro"));
			        	per.setObs(rs.getString("obs"));
			        	per.setFecha(rs.getDate("fecha"));
			        	per.setMonto(rs.getFloat("monto"));
			        	per.setCantcuota(rs.getInt("cantcuota"));
			        	per.setInteres(rs.getFloat("interes"));
			        	per.setCuota(rs.getFloat("cuota"));
			        	per.setMontotal(rs.getFloat("montotal"));
			        	per.setSaldo(rs.getFloat("saldo"));
			        	per.setLogin(rs.getString("login"));
			        	per.setEstado(rs.getInt("estado"));
			        	return per;
			        }
			    },new Object[] {xcoda});
		return per;		
	}
	
	public String setAddAccion(Date fecha,int xnro,int xcodper,float xmonto,String xobser,int xmesactiv,int xanioactiv,String xlogin,int xnrocuota,float xinteres,float xcuota,float xmontotal){
		return this.jdbcTemplate.queryForObject("select addAccion(?,?,?,?,?,?,?,?,?,?,?,?)", String.class,new Object[] {fecha,xnro,xcodper,xmonto,xobser,xmesactiv,xanioactiv,xlogin,xnrocuota,xinteres,xcuota,xmontotal});
	}
	
	public String setDelAccion(String xcoda){
		return this.jdbcTemplate.queryForObject("select delAccion(?)", String.class,new Object[] {xcoda});
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
	
	