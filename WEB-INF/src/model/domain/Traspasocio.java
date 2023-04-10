package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Traspasocio {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codt;
	public int codper;
	public String nombre;
	public String ap;
	public String am;
	public int estado;
	public Date fecha;
	public String login;
	public int getCodt() {
		return codt;
	}
	public void setCodt(int codt) {
		this.codt = codt;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
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
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
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
	
	public String getFecharegFormat(){
		return u.dateFormat(this.fecha);
	}

}
