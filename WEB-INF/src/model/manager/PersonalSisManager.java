package model.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import model.domain.PersonalSis;

@Service
public class PersonalSisManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<PersonalSis> listarPersonal(int xest1, int xest2){
		String xsql=" select  codper,nombre,ap,am,estado,telefono,celular,login,cedula,ecivil,genero, fnac, email "
				+ " from ( "
				+ " select p.codper,p.nombre,p.ap,p.am,p.estado,p.telefono,p.celular,p.fnac,p.email, "
				+ " '-' as login, dat2.cedula as cedula,p.ecivil,p.genero "
				+ " from personalsis p,  (  "	
				+ " select pp.codper, ' ' as cedula "
				+ " from personalsis pp "
				+ " where (not exists (select * from datospersis d where d.codper=pp.codper)) "
				+ " union all "
				+ " select codper, ci as cedula "
				+ " from datospersis "
				+ " ) as dat2 "
				+ " where (not exists(select * from usuarios x where x.codper=p.codper ))and "
				+ " p.codper=dat2.codper "
				+ " UNION ALL "
				+ " select p.codper,p.nombre,p.ap,p.am,p.estado,p.telefono,p.celular,p.fnac,p.email, "
				+ " u.login , dat2.cedula as cedula,p.ecivil,p.genero "
				+ " from personalsis p,  usuarios u, (  "
				+ " select pp.codper, ' ' as cedula "
				+ " from personalsis pp "
				+ " where (not exists (select * from datospersis d where d.codper=pp.codper)) "
				+ " union all "
				+ " select codper, ci as cedula "
				+ " from datospersis "
				+ " ) as dat2 "
				+ " where p.codper=u.codper and p.codper=dat2.codper "
				+ " ) as datos "
				+ "  where estado between "+xest1+" and "+xest2+"  order by nombre ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	PersonalSis per = new PersonalSis();
	                    per.setCodper(rs.getInt("codper"));
	                    per.setNombre(rs.getString("nombre"));
	                    per.setAp(rs.getString("ap"));
	                    per.setAm(rs.getString("am"));
	                    per.setEstado(rs.getInt("estado"));
	                    per.setTelf(rs.getString("telefono"));
	                    per.setCelular(rs.getString("celular"));
	                    per.setLogin(rs.getString("login"));
	                    per.setCedula(rs.getString("cedula"));
	                    per.setEcivil(rs.getString("ecivil"));
	                    per.setGenero(rs.getString("genero"));
	                    per.setFechanac(rs.getDate("fnac"));
	                    per.setEmail(rs.getString("email"));
	                    return per;
			        }
			    });
		return per;		
	}
	

	public String setDelPersonalSis(int codper){
		return this.jdbcTemplate.queryForObject("select delPersonaSis(?)", String.class,new Object[] {codper});
	}
	public String setHabPersonalSis(int codper){
		return this.jdbcTemplate.queryForObject("select habPersonaSis(?)", String.class,new Object[] {codper});
	}
	public String setAddUsuario(String clave,int codper,String login){
		return this.jdbcTemplate.queryForObject("select addUsuario(?,?,?)", String.class,new Object[] {clave,codper,login});
	}
	public String setModUsuario(int codper,String clave,String login){
		return this.jdbcTemplate.queryForObject("select modUsuario(?,?,?)", String.class,new Object[] {codper,clave,login});
	}
	public String setAddPersonalSis(String ci,String nombre,String ap,String am,String telef,String celular,String email,String genero,String ecivil, Date fechaNac){
		return this.jdbcTemplate.queryForObject("select addPersonaSis(?,?,?,?,?,?,?,?,?,?)", String.class,new Object[] {ci,nombre,ap,am,telef,celular,email,genero,ecivil,fechaNac});
	}
	public String setModPersonalSis(String ci,int codper,String nombre,String ap,String am,String telef,String celular,String email,String genero,String ecivil, Date fechaNac){
		return this.jdbcTemplate.queryForObject("select modPersonaSis(?,?,?,?,?,?,?,?,?,?,?)", String.class,new Object[] {ci,codper,nombre,ap,am,telef,celular,email,genero,ecivil,fechaNac});
	}
	public String setModUsuarioClave(String clave,String login){
		return this.jdbcTemplate.queryForObject("select modUsuarioClave(?,?)", String.class,new Object[] {clave,login});
	}
	
}
