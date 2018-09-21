package com.passport.dto.coreobject;

/**
 * 区块头信息
 * @author: xujianfeng
 * @create: 2018-07-19 15:58
 **/
public class BlockHeader {
    private Long timeStamp;
    private Object hashPrevBlock;
    private Object hashMerkleRoot;
    private Object hash;
    private Object version;
    private long eggMax;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Object getHashPrevBlock() {
        return hashPrevBlock;
    }

    public void setHashPrevBlock(Object hashPrevBlock) {
        this.hashPrevBlock = hashPrevBlock;
    }

    public Object getHashMerkleRoot() {
        return hashMerkleRoot;
    }

    public void setHashMerkleRoot(Object hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public Object getHash() {
        return hash;
    }

    public void setHash(Object hash) {
        this.hash = hash;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public long getEggMax() {
        return eggMax;
    }

    public void setEggMax(long eggMax) {
        this.eggMax = eggMax;
    }
}
