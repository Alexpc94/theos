package model.manager;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class TconceptosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Map<String,Object>> listar(int xest1, int xest2){
		String xsql="select codc, nombre, estado from tconceptos where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2});
	}

	public String setAddTconceptos(String nombre){
		return this.jdbcTemplate.queryForObject("select addTconceptos(?)", String.class,new Object[] {nombre});
	}
	
	public String setModTconceptos(int codc,String nombre){
		return this.jdbcTemplate.queryForObject("select modTconceptos(?,?)", String.class,new Object[] {codc,nombre});
	}
	
	public String setDelTconceptos(int codc){
		return this.jdbcTemplate.queryForObject("select delTconceptos(?)", String.class,new Object[] {codc});
	}
	public String setHabTconceptos(int codc){
		return this.jdbcTemplate.queryForObject("select habTconceptos(?)", String.class,new Object[] {codc});
	}
}
