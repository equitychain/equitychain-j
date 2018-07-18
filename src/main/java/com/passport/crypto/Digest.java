package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
public interface Digest {

  int length();


  Digest reset();


  Digest update(byte input);


  Digest update(byte... input);


  Digest update(byte[] input, int off, int len);


  byte[] digest();


  byte[] digest(byte... input);


  byte[] digest(byte[] input, int off, int len);


}
