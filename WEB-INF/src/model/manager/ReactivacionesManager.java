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

import model.domain.Boletas;
import model.domain.Personal;
import model.domain.Reactivar;

//@Service indica que la clase es un bean de la capa de negocio
@Service
public class ReactivacionesManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	//ES PARA PAGOS BOLETAS
	public List<Personal> listarSocios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
				+"		from personal p, estado e  "
				+"		where 	(p.estado=1)and(p.activo=0)and(p.benef=0)and(p.codper=e.codper)and(e.sw=1)and  "
				+"			EXISTS (   "
				+"				   select b.*  "
				+"				   from boletas b   "
				+"				   where b.codestado=e.codestado and b.saldo > 0   "
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

/*	
	//ES PARA LISTA DE BOLETAS
	public List<Personal> listar_boletas_Socios(){
		String xsql="   select p.codper,p.nombre,p.ap,p.am  " 
				+"		from personal p, estado e  "
				+"		where 	(p.estado=1)and(p.benef=0)and(p.codper=e.codper)and(e.sw=1) "
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
*/	
/*	
	public List<Boletas> listarBoletas(int xcodper,int xest1, int xest2){			
		String xsql="   select b.boleta_id,b.mes,b.anio,b.monto,b.saldo,b.estado,ec.nombre,b.generado,b.obser,b.login,b.fechareg,b.promocion "
					+"	from boletas b, estado e, estadosoc ec  "
					+"	where (b.estado between ? and ?)and(b.codestado=e.codestado)and(e.estado=1)and(e.codper=?)and(e.codes=ec.codes) "
					+"  order by  b.anio, b.mes ";			

		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
	                    per.setBoleta_id(rs.getString("boleta_id"));
	                    per.setObser(rs.getString("obser"));
	                    per.setLogin(rs.getString("login"));
	                    per.setFechareg(rs.getDate("fechareg"));
	                    per.setGenerado(rs.getInt("generado"));
	                    per.setMes(rs.getInt("mes"));
	                    per.setAnio(rs.getInt("anio"));
	                    per.setSaldo(rs.getFloat("saldo"));
	                    per.setMonto(rs.getFloat("monto"));
	                    per.setNombre(rs.getString("nombre"));
	                    per.setEstado(rs.getInt("estado"));
	                    per.setPromocion(rs.getInt("promocion"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2,xcodper});
		return per;		
	}
*/
	
	public List<Reactivar> listaRactivarMon(int xest1, int xest2, Date xfini, Date xffin){			
		String xsql="  select  r.codr,r.fecha,r.obser,r.montoriginal,r.montotal,r.saldo,r.estado,r.login,r.fechareg,r.contador,  "
				+"			p.nombre,p.ap,p.am  "
				+"		from reactivar r, personal p  "
				+"		where r.codper=p.codper and r.estado between ? and ? and r.fecha between ? and ?   "
				+" 		order by r.fecha DESC, r.contador DESC  ";
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Reactivar per = new Reactivar();
			        	per.setCodr(rs.getString("codr"));
			        	per.setFecha(rs.getDate("fecha"));
			        	per.setObser(rs.getString("obser"));
			        	per.setMontoriginal(rs.getFloat("montoriginal"));
			        	per.setMontotal(rs.getFloat("montotal"));
			        	per.setSaldo(rs.getFloat("saldo"));			        	
			        	per.setEstado(rs.getInt("estado"));
			        	per.setLogin(rs.getString("login"));
			        	per.setFechareg(rs.getDate("fechareg"));		        	
			        	per.setContador(rs.getInt("contador"));
			        	per.setNombre(rs.getString("nombre"));
			        	per.setAp(rs.getString("ap"));
			        	per.setAm(rs.getString("am"));
	                    return per;
			        }
			    },new Object[] {xest1,xest2, xfini, xffin});
		return per;		
	}	

	public List<Boletas> listaBoletasApagar(int xcodper){			
		String xsql="   select b.boleta_id, b.codestado,b.mes,b.anio,b.contador,b.saldo,d.ci,d.cliente,ec.nombre  "
					+"	from  estado e, boletas b, datosfac d, estadosoc ec  "
					+"	where (e.codper=?)and(e.estado=1)and  "
					+"	      (e.codestado=b.codestado)and(b.estado=1)and(b.saldo>0.01)and "
					+"	      (e.codper=d.codper)and(e.codes=ec.codes) "
					+"	order by 4,3  ";

		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
			        	per.setBoleta_id(rs.getString("boleta_id"));
			        	per.setCodestado(rs.getInt("codestado"));
	                    per.setMes(rs.getInt("mes"));
	                    per.setAnio(rs.getInt("anio"));
	                    per.setContador(rs.getInt("contador"));
	                    per.setSaldo(rs.getFloat("saldo"));
	                    per.setCi(rs.getString("ci"));
	                    per.setCliente(rs.getString("cliente"));
	                    per.setNombre(rs.getString("nombre"));
	                    return per;
			        }
			    },new Object[] {xcodper});
		return per;		
	}

	
	public List<Boletas> listaDetalle_Reactivaciones(String xcodr){			
		String xsql="   select b.mes,b.anio,b.monto,b.contador,b.codestado,b.promocion,b.reactivar   "
				+"		from boletas b  "
				+"		where (b.codr=?)  " 
				+"		order by 1  "; 
		List per = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Boletas per = new Boletas();
	                    per.setMes(rs.getInt("mes"));
	                    per.setAnio(rs.getInt("anio"));
	                    per.setMonto(rs.getFloat("monto"));	                
			        	per.setCodestado(rs.getInt("codestado"));
			        	per.setPromocion(rs.getInt("promocion"));
			        	per.setReactivar(rs.getInt("reactivar"));
	                    return per;
			        }
			    },new Object[] {xcodr});
		return per;		
	}
	
	
	public String setAddReactivaciones(Date xfecha,String xobser,float xmontoriginal,float xmontotal,int xcodper,String xlogin){
		String xsql="select addreactivaciones(?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xfecha,xobser,xmontoriginal,xmontotal,xcodper,xlogin});
	}

	public String setAddDetalleReactivar(String xcodreac,int xcodestado,int xmes,int xanio, float xmonto, int xcontador){
		String xsql="select modReactivarBoletas(?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodreac,xcodestado,xmes,xanio,xmonto,xcontador});
	}

	public String setDelMpagos(String xcodr){
		String xsql="select eliminaReactivacion(?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodr});
	}
	
/*	
	public String setadicionaUnaBoleta(int xcodper,int xmes,int xanio,float xmonto,String xobser,String xlogin){
		String xsql="select creaUnaboleta(?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodper,xmes,xanio,xmonto,xobser,xlogin});
	}
	
	public String setadicionaMUCHASBoleta(int xcodper,int xmes1,int xanio1,float xmonto,String xobser,String xlogin,int xmes2,int xanio2){
		String xsql="select creaMuchasboleta(?,?,?,?,?,?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodper,xmes1,xanio1,xmonto,xobser,xlogin,xmes2,xanio2});
	}		
	
	public String setDelBoletas(String xcodbol,String xlogin,String xobser){
		String xsql="select eliminarBoleta(?,?,?)";
		return this.jdbcTemplate.queryForObject(xsql, String.class,new Object[] {xcodbol,xlogin,xobser});
	}
*/	
	
}