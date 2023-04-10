package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Promocion {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String codp;
	public Date fecha;
	public float monto;
	public String obser;
	public int mesini;
	public int anioini;
	public int mescondonado;
	public int mesfin;
	public int aniofin;
	public int codper;
	public String login;
	public int estado;
	public String logindel;
	public Date fechareg;
	public String nombre;
	public String ap;
	public String am;
	
	// GETTER AND SETTER ----------------------------------------------------------------------------------
		public String getCodp() {
			return codp;
		}
		public void setCodp(String codp) {
			this.codp = codp;
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
		public String getObser() {
			return obser;
		}
		public void setObser(String obser) {
			this.obser = obser;
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
		public int getMescondonado() {
			return mescondonado;
		}
		public void setMescondonado(int mescondonado) {
			this.mescondonado = mescondonado;
		}
		public int getMesfin() {
			return mesfin;
		}
		public void setMesfin(int mesfin) {
			this.mesfin = mesfin;
		}
		public int getAniofin() {
			return aniofin;
		}
		public void setAniofin(int aniofin) {
			this.aniofin = aniofin;
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
		public int getEstado() {
			return estado;
		}
		public void setEstado(int estado) {
			this.estado = estado;
		}
		public String getLogindel() {
			return logindel;
		}
		public void setLogindel(String logindel) {
			this.logindel = logindel;
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
		

		//Format of Data   --------------------------------------------------------------------------------------
		public String getFecharegFormat(){
			return u.dateFormat(this.fechareg);
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
		public String getMesAnioIniFormat(){
			return u.getMes(this.mesini)+"/"+String.valueOf(this.anioini);
		}
		public String getMesAnioFinFormat(){
			return u.getMes(this.mesfin)+"/"+String.valueOf(this.aniofin);
		}
		public String getMontoFormat() {
			return u.decimalFormat("###,##0.00", this.monto);
		}

}
