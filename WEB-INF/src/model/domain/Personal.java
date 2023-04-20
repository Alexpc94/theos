package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Personal {
	Jencryption crypt = new Jencryption();
	Utilitarios u = new Utilitarios();

	public int codper;
	public int codigoper;
	public int codes;
	public String nombre;
	public String ap;
	public String am;
	public int estado;
	public String telef;
	public String genero;
	public String ecivil;
	public String newcodigoper;
	public String nomsocio;
	public String login;
	public String cedula;
	public String email;
	public Date fechanac;
	public Date fechaing;
	public int benef;
	public int benef_ini;
	public int benef_estado;
	public int padre;
	public String cicli;
	public String cliente;
	public int conyuge;
	public int activo;
	public int activosw;
	public int mesini;
	public int anioini;
	public String concepto;

	public int codestado;
	public String estsocio;
	public float costo;

	public int anio;
	public String nombre2;
	public String ap2;
	public String am2;

	public String obs;

	// GETTERS AND SETTERS

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public int getConyuge() {
		return conyuge;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public String getNombre2() {
		return nombre2;
	}

	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}

	public String getAp2() {
		return ap2;
	}

	public void setAp2(String ap2) {
		this.ap2 = ap2;
	}

	public String getAm2() {
		return am2;
	}

	public void setAm2(String am2) {
		this.am2 = am2;
	}

	public int getBenef_estado() {
		return benef_estado;
	}

	public void setBenef_estado(int benef_estado) {
		this.benef_estado = benef_estado;
	}

	public int getBenef_ini() {
		return benef_ini;
	}

	public void setBenef_ini(int benef_ini) {
		this.benef_ini = benef_ini;
	}

	public int getCodestado() {
		return codestado;
	}

	public void setCodestado(int codestado) {
		this.codestado = codestado;
	}

	public String getEstsocio() {
		return estsocio;
	}

	public void setEstsocio(String estsocio) {
		this.estsocio = estsocio;
	}

	public float getCosto() {
		return costo;
	}

	public void setCosto(float costo) {
		this.costo = costo;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public int getActivosw() {
		return activosw;
	}

	public void setActivosw(int activosw) {
		this.activosw = activosw;
	}

	public int getMesini() {
		return mesini;
	}

	public void setMesini(int mesini) {
		this.mesini = mesini;
	}

	public int getAnioini() {
		return anioini;
	}

	public void setAnioini(int anioini) {
		this.anioini = anioini;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}

	public void setConyuge(int conyuge) {
		this.conyuge = conyuge;
	}

	public int getCodigoper() {
		return codigoper;
	}

	public void setCodigoper(int codigoper) {
		this.codigoper = codigoper;
	}

	public String getCicli() {
		return cicli;
	}

	public void setCicli(String cicli) {
		this.cicli = cicli;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public int getBenef() {
		return benef;
	}

	public void setBenef(int benef) {
		this.benef = benef;
	}

	public int getPadre() {
		return padre;
	}

	public void setPadre(int padre) {
		this.padre = padre;
	}

	public Date getFechaing() {
		return fechaing;
	}

	public void setFechaing(Date fechaing) {
		this.fechaing = fechaing;
	}

	public Date getFechanac() {
		return fechanac;
	}

	public void setFechanac(Date fechanac) {
		this.fechanac = fechanac;
	}

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

	public int getCodes() {
		return codes;
	}

	public void setCodes(int codes) {
		this.codes = codes;
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

	public String getTelef() {
		return telef;
	}

	public void setTelef(String telef) {
		this.telef = telef;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getEcivil() {
		return ecivil;
	}

	public void setEcivil(String ecivil) {
		this.ecivil = ecivil;
	}

	public String getNewcodigoper() {
		return newcodigoper;
	}

	public void setNewcodigoper(String newcodigoper) {
		this.newcodigoper = newcodigoper;
	}

	public String getNomsocio() {
		return nomsocio;
	}

	public void setNomsocio(String nomsocio) {
		this.nomsocio = nomsocio;
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

	// F O R M A T O S
	// ------------------------------------------------------------------------------------------------------------

	public String getDatoscicli() {
		String xcicli = this.cicli;
		if (this.cicli == null) {
			xcicli = "";
		}

		return xcicli;
	}

	public String getDatosPersona() {
		String xpersona = "";
		if (this.ap != null) {
			xpersona = this.ap;
		}
		if (this.am != null) {
			xpersona = xpersona + " " + this.am;
		}
		xpersona = xpersona + " " + this.nombre;

		return xpersona;
	}

	public String getDatosPersona2() {
		String xpersona2 = "";
		if (this.ap2 != null) {
			xpersona2 = this.ap2;
		}
		if (this.am2 != null) {
			xpersona2 = xpersona2 + " " + this.am2;
		}
		xpersona2 = xpersona2 + " " + this.nombre2;

		return xpersona2;
	}

	public String getLoginCrypt() {
		return crypt.encrypt(this.login);
	}

	public String getDatosTelef() {
		String xtelefono = this.telef;
		if (this.telef == null) {
			xtelefono = "";
		}

		return xtelefono;
	}

	public String getDatosEcivil() {
		String xecivil = this.ecivil;
		if (this.ecivil.equals("C")) {
			xecivil = "Casado";
		} else {
			xecivil = "Soltero";
		}

		return xecivil;
	}

	public String getDatosGenero() {
		String xgenero = this.genero;
		if (this.genero.equals("M")) {
			xgenero = "M";
		} else {
			xgenero = "F";
		}

		return xgenero;
	}

	public String getDatosAm() {
		String xam = this.am;
		if (this.am == null) {
			xam = "";
		}

		return xam;
	}

	public String getDatosCedula() {
		String xcedula = this.cedula;
		if (this.cedula == null) {
			xcedula = "-";
		}

		return xcedula;
	}

	public String getDatosEmail() {
		String xemail = this.email;
		if (this.email == null) {
			xemail = "";
		}

		return xemail;
	}

	// Format of Data
	public String getFecharegFormat() {
		return u.dateFormat(this.fechanac);
	}

	public String getFecharnacFormat() {
		return u.dateFormat(this.fechanac);
	}

	public String getFechaNregFormat() {
		return u.dateFormat(this.fechaing);
	}

	public String getMesiniFormat() {
		return u.getMes(this.mesini);
	}

	public String getConyugeFormat() {
		String res = "";
		if (this.conyuge == 0) {
			res = "hijo";
		}
		if (this.conyuge == 1) {
			res = "conyuge";
		}
		if (this.conyuge == 2) {
			res = "familiar";
		}
		if (this.conyuge == 3) {
			res = "otros";
		}
		return res;
	}
}
