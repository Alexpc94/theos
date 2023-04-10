package model.domain;

import java.util.Date;

import utils.Jencryption;
import utils.Utilitarios;

public class Boletas {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();
	
	public String boleta_id;
	public int codestado;
	public int codest;
	public int codpro;
	public int promocion;
	public int mes;
	public int anio;
	public Date fechareg;
	public float monto;
	public float pagado;
	public int estado;
	public String login;
	public String nombre;
	public String cedula;
	public String cliente;
	public String ci;
	public float saldo;
	public int contador;
	public int generado;
	public String obser;
	public int reactivar;
	public String codr;
	public float saldorig;
	public float monadicional;
	public float montotal;
	
	
	public float getMontotal() {
		return montotal;
	}
	public void setMontotal(float montotal) {
		this.montotal = montotal;
	}
	public float getMonadicional() {
		return monadicional;
	}
	public void setMonadicional(float monadicional) {
		this.monadicional = monadicional;
	}
	public float getSaldorig() {
		return saldorig;
	}
	public void setSaldorig(float saldorig) {
		this.saldorig = saldorig;
	}
	public int getReactivar() {
		return reactivar;
	}
	public void setReactivar(int reactivar) {
		this.reactivar = reactivar;
	}
	public String getCodr() {
		return codr;
	}
	public void setCodr(String codr) {
		this.codr = codr;
	}
	public int getGenerado() {
		return generado;
	}
	public void setGenerado(int generado) {
		this.generado = generado;
	}
	public String getObser() {
		return obser;
	}
	public void setObser(String obser) {
		this.obser = obser;
	}
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
	}
	public float getSaldo() {
		return saldo;
	}
	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getBoleta_id() {
		return boleta_id;
	}
	public void setBoleta_id(String boleta_id) {
		this.boleta_id = boleta_id;
	}
	public int getCodestado() {
		return codestado;
	}
	public void setCodestado(int codestado) {
		this.codestado = codestado;
	}
	public int getCodest() {
		return codest;
	}
	public void setCodest(int codest) {
		this.codest = codest;
	}
	public int getCodpro() {
		return codpro;
	}
	public void setCodpro(int codpro) {
		this.codpro = codpro;
	}
	public int getPromocion() {
		return promocion;
	}
	public void setPromocion(int promocion) {
		this.promocion = promocion;
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
	public Date getFechareg() {
		return fechareg;
	}
	public void setFechareg(Date fechareg) {
		this.fechareg = fechareg;
	}
	public float getMonto() {
		return monto;
	}
	public void setMonto(float monto) {
		this.monto = monto;
	}
	public float getPagado() {
		return pagado;
	}
	public void setPagado(float pagado) {
		this.pagado = pagado;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	//Format of Data   
	public String getFecharegFormat(){
		return u.dateFormat(this.fechareg);
	}
	
	//Format Numeros
	public String getMontoFormat() {
		return u.decimalFormat("###,##0.00", this.monto);
	}
	public String getMontotalFormat() {
		return u.decimalFormat("###,##0.00", this.montotal);
	}
	public String getPagadoFormat() {
		return u.decimalFormat("###,##0.00", this.pagado);
	}
	public String getSaldoFormat() {
		return u.decimalFormat("###,##0.00", this.saldo);
	}
	public String getSaldorigFormat() {
		return u.decimalFormat("###,##0.00", this.saldorig);
	}
	public String getMonadicionalFormat() {
		return u.decimalFormat("###,##0.00", this.monadicional);
	}
	public String getMesFormat() {
		return u.getMes(this.mes);
	}
	
	public String getGeneradoFormat() {
		String xres="Sistema";
		if (this.generado==0){
			xres="Manual";
			if (this.promocion==1){
				xres=xres+"(prom)";
			}
		}
		
		return xres;
	}
	public String getMesAnioFormat(){
		return u.getMes(this.mes)+"/"+String.valueOf(this.anio);
	}
}