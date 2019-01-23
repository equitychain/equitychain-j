package com.passport.utils;

import com.passport.core.Block;
<<<<<<< HEAD

import java.security.MessageDigest;

public class HashUtils {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String getHash(Block block) throws Exception {
        byte[] merkleRoot = block.getBlockHeader().getHashMerkleRoot();
        long height = block.getBlockHeight();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update((new String(merkleRoot)+height).getBytes("UTF-8"));
        return getFormattedText(messageDigest.digest());
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
=======
import java.security.MessageDigest;

public class HashUtils {

  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String getHash(Block block) throws Exception {
    byte[] merkleRoot = block.getBlockHeader().getHashMerkleRoot();
    long height = block.getBlockHeight();

    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.update((new String(merkleRoot) + height).getBytes("UTF-8"));
    return getFormattedText(messageDigest.digest());
  }

  private static String getFormattedText(byte[] bytes) {
    int len = bytes.length;
    StringBuilder buf = new StringBuilder(len * 2);

    for (int j = 0; j < len; j++) {
      buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
      buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
    }
    return buf.toString();
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
