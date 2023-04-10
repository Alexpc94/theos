package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class CompraDolar {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	public String codcom;
	public double mondol;
	public double monbol;
	public double tc;
	public int estado;
	public String login;
	public Date fecha;
	public String cliente;
	public int contador;
	
	
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
	}
	public String getCodcom() {
		return codcom;
	}
	public void setCodcom(String codcom) {
		this.codcom = codcom;
	}
	public double getMondol() {
		return mondol;
	}
	public void setMondol(double mondol) {
		this.mondol = mondol;
	}
	public double getMonbol() {
		return monbol;
	}
	public void setMonbol(double monbol) {
		this.monbol = monbol;
	}
	public double getTc() {
		return tc;
	}
	public void setTc(double tc) {
		this.tc = tc;
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
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	
	//Format of Data   
	public String getCodcomEncript(){
		return crypt.encrypt(this.codcom);
	}
	
	public String getFecharegFormat(){
		return u.dateFormat(this.fecha);
	}
//Format Numeros
	public String getTcFormat() {
		return u.decimalFormat("###,##0.00", this.tc);
	}
	public String getMondolFormat() {
		return u.decimalFormat("###,##0.00", this.mondol);
	}
	public String getMonbolFormat() {
		return u.decimalFormat("###,##0.00", this.monbol);
	}
}
