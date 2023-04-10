package model.manager;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import model.domain.Personal;
import model.domain.Traspasocio;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class TraspaSocioManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Traspasocio> listar(int xest1, int xest2){
		String xsql="	select p.codper,p.nombre,p.ap,p.am,t.codt,t.estado,t.fecha,t.login  "
					+"	from traspasocio t, personal p "
					+"	where t.estado between ? and ? and  "
					+"	      t.codper=p.codper  "
					+"	order by t.fecha DESC, p.ap,p.am,p.nombre  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Traspasocio pro = new Traspasocio();
			        	pro.setCodper(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    pro.setCodt(rs.getInt("codt"));
	                    pro.setEstado(rs.getInt("estado"));
	                    pro.setLogin(rs.getString("login"));
	                    pro.setFecha(rs.getDate("fecha"));
	                    return pro;
			        }
			    },new Object[] {xest1,xest2});
		return per;	
	}
	
	public List<Traspasocio> listaBeneficiarios(){
		String xsql="	select p.codper,p.nombre,p.ap,p.am  "
					+"	from personal p  "
					+"	where p.estado=1 and p.benef=1 and benef_estado=1  "
					+"	order by p.ap,p.am,p.nombre  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Traspasocio pro = new Traspasocio();
			        	pro.setCodper(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;	
	}

	public String setAddTraspasocio(Date xfecha,int xcodper,String xlogin, String xsocio){
		return this.jdbcTemplate.queryForObject("select adicionarTraspasocio(?,?,?,?)", String.class,new Object[] {xfecha,xcodper,xlogin,xsocio});
	}

	
	public String setDelTraspasocio(int codt,int codper, String xlogin){
		return this.jdbcTemplate.queryForObject("select deltraspasocio(?,?,?)", String.class,new Object[] {codt,codper,xlogin});
	}

	
}
