package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Mpagos {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String codpag;
	public Date fecha;
	public float monto;
	public String concepto;
	public String tipoie;
	public int facturaman;
	public String login;
	public Date fechareg;
	public int estado;
	public String nombre;
	public String ap;
	public String am;
	public int contador;
	public int nrofac;
	public String codigocontrol;
	public String tipotran;
	public String cliente;
	public float descuento;
	public float monadicional;
	public String obs;
	public String detalle2;
	
//    facturaman integer DEFAULT 0,
//	  nrofac integer DEFAULT 0,
	
	public String getTipotran() {
		return tipotran;
	}
	public String getDetalle2() {
		return detalle2;
	}
	public void setDetalle2(String detalle2) {
		this.detalle2 = detalle2;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public float getMonadicional() {
		return monadicional;
	}
	public void setMonadicional(float monadicional) {
		this.monadicional = monadicional;
	}
	public int getNrofac() {
		return nrofac;
	}
	public void setNrofac(int nrofac) {
		this.nrofac = nrofac;
	}
	public float getDescuento() {
		return descuento;
	}
	public void setDescuento(float descuento) {
		this.descuento = descuento;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public int getFacturaman() {
		return facturaman;
	}
	public void setFacturaman(int facturaman) {
		this.facturaman = facturaman;
	}
	public void setTipotran(String tipotran) {
		this.tipotran = tipotran;
	}
	public String getCodigocontrol() {
		return codigocontrol;
	}
	public void setCodigocontrol(String codigocontrol) {
		this.codigocontrol = codigocontrol;
	}
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
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
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
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
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getTipoie() {
		return tipoie;
	}
	public void setTipoie(String tipoie) {
		this.tipoie = tipoie;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	
	//F O R M A T O S   -------------------------------------------------------------------------------------------
	
	public String getNroFacturaEncript(){
		return crypt.encrypt(String.valueOf(this.nrofac));
	}
	
	public String getCodpagEncript(){
		return crypt.encrypt(this.codpag);
	}
	
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	
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
	public String geDescuentoFormat() {
		return u.decimalFormat("###,##0.00", this.descuento);
	}
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getMonadicionalFormat() {
		return u.decimalFormat("###,##0.00", this.monadicional);
	}
}
