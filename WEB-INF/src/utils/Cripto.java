package utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;

import javax.crypto.KeyGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;


public class Cripto {
	private static Cipher desCipher;
	private static SecretKey myDesKey;
	static {
        try {        	
        	DESKeySpec desKeySpec = new DESKeySpec("PalabraSecreta".getBytes("UTF8"));
        	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        	//KeyGenerator keygenerator = KeyGenerator.getInstance("DES");        	
		    //myDesKey = keygenerator.generateKey();
		    myDesKey = keyFactory.generateSecret(desKeySpec);
		    
		    //System.out.println("Secret key"+myDesKey.toString());
 
		    // Create the cipher 
		    desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		    //desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 
		    //desCipher = Cipher.getInstance("DES/ECB/NoPadding","SunJCE"); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
	
	
	public static Cipher getCipher() { 
        return desCipher;
    }
	public static SecretKey getMyDesKey() { 
        return myDesKey;
    }
}
