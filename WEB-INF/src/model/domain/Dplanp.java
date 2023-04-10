package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Dplanp {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int coddp;
	public String codmp;
	public Date fecha;
	public float monto;
	public float saldo;
	public int codper;
	public String ci;
	public String cliente;
	public String referencia;
	
	//GEETER AND SETTER
	
	public int getCoddp() {
		return coddp;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public void setCoddp(int coddp) {
		this.coddp = coddp;
	}
	public String getCodmp() {
		return codmp;
	}
	public void setCodmp(String codmp) {
		this.codmp = codmp;
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
	public float getSaldo() {
		return saldo;
	}
	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
	}
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	
	//F O R M A T O S  -------------------------------------------------------------------------------------------
	
	public String getSaldoFormat() {
		return u.decimalFormat("###,##0.00", this.saldo);
	}
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getCiFormat() {
		String res="";
		if (this.ci==null){ res=""; }
		return res;
	}
	public String getClienteFormat() {
		String res="";
		if (this.cliente==null){ res=""; }
		return res;
	}
	
}
