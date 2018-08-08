package com.passport.core;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static com.passport.constant.Constant.BYTE_ADDRESS_LENGTH;
import static com.passport.constant.Constant.BYTE_HASH_LENGTH;


/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
public class Transaction {

  private static Logger logger = LoggerFactory.getLogger(Transaction.class);
  private static final BigInteger DEFAULT_EGG_PRICE = new BigInteger("100000000");
  private static final BigInteger DEFAULT_EGG_AMOUNT = new BigInteger("18000");

  private Integer nonce;

  //32 byte
  private byte[] hash;

  private byte[] signature;

  private byte[] value;

  private byte[] payAddress;

  private byte[] receiptAddress;

  private byte[] eggPrice;

  private byte[] eggMax;

  private byte[] time;

  private byte[] extarData;

  private byte[] publicKey;


  public Integer getNonce() {
    return nonce;
  }

  public void setNonce(Integer nonce) {
    this.nonce = nonce;
  }

  public byte[] getHash() {
    return hash;
  }

  public void setHash(byte[] hash) {
    this.hash = hash;
  }

  public byte[] getSignature() {
    return signature;
  }

  public void setSignature(byte[] signature) {
    this.signature = signature;
  }

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }

  public byte[] getPayAddress() {
    return payAddress;
  }

  public void setPayAddress(byte[] payAddress) {
    this.payAddress = payAddress;
  }

  public byte[] getReceiptAddress() {
    return receiptAddress;
  }

  public void setReceiptAddress(byte[] receiptAddress) {
    this.receiptAddress = receiptAddress;
  }

  public byte[] getEggPrice() {
    return eggPrice;
  }

  public void setEggPrice(byte[] eggPrice) {
    this.eggPrice = eggPrice;
  }

  public byte[] getEggMax() {
    return eggMax;
  }

  public void setEggMax(byte[] eggMax) {
    this.eggMax = eggMax;
  }

  public byte[] getTime() {
    return time;
  }

  public void setTime(byte[] time) {
    this.time = time;
  }

  public byte[] getExtarData() {
    return extarData;
  }

  public void setExtarData(byte[] extarData) {
    this.extarData = extarData;
  }

  public byte[] getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey;
  }

  public Transaction(Integer nonce, byte[] hash, byte[] signature, byte[] value, byte[] payAddress,
                     byte[] receiptAddress, byte[] eggPrice, byte[] eggMax, byte[] time, byte[] extarData) {
    this.nonce = nonce;
    this.hash = hash;
    this.signature = signature;
    this.value = value;
    this.payAddress = payAddress;
    this.receiptAddress = receiptAddress;
    this.eggPrice = eggPrice;
    this.eggMax = eggMax;
    this.time = time;
    this.extarData = extarData;
  }

  public Transaction() {

  }

  private void validate(){
    if (this.hash.length != BYTE_HASH_LENGTH) logger.info("1");
    if(this.payAddress.length != BYTE_ADDRESS_LENGTH) logger.info("2");
    if(this.receiptAddress.length != BYTE_ADDRESS_LENGTH) logger.info("3");
  }
}
