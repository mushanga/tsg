package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;

public class Codec {
	public static String sha512_64(String val) {
		return sha_64(val,"SHA-512");
	}
	
	public static String sha1_hex(String val){
		return sha_hex(val,"SHA-1");
	}
	
	private static String sha_hex(String value, String shafunc){
		return  new String(new Hex().encode(sha(value,shafunc)));
	}
	
	private static String sha_64(String value, String shafunc){
		return new String(Base64.encode(sha(value,shafunc)));
	}
	
	private static byte[] sha(String value, String shafunc){
		byte[] hash = null;
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(shafunc);
			messageDigest.update(value.toString().getBytes("UTF-8"));
			hash = messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash;
	}
}
