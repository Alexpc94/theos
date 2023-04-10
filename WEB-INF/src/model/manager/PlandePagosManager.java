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
import model.domain.Mplanp;
import model.domain.Personal;
import model.domain.Tcambio;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class PlandePagosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}


	public List<Mplanp> listarPlanPagosMonitor(int xest1, int xest2, Date xfini, Date xffin){
		String xsql=" 	select 	m.codmp,m.tipo,m.fecha,m.monto,m.saldo,m.sw,m.login,m.contador,m.referencia,m.obser,m.fechareg,   "
				+"			p.nombre,p.ap,p.am   "
				+"		from  mplanp m, personal p  "
				+"		where m.sw between ? and ? and m.codper=p.codper and m.fecha between ? and ?  "
				+"		order by m.fecha DESC,m.contador DESC  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Mplanp est = new Mplanp();
			        	est.setCodmp(rs.getString("codmp"));
			        	est.setTipo(rs.getString("tipo"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setMonto(rs.getFloat("monto"));
			        	est.setSaldo(rs.getFloat("saldo"));
			        	est.setSw(rs.getInt("sw"));
			        	est.setLogin(rs.getString("login"));
			        	est.setContador(rs.getInt("contador"));
			        	est.setReferencia(rs.getString("referencia"));
			        	est.setObser(rs.getString("obser"));
			        	est.setFechareg(rs.getDate("fechareg"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));			        			        	
			        return est;
			        }
			    },new Object[] {xest1,xest2,xfini,xffin});
		return est;		
	}
	
	public List<Personal> listarSociosConDeudas(){
		String xsql=" 	select a.coda as codigo,a.montotal,a.saldo,a.cantcuota,a.cuota,p.codper,p.nombre,p.ap,p.am,'ACCION' as tipo " 
				+"		from accion a,personal p    "
				+"		where  a.codper=p.codper and a.estado=1 and a.saldo>0.1  " 
				+"		union all  "
				+"		select r.codr as codigo,r.montotal,r.saldo,0 as cantcuota,0 as cuota,p.codper,p.nombre,p.ap,p.am,'REACTIVAR' as  tipo  "
				+"		from reactivar r, personal p   "
				+"		where r.estado=1 and r.codper=p.codper and r.saldo>0.1  "
				+"		order by 8,9,7 "; 
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Accion est = new Accion();
			        	est.setCoda(rs.getString("codigo"));
			        	est.setMontotal(rs.getFloat("montotal"));
			        	est.setSaldo(rs.getFloat("saldo"));
			        	est.setCantcuota(rs.getInt("cantcuota"));
			        	est.setCuota(rs.getFloat("cuota"));
			        	est.setCodper(rs.getInt("codper"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        	est.setTipo(rs.getString("tipo"));
			        return est;
			        }
			    },new Object[] {});
		return est;		
	}

	public List<Personal> detalleDePlandePagos(String xcodmp){
		String xsql=" 	select 	m.codmp,m.tipo,m.fecha,m.monto,m.saldo,m.sw,m.login,m.contador,m.referencia,m.obser,m.fechareg,  "
				+"			p.nombre,p.ap,p.am, d.fecha as fechapago,d.monto as montopago,d.saldo as saldopago  "
				+"		from  mplanp m, personal p, dplanp d  "
				+"		where m.codmp=? and m.codper=p.codper and m.codmp=d.codmp  "
				+"		order by d.fecha  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Mplanp est = new Mplanp();
			        	est.setCodmp(rs.getString("codmp"));
			        	est.setTipo(rs.getString("tipo"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setMonto(rs.getFloat("monto"));
			        	est.setSaldo(rs.getFloat("saldo"));
			        	est.setSw(rs.getInt("sw"));
			        	est.setLogin(rs.getString("login"));
			        	est.setContador(rs.getInt("contador"));
			        	est.setReferencia(rs.getString("referencia"));
			        	est.setObser(rs.getString("obser"));
			        	est.setFechareg(rs.getDate("fechareg"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        	est.setFechapago(rs.getDate("fechapago"));
			        	est.setMontopago(rs.getFloat("montopago"));
			        	est.setSaldopago(rs.getFloat("saldopago"));
			        return est;
			        }
			    },new Object[] {xcodmp});
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
		
	public String setAddPlanPagos(String xtipo,Date xfecha,float xmonto,String xlogin,int xcodper,String xcoda,String xobser){
		return this.jdbcTemplate.queryForObject("select addplandepagos(?,?,?,?,?,?,?)", String.class,new Object[] {xtipo,xfecha,xmonto,xlogin,xcodper,xcoda,xobser});
	}	
	public String setAddDetallePlanPagos(String xcodigo,Date xfecha,float xmonto){
		return this.jdbcTemplate.queryForObject("select addDetalleplandepagos(?,?,?)", String.class,new Object[] {xcodigo,xfecha,xmonto});
	}
	public String setDelPlanPagos(String xcodmp,String xlogin){
		return this.jdbcTemplate.queryForObject("select delPlanPagos(?,?)", String.class,new Object[] {xcodmp,xlogin});
	}
	
	
}
	
	