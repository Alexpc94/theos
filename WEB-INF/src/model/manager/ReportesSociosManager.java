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

import model.domain.Accion;
import model.domain.Mplanp;
import model.domain.Personal;
import model.domain.Tcambio;

//@Service indica que la clase es un bean de la capa de negocio
	@Service
public class ReportesSociosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	//lista de socios con boletas
	public List<Personal> listarSocios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
				+"		from personal p  "
				+"		where 	(p.estado=1)and  "
				+"			EXISTS (   "
				+"				   select b.*  "
				+"				   from boletas b, estado es   "
				+"				   where b.codestado=es.codestado and es.codper=p.codper and b.estado=1   "
				+"				)   "
				+"		order by p.ap,p.am,p.nombre  ";			
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Personal pro = new Personal();
			        	pro.setCodper(rs.getInt("codper"));
	                    pro.setAm(rs.getString("am"));
	                    pro.setAp(rs.getString("ap"));
	                    pro.setNombre(rs.getString("nombre"));
	                    return pro;
			        }
			    },new Object[] {});
		return per;		
	}
		
	public String setSaldos2Anios(int gestion1, int gestion2,String xlogin){
		return this.jdbcTemplate.queryForObject("select xgeneraSaldosSocios(?,?,?)", String.class,new Object[] {gestion1, gestion2,xlogin});
	}		
//setKardexPagosSocios(isAll,Integer.parseInt(xsocioVal),xfini,xffin,xlogin);
	public String setKardexPagosSocios(int isAll, int codper,Date xfini,Date xffin,String xlogin){
		return this.jdbcTemplate.queryForObject("select xgen_kardex_pagos(?,?,?,?,?)", String.class,new Object[] {codper,xfini,xffin,xlogin,isAll});
	}	
	
	public String setPagosMes(int gestion1, int mes,String xlogin,int xpro1,int xpro2,int xpro3,int xpro4,int xproa1,int xproa2,int xproa3,int xproa4){
		return this.jdbcTemplate.queryForObject("select xgeneraPagosMes(?,?,?,?,?,?,?,?,?,?,?)", String.class,new Object[] {gestion1, mes,xlogin,xpro1,xpro2,xpro3,xpro4,xproa1,xproa2,xproa3,xproa4});
	}
	
	public String setSaldoDeudor(int gestionini, int mesini,int gestion1, int mes,String xlogin){
		return this.jdbcTemplate.queryForObject("select xgen_saldo_deudor(?,?,?,?,?)", String.class,new Object[] {gestionini, mesini,gestion1, mes,xlogin});
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
