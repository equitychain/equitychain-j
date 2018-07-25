package com.passport.core;

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
}
