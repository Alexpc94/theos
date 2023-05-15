package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Accion {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String coda;
	public String nro;
	public float monto;
	public float saldo;
	public String obs;
	public Date fecha;
	public int mesactiv;
	public int anioactiv;
	public int estado;
	public int codper;
	public String login;
	public Date fechareg;
	public int contador;
	public String estadosoc;
	public String nombre;
	public String ap;
	public String am;
	public String tipo;
	public int cantcuota;
	public float interes;
	public float cuota;
	public float montotal;

//-----------------------------------------------------------		
	public int getCantcuota() {
		return cantcuota;
	}
	public void setCantcuota(int cantcuota) {
		this.cantcuota = cantcuota;
	}
	public float getInteres() {
		return interes;
	}
	public void setInteres(float interes) {
		this.interes = interes;
	}
	public float getCuota() {
		return cuota;
	}
	public void setCuota(float cuota) {
		this.cuota = cuota;
	}
	public float getMontotal() {
		return montotal;
	}
	public void setMontotal(float montotal) {
		this.montotal = montotal;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCoda() {
		return coda;
	}
	public void setCoda(String coda) {
		this.coda = coda;
	}
	public String getNro() {
		return nro;
	}
	public void setNro(String nro) {
		this.nro = nro;
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
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getMesactiv() {
		return mesactiv;
	}
	public void setMesactiv(int mesactiv) {
		this.mesactiv = mesactiv;
	}
	public int getAnioactiv() {
		return anioactiv;
	}
	public void setAnioactiv(int anioactiv) {
		this.anioactiv = anioactiv;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
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
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
	}
	public String getEstadosoc() {
		return estadosoc;
	}
	public void setEstadosoc(String estadosoc) {
		this.estadosoc = estadosoc;
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
	
	//FORMATO DE DATOS
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
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	public String getSaldoFormat() {
		return u.decimalFormat("###,##0.00", this.saldo);
	}
	public String getInteresFormat() {
		return u.decimalFormat("###,##0.00", this.interes);
	}
	public String getCuotaFormat() {
		return u.decimalFormat("###,##0.00", this.cuota);
	}
	public String getMontotalFormat() {
		return u.decimalFormat("###,##0.00", this.montotal);
	}
	public String getMontotal2Format() {
		return u.decimalFormat("#####0.00", this.montotal);
	}	
	public String getSaldo2Format() {
		return u.decimalFormat("#####0.00", this.saldo);
	}
	public String getEstadosocio(){
		String res="-";
		if (this.estadosoc != null){
			res=this.estadosoc;
		}		
		return res;
	}
	
}
