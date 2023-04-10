package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class EstadoSoc {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codper;
	public int codes;
	public int codestado;
	public Date fecha;
	public String login;
	public int codes_real;
	public int padre;
	public int grado;
	public int estado;
	public int sw;
	public String nomestado;
	public double cuota;	  
	
	public int getPadre() {
		return padre;
	}
	public void setPadre(int padre) {
		this.padre = padre;
	}
	public int getGrado() {
		return grado;
	}
	public void setGrado(int grado) {
		this.grado = grado;
	}
	public int getCodestado() {
		return codestado;
	}
	public void setCodestado(int codestado) {
		this.codestado = codestado;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public int getCodes_real() {
		return codes_real;
	}
	public void setCodes_real(int codes_real) {
		this.codes_real = codes_real;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
	}
	public int getCodes() {
		return codes;
	}
	public void setCodes(int codes) {
		this.codes = codes;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
	public String getNomestado() {
		return nomestado;
	}
	public void setNomestado(String nomestado) {
		this.nomestado = nomestado;
	}
	public double getCuota() {
		return cuota;
	}
	public void setCuota(double cuota) {
		this.cuota = cuota;
	}
	//format datos
	//Format of Data   
			public String getFecharegFormat(){
				return u.dateFormat(this.fecha);
			}

}
