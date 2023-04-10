package model.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import model.domain.Personal;

//@Service indica que la clase es un bean de la capa de negocio
@Service
public class AccesoManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Personal> listarMayores21(){
		String xsql=" 	select  pp.codigoper,pp.nombre as nombre2,pp.ap as ap2,pp.am as am2, "
				+"			p.conyuge,p.nombre,p.ap,p.am,	"
				+"			p.fnac,   "
				+"			extract(year from age(CURRENT_DATE,p.fnac)) as anio  "
				+"		from personal p, personal pp  "
				+"		where p.benef=1 and p.benef_estado=1 and p.conyuge=0 and extract(year from age(p.fnac))>=21 and  "
				+"		      p.padre=pp.codper and pp.activo=1 "
				+"		order by 3,4,2  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal est = new Personal();
			        	est.setCodigoper(rs.getInt("codigoper"));
			        	est.setNombre2(rs.getString("nombre2"));
			        	est.setAp2(rs.getString("ap2"));
			        	est.setAm2(rs.getString("am2"));
			        	est.setConyuge(rs.getInt("conyuge"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        	est.setFechanac(rs.getDate("fnac"));
			        	est.setAnio(rs.getInt("anio"));
			        	return est;
			        }
			    },new Object[] {});
		return est;		
	}

	public List<Personal> listarAlertaVarios(){
		String xsql=" 	select  p.codigoper,p.codper,p.nombre,p.ap,p.am,s.nombre as estsocio,	"
				+"			p.fnac, "
				+"			extract(year from age(CURRENT_DATE,p.fnac)) as anio, e.codes_real, 'Socio MENOR pero su estado actual NO Coincide' as obs  "
				+"		from personal p, estado e, estadosoc s  "
				+"		where p.activo=1 and extract(year from age(p.fnac))>=0 and extract(year from age(p.fnac))<21 and  "
				+"		      p.codper=e.codper and e.sw=1 and e.codes_real <> 200 and e.codes_real=s.codes  "
				+"		UNION ALL  "
				+"		select  p.codigoper,p.codper,p.nombre,p.ap,p.am,s.nombre as estsocio,   "	
				+"			p.fnac,  "
				+"			extract(year from age(CURRENT_DATE,p.fnac)) as anio, e.codes_real, 'Socio JOVEN pero su Estado Actual NO Coincide' as obs  "
				+"		from personal p, estado e, estadosoc s   "
				+"		where p.activo=1 and extract(year from age(p.fnac))>=21 and extract(year from age(p.fnac))<25 and   "
				+"		      p.codper=e.codper and e.sw=1 and e.codes_real <> 400 and e.codes_real=s.codes   "
				+"		UNION ALL   "
				+"		select  p.codigoper,p.codper,p.nombre,p.ap,p.am,s.nombre as estsocio,   "	
				+"			p.fnac,   "
				+"			extract(year from age(CURRENT_DATE,p.fnac)) as anio, e.codes_real, 'Socio MAYOR (activo/emerito) pero su Estado Actual NO Coincide' as obs  "
				+"		from personal p, estado e, estadosoc s   "
				+"		where p.activo=1 and extract(year from age(p.fnac))>25 and  "
				+"		      p.codper=e.codper and e.sw=1 and e.codes_real <> 100 and e.codes_real <> 300 and e.codes_real=s.codes  "
				+"		order by 4,5,3  ";
				List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal est = new Personal();
			        	est.setCodigoper(rs.getInt("codigoper"));
			        	est.setCodper(rs.getInt("codper"));
			        	est.setNombre(rs.getString("nombre"));
			        	est.setAp(rs.getString("ap"));
			        	est.setAm(rs.getString("am"));
			        	est.setEstsocio(rs.getString("estsocio"));
			        	est.setFechanac(rs.getDate("fnac"));
			        	est.setAnio(rs.getInt("anio"));
			        	est.setObs(rs.getString("obs"));
			        	return est;
			        }
			    },new Object[] {});
		return est;		
	}
	
	public String getLogin(String xlogin, String xclave){
		return this.jdbcTemplate.queryForObject("select login(?,?);", String.class,new Object[] {xlogin,xclave});
	}

}
