package com.passport.core;

<<<<<<< HEAD
import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.crypto.ECDSAUtil;
import com.passport.utils.GsonUtils;

/**
 * 区块头信息
 * @author: xujianfeng
 * @create: 2018-07-19 15:58
 **/
@EntityClaz(name = "blockHeader")
public class BlockHeader {
    @FaildClaz(name = "timeStamp",type = Long.class)
    private Long timeStamp;
    @FaildClaz(name = "hashPrevBlock",type = byte[].class)
    private byte[] hashPrevBlock;
    @FaildClaz(name = "hashMerkleRoot",type = byte[].class)
    private byte[] hashMerkleRoot;
    @KeyField
    @FaildClaz(name = "hash",type = byte[].class)
    private byte[] hash;
    @FaildClaz(name = "version",type = byte[].class)
    private byte[] version;
    @FaildClaz(name = "eggMax",type = long.class)
    private long eggMax;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getHashPrevBlock() {
        return hashPrevBlock;
    }

    public void setHashPrevBlock(byte[] hashPrevBlock) {
        this.hashPrevBlock = hashPrevBlock;
    }

    public byte[] getHashMerkleRoot() {
        return hashMerkleRoot;
    }

    public void setHashMerkleRoot(byte[] hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getVersion(){
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public long getEggMax() {
        return eggMax;
    }

    public void setEggMax(long eggMax) {
        this.eggMax = eggMax;
    }

    public void calculateHash () {
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setTimeStamp(this.timeStamp);
        blockHeader.setHashPrevBlock(this.hashPrevBlock);
        blockHeader.setHashMerkleRoot(this.hashMerkleRoot);
        //this.hash = Hash.sha3(GsonUtils.toJson(blockHeader).getBytes());
        this.hash = ECDSAUtil.applySha256(GsonUtils.toJson(blockHeader)).getBytes();
    }
=======
import com.passport.crypto.eth.Hash;
import com.passport.utils.rpc.SerializationUtil;


public class BlockHeader {

  private Long timeStamp;
  private byte[] hashPrevBlock;
  private byte[] hashMerkleRoot;
  private byte[] hash;
  private byte[] version;
  private long eggMax;

  public Long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public byte[] getHashPrevBlock() {
    return hashPrevBlock;
  }

  public void setHashPrevBlock(byte[] hashPrevBlock) {
    this.hashPrevBlock = hashPrevBlock;
  }

  public byte[] getHashMerkleRoot() {
    return hashMerkleRoot;
  }

  public void setHashMerkleRoot(byte[] hashMerkleRoot) {
    this.hashMerkleRoot = hashMerkleRoot;
  }

  public byte[] getHash() {
    return hash;
  }

  public void setHash(byte[] hash) {
    this.hash = hash;
  }

  public byte[] getVersion() {
    return version;
  }

  public void setVersion(byte[] version) {
    this.version = version;
  }

  public long getEggMax() {
    return eggMax;
  }

  public void setEggMax(long eggMax) {
    this.eggMax = eggMax;
  }

  public void calculateHash() {
    BlockHeader blockHeader = new BlockHeader();
    blockHeader.setTimeStamp(this.timeStamp);
    blockHeader.setHashPrevBlock(this.hashPrevBlock);
    blockHeader.setHashMerkleRoot(this.hashMerkleRoot);
    this.hash = Hash.sha3(SerializationUtil.serialize(blockHeader));
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
