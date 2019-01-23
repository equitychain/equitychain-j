package com.passport.crypto;

import com.google.gson.GsonBuilder;
<<<<<<< HEAD
import sun.misc.BASE64Decoder;

import java.security.*;
=======
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
<<<<<<< HEAD

/**
 * 对称加解密
 */
public class ECDSAUtil {
	//Applies Sha256 to a string and returns the result.
	public static String applySha256(String input){
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	//Verifies a String signature 
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成公钥私钥对
	 * @return
	 */
	public static KeyPair generateKeyPair() {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random); //256
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			return keyPair;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	//Short hand helper to turn Object into a json string
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"  
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	//根据字符串获取公钥
	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	//根据字符串获取私钥
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
=======
import sun.misc.BASE64Decoder;


public class ECDSAUtil {

  //Applies Sha256 to a string and returns the result.
	public static String applySha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      //Applies sha256 to our input,
      byte[] hash = digest.digest(input.getBytes("UTF-8"));

      StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //Applies ECDSA Signature and returns the result ( as bytes ).
  public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
    Signature dsa;
    byte[] output = new byte[0];
    try {
      dsa = Signature.getInstance("ECDSA", "BC");
      dsa.initSign(privateKey);
      byte[] strByte = input.getBytes();
      dsa.update(strByte);
      byte[] realSig = dsa.sign();
      output = realSig;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return output;
  }

  //Verifies a String signature
  public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
    try {
      Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
      ecdsaVerify.initVerify(publicKey);
      ecdsaVerify.update(data.getBytes());
      return ecdsaVerify.verify(signature);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static KeyPair generateKeyPair() {
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
      // Initialize the key generator and generate a KeyPair
      keyGen.initialize(ecSpec, random); //256
      KeyPair keyPair = keyGen.generateKeyPair();
      // Set the public and private keys from the keyPair
      return keyPair;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //Short hand helper to turn Object into a json string
  public static String getJson(Object o) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(o);
  }

  //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
  public static String getDificultyString(int difficulty) {
    return new String(new char[difficulty]).replace('\0', '0');
  }

  public static String getStringFromKey(Key key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }


  public static PublicKey getPublicKey(String key) throws Exception {
    byte[] keyBytes;
    keyBytes = (new BASE64Decoder()).decodeBuffer(key);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
    PublicKey publicKey = keyFactory.generatePublic(keySpec);
    return publicKey;
  }


  public static PrivateKey getPrivateKey(String key) throws Exception {
    byte[] keyBytes;
    keyBytes = (new BASE64Decoder()).decodeBuffer(key);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    return privateKey;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

}
