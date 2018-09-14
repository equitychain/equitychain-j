package com.passport.core;



import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static com.passport.constant.Constant.BYTE_ADDRESS_LENGTH;
import static com.passport.constant.Constant.BYTE_HASH_LENGTH;


/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
@EntityClaz(name = "transaction")
public class Transaction {

  private static Logger logger = LoggerFactory.getLogger(Transaction.class);
  private static final BigInteger DEFAULT_EGG_PRICE = new BigInteger("100000000");
  private static final BigInteger DEFAULT_EGG_AMOUNT = new BigInteger("18000");

  @FaildClaz(name = "nonce",type = Integer.class)
  private Integer nonce;

  //32 byte
  @KeyField
  @FaildClaz(name = "hash",type = byte[].class)
  private byte[] hash;
  @FaildClaz(name = "signature",type = byte[].class)
  private byte[] signature;
  @FaildClaz(name = "value",type = byte[].class)
  private byte[] value;
  @FaildClaz(name = "payAddress",type = byte[].class)
  private byte[] payAddress;

  @FaildClaz(name = "receiptAddress",type = byte[].class)
  private byte[] receiptAddress;
  @FaildClaz(name = "eggPrice",type = byte[].class)
  private byte[] eggPrice;
  @FaildClaz(name = "eggMax",type = byte[].class)
  private byte[] eggMax;
  @FaildClaz(name = "time",type = byte[].class)
  private byte[] time;

  @FaildClaz(name = "extarData",type = byte[].class)
  private byte[] extarData;

  @FaildClaz(name = "publicKey",type = byte[].class)
  private byte[] publicKey;

  @FaildClaz(name = "eggUsed",type = byte[].class)
  private byte[] eggUsed;
  @FaildClaz(name = "tradeType",type = byte[].class)
  private byte[] tradeType;//交易类型
  @FaildClaz(name = "blockHeight",type = byte[].class)
  private byte[] blockHeight;//已确认流水打包到哪个区块
  //流水状态（等待 0， 成功 1， 失败 2）
  @FaildClaz(name = "status",type = Integer.class)
  private Integer status;

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

  public byte[] getEggUsed() {
    return eggUsed;
  }

  public void setEggUsed(byte[] eggUsed) {
    this.eggUsed = eggUsed;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public byte[] getTradeType() {
    return tradeType;
  }

  public void setTradeType(byte[] tradeType) {
    this.tradeType = tradeType;
  }

  public byte[] getBlockHeight() {
    return blockHeight;
  }

  public void setBlockHeight(byte[] blockHeight) {
    this.blockHeight = blockHeight;
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
