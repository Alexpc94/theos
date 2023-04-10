package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Bol_adicional {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	public int codper;
	public String codpag;
	public Date fecha;
	public float monto;
	public String obs;
	public Date fechareg;
	public String login;
	public String logindel;
	
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
	}
	public String getCodpag() {
		return codpag;
	}
	public void setCodpag(String codpag) {
		this.codpag = codpag;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public float getMonto() {
		return monto;
	}
	public void setMonto(float monto) {
		this.monto = monto;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
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
	public String getLogindel() {
		return logindel;
	}
	public void setLogindel(String logindel) {
		this.logindel = logindel;
	}
	
	// D A T O S   F O R M A T E A D O S
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	
}
