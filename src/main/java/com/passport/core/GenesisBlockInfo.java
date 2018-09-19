package com.passport.core;

import java.util.List;

/**
 * 创世块信息
 * @author: xujianfeng
 * @create: 2018-09-05 13:57
 **/
public class GenesisBlockInfo {
    private List<Account> accounts;
    private List<Transaction> transactions;
    private List<Trustee> trustees;
    private List<VoteRecord> voteRecords;

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Trustee> getTrustees() {
        return trustees;
    }

    public void setTrustees(List<Trustee> trustees) {
        this.trustees = trustees;
    }

    public List<VoteRecord> getVoteRecords() {
        return voteRecords;
    }

    public void setVoteRecords(List<VoteRecord> voteRecords) {
        this.voteRecords = voteRecords;
    }
}
