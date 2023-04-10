package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Tcambio {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	public int codtc;
	public Date fecha;
	public double tc;
	public int sw;
	public int estado;
	public String login;
	public int getCodtc() {
		return codtc;
	}
	public void setCodtc(int codtc) {
		this.codtc = codtc;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public double getTc() {
		return tc;
	}
	public void setTc(double tc) {
		this.tc = tc;
	}
	public int getSw() {
		return sw;
	}
	public void setSw(int sw) {
		this.sw = sw;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	//Format of Data   
			public String getFecharegFormat(){
				return u.dateFormat(this.fecha);
			}
	//Format Numeros
			public String getTcFormat() {
				return u.decimalFormat("###,##0.00", this.tc);
			}
}
