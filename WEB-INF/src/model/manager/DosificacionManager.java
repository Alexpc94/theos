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

import model.domain.Dosificacion;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class DosificacionManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Dosificacion> listar(int xest1, int xest2){
		String xsql=" select nrotram,autorizacion,fechalimite,numfac,estado,llave,sw,ley,login"
				+ " from dosificacion"
				+ " where estado between "+xest1+" and "+xest2+"  order by sw desc";
		List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Dosificacion est = new Dosificacion();
			        	est.setNrotram(rs.getString("nrotram"));
			        	est.setAutorizacion(rs.getString("autorizacion"));
			        	est.setFechalimite(rs.getDate("fechalimite"));
			        	est.setNumfac(rs.getInt("numfac"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setLlave(rs.getString("llave"));
			        	est.setSw(rs.getInt("sw"));
			        	est.setLey(rs.getString("ley"));
			        	est.setLogin(rs.getString("login"));
			        return est;
			        }
			    });
		return est;		
	}
	
	public String setAddDosificacion(String nrotram,String autorizacion,Date fechalimite,int numfac,String llave,int sw,String ley,String login){
		return this.jdbcTemplate.queryForObject("select addDosificacion(?,?,?,?,?,?,?,?)", String.class,new Object[] {nrotram,autorizacion,fechalimite,numfac,llave,sw,ley,login});
	}
	public String setModDosificacion(String antnrotram,String nrotram,String autorizacion,Date fechalimite,int numfac,String llave,String ley,String login){
		return this.jdbcTemplate.queryForObject("select modDosificacion(?,?,?,?,?,?,?,?)", String.class,new Object[] {antnrotram,nrotram,autorizacion,fechalimite,numfac,llave,ley,login});
	}
}
	
	