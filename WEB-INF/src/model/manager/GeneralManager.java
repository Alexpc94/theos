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

import model.domain.General;



//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class GeneralManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	

	public General listargeneral() {
        BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(General.class);
   
        String xsql="   select codg, mes, anio, ges,boleta_id,mpagos_id,compradolar_id,nit,nroaccion  "
					+"	from general ";        
        
        return (General) jdbcTemplate.queryForObject(xsql, new Object[]{},bprm);
    }
	
	public String setModGeneral(int mes,int anio,String ges,int boleta_id,int mpagos_id,int compradolar_id,String nit,int xnroaccion){
		return this.jdbcTemplate.queryForObject("select modGeneral(?,?,?,?,?,?,?,?)", String.class,new Object[] {mes,anio,ges,boleta_id,mpagos_id,compradolar_id,nit,xnroaccion});
	}
	
}
	