package model.manager;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class DptosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Map<String,Object>> listar(int xest1, int xest2){
		String xsql="select codd, nombre, estado from dpto where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2});
	}

	public String setAddDptos(String dptosname){
		return this.jdbcTemplate.queryForObject("select addDptos(?)", String.class,new Object[] {dptosname});
	}
	
	public String setModDptos(String dptosname,int codd){
		return this.jdbcTemplate.queryForObject("select modDptos(?,?)", String.class,new Object[] {dptosname,codd});
	}
	
	public String setDelDptos(int codd){
		return this.jdbcTemplate.queryForObject("select delDptos(?)", String.class,new Object[] {codd});
	}
	public String setHabDptos(int codd){
		return this.jdbcTemplate.queryForObject("select habDptos(?)", String.class,new Object[] {codd});
	}
}
