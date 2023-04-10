package model.domain;

import utils.Jencryption;
import utils.Utilitarios;

public class Dpagos {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String codpag;
	public int codestado;
	public int mes;
	public int anio;
	public float monto;
	public String obser;
	public float monadicional;
	public float montotal;
	
	public String getObser() {
		return obser;
	}
	public void setObser(String obser) {
		this.obser = obser;
	}
	public float getMonadicional() {
		return monadicional;
	}
	public void setMonadicional(float monadicional) {
		this.monadicional = monadicional;
	}
	public float getMontotal() {
		return montotal;
	}
	public void setMontotal(float montotal) {
		this.montotal = montotal;
	}
	public String getCodpag() {
		return codpag;
	}
	public void setCodpag(String codpag) {
		this.codpag = codpag;
	}
	public int getCodestado() {
		return codestado;
	}
	public void setCodestado(int codestado) {
		this.codestado = codestado;
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
	public float getMonto() {
		return monto;
	}
	public void setMonto(float monto) {
		this.monto = monto;
	}
	
	//F O R M A T O S
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	public String getMontotalFormat() {
		return u.decimalFormat("###,##0.00", this.montotal);
	}
	public String getMonadicionalFormat() {
		return u.decimalFormat("###,##0.00", this.monadicional);
	}
	public String getMesFormat() {
		return u.getMes(this.mes);
	}
}