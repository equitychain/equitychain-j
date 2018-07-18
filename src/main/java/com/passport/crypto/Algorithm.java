package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
public final class Algorithm<T> {

  /** The MD2 digest algorithm. */
  public static final Algorithm<Digest> MD2 = new Algorithm<Digest>("MD2");

  /** The MD4 digest algorithm. */
  public static final Algorithm<Digest> MD4 = new Algorithm<Digest>("MD4");

  /** The MD5 digest algorithm. */
  public static final Algorithm<Digest> MD5 = new Algorithm<Digest>("MD5");

  /** The SHA1 digest algorithm. */
  public static final Algorithm<Digest> SHA1 = new Algorithm<Digest>("SHA1");

  /** The SHA-256 digest algorithm. */
  public static final Algorithm<Digest> SHA256 = new Algorithm<Digest>("SHA-256");

  /** The SHA-512 digest algorithm. */
  public static final Algorithm<Digest> SHA512 = new Algorithm<Digest>("SHA-512");

  /** The Keccak-224 digest algorithm. */
  public static final Algorithm<Digest> KECCAK224 = new Algorithm<Digest>("Keccak-224");

  /** The Keccak-256 digest algorithm. */
  public static final Algorithm<Digest> KECCAK256 = new Algorithm<Digest>("Keccak-256");

  /** The Keccak-384 digest algorithm. */
  public static final Algorithm<Digest> KECCAK384 = new Algorithm<Digest>("Keccak-384");

  /** The Keccak-512 digest algorithm. */
  public static final Algorithm<Digest> KECCAK512 = new Algorithm<Digest>("Keccak-512");

  /** The HMAC-MD2 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_MD2 = new Algorithm<MAC>("HMAC-MD2");

  /** The HMAC-MD4 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_MD4 = new Algorithm<MAC>("HMAC-MD4");

  /** The HMAC-MD5 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_MD5 = new Algorithm<MAC>("HMAC-MD5");

  /** The HMAC-SHA1 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_SHA1 = new Algorithm<MAC>("HMAC-SHA1");

  /** The HMAC-SHA-256 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_SHA256 = new Algorithm<MAC>("HMAC-SHA-256");

  /** The HMAC-SHA-512 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_SHA512 = new Algorithm<MAC>("HMAC-SHA-512");

  /** The HMAC-Keccak-224 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_KECCAK224 = new Algorithm<MAC>("HMAC-Keccak-224");

  /** The HMAC-Keccak-256 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_KECCAK256 = new Algorithm<MAC>("HMAC-Keccak-256");

  /** The HMAC-Keccak-384 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_KECCAK384 = new Algorithm<MAC>("HMAC-Keccak-384");

  /** The HMAC-Keccak-512 MAC algorithm. */
  public static final Algorithm<MAC> HMAC_KECCAK512 = new Algorithm<MAC>("HMAC-Keccak-512");

  private final String name;

  private Algorithm(String name)
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return name;
  }

}
