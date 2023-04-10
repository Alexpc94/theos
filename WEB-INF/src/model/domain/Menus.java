package model.domain;

import utils.Jencryption;

public class Menus {
	Jencryption crypt=new Jencryption();
	
	public int codm;
	public String nombre;
	public int estado;
	public int codr;
	public String codmCrypt;

	public String getCodmCrypt() {
		return codmCrypt;
	}
	public void setCodmCrypt(String codmCrypt) {
		this.codmCrypt = codmCrypt;
	}
	public int getCodm() {
		return codm;
	}
	public void setCodm(int codm) {
		this.codm = codm;
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
	public int getCodr() {
		return codr;
	}
	public void setCodr(int codr) {
		this.codr = codr;
	}
	
	// format data
	public String getCodmEncrypt(){
		return crypt.encrypt(String.valueOf(codm));
	}
	
}