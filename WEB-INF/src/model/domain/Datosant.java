package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Datosant {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codper;
	public int codes;
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
	public Date getFechaini() {
		return fechaini;
	}
	public void setFechaini(Date fecha) {
		this.fechaini = fecha;
	}
	public Date fechaini;
	
	//Format of Data   
	public String getFecharegFormat(){
		return u.dateFormat(this.fechaini);
	}

}
