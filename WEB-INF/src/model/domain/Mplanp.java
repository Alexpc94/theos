package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Mplanp {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String codmp;
	public String tipo;
	public Date fecha;
	public float monto;
	public float saldo;
	public int estado;
	public int sw;
	public String login;
	public int codper;
	public int contador;
	public String referencia;
	public String obser;
	public String nombre;
	public String ap;
	public String am;
	public Date fechareg;
	public Date fechapago;
	public float montopago;
	public float saldopago;
	
	
	public Date getFechapago() {
		return fechapago;
	}
	public void setFechapago(Date fechapago) {
		this.fechapago = fechapago;
	}
	public float getMontopago() {
		return montopago;
	}
	public void setMontopago(float montopago) {
		this.montopago = montopago;
	}
	public float getSaldopago() {
		return saldopago;
	}
	public void setSaldopago(float saldopago) {
		this.saldopago = saldopago;
	}
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAp() {
		return ap;
	}
	public void setAp(String ap) {
		this.ap = ap;
	}
	public String getAm() {
		return am;
	}
	public void setAm(String am) {
		this.am = am;
	}
	public String getCodmp() {
		return codmp;
	}
	public void setCodmp(String codmp) {
		this.codmp = codmp;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getObser() {
		return obser;
	}
	public void setObser(String obser) {
		this.obser = obser;
	}
	
	//F O R M A T O S -------------------------------------------------------------------------------------
	
	public String getDatosPersona(){
		String xpersona=this.nombre;
		if (this.ap != null){
			xpersona=xpersona+" "+this.ap;
		}		
		if (this.am != null){
			xpersona=xpersona+" "+this.am;
		}		
		return xpersona;
	}
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	public String getSaldoFormat() {
		return u.decimalFormat("###,##0.00", this.saldo);
	}
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getFechapagoFormat(){
		return u.dateFormat(this.fechapago);
	}
	public String getMontopagoFormat() {
		return u.decimalFormat("###,##0.00", this.montopago);
	}
	public String getSaldopagoFormat() {
		return u.decimalFormat("###,##0.00", this.saldopago);
	}
	
}

