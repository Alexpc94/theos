package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Dosificacion {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String nrotram;
	public String getNrotram() {
		return nrotram;
	}
	public void setNrotram(String nrotram) {
		this.nrotram = nrotram;
	}

	public String autorizacion;
	public Date fechalimite;
	public int numfac;
	public int estado;
	public String llave;
	public int sw;
	public String ley;
	public String login;
	
	
	public String getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}
	public Date getFechalimite() {
		return fechalimite;
	}
	public void setFechalimite(Date fechalimite) {
		this.fechalimite = fechalimite;
	}
	public int getNumfac() {
		return numfac;
	}
	public void setNumfac(int numfac) {
		this.numfac = numfac;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getLlave() {
		return llave;
	}
	public void setLlave(String llave) {
		this.llave = llave;
	}
	public int getSw() {
		return sw;
	}
	public void setSw(int sw) {
		this.sw = sw;
	}
	public String getLey() {
		return ley;
	}
	public void setLey(String ley) {
		this.ley = ley;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	//Format of Data   
	public String getFecharegFormat(){
		return u.dateFormat(this.fechalimite);
	}
}
