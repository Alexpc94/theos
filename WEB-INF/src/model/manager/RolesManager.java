package model.manager;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service indica que la clase es un bean de la capa de negocio
@Service
public class RolesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Map<String,Object>> listar(int xest1, int xest2){
		String xsql="select codr, nombre, estado from roles where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2});
	}

	public String setAddRoles(String rolname){
		return this.jdbcTemplate.queryForObject("select addRoles(?)", String.class,new Object[] {rolname});
	}
	
	public String setModRoles(String rolname,int codr){
		return this.jdbcTemplate.queryForObject("select modRoles(?,?)", String.class,new Object[] {rolname,codr});
	}
	
	public String setDelRoles(int codr){
		return this.jdbcTemplate.queryForObject("select delRoles(?)", String.class,new Object[] {codr});
	}
	public String setHabRoles(int codr){
		return this.jdbcTemplate.queryForObject("select habRoles(?)", String.class,new Object[] {codr});
	}
}
