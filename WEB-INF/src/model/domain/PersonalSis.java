package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class PersonalSis {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public int codper;
	public String nombre;
	public String ap;
	public String am;
	public String email;
	public Date fechanac;
	public String ecivil;
	public String genero;
	public String telf;
	public String celular;
	public String login;
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getFechanac() {
		return fechanac;
	}
	public void setFechanac(Date fechanac) {
		this.fechanac = fechanac;
	}
	public String getEcivil() {
		return ecivil;
	}
	public void setEcivil(String ecivil) {
		this.ecivil = ecivil;
	}
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}
	public String getTelf() {
		return telf;
	}
	public void setTelf(String telf) {
		this.telf = telf;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String cedula;
	public int estado;
	
	//Format of Data   
		public String getFecharegFormat(){
			return u.dateFormat(this.fechanac);
		}
		
		//format datos
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
		
		public String getLoginCrypt(){
			return crypt.encrypt(this.login);
		}
		
		public String getDatosTelf(){
			String xtelefono=this.telf;
			if (this.telf == null){
				xtelefono="";
			}
			
			return xtelefono;
		}
		
		public String getDatosCell(){
			String xcell=this.celular;
			if (this.celular == null){
				xcell="";
			}
			
			return xcell;
		}
		
		public String getDatosEmail(){
			String xemail=this.email;
			if (this.email == null){
				xemail="";
			}
			
			return xemail;
		}
		
		public String getDatosGenero(){
			String xgenero=this.genero;
			if (this.genero.equals("M")){
				xgenero="Masculino";
			}else{
				xgenero="Femenimo";
			}
			
			return xgenero;
		}
		
		public String getDatosAm(){
			String xam=this.am;
			if (this.am == null){
				xam="";
			}
			
			return xam;
		}	
		public String getDatosCedula(){
			String xcedula=this.cedula;
			if (this.cedula == null){
				xcedula="-";
			}
			
			return xcedula;
		}
		
}
