package model.domain;

import utils.Jencryption;

public class Roles {
	Jencryption crypt=new Jencryption();
	
	public int codr;
	public String nombre;
	public int estado;
	public String cod2;
	
	public String getCod2() {
		return cod2;
	}
	public void setCod2(String cod2) {
		this.cod2 = cod2;
	}
	public int getCodr() {
		return codr;
	}
	public void setCodr(int codr) {
		this.codr = codr;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	// format data
	public String getCodrEncrypt(){
		return crypt.encrypt(String.valueOf(codr));
	}
	
}