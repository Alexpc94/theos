package utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utilitarios {
	
	//formato para las fechas  dia/mes/año
	public String dateFormat(Date d){		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");	    
	    Calendar cal = new GregorianCalendar();
		cal.setTime(d);		
		return sdf.format(cal.getTime());
	}

	//formato para numeros decimales
	public String decimalFormat(String pattern, double value ) {
		DecimalFormat xdf = new DecimalFormat(pattern,new DecimalFormatSymbols(Locale.US));
		return xdf.format(value);
	}
	
	public String getFecha(){
		Calendar c = new GregorianCalendar();		   
		String dia, mes, annio;		   
		   dia = Integer.toString(c.get(Calendar.DATE));
		   mes = Integer.toString(c.get(Calendar.MONTH)+1);

		   annio = Integer.toString(c.get(Calendar.YEAR));
		   if(dia.length()==1) dia="0"+dia;
		   if(mes.length()==1) mes="0"+mes;
		return dia+"/"+mes+"/"+annio;   		
	}
	
	//Restriccion para filtrar parametro fecha para cierta EDAD
	public String getFechaEdad(int xedad){
		xedad=xedad + 1;
		Calendar c = new GregorianCalendar();		   
		String dia, mes, annio;		   
		   dia = Integer.toString(c.get(Calendar.DATE));
		   mes = Integer.toString(c.get(Calendar.MONTH)+1);

		   annio = Integer.toString(c.get(Calendar.YEAR) - xedad);
		   if(dia.length()==1) dia="0"+dia;
		   if(mes.length()==1) mes="0"+mes;
		return dia+"/"+mes+"/"+annio;   		
	}
	
	public String getMes(int xmes){
		String res;
        switch (xmes) {
        case 1:  res= "ENERO";
                 break;
        case 2:  res= "FEBRERO";
                 break;
        case 3:  res= "MARZO";
                 break;
        case 4:  res= "ABRIL";
                 break;
        case 5:  res= "MAYO";
                 break;
        case 6:  res= "JUNIO";
                 break;
        case 7:  res= "JULIO";
                 break;
        case 8:  res= "AGOSTO";
                 break;
        case 9:  res= "SEPTIEMPRE";
                 break;
        case 10: res= "OCTUBRE";
                 break;
        case 11: res= "NOVIEMBRE";
                 break;
        case 12: res= "DICIEMBRE";
                 break;
        default: res= "MES NO VALIDO";
                 break;
        }
		return res;
	}
	
}
