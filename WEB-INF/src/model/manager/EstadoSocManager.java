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

import model.domain.CostoEstado;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class EstadoSocManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	//public List<Map<String,Object>> listar(int xest1, int xest2){
	//	String xsql="select e.codes, e.nombre, e.estado,ce.costo from estadosoc e,costoestado ce where  e.codes=ce.codes and ce.sw=1 and e.estado between ? and ? order by e.nombre";
	//	return this.jdbcTemplate.queryForList(xsql,new Object[] {xest1,xest2});
	//}
	public List<CostoEstado> listar(int xest1, int xest2){
		String xsql=" select e.codes, e.nombre, e.estado,ce.costo "
				+ " from estadosoc e,costoestado ce "
				+ " where  e.codes=ce.codes and ce.sw=1 and e.padre=0 and e.estado between "+xest1+" and "+xest2+" "
				+ " order by e.grado DESC ";
		List cost = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CostoEstado cost = new CostoEstado();
			        	cost.setCodes(rs.getInt("codes"));
			        	cost.setNombre(rs.getString("nombre"));
			        	cost.setEstado(rs.getInt("estado"));
			        	cost.setCosto(rs.getDouble("costo"));
			        	
			        return cost;
			        }
			    });
		return cost;		
	}
	
	public String setModEstSoc(double costo,int codes,int codest,Date fecha,String login){
		return this.jdbcTemplate.queryForObject("select modEstSoc(?,?,?,?,?)", String.class,new Object[] {costo,codes,codest,fecha,login});
	}
	public String setAddEstSoc(double costo,int codes,Date fecha,String login,int sw){
		return this.jdbcTemplate.queryForObject("select addEstSoc(?,?,?,?,?)", String.class,new Object[] {costo,codes,fecha,login,sw});
	}
	
	public List<CostoEstado> listarCosto_estado(int xcodes){
		String xsql=" select codest,fecha,costo,codes,estado,sw,login "
				+ " from costoestado "
				+ " where (codes="+xcodes+") "
				+ " order by sw desc ";
		List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CostoEstado est = new CostoEstado();
			        	est.setCodest(rs.getInt("codest"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setCosto(rs.getDouble("costo"));
			        	est.setCodes(rs.getInt("codes"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setSw(rs.getInt("sw"));
			        	est.setLogin(rs.getString("login"));
			        	
			        return est;
			        }
			    });
		return est;		
	}
	
}
	
	