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

import model.domain.TransferenciaT;


//@Service indica que la clase es un bean de la capa de negocio
@Service
public class TransferenciaManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	public int codtra;
	public Date fecha;
	public String login;
	public String logindel;
	public int estado;
	public Date fechareg;
	public String obs;

	public List<TransferenciaT> listar(int xest1, int xest2){
		String xsql="	select p.nombre,p.ap,p.am,pp.nombre as nombre2,pp.ap as ap2,pp.am as am2,t.estado,t.fecha,t.login,t.codtra,   "
				+"		       t.codper_padre,t.codper_hijo,t.obser  "
				+"		from transferencias t, personal p, personal pp  "
				+"		where 	t.estado between ? and ? and  "
				+"			    t.codper_padre=p.codper  and  "
				+"			    t.codper_hijo=pp.codper  "
				+"		order by t.fecha DESC, p.ap,p.am,p.nombre  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	TransferenciaT pro = new TransferenciaT();
			        	pro.setCodper_padre(rs.getInt("codper_padre"));
			        	pro.setCodper_hijo(rs.getInt("codper_hijo"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    pro.setAm2(rs.getString("am2"));
	                    pro.setAp2(rs.getString("ap2"));
	                    pro.setNombre2(rs.getString("nombre2"));                  
	                    pro.setCodtra(rs.getInt("codtra"));
	                    pro.setEstado(rs.getInt("estado"));
	                    pro.setLogin(rs.getString("login"));
	                    pro.setFecha(rs.getDate("fecha"));
	                    pro.setObs(rs.getString("obser"));
	                    return pro;
			        }
			    },new Object[] {xest1,xest2});
		return per;	
	}

	public List<TransferenciaT> listaSocios(){
		String xsql="	select p.codper,p.nombre,p.ap,p.am  "  
					+"	from personal p  "
					+"	where p.estado=1 and p.benef=0 and p.activo=1   " 
					+"	order by p.ap,p.am,p.nombre  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	TransferenciaT pro = new TransferenciaT();
			        	pro.setCodper_padre(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;	
	}

	public List<TransferenciaT> listaSociosDeshabilitados(){
		String xsql="	select p.codper,p.nombre,p.ap,p.am  "
				+"		from personal p  "  
				+"		where p.estado=1 and p.benef=1 and p.benef_estado=1 and p.activo=0  "
				+"		UNION ALL  "
				+"		select p.codper,p.nombre,p.ap,p.am  "
				+"		from personal p  "  
				+"		where p.estado=1 and p.benef=0 and p.activo=0   " 
				+"		order by 3,4,2   ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	TransferenciaT pro = new TransferenciaT();
			        	pro.setCodper_padre(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;	
	}
	
	public String setAddTransferencia(Date xfecha,int xcodper_ant,int xcodper_nue,String xlogin, String xobser){
		return this.jdbcTemplate.queryForObject("select add_transferencia(?,?,?,?,?)", String.class,new Object[] {xfecha,xcodper_ant,xcodper_nue,xlogin,xobser});
	}
	
	public String setDelTransferencia(int codtra,int codper,int codper2, String xlogin){
		return this.jdbcTemplate.queryForObject("select deltransferencia(?,?,?,?)", String.class,new Object[] {codtra,codper,codper2,xlogin});
	}

}

