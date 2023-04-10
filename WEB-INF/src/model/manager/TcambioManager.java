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

import model.domain.Tcambio;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class TcambioManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Tcambio> listar(int xest1, int xest2){
		String xsql=" select codtc,fecha,tc,sw,estado"
				+ " from tcambio"
				+ " where estado between "+xest1+" and "+xest2+"  order by fecha ";
		List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Tcambio est = new Tcambio();
			        	est.setCodtc(rs.getInt("codtc"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setTc(rs.getDouble("tc"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setSw(rs.getInt("sw"));
			        return est;
			        }
			    });
		return est;		
	}
	

	public String setModEstSoc(double cuota,int codes){
		return this.jdbcTemplate.queryForObject("select modEstSoc(?,?)", String.class,new Object[] {cuota,codes});
	}
	public String setDelTcambio(int codtc){
		return this.jdbcTemplate.queryForObject("select delTcambio(?)", String.class,new Object[] {codtc});
	}
	public String setHabTcambio(int codtc){
		return this.jdbcTemplate.queryForObject("select habTcambio(?)", String.class,new Object[] {codtc});
	}
	public String setModTcambio(Date fecha,double tc,int codtc){
		return this.jdbcTemplate.queryForObject("select modTcambio(?,?,?)", String.class,new Object[] {fecha,tc,codtc});
	}
	public String setAddTcambio(Date fecha,double tc,int sw,String xlogin){
		return this.jdbcTemplate.queryForObject("select addTcambio(?,?,?,?)", String.class,new Object[] {fecha,tc,sw,xlogin});
	}
}
	
	