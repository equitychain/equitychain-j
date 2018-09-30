package com.passport.dto.coreobject;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
public class Transaction {
  private Integer nonce;
  private Object hash;
  private Object signature;
  private Object value;
  private Object payAddress;
  private Object receiptAddress;
  private Object eggPrice;
  private Object eggMax;
  private Object time;
  private Object extarData;
  private Object publicKey;
  private Object eggUsed;
  private Object tradeType;//交易类型
  private Object blockHeight;//已确认流水打包到哪个区块
  private Integer status;
  private Object confirms;
  private Object fee;
  private Object tokenName;

  public Object getTokenName() {
    return tokenName;
  }

  public void setTokenName(Object tokenName) {
    this.tokenName = tokenName;
  }

  public Object getFee() {
    return fee;
  }

  public void setFee(Object fee) {
    this.fee = fee;
  }

  public Object getConfirms() {
    return confirms;
  }

  public void setConfirms(Object confirms) {
    this.confirms = confirms;
  }

  public Integer getNonce() {
    return nonce;
  }

  public void setNonce(Integer nonce) {
    this.nonce = nonce;
  }

  public Object getHash() {
    return hash;
  }

  public void setHash(Object hash) {
    this.hash = hash;
  }

  public Object getSignature() {
    return signature;
  }

  public void setSignature(Object signature) {
    this.signature = signature;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Object getPayAddress() {
    return payAddress;
  }

  public void setPayAddress(Object payAddress) {
    this.payAddress = payAddress;
  }

  public Object getReceiptAddress() {
    return receiptAddress;
  }

  public void setReceiptAddress(Object receiptAddress) {
    this.receiptAddress = receiptAddress;
  }

  public Object getEggPrice() {
    return eggPrice;
  }

  public void setEggPrice(Object eggPrice) {
    this.eggPrice = eggPrice;
  }

  public Object getEggMax() {
    return eggMax;
  }

  public void setEggMax(Object eggMax) {
    this.eggMax = eggMax;
  }

  public Object getTime() {
    return time;
  }

  public void setTime(Object time) {
    this.time = time;
  }

  public Object getExtarData() {
    return extarData;
  }

  public void setExtarData(Object extarData) {
    this.extarData = extarData;
  }

  public Object getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(Object publicKey) {
    this.publicKey = publicKey;
  }

  public Object getEggUsed() {
    return eggUsed;
  }

  public void setEggUsed(Object eggUsed) {
    this.eggUsed = eggUsed;
  }

  public Object getTradeType() {
    return tradeType;
  }

  public void setTradeType(Object tradeType) {
    this.tradeType = tradeType;
  }

  public Object getBlockHeight() {
    return blockHeight;
  }

  public void setBlockHeight(Object blockHeight) {
    this.blockHeight = blockHeight;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
