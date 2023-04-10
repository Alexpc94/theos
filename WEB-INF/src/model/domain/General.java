package model.domain;

import utils.Jencryption;
import utils.Utilitarios;

public class General {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codg;
	public int mes;
	public int anio;
	public int boleta_id;
	public int mpagos_id;
	public int compradolar_id;
	public String ges;
	public String nit;
	public int nroaccion;
	
	public int getNroaccion() {
		return nroaccion;
	}
	public void setNroaccion(int nroaccion) {
		this.nroaccion = nroaccion;
	}
	public String getNit() {
		return nit;
	}
	public void setNit(String nit) {
		this.nit = nit;
	}
	public int getCodg() {
		return codg;
	}
	public void setCodg(int codg) {
		this.codg = codg;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getBoleta_id() {
		return boleta_id;
	}
	public void setBoleta_id(int boleta_id) {
		this.boleta_id = boleta_id;
	}
	public int getMpagos_id() {
		return mpagos_id;
	}
	public void setMpagos_id(int mpagos_id) {
		this.mpagos_id = mpagos_id;
	}
	public int getCompradolar_id() {
		return compradolar_id;
	}
	public void setCompradolar_id(int compradolar_id) {
		this.compradolar_id = compradolar_id;
	}
	public String getGes() {
		return ges;
	}
	public void setGes(String ges) {
		this.ges = ges;
	}
	
	//FORMATOS
	public String getMesFormat(){
		return u.getMes(this.mes);
	}
	
	
}