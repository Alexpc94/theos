package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class ActivosPer {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codper;
	public int contador;
	public Date fecha;
	public int mesini;
	public int anioini;
	public Date fechareg;
	public String login;
	public String obs;
	
	
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
	}
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getMesini() {
		return mesini;
	}
	public void setMesini(int mesini) {
		this.mesini = mesini;
	}
	public int getAnioini() {
		return anioini;
	}
	public void setAnioini(int anioini) {
		this.anioini = anioini;
	}
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	//F O R M A T O S
	
	public String getMesiniFormat() {
		return u.getMes(this.mesini);
	}	
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}

}
