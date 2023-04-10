package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Reactivar {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String codr;
	public Date fecha;
	public String obser;
	public float montoriginal;
	public float montotal;
	public float saldo;
	public int mesactiv;
	public int anioactiv;
	public int estado;
	public int codper;
	public String login;
	public Date fechareg;
	public int contador;
	public String logindel;
	public String nombre;
	public String ap;
	public String am;
	
	//GETTER AND SETTER  ------------------------------------------------------------------------------------------------------
	
	
	public String getCodr() {
		return codr;
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
	public void setCodr(String codr) {
		this.codr = codr;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getObser() {
		return obser;
	}
	public void setObser(String obser) {
		this.obser = obser;
	}
	public float getMontoriginal() {
		return montoriginal;
	}
	public void setMontoriginal(float montoriginal) {
		this.montoriginal = montoriginal;
	}
	public float getMontotal() {
		return montotal;
	}
	public void setMontotal(float montotal) {
		this.montotal = montotal;
	}
	public float getSaldo() {
		return saldo;
	}
	public void setSaldo(float saldo) {
		this.saldo = saldo;
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
	public String getLogindel() {
		return logindel;
	}
	public void setLogindel(String logindel) {
		this.logindel = logindel;
	}

	//FORMATOS ------------------------------------------------------------------------------------------------------------
	
	public String getFechaFormat(){
		return u.dateFormat(this.fecha);
	}
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	public String getMontoriginalFormat() {
		return u.decimalFormat("###,##0.00", this.montoriginal);
	}
	public String getMontotalFormat() {
		return u.decimalFormat("###,##0.00", this.montotal);
	}
	public String getSaldoFormat() {
		return u.decimalFormat("###,##0.00", this.saldo);
	}	
	public String getMesactivFormat(){
		return u.getMes(this.mesactiv);
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
	
	
}
