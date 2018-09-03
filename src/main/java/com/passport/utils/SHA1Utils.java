package com.passport.utils;

import java.security.MessageDigest;


public final class SHA1Utils {

  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * Takes the raw bytes from the digest and formats them correct.
   *
   * @param bytes the raw bytes from the digest.
   * @return the formatted bytes.
   */
  private static String getFormattedText(byte[] bytes) {
    int len = bytes.length;
    StringBuilder buf = new StringBuilder(len * 2);

    for (int j = 0; j < len; j++) {
      buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
      buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
    }
    return buf.toString();
  }

  public static String encode(String str) {
    if (str == null) {
      return null;
    }
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      messageDigest.update(str.getBytes("UTF-8"));
      return getFormattedText(messageDigest.digest());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public static String encode(String secretKey, String msg) {
    if (secretKey == null || msg == null) {
      return null;
    }
    String result = secretKey + msg + secretKey;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      messageDigest.update(result.getBytes("UTF-8"));
      return getFormattedText(messageDigest.digest());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
