package com.passport.core;

import com.passport.crypto.eth.Hash;
import com.passport.utils.rpc.SerializationUtil;

/**
 * 区块头信息
 * @author: xujianfeng
 * @create: 2018-07-19 15:58
 **/
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
        this.hash = Hash.sha3(SerializationUtil.serialize(blockHeader));
    }
}
