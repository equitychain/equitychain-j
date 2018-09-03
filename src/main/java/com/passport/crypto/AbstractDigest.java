package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
public abstract class AbstractDigest implements Digest {

  private final String name;
  private final int length;

  /**
   * Creates a new {@code AbstractDigest}.
   *
   * @param name the digest algorithm's name.
   * @param length the digest's length in bytes.
   */
  AbstractDigest(String name, int length) {
    this.name = name;
    this.length = length;
  }

  @Override
  public int length() {
    return length;
  }

  @Override
  public Digest update(byte... input) {
    return update(input, 0, input.length);
  }

  @Override
  public byte[] digest(byte... input) {
    return update(input, 0, input.length).digest();
  }

  @Override
  public byte[] digest(byte[] input, int off, int len) {
    return update(input, off, len).digest();
  }

  @Override
  public String toString() {
    return name;
  }

}
