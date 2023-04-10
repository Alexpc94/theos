package model.domain;

import utils.Jencryption;
import utils.Utilitarios;

public class Dpagostodo {
	Jencryption crypt=new Jencryption();
	Utilitarios u=new Utilitarios();

	public int id;
	public String codpag;
	public int codc;
	public String nombre;
	public String detalle;
	public float importe;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCodpag() {
		return codpag;
	}
	public void setCodpag(String codpag) {
		this.codpag = codpag;
	}
	public int getCodc() {
		return codc;
	}
	public void setCodc(int codc) {
		this.codc = codc;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public float getImporte() {
		return importe;
	}
	public void setImporte(float importe) {
		this.importe = importe;
	}

	//F O R M A T O S
	public String getImporteFormat() {
		return u.decimalFormat("###,##0.00", this.importe);
	}
}
