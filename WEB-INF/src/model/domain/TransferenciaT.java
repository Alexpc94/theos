package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class TransferenciaT {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codtra;
	public Date fecha;
	public int codper_padre;
	public int codper_hijo;
	public String login;
	public String logindel;
	public int estado;
	public Date fechareg;
	public String obs;
	public String nombre;
	public String ap;
	public String am;
	public String nombre2;
	public String ap2;
	public String am2;
	public int accion;
	
	public int getAccion() {
		return accion;
	}
	public void setAccion(int accion) {
		this.accion = accion;
	}
	public String getNombre2() {
		return nombre2;
	}
	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}
	public String getAp2() {
		return ap2;
	}
	public void setAp2(String ap2) {
		this.ap2 = ap2;
	}
	public String getAm2() {
		return am2;
	}
	public void setAm2(String am2) {
		this.am2 = am2;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAp() {
		return ap;
	}
	public void setAp(String ap) {
		this.ap = ap;
	}
	public String getAm() {
		return am;
	}
	public void setAm(String am) {
		this.am = am;
	}
	public int getCodtra() {
		return codtra;
	}
	public void setCodtra(int codtra) {
		this.codtra = codtra;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getCodper_padre() {
		return codper_padre;
	}
	public void setCodper_padre(int codper_padre) {
		this.codper_padre = codper_padre;
	}
	public int getCodper_hijo() {
		return codper_hijo;
	}
	public void setCodper_hijo(int codper_hijo) {
		this.codper_hijo = codper_hijo;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getLogindel() {
		return logindel;
	}
	public void setLogindel(String logindel) {
		this.logindel = logindel;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
//F O R M A T O S
	
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}	
	public String getDatosPersona(){
		String xpersona="";
		if (this.ap != null){
			xpersona=this.ap;
		}		
		if (this.am != null){
			xpersona=xpersona+" "+this.am;
		}		
		xpersona=xpersona + " "+this.nombre;
		
		return xpersona;
	}
	public String getDatosPersona_2(){
		String xpersona="";
		if (this.ap2 != null){
			xpersona=this.ap2;
		}		
		if (this.am2 != null){
			xpersona=xpersona+" "+this.am2;
		}		
		xpersona=xpersona + " "+this.nombre2;
		
		return xpersona;
	}
	
}
