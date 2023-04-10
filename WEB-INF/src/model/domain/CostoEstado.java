package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class CostoEstado {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
		
	public int codest;
	public Date fecha;
	public double costo;
	public int codes;
	public int estado;
	public int sw;
	public String login;
	public String nombre;
	public Date fechareg;
	
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	public int getCodest() {
		return codest;
	}
	public void setCodest(int codest) {
		this.codest = codest;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public double getCosto() {
		return costo;
	}
	public void setCosto(double costo) {
		this.costo = costo;
	}
	public int getCodes() {
		return codes;
	}
	public void setCodes(int codes) {
		this.codes = codes;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public int getSw() {
		return sw;
	}
	public void setSw(int sw) {
		this.sw = sw;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	//Format of Data   
	public String getFecharegFormat(){
		return u.dateFormat(this.fecha);
	}
		
	//Format Numeros
	public String getCostoFormat() {
		return u.decimalFormat("###,##0.00", this.costo);
	}
}
