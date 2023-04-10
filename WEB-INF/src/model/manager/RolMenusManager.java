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

import model.domain.Roles;

@Service
public class RolMenusManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Roles> listarRoles100(){
		String xsql=" select codr, nombre, estado "
				  +"  from roles "
				  +"  where (estado=1) " 
				  +"  order by nombre ";
		
		List rol = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Roles rol = new Roles();
			        	rol.setCodr(Integer.parseInt(rs.getString("codr")));
			        	rol.setNombre(rs.getString("nombre"));
			        	rol.setEstado(Integer.parseInt(rs.getString("estado")));
			        	return rol;
			        }
			    });
		return rol;		
	}
    
	public List<Map<String,Object>> listarMenusTodos(int xcodr){
		String xsql=" select codm, nombre, max(estado) as estado "
				+"    from ( "
				+"			select codm, nombre, 0 as estado "
				+"			from menus "
				+"			where estado=1 "
				+"			union all "
				+"			select m.codm, m.nombre, 1 as estado "
				+"			from menus m, rolmen r "
				+"			where (m.estado=1)and(m.codm=r.codm)and(r.codr=?) "
				+"        ) as datos "
				+"    group by 1,2 "
				+"    order by 2";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xcodr});
	}
	
	public List<Map<String,Object>> listarMenusAsignados(int xcodr){
		String xsql=" select m.codm, m.nombre, 1 as estado "
				+"    from menus m, rolmen r "
				+"	  where (m.estado=1)and(m.codm=r.codm)and(r.codr=?) "
				+"	  order by 2 ";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xcodr});
	}

	public String setAsignacionRolesMenus(int xcodr,int xcodm){
		return this.jdbcTemplate.queryForObject("select asignacionRolesMenus15(?,?)", String.class,new Object[] {xcodr,xcodm});
	}
	
}
