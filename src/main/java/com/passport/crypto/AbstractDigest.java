package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
<<<<<<< HEAD
public abstract class AbstractDigest implements Digest{
=======
public abstract class AbstractDigest implements Digest {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

  private final String name;
  private final int length;

  /**
   * Creates a new {@code AbstractDigest}.
   *
   * @param name the digest algorithm's name.
   * @param length the digest's length in bytes.
   */
<<<<<<< HEAD
  AbstractDigest(String name, int length)
  {
=======
  AbstractDigest(String name, int length) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    this.name = name;
    this.length = length;
  }

  @Override
<<<<<<< HEAD
  public int length()
  {
=======
  public int length() {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return length;
  }

  @Override
<<<<<<< HEAD
  public Digest update(byte... input)
  {
=======
  public Digest update(byte... input) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return update(input, 0, input.length);
  }

  @Override
<<<<<<< HEAD
  public byte[] digest(byte... input)
  {
=======
  public byte[] digest(byte... input) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return update(input, 0, input.length).digest();
  }

  @Override
<<<<<<< HEAD
  public byte[] digest(byte[] input, int off, int len)
  {
=======
  public byte[] digest(byte[] input, int off, int len) {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return update(input, off, len).digest();
  }

  @Override
<<<<<<< HEAD
  public String toString()
  {
=======
  public String toString() {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    return name;
  }

}
