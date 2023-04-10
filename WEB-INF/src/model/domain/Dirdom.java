package model.domain;
import utils.Jencryption;
public class Dirdom {
Jencryption crypt=new Jencryption();
	
	public int codper;
	public int coddom;
	public int codl;
	public String zona;
	public String calle;
	public String numero;
	public String edificio;
	public String bloque;
	public String piso;
	public String telefono;
	public String celular;
	public String email;
	

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getCodper() {
		return codper;
	}
	public void setCodper(int codper) {
		this.codper = codper;
	}
	public int getCoddom() {
		return coddom;
	}
	public void setCoddom(int coddom) {
		this.coddom = coddom;
	}
	public int getCodl() {
		return codl;
	}
	public void setCodl(int codl) {
		this.codl = codl;
	}
	public String getZona() {
		return zona;
	}
	public void setZona(String zona) {
		this.zona = zona;
	}
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getEdificio() {
		return edificio;
	}
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}
	public String getBloque() {
		return bloque;
	}
	public void setBloque(String bloque) {
		this.bloque = bloque;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	
}
