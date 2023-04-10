package model.manager;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class MenusManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Map<String,Object>> listar(int xest1, int xest2){
		String xsql="select codm, nombre, estado from menus where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2});
	}
	public String setAddMenus(String menuname){
		return this.jdbcTemplate.queryForObject("select addMenus(?)", String.class,new Object[] {menuname});
	}
	public String setModMenus(String menuname,int codm){
		return this.jdbcTemplate.queryForObject("select modMenus(?,?)", String.class,new Object[] {menuname,codm});
	}
	public String setDelMenus(int codm){
		return this.jdbcTemplate.queryForObject("select delMenus(?)", String.class,new Object[] {codm});
	}
	public String setHabMenus(int codm){
		return this.jdbcTemplate.queryForObject("select habMenus(?)", String.class,new Object[] {codm});
	}
}
	
	