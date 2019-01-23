package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/24.
 */
<<<<<<< HEAD
public final class LittleEndian
{
  /**
   * Encodes the given {@code int} value using little-endian byte ordering
   * convention.
   *
   * @param n the {@code int} value to encode.
   *
   * @return the encoded value.
   */
  public static byte[] encode(int n)
  {
=======
public final class LittleEndian {

  private LittleEndian() {
    /* ... */
  }

  /**
   * Encodes the given {@code int} value using little-endian byte ordering convention.
   *
   * @param n the {@code int} value to encode.
   * @return the encoded value.
   */
  public static byte[] encode(int n) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    byte[] out = new byte[4];
    encode(n, out, 0);
    return out;
  }

  /**
<<<<<<< HEAD
   * Encodes the given {@code int} value using little-endian byte ordering
   * convention into the given array, starting at the given offset.
=======
   * Encodes the given {@code int} value using little-endian byte ordering convention into the given
   * array, starting at the given offset.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   *
   * @param n the {@code int} value to encode.
   * @param out the output buffer.
   * @param off the output offset.
<<<<<<< HEAD
   *
   * @throws NullPointerException if {@code out} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code out}'s length is lower than {@code off + 4}.
   */
  public static void encode(int n, byte[] out, int off)
  {
=======
   * @throws NullPointerException if {@code out} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code out}'s length is
   * lower than {@code off + 4}.
   */
  public static void encode(int n, byte[] out, int off) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    out[off] = (byte) n;
    out[off + 1] = (byte) (n >>> 8);
    out[off + 2] = (byte) (n >>> 16);
    out[off + 3] = (byte) (n >>> 24);
  }

  /**
<<<<<<< HEAD
   * Encodes the given {@code long} value using little-endian byte
   * ordering convention.
   *
   * @param n the {@code long} value to encode.
   *
   * @return the encoded value.
   */
  public static byte[] encode(long n)
  {
=======
   * Encodes the given {@code long} value using little-endian byte ordering convention.
   *
   * @param n the {@code long} value to encode.
   * @return the encoded value.
   */
  public static byte[] encode(long n) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    byte[] out = new byte[8];
    encode(n, out, 0);
    return out;
  }

  /**
<<<<<<< HEAD
   * Encodes the given {@code long} value using little-endian byte
   * ordering convention into the given array, starting at the given
   * offset.
=======
   * Encodes the given {@code long} value using little-endian byte ordering convention into the
   * given array, starting at the given offset.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   *
   * @param n the {@code long} value to encode.
   * @param out the output buffer.
   * @param off the output offset.
<<<<<<< HEAD
   *
   * @throws NullPointerException if {@code out} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code out}'s length is lower than {@code off + 8}.
   */
  public static void encode(long n, byte[] out, int off)
  {
=======
   * @throws NullPointerException if {@code out} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code out}'s length is
   * lower than {@code off + 8}.
   */
  public static void encode(long n, byte[] out, int off) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    out[off] = (byte) n;
    out[off + 1] = (byte) (n >>> 8);
    out[off + 2] = (byte) (n >>> 16);
    out[off + 3] = (byte) (n >>> 24);
    out[off + 4] = (byte) (n >>> 32);
    out[off + 5] = (byte) (n >>> 40);
    out[off + 6] = (byte) (n >>> 48);
    out[off + 7] = (byte) (n >>> 56);
  }

  /**
<<<<<<< HEAD
   * Decodes the first 4 bytes of the given array into an {@code int}
   * value using little-endian byte ordering convention.
   *
   * @param in the encoded value.
   *
   * @return the decoded {@code int} value.
   *
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code in}'s length is lower
   *	than {@code 4}.
   */
  public static int decodeInt(byte[] in)
  {
=======
   * Decodes the first 4 bytes of the given array into an {@code int} value using little-endian byte
   * ordering convention.
   *
   * @param in the encoded value.
   * @return the decoded {@code int} value.
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code in}'s length is lower than {@code 4}.
   */
  public static int decodeInt(byte[] in) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return decodeInt(in, 0);
  }

  /**
<<<<<<< HEAD
   * Decodes the first 4 bytes starting at {@code off} of the given array
   * into an {@code int} value using little-endian byte ordering
   * convention.
   *
   * @param in the encoded value.
   * @param off the input offset.
   *
   * @return the decoded {@code int} value.
   *
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code in}'s length is lower than {@code off + 4}.
   */
  public static int decodeInt(byte[] in, int off)
  {
=======
   * Decodes the first 4 bytes starting at {@code off} of the given array into an {@code int} value
   * using little-endian byte ordering convention.
   *
   * @param in the encoded value.
   * @param off the input offset.
   * @return the decoded {@code int} value.
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code in}'s length is lower
   * than {@code off + 4}.
   */
  public static int decodeInt(byte[] in, int off) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return (in[off] & 0xFF)
        | ((in[off + 1] & 0xFF) << 8)
        | ((in[off + 2] & 0xFF) << 16)
        | ((in[off + 3] & 0xFF) << 24);
  }

  /**
<<<<<<< HEAD
   * Decodes the first 8 bytes of the given array into a {@code long}
   * value using little-endian byte ordering convention.
   *
   * @param in the encoded value.
   *
   * @return the decoded {@code long} value.
   *
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code in}'s length is lower
   *	than {@code 8}.
   */
  public static long decodeLong(byte[] in)
  {
=======
   * Decodes the first 8 bytes of the given array into a {@code long} value using little-endian byte
   * ordering convention.
   *
   * @param in the encoded value.
   * @return the decoded {@code long} value.
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code in}'s length is lower than {@code 8}.
   */
  public static long decodeLong(byte[] in) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return decodeLong(in, 0);
  }

  /**
<<<<<<< HEAD
   * Decodes the first 8 bytes starting at {@code off} of the given array
   * into a {@code long} value using little-endian byte ordering
   * convention.
   *
   * @param in the encoded value.
   * @param off the input offset.
   *
   * @return the decoded {@code long} value.
   *
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code in}'s length is lower than {@code off + 8}.
   */
  public static long decodeLong(byte[] in, int off)
  {
=======
   * Decodes the first 8 bytes starting at {@code off} of the given array into a {@code long} value
   * using little-endian byte ordering convention.
   *
   * @param in the encoded value.
   * @param off the input offset.
   * @return the decoded {@code long} value.
   * @throws NullPointerException if {@code in} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code in}'s length is lower
   * than {@code off + 8}.
   */
  public static long decodeLong(byte[] in, int off) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return (long) (in[off] & 0xFF)
        | ((long) (in[off + 1] & 0xFF) << 8)
        | ((long) (in[off + 2] & 0xFF) << 16)
        | ((long) (in[off + 3] & 0xFF) << 24)
        | ((long) (in[off + 4] & 0xFF) << 32)
        | ((long) (in[off + 5] & 0xFF) << 40)
        | ((long) (in[off + 6] & 0xFF) << 48)
        | ((long) (in[off + 7] & 0xFF) << 56);
  }
<<<<<<< HEAD

  private LittleEndian()
  {
		/* ... */
  }
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}