package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
public class Keccak extends AbstractDigest {

  private static final long[] RC = new long[] {
      0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
      0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
      0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
      0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
      0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
      0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
      0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
      0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
  };
  private static final int[] R = new int[] {
      0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43,
      25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14
  };

  private final long[] A;
  private final long[] B;
  private final long[] C;
  private final long[] D;
  private final int blockLen;
  private final byte[] buffer;
  private int bufferLen;

  /**
   * Creates a new ready to use {@code Keccak}.
   *
   * @param length the digest length (in bytes).
   *
   * @throws IllegalArgumentException if {@code length} is not one of 28,
   *	32, 48 or 64.
   */
  Keccak(int length)
  {
    super("Keccak-" + length * 8, length);
    checkCondition(length == 28 || length == 32
        || length == 48 || length == 64);
    this.A = new long[25];
    this.B = new long[25];
    this.C = new long[5];
    this.D = new long[5];
    this.blockLen = 200 - 2 * length;
    this.buffer = new byte[blockLen];
    this.bufferLen = 0;
  }

  @Override
  public Digest reset() {
    for (int i = 0; i < 25; i++) {
      A[i] = 0L;
    }
    bufferLen = 0;
    return this;
  }

  @Override
  public Digest update(byte input) {
    return null;
  }

  @Override
  public Digest update(byte[] input, int off, int len) {
    return null;
  }

  @Override
  public byte[] digest() {
    return new byte[0];
  }

  private long rot(long w, int r)
  {
    return Long.rotateLeft(w, r);
  }

  private int index(int x)
  {
    return x < 0 ? index(x + 5) : x % 5;
  }

  private int index(int x, int y)
  {
    return index(x) + 5 * index(y);
  }

  private void checkCondition(boolean condition)
  {
    if (!condition) {
      throw new IllegalArgumentException();
    }
  }
}
