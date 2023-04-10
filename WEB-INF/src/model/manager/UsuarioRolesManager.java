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

@Service
public class UsuarioRolesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Personal> listarPersonal(){
		String xsql="select u.login,p.nombre,p.ap,p.am "
				+ "  from personalsis p, usuarios u "
				+ "  where (p.codper=u.codper)and(p.estado=1) "
				+ "  order by p.ap,p.am,p.nombre";
		
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal per = new Personal();
	                    per.setLogin(rs.getString("login"));
	                    per.setNombre(rs.getString("nombre"));
	                    per.setAp(rs.getString("ap"));
	                    per.setAm(rs.getString("am"));
	                    return per;
			        }
			    });
		return per;		
	}
    
	public List<Map<String,Object>> listarRolesTodos(String xlogin){
		String xsql=" select codr, nombre, max(estado) as estado "
				   +" from ( "
				   +"   select codr, nombre, 0 as estado "
				   +"	from roles "
				   +" 	where estado=1 "
				   +" 	union all "
				   +"   select r.codr, r.nombre, 1 as estado "
				   +"   from roles r, usurol u "
				   +"   where (r.estado=1)and(r.codr=u.codr)and(u.login=?) "
				   +" ) as datos  "
				   +" group by 1,2 "
				   +" order by 2 ";

		return this.jdbcTemplate.queryForList(xsql,new Object[] {xlogin});
	}
	
	public List<Map<String,Object>> listarRolesAsignados(String xlogin){
		String xsql="   select r.codr, r.nombre, 1 as estado "
				   +"   from roles r, usurol u "
				   +"   where (r.estado=1)and(r.codr=u.codr)and(u.login=?) "
				   +"   order by 2 ";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xlogin});
	}

	public String setAsignacionUsuariosRoles(String xlogin,int codr){
		return this.jdbcTemplate.queryForObject("select asignacionUsuarioROles(?,?)", String.class,new Object[] {xlogin,codr});
	}
	
}
