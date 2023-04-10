package model.manager;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class LugarManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Map<String,Object>> listarLugares(int xest1, int xest2, int xsel1,  int xsel2){
		String xsql="select l.codl ,l.nombre as nomlug ,l.estado ,l.codd ,d.codd,d.nombre as nomdp  "
				+"   from lugar l join dpto d on l.codd=d.codd "
				+"   where (l.estado between ? and ?)and "
				+"         (d.codd between ? and ?) "
				+ "  order by nomlug ";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2,xsel1,xsel2});
	}
	public List<Map<String,Object>> listardpto(){
		String xsql="select codd, nombre, estado from dpto where estado=1 order by nombre";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {});
	}
	public String setAddLugar(String lugarname,int dpto){
		return this.jdbcTemplate.queryForObject("select addLugar(?,?)", String.class,new Object[] {lugarname,dpto});
	}
	public String setModLugar(int codl,String dptosname,int codd){
		return this.jdbcTemplate.queryForObject("select modLugar(?,?,?)", String.class,new Object[] {codl,dptosname,codd});
	}
	
	public String setDelLugar(int codl){
		return this.jdbcTemplate.queryForObject("select delLugar(?)", String.class,new Object[] {codl});
	}
	public String setHabLugar(int codl){
		return this.jdbcTemplate.queryForObject("select habLugar(?)", String.class,new Object[] {codl});
	}
}
