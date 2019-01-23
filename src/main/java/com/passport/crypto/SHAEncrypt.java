package com.passport.crypto;

<<<<<<< HEAD
import org.spongycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author:Lin
 * @Description:
 * @Date:15:07 2018/1/4
 * @Modified by:
 */
public class SHAEncrypt {
    public static byte[] SHA(final byte[] strText, final String strType) {
        byte[] strResult = null;
        if (strText != null && strText.length > 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                messageDigest.update(strText);
                byte byteBuffer[] = messageDigest.digest();
                StringBuilder strHexString = new StringBuilder();
                for (byte aByteBuffer : byteBuffer) {
                    String hex = Integer.toHexString(0xff & aByteBuffer);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                strResult = strHexString.toString().getBytes();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
=======
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SHAEncrypt {

  public static byte[] SHA(final byte[] strText, final String strType) {
    byte[] strResult = null;
    if (strText != null && strText.length > 0) {
      try {
        MessageDigest messageDigest = MessageDigest.getInstance(strType);
        messageDigest.update(strText);
        byte byteBuffer[] = messageDigest.digest();
        StringBuilder strHexString = new StringBuilder();
        for (byte aByteBuffer : byteBuffer) {
          String hex = Integer.toHexString(0xff & aByteBuffer);
          if (hex.length() == 1) {
            strHexString.append('0');
          }
          strHexString.append(hex);
        }
        strResult = strHexString.toString().getBytes();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
    return strResult;
  }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
