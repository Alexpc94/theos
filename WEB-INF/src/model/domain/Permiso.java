package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Permiso {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codpermiso;
	public int mesini;
	public int anioini;
	public int mesfin;
	public int aniofin;
	public int estado;
	public int sw;
	public String login;
	public int codestado;
	public Date fechareg;
	
	public int getCodpermiso() {
		return codpermiso;
	}
	public void setCodpermiso(int codpermiso) {
		this.codpermiso = codpermiso;
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
	public int getMesfin() {
		return mesfin;
	}
	public void setMesfin(int mesfin) {
		this.mesfin = mesfin;
	}
	public int getAniofin() {
		return aniofin;
	}
	public void setAniofin(int aniofin) {
		this.aniofin = aniofin;
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
	public int getCodestado() {
		return codestado;
	}
	public void setCodestado(int codestado) {
		this.codestado = codestado;
	}
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	
	
}