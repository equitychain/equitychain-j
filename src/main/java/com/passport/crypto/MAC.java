package com.passport.crypto;

/**
 * @author Wu Created by SKINK on 2018/7/17.
 */
public interface MAC {


  /**
   * Returns the MAC's length (in bytes).
   *
   * @return the MAC's length (in bytes).
   */
  int length();

  /**
   * Resets the engine.
   *
   * @return this object.
   */
  MAC reset();

  /**
   * Updates the MAC using the given byte.
   *
   * @param input the byte with which to update the MAC.
<<<<<<< HEAD
   *
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   * @return this object.
   */
  MAC update(byte input);

  /**
   * Updates the MAC using the specified array of bytes.
   *
   * @param input the array of bytes with which to update the MAC.
<<<<<<< HEAD
   *
   * @return this object.
   *
=======
   * @return this object.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   * @throws NullPointerException if {@code input} is {@code null}.
   */
  MAC update(byte... input);

  /**
<<<<<<< HEAD
   * Updates the MAC using the specified number of bytes from the given
   * array of bytes, starting at the specified offset.
=======
   * Updates the MAC using the specified number of bytes from the given array of bytes, starting at
   * the specified offset.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   *
   * @param input the array of bytes.
   * @param off the offset to start from in the array of bytes, inclusive.
   * @param len the number of bytes to use, starting at offset.
<<<<<<< HEAD
   *
   * @return this object.
   *
   * @throws NullPointerException if {@code input} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code off + len} is greater than {@code input}'s length.
=======
   * @return this object.
   * @throws NullPointerException if {@code input} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code off + len} is greater
   * than {@code input}'s length.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   */
  MAC update(byte[] input, int off, int len);

  /**
<<<<<<< HEAD
   * Completes the MAC computation. Note that the engine is reset after
   * this call is made.
=======
   * Completes the MAC computation. Note that the engine is reset after this call is made.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   *
   * @return the resulting MAC.
   */
  byte[] digest();

  /**
<<<<<<< HEAD
   * Performs a final update on the MAC using the specified array of
   * bytes, then completes the MAC computation. That is, this method first
   * calls {@link #update(byte...)}, passing the input array to the update
   * method, then calls {@link #digest()}. Note that the engine is reset
   * after this call is made.
   *
   * @param input the array of bytes with which to update the MAC before
   *	completing its computation.
   *
   * @return the resulting MAC.
   *
=======
   * Performs a final update on the MAC using the specified array of bytes, then completes the MAC
   * computation. That is, this method first calls {@link #update(byte...)}, passing the input array
   * to the update method, then calls {@link #digest()}. Note that the engine is reset after this
   * call is made.
   *
   * @param input the array of bytes with which to update the MAC before completing its
   * computation.
   * @return the resulting MAC.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   * @throws NullPointerException if {@code input} is {@code null}.
   */
  byte[] digest(byte... input);

  /**
<<<<<<< HEAD
   * Performs a final update on the MAC using the specified data bytes,
   * then completes the MAC computation. That is, this method first calls
   * {@link #update(byte[], int, int)}, passing the input array to the
   * update method, then calls {@link #digest()}. Note that the engine is
   * reset after this call is made.
=======
   * Performs a final update on the MAC using the specified data bytes, then completes the MAC
   * computation. That is, this method first calls {@link #update(byte[], int, int)}, passing the
   * input array to the update method, then calls {@link #digest()}. Note that the engine is reset
   * after this call is made.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   *
   * @param input the array of bytes.
   * @param off the offset to start from in the array of bytes, inclusive.
   * @param len the number of bytes to use, starting at {@code off}.
<<<<<<< HEAD
   *
   * @return the resulting MAC.
   *
   * @throws NullPointerException if {@code input} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if
   *	{@code off + len} is greater than {@code input}'s length.
=======
   * @return the resulting MAC.
   * @throws NullPointerException if {@code input} is {@code null}.
   * @throws IndexOutOfBoundsException if {@code off} is negative or if {@code off + len} is greater
   * than {@code input}'s length.
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   */
  byte[] digest(byte[] input, int off, int len);

}
