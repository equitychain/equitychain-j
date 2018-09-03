package com.passport.db.exception;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public class NullNameException extends RuntimeException {

  public NullNameException() {
  }

  public NullNameException(String message) {
    super(message);
  }
}
