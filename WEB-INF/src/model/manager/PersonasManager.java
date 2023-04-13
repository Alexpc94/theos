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

import model.domain.Personal;
import model.domain.EstadoSoc;
import model.domain.General;
import model.domain.Permiso;
import model.domain.Dirdom;
import model.domain.Dirtrab;
import model.domain.ActivosPer;
import model.domain.Boletas;
import model.domain.Datosant;

@Service
public class PersonasManager {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource2) {
		jdbcTemplate = new JdbcTemplate(dataSource2);
	}

	public List<Personal> listarPersonal(int xact1, int xact2, int xactivos) {
		String xsql = "  select  codper,codigoper,nombre,ap,am,estado,fingreso,fnac,genero,ecivil,newcodigoper,telefono,email,cicli,cliente,max(cedula) as cedula, max(nomsocio) as nomsocio,max(codes) as codes, max(activo) as activo,max(mesini) as mesini,max(anioini) as anioini  "
				+ "	from (  "
				+ " 	 select p.codper,p.codigoper,p.nombre,p.ap,p.am,p.estado,p.fnac,p.fingreso,p.genero,p.ecivil,p.email,p.newcodigoper,p.telefono,df.ci as cicli,df.cliente,dat2.cedula as cedula,0 as codes, '-' as nomsocio, p.activo,p.mesini,p.anioini  "
				+ "	 from datosfac df, personal p ,(  "
				+ " 		select pp.codper, '-' as cedula "
				+ "	 	from personal pp  "
				+ "		where (not exists (select * from datosper d where d.codper=pp.codper))and(pp.benef=0) "
				+ "		union all  "
				+ "		select p.codper, ci as cedula "
				+ "		from datosper d, personal p "
				+ "		where (d.codper=p.codper)and(p.benef=0) "
				+ "		  ) as dat2  "
				+ "	 where p.codper=dat2.codper and p.codper=df.codper "
				+ "  UNION ALL 				"
				+ "	   select p.codper,p.codigoper,p.nombre,p.ap,p.am,p.estado,p.fnac,p.fingreso,p.genero,p.ecivil,p.email,p.newcodigoper,p.telefono,df.ci as cicli,df.cliente,'-' as cedula,dat2.codes,dat2.nombre as nomsocio, p.activo,p.mesini,p.anioini  "
				+ "	   from datosfac df, personal p, (   "
				+ "		  select e.codper,e.codes, s.nombre   "
				+ "		  from personal p, estado e, estadosoc s   "
				+ "		  where (p.codper=e.codper)and(e.codes=s.codes)and(e.sw=1)and(e.estado=1)and(p.benef=0) "
				+ "		  union all  "
				+ "		  select p.codper,0 as codes, '-' as nombre "
				+ "		  from personal p   "
				+ "		  where (not exists(select * from estado e where e.codper=p.codper and e.estado=1))and(p.benef=0) "
				+ "	  ) as dat2  "
				+ "	  where p.codper=dat2.codper and p.codper=df.codper  "
				+ " ) as datos "
				+ " where activo between ? and ? and estado=1 "
				+ " group by 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15  "
				+ " order by 4,5,3,2 ";

		// + " where estado between "+xest1+" and "+xest2+" order by nombre ";
		List per = this.jdbcTemplate.query(
				xsql,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Personal per = new Personal();
						per.setCodper(rs.getInt("codper"));
						per.setCodigoper(rs.getInt("codigoper"));
						per.setNombre(rs.getString("nombre"));
						per.setAp(rs.getString("ap"));
						per.setAm(rs.getString("am"));
						per.setEstado(rs.getInt("estado"));
						// per.setTelef(rs.getString("telef"));
						per.setFechanac(rs.getDate("fnac"));
						per.setFechaing(rs.getDate("fingreso"));
						per.setGenero(rs.getString("genero"));
						per.setEmail(rs.getString("email"));
						per.setEcivil(rs.getString("ecivil"));
						per.setNewcodigoper(rs.getString("newcodigoper"));
						per.setTelef(rs.getString("telefono"));
						per.setCicli(rs.getString("cicli"));
						per.setCliente(rs.getString("cliente"));
						per.setNomsocio(rs.getString("nomsocio"));
						per.setCodes(rs.getInt("codes"));
						// per.setLogin(rs.getString("login"));
						per.setCedula(rs.getString("cedula"));
						per.setActivo(rs.getInt("activo"));
						per.setMesini(rs.getInt("mesini"));
						per.setAnioini(rs.getInt("anioini"));
						return per;
					}
				}, new Object[] { xact1, xact2 });
		return per;
	}

	public String setActivarSocio(int xcodper, Date xfecha, int xmes, int xanio, String xlogin, int xopcion) {
		return this.jdbcTemplate.queryForObject("select activar_socio(?,?,?,?,?,?)", String.class,
				new Object[] { xcodper, xfecha, xmes, xanio, xlogin, xopcion });
	}

	public String setDelPersonal(int codper) {
		return this.jdbcTemplate.queryForObject("select delPersona(?)", String.class, new Object[] { codper });
	}

	public String setHabPersonal(int codper) {
		return this.jdbcTemplate.queryForObject("select habPersona(?)", String.class, new Object[] { codper });
	}

	public String setAddPersonal(String ci, int codigoper, String nombre, String ap, String am, String email,
			String ecivil, String genero, Date fechaNac, Date fechaIng, String cliente, int xconyuge,String newcodigoper, String telefono) {
		return this.jdbcTemplate.queryForObject("select addPersona(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { ci, codigoper, nombre, ap, am, email, ecivil, genero, fechaNac, fechaIng, cliente,
						xconyuge, newcodigoper, telefono });
	}

	public String setModPersonal(String ci, int codper, String nombre, String ap, String am, String email,
			String ecivil, String genero, Date fechaNac, Date fechaIng, int xconyuge, int xcodigoper,String xnewcodigoper, String telefono) {
		return this.jdbcTemplate.queryForObject("select modPersona(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { ci, codper, nombre, ap, am, email, ecivil, genero, fechaNac, fechaIng, xconyuge,
						xcodigoper,xnewcodigoper,telefono });
	}

	public String setModFacPersonal(String ci, int codper, String nombre) {
		return this.jdbcTemplate.queryForObject("select modFacPersona(?,?,?)", String.class,
				new Object[] { ci, codper, nombre });
	}

	public List<Map<String, Object>> listarlugar(int xest1, int xest2) {
		String xsql = "select codl, nombre, estado from lugar where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { xest1, xest2 });
	}

	public List<Map<String, Object>> listarestadosoc(int xest1, int xest2) {
		String xsql = "select codes, nombre, estado, cuota from estadosoc where estado between ? and ? order by nombre";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { xest1, xest2 });
	}

	public List<Map<String, Object>> listarestadosoc222(int xest1, int xest2, int xcodper) {
		String xsql = "   select e.codes, e.nombre, e.estado, e.cuota,e.padre  "
				+ "		from estadosoc e  "
				+ "		where 	e.estado between 1 and 1 and e.grado>0 and  "
				+ "			not EXISTS ( 	select t.*   "
				+ "					from estado t   "
				+ "					where t.codes_real=e.codes and t.codper=?  "
				+ "				)  "
				+ "			and grado > (  "
				+ "		                  	select b.grado   "
				+ "							from estado a, estadosoc b  "
				+ "							where a.codper=? and a.sw=1 and a.codes_real=b.codes  "
				+ "				    )  "
				+ "		UNION ALL  "
				+ "		select e.codes, e.nombre, e.estado, e.cuota,e.padre  "
				+ "		from estadosoc e  "
				+ "		where 	e.estado between 1 and 1 and e.grado>0 and  "
				+ "			EXISTS ( 	select t.*  "
				+ "					from estado t  "
				+ "					where t.codes_real=e.codes and t.codper=? and t.sw=1 "
				+ "				)  "
				+ "		UNION ALL  "
				+ "		select e.codes, e.nombre, e.estado, e.cuota,e.padre  "
				+ "		from estadosoc e  "
				+ "		where 	e.estado between 1 and 1 and  "
				+ "			e.padre = (  "
				+ "                      select b.codes  "
				+ "						from estado a, estadosoc b "
				+ "						where a.codper=? and a.sw=1 and a.codes_real=b.codes  "
				+ "			          )  ";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { xcodper, xcodper, xcodper, xcodper });
	}

	public List<Map<String, Object>> listarestadosoc333(int xest1, int xest2) {
		String xsql = "   select e.codes, e.nombre, e.estado, e.cuota,e.padre  "
				+ "		from estadosoc e  "
				+ "		where 	e.estado between 1 and 1 and e.grado>0  "
				+ "		order by grado  ";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	// extrae UN OBJETO de dirdom
	public Dirdom getDirDom(int xcodper) {
		BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(Dirdom.class);

		String xsql = " select codper,coddom,codl,zona,calle,numero,edificio,bloque,piso,telefono,celular,email "
				+ "	  from dirdom "
				+ "	  where (codper=?) ";
		return (Dirdom) jdbcTemplate.queryForObject(xsql,
				new Object[] { xcodper }, bprm);
	}

	public String setAddDomicilio(int codper, int codl, String zona, String calle, String numero, String edificio,
			String bloque, String telefono, String celular, String email) {
		return this.jdbcTemplate.queryForObject("select addDomicilio(?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { codper, codl, zona, calle, numero, edificio, bloque, telefono, celular, email });
	}

	public String setModDomicilio(int codper, int codl, String zona, String calle, String numero, String edificio,
			String bloque, String telefono, String celular, String email) {
		return this.jdbcTemplate.queryForObject("select modDomicilio(?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { codper, codl, zona, calle, numero, edificio, bloque, telefono, celular, email });
	}

	public String setDelDomicilio(int codper) {
		return this.jdbcTemplate.queryForObject("select delDomicilio(?)", String.class, new Object[] { codper });
	}

	// extrae UN OBJETO de dirtrab
	public Dirtrab getDirTrab(int xcodper) {
		BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(Dirtrab.class);

		String xsql = " select codper,coddir,empresa,cargo,codl,zona,calle,numero,edificio,bloque,piso,telefono,celular,email "
				+ "	  from dirtrab "
				+ "	  where (codper=?) ";
		return (Dirtrab) jdbcTemplate.queryForObject(xsql,
				new Object[] { xcodper }, bprm);
	}

	public String setAddTrabajo(int codper, int codl, String empresa, String cargo, String zona, String calle,
			String numero, String edificio, String bloque, String telefono, String celular, String email) {
		return this.jdbcTemplate.queryForObject("select addTrabajo(?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { codper, codl, empresa, cargo, zona, calle, numero, edificio, bloque, telefono, celular,
						email });
	}

	public String setModTrabajo(int codper, int codl, String empresa, String cargo, String zona, String calle,
			String numero, String edificio, String bloque, String telefono, String celular, String email) {
		return this.jdbcTemplate.queryForObject("select modTrabajo(?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { codper, codl, empresa, cargo, zona, calle, numero, edificio, bloque, telefono, celular,
						email });
	}

	public String setDelTrabajo(int codper) {
		return this.jdbcTemplate.queryForObject("select delTrabajo(?)", String.class, new Object[] { codper });
	}

	public List<EstadoSoc> listarPersonal_estado(int xcodper) {
		String xsql = " select e.codestado,e.codper,e.codes,e.fecha,e.estado,e.sw,e.login,e.codes_real, s.nombre as nomestado,s.cuota,s.padre "
				+ " from estado e, estadosoc s "
				+ " where (e.codper=" + xcodper + ")and(e.codes=s.codes) "
				+ "  order by 6 desc, 4 desc,1 desc ";
		List est = this.jdbcTemplate.query(
				xsql,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						EstadoSoc est = new EstadoSoc();
						est.setCodestado(rs.getInt("codestado"));
						est.setCodper(rs.getInt("codper"));
						est.setCodes(rs.getInt("codes"));
						est.setFecha(rs.getDate("fecha"));
						est.setEstado(rs.getInt("estado"));
						est.setSw(rs.getInt("sw"));
						est.setLogin(rs.getString("login"));
						est.setCodes_real(rs.getInt("codes_real"));
						est.setNomestado(rs.getString("nomestado"));
						est.setCuota(rs.getDouble("cuota"));
						est.setPadre(rs.getInt("padre"));
						return est;
					}
				});
		return est;
	}

	public Date getFechaAntiguedad(int xcodper) {
		String xsql = "	select fechaini  "
				+ "	from datosant  "
				+ "	where codper=?  ";
		return this.jdbcTemplate.queryForObject(xsql, Date.class, new Object[] { xcodper });
	}

	public List<ActivosPer> listarPersonal_activaciones(int xcodper) {
		String xsql = " 	select a.fecha, a.mesini, a.anioini, a.obs,a.login  "
				+ "		from activosper a  "
				+ "		where (a.codper=?)  "
				+ "		order by a.contador  ";
		List est = this.jdbcTemplate.query(
				xsql,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						ActivosPer est = new ActivosPer();
						est.setFecha(rs.getDate("fecha"));
						est.setMesini(rs.getInt("mesini"));
						est.setAnioini(rs.getInt("anioini"));
						est.setObs(rs.getString("obs"));
						est.setLogin(rs.getString("login"));
						return est;
					}
				}, new Object[] { xcodper });
		return est;
	}

	/*
	 * 
	 * public List<Map<String,Object>> listarPersonal_estado(int xcodper){
	 * String
	 * xsql=" 	select e.codper,e.codes,e.fecha,e.estado,e.sw, s.nombre as nomestado,s.cuota"
	 * +"	  	from estado e, estadosoc s "
	 * +"		where (e.codper=?)and(e.codes=s.codes) "
	 * + "		order by 5 desc;";
	 * return this.jdbcTemplate.queryForList(xsql,new Object[] {xcodper});
	 * }
	 */

	public String setAddEstados(int codper, int codes, Date fechaest, int sw, String xlogin, int mesini, int anioini,
			int mesfin, int aniofin, int xpermiso) {
		return this.jdbcTemplate.queryForObject("select addEstado(?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { codper, codes, fechaest, sw, xlogin, mesini, anioini, mesfin, aniofin, xpermiso });
	}

	public String setModEstados(int codper, int codcodestado, int Actcodes, Date fechaest, String xlogin, int mesini,
			int anioini, int mesfin, int aniofin, int xpermiso) {
		return this.jdbcTemplate.queryForObject("select modEstado(?,?,?,?,?,?,?,?,?,?)", String.class, new Object[] {
				codper, codcodestado, Actcodes, fechaest, xlogin, mesini, anioini, mesfin, aniofin, xpermiso });
	}

	public List<EstadoSoc> listarPersonal_beneficiarios(int xcodper) {
		String xsql = " 	select  codper,codigoper,nombre,ap,am,email,genero,ecivil,fnac,fingreso,estado,benef,benef_estado,padre,ci, conyuge "
				+ "	from (   "
				+ "		select p.codper,p.codigoper,p.nombre,p.ap,p.am,p.email,p.genero,p.ecivil,p.fnac,p.fingreso,p.estado,p.benef,p.padre, '-' as ci,p.conyuge,p.benef_estado  "
				+ "		from personal p  "
				+ "		where (not exists(select * from datosper x where x.codper=p.codper ))  "
				+ "		UNION ALL  "
				+ "		select p.codper,p.codigoper,p.nombre,p.ap,p.am,p.email,p.genero,p.ecivil,p.fnac,p.fingreso,p.estado,p.benef,p.padre, d.ci, p.conyuge,p.benef_estado  "
				+ "		from personal p, datosper d  "
				+ "		where  p.codper=d.codper  "
				+ "	) as datos  "
				+ "	where padre=" + xcodper + " and benef_estado between 0 and 1  "
				+ "	order by conyuge desc, nombre ";
		List ben = this.jdbcTemplate.query(
				xsql,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Personal ben = new Personal();
						ben.setCodper(rs.getInt("codper"));
						ben.setCodigoper(rs.getInt("codigoper"));
						ben.setNombre(rs.getString("nombre"));
						ben.setAp(rs.getString("ap"));
						ben.setAm(rs.getString("am"));
						ben.setEmail(rs.getString("email"));
						ben.setGenero(rs.getString("genero"));
						ben.setEcivil(rs.getString("ecivil"));
						ben.setFechanac(rs.getDate("fnac"));
						ben.setFechaing(rs.getDate("fingreso"));
						ben.setEstado(rs.getInt("estado"));
						ben.setBenef(rs.getInt("benef"));
						ben.setBenef_estado(rs.getInt("benef_estado"));
						ben.setPadre(rs.getInt("padre"));
						ben.setCedula(rs.getString("ci"));
						ben.setConyuge(rs.getInt("conyuge"));
						return ben;
					}
				});
		return ben;
	}

	public String setDelBeneficiario(int codper) {
		return this.jdbcTemplate.queryForObject("select delBeneficiario(?)", String.class, new Object[] { codper });
	}

	public String setHabBeneficiario(int codper) {
		return this.jdbcTemplate.queryForObject("select habBeneficiario(?)", String.class, new Object[] { codper });
	}

	public String setAddBeneficiario(String ci, int codigoper, String nombre, String ap, String am, String email,
			String ecivil, String genero, Date fechaNac, Date fechaIng, int padrecodper, int benef, int xconyuge) {
		return this.jdbcTemplate.queryForObject("select addBeneficiario(?,?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
				new Object[] { ci, codigoper, nombre, ap, am, email, ecivil, genero, fechaNac, fechaIng, padrecodper,
						benef, xconyuge });
	}

	public Datosant getDirAnt(int xcodper) {
		BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(Datosant.class);

		String xsql = " select codper,fechaini,codes "
				+ "	  from datosant "
				+ "	  where (codper=?) ";
		return (Datosant) jdbcTemplate.queryForObject(xsql,
				new Object[] { xcodper }, bprm);
	}

	public Permiso getDatosDePermiso(int xcodestado) {
		BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(Permiso.class);

		String xsql = " select codpermiso,mesini,anioini,mesfin,aniofin,sw "
				+ "	  from permisos  "
				+ "	  where (codestado=?)  ";
		return (Permiso) jdbcTemplate.queryForObject(xsql,
				new Object[] { xcodestado }, bprm);
	}

	public General getDatosGeneral() {
		BeanPropertyRowMapper bprm = new BeanPropertyRowMapper(General.class);

		String xsql = " select nroaccion "
				+ "	  from general  ";
		return (General) jdbcTemplate.queryForObject(xsql,
				new Object[] {}, bprm);
	}

	public String setAddAntiguedad(int codper, int codes, Date fechaest) {
		return this.jdbcTemplate.queryForObject("select addAntiguedad(?,?,?)", String.class,
				new Object[] { codper, codes, fechaest });
	}

	public String setDelAntiguedad(int codper) {
		return this.jdbcTemplate.queryForObject("select delAntiguedad(?)", String.class, new Object[] { codper });
	}
}
