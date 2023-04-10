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

import model.domain.CompraDolar;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class CompradolarManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<CompraDolar> listar(int xest1, int xest2, Date xfini, Date xffin){
		String xsql=" select DISTINCT codcom,mondol,monbol,tc,estado,login,fecha,cliente,contador"
				+ " from compradolar"
				+ " where (estado between ? and ?)and(fecha between ? and ?) "
				+ " order by fecha DESC, contador DESC ";
		List est = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CompraDolar est = new CompraDolar();
			        	est.setCodcom(rs.getString("codcom"));
			        	est.setMondol(rs.getDouble("mondol"));
			        	est.setMonbol(rs.getDouble("monbol"));
			        	est.setTc(rs.getDouble("tc"));
			        	est.setEstado(rs.getInt("estado"));
			        	est.setLogin(rs.getString("login"));
			        	est.setFecha(rs.getDate("fecha"));
			        	est.setCliente(rs.getString("cliente"));
			        return est;
			        }
			    },new Object[] {xest1,xest2, xfini, xffin});
		return est;		
	}
	public String setAddMontodolar(double mondol,double monbol,double tc,String cliente,Date fecha,String xlogin){
		return this.jdbcTemplate.queryForObject("select addMondolar(?,?,?,?,?,?)", String.class,new Object[] {mondol,monbol,tc,cliente,fecha,xlogin});
	}
	public String setDelCompradolar(String codcom){
		return this.jdbcTemplate.queryForObject("select delCompradolar(?)", String.class,new Object[] {codcom});
	}
	
	public List<Map<String,Object>> listarTcambio(){
		String xsql="select  tc from tcambio where sw=1";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {});
	}

	public String setModEstSoc(double cuota,int codes){
		return this.jdbcTemplate.queryForObject("select modEstSoc(?,?)", String.class,new Object[] {cuota,codes});
	}
	
	public String setHabTcambio(int codtc){
		return this.jdbcTemplate.queryForObject("select habTcambio(?)", String.class,new Object[] {codtc});
	}
	public String setModTcambio(Date fecha,double tc,int codtc){
		return this.jdbcTemplate.queryForObject("select modTcambio(?,?,?)", String.class,new Object[] {fecha,tc,codtc});
	}
	
}
	
	