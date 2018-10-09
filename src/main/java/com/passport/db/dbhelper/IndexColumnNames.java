package com.passport.db.dbhelper;

import org.rocksdb.ColumnFamilyDescriptor;

public enum IndexColumnNames {
    TRANSTIMEINDEX("transactionTime-index","transactionTime-overAndNext"),
    VOTERNUMBEROFVOTE("voterVoteNum-index","voterVoteNum-overAndNext"),
    VOTERECORDVOTENUMBER("voteRecordVoteNum-index","voteRecordVoteNum-overAndNext"),
    VOTERECORDTIME("voteRecordTime-index","voteRecordTime-overAndNext"),
    TRUSTEEVOTESINDEX("trusteeVotes-index","trusteeVotes-overAndNext"),
    TRANSBLOCKHEIGHTINDEX("transactionBlockHeight-index","transactionBlockHeight-overAndNext");
    protected String indexName;
    protected String overAndNextName;
    IndexColumnNames(String indexName,String overAndNextName){
        this.indexName = indexName;
        this.overAndNextName = overAndNextName;
    }

    public ColumnFamilyDescriptor getIndexName() {
        return new ColumnFamilyDescriptor(indexName.getBytes());
    }

    public ColumnFamilyDescriptor getOverAndNextName() {
        return new ColumnFamilyDescriptor(overAndNextName.getBytes());
    }
}
