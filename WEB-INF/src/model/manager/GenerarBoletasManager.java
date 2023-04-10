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
public class GenerarBoletasManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	//extrae UN OBJETO GENERAL
	public General getGeneralUno() {
        BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(General.class);
   
        String xsql="   select codg,mes,anio "
					+"	from general "
					+"	where codg=1 ";        
        
        return (General) jdbcTemplate.queryForObject(xsql, new Object[]{},bprm);
    }
		
	public String setAddBoletas(int xmes,int xanio, String xlogin){
		String xsql="select generarBoletas(?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xmes,xanio,xlogin});
	}
	
	
	
	
}
