package aharisu.tools.SyncBookmarks.Data;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * 文字列の暗号化と復号化を行うクラス
 * @author aharisu
 *
 */
public class Cryption {
	private static final String TRANSFORMATION = "Blowfish";
	
	public Cryption() {}
	
	public static byte[] encrypt(String key, String text) {
		SecretKeySpec spec = new SecretKeySpec(key.getBytes(), TRANSFORMATION);
		
		Exception exception;
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, spec);
			return cipher.doFinal(text.getBytes());
		} catch (NoSuchAlgorithmException e) {
			exception = e;
		} catch (NoSuchPaddingException e) {
			exception = e;
		} catch (InvalidKeyException e) {
			exception = e;
		} catch (IllegalBlockSizeException e) {
			exception = e;
		} catch (BadPaddingException e) {
			exception = e;
		}
		
		throw new RuntimeException(exception);
	}
	
	public static String decrypt(String key, byte[] encrypted) {
		SecretKeySpec spec = new SecretKeySpec(key.getBytes(), TRANSFORMATION);
		
		Exception exception;
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, spec);
			return new String(cipher.doFinal(encrypted));
		} catch (NoSuchAlgorithmException e) {
			exception = e;
		} catch (NoSuchPaddingException e) {
			exception = e;
		} catch (InvalidKeyException e) {
			exception = e;
		} catch (IllegalBlockSizeException e) {
			exception = e;
		} catch (BadPaddingException e) {
			exception = e;
		}
		
		throw new RuntimeException(exception);
	}

}
