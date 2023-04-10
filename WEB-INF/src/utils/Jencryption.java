package utils;

/*
 * Nota caracteres especiales son / = + 
 * son los especiales de base64
 * tener en cuenta a la hora de manejar datos  * 
 * */
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.SecretKey;

public class Jencryption {
	
	public String encrypt(String str) {		
		Cipher c = Cripto.getCipher();
		SecretKey s = Cripto.getMyDesKey();
		
		String textoEncriptado="";		 
		try{			
			// Inicializa el cifrado de encriptacion
		    c.init(Cipher.ENCRYPT_MODE, s);
	
		    //sensitive information
		    byte[] text = str.getBytes("UTF8");
	
		    //System.out.println("Texto [Byte Format] : " + text);
		    //System.out.println("Texto : " + new String(text));
	
		    // Encriptando el texto
		    byte[] textEncrypted = c.doFinal(text);   
		    
		    textoEncriptado = new sun.misc.BASE64Encoder().encode(textEncrypted);
		    textoEncriptado=textoEncriptado.replace("/", "_");				
		    textoEncriptado=textoEncriptado.replace("+", "-");
		    //System.out.println("Text Encryted : " + textoEncriptado);
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
		}	
		
        return textoEncriptado;
    }
	public String decrypt(String str) {
		//str=str.replace(" ", "+");
		str=str.replace("-", "+");
		str=str.replace("_", "/");
		//System.out.println("String " + str);
		Cipher c = Cripto.getCipher();
		SecretKey s = Cripto.getMyDesKey();
		
		String textoDesencriptado="";		
		try{							
		    // Initialize the same cipher for decryption
		    c.init(Cipher.DECRYPT_MODE, s);
            byte[] b = new sun.misc.BASE64Decoder().decodeBuffer(str);
            
		    // Decrypt the text
		    byte[] textDecrypted = c.doFinal(b);		    
		    
		    textoDesencriptado = new String(textDecrypted, "UTF8"); 

		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		}catch (java.io.IOException e) {
		}		
		
		return textoDesencriptado;
    }

	
	public static void main(String[] argv) {
		Jencryption j = new Jencryption();  	
		   String e=j.encrypt("hola");
		   String d=j.decrypt("uOLHS8KgUkmWW6KxWPJPnxEInQChIBzIMdBqI8Ef2dBwb9lREzRDdWcyOGwtU3QNuZwUucw-FNSpNSagQPX-FWjtostze-_CU9wp_fVeIQxyVv4rJu9S2oE-_W4ZRVnSbFpc7aqGU3U=");	   					 
		   System.out.println("e = " + e+" d = "+d);
		
		   /*System.out.println (new Date());		   
		   Calendar c = new GregorianCalendar();		   
		   String dia, mes, annio;		   
		   dia = Integer.toString(c.get(Calendar.DATE));
		   mes = Integer.toString(c.get(Calendar.MONTH));
		   annio = Integer.toString(c.get(Calendar.YEAR));		   
		   System.out.println (dia + "/" + mes +"/" + annio+"   ");*/
		   
		   
	}
}
