package model.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import model.domain.Menus;

@Service
public class MenusProcesosManager {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource2){
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}
	
	public List<Menus> listarMenus100(){
		String xsql=" select codm, nombre, estado "
				  +"  from Menus "
				  +"  where (estado=1) " 
				  +"  order by nombre ";
		
		List men = this.jdbcTemplate.query(
			    xsql,
			    new RowMapper() {
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	Menus men = new Menus();
			        	men.setCodm(Integer.parseInt(rs.getString("codm")));
			        	men.setNombre(rs.getString("nombre"));
			        	men.setEstado(Integer.parseInt(rs.getString("estado")));
			        	return men;
			        }
			    });
		return men;		
	}
    
	public List<Map<String,Object>> listarProcesosTodos(int xcodm){
		String xsql=" 	select codp, nombre, max(estado) as estado, max(priv) as priv "
					+"	from ( "
					+"		select codp, nombre, 0 as estado, 0 as priv "
					+"		from procesos "
					+"		where estado=1 "
					+"		union all "
					+"		select p.codp, p.nombre, 1 as estado, 0 as priv "
					+"		from procesos p, menpro m "
					+"		where (p.estado=1)and(p.codp=m.codp)and(m.codm=?) "
					+"		union all "
					+"		select distinct p.codp, p.nombre, 0, 1 as priv "
					+"		from procesos p, privilegios r "
					+"		where p.codp=r.codp "
					+"	     ) as datos "
					+"	group by 1,2 "
					+"	order by 2 "; 
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xcodm});
	}
	
	public List<Map<String,Object>> listarProcesosTodosPrivs(int xcodm, String xlogin){
		String xsql="   select codp,opcion,descrip, max(estado) as estado "
					+"	from ( "
					+"			select pr.codp,pr.opcion,pr.descrip, 0 as estado "
					+"			from procesos p, privilegios pr "
					+"			where (p.codp=pr.codp)and(p.estado=1) "
					+"			union all "
					+"			select distinct priv.codp2,priv.opcion,pv.descrip, 1 as estado "
					+"			from usurol u, rolmen ro, menpro me, mepriv priv, privilegios pv "
					+"			where (u.login=?)and(u.codr=ro.codr)and "
					+"				  (ro.codm=me.codm)and(me.codm=?)and "
					+"				  (me.codp=priv.codp2)and(me.codm=priv.codm2)and "
					+"				  (priv.codp=pv.codp)and(priv.opcion=pv.opcion) " 
					+"		  ) as datos "
					+"	group by 1,2,3 "
					+"	order by 1,2 ";
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xlogin,xcodm});
	}
	
	public List<Map<String,Object>> listarProcesosAsignados(int xcodm){
		String xsql=" 	select codp, nombre, max(estado) as estado, max(priv) as priv "
					+"	from ( "
					+"		select m.codp, p.nombre, 1 as estado, 0 as priv "
					+"		from procesos p, menpro m "
					+"		where (p.estado=1)and(p.codp=m.codp)and(m.codm=?) "
					+"		union all "
					+"		select distinct m.codp, p.nombre, 0 as estado, 1 as priv "
					+"		from procesos p, menpro m, privilegios r "
					+"		where (p.estado=1)and(p.codp=m.codp)and(m.codm=?)and(p.codp=r.codp) "
					+"	     ) as datos  "
					+"	group by 1,2 "
					+"	order by 2 "; 
		return this.jdbcTemplate.queryForList(xsql,new Object[] {xcodm, xcodm});
	}
	
	public String setAsignacionMenusProcesos(int xcodm,int xcodp){
		return this.jdbcTemplate.queryForObject("select asignacionMenusProcesos16(?,?)", String.class,new Object[] {xcodm,xcodp});
	}
	
	public String setAsignacionMenusProcesosPrivs(int xcodm,int xcodp,String xletra){
		return this.jdbcTemplate.queryForObject("select asignaMenusProcesoPriv16(?,?,?)", String.class,new Object[] {xcodm,xcodp,xletra});
	}
	
}
