package com.passport.core;



import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;


import static com.passport.constant.Constant.BYTE_HASH_LENGTH;
import static com.passport.constant.Constant.BYTE_ADDRESS_LENGTH;


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


  public static void main(String[] args) {
    System.out.println("2b99ad7a885fa11e75d899925642ec4d2174afc5".length());
    System.out.println(Hex.decode("2b99ad7a885fa11e75d899925642ec4d2174afc5").length);
  }



}
