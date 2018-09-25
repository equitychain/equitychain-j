package com.passport.db.transaction;

import org.rocksdb.Snapshot;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName RocksBackup
 * @Description TODO
 * @Author 岳东方
 * @Date 上午9:25
 **/
public class RocksBackup implements Serializable {

    public Snapshot snapshots ;
    public List keySets = new LinkedList();
    public String url ="";

    public Snapshot getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(Snapshot snapshots) {
        this.snapshots = snapshots;
    }

    public List getKeySets() {
        return keySets;
    }

    public void setKeySets(List keySets) {
        this.keySets = keySets;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
