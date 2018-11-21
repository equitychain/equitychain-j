package com.passport.db.dbhelper;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.passport.constant.Constant;
import com.passport.core.*;
import com.passport.peer.ChannelsManager;
import com.passport.utils.NetworkTime;
import com.passport.utils.SerializeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Component
public class BaseDBRocksImpl extends BaseDBAccess {
    private static final Logger logger = LoggerFactory.getLogger(BaseDBRocksImpl.class);
    //存放节点列表
    private static final String CLIENT_NODES_LIST_KEY = "client-node-list";
    private static final String MINERACCOUNT = "miner_account";
    @Value("${db.dataDir}")
    private String dataDir;
    @Autowired
    private ChannelsManager channelsManager;

    public BaseDBRocksImpl() {

    }

    @Override
    @PostConstruct
    protected void initDB() {
        super.initDB();
    }

    /**
     * 放置最后一个区块高度，过期
     * 插入区块时会自动的
     *
     * @param lastBlock
     * @return
     */
    @Override
    public boolean putLastBlockHeight(Object lastBlock) {
        return true;
    }

    @Override
    public Optional<Object> getLastBlockHeight() {
        ColumnFamilyHandle handle = handleMap.get(getColName("block", "blockHeight"));
        RocksIterator heightIter;
//        if (transaction != null) {
//            heightIter = transaction.getIterator(new ReadOptions(),handle);
//        }else{
        heightIter = rocksDB.newIterator(handle);
//        }
        Long height = 0l;
        for (heightIter.seekToFirst(); heightIter.isValid(); heightIter.next()) {
            if (heightIter.key() == null) {
                continue;
            }
            Long curHeight = Long.parseLong(new String(heightIter.key()));
            if (curHeight > height) {
                height = curHeight;
            }
        }
        return Optional.of(height);
    }

    @Override
    public Optional<Object> getLastBlockHeightT() {
        ColumnFamilyHandle handle = handleMap.get(getColName("block", "blockHeight"));
        RocksIterator heightIter;
        heightIter = rocksDB.newIterator(handle);
        Long height = 0l;
        for (heightIter.seekToFirst(); heightIter.isValid(); heightIter.next()) {
            if (heightIter.key() == null) {
                continue;
            }
            Long curHeight = Long.parseLong(new String(heightIter.key()));
            if (curHeight > height) {
                height = curHeight;
            }
        }
        return Optional.of(height);
    }

    @Override
    public boolean putBlock(Block block) {
        try {
            addObj(block);
            //todo 区块的索引数据添加，要那些字段加索引
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Block> getBlock(Object blockHeight) {
        try {
            Block block = getObj("blockHeight", blockHeight.toString(), Block.class);
            if (block.isNullContent()) {
                return Optional.absent();
            }
            return Optional.of(block);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }

    @Override
    public Optional<Block> getLastBlock() {
        Optional heightOpti = getLastBlockHeight();
        if (heightOpti.isPresent()) {
            Long height = (Long) heightOpti.get();
            if (height != null) {
                return getBlock(height);
            }
        }
        return Optional.absent();
    }

    @Override
    public Optional<List<String>> getNodeList() {
        byte[] objByt = get(CLIENT_NODES_LIST_KEY.getBytes());
        if (objByt != null) {
            List<String> nodeList = (List<String>) SerializeUtils.unSerialize(objByt);
            if (nodeList != null) {
                return Optional.of(nodeList);
            }
        }
        return Optional.absent();
    }

    @Override
    public boolean putNodeList(List<String> nodes) {
        try {
            rocksDB.put(CLIENT_NODES_LIST_KEY.getBytes(), SerializeUtils.serialize(nodes));
            return true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean put(String key, Object value) {
        try {
            rocksDB.put(key.getBytes(), SerializeUtils.serialize(value));
            return true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Object> get(String key) {
        try {
            byte[] objByt = rocksDB.get(key.getBytes());
            if (objByt != null) {
                return Optional.of(SerializeUtils.unSerialize(objByt));
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public boolean delete(String key) {
        try {
            rocksDB.delete(key.getBytes());
            return true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Account> listAccounts() {
        RocksIterator accountIter;
//        if(transaction!=null){
//            accountIter = transaction.getIterator(new ReadOptions(),handleMap.get(getColName("account", "address")));
//        }else{
        accountIter = rocksDB.newIterator(handleMap.get(getColName("account", "address")));
//        }

        ArrayList<Account> accounts = new ArrayList<>();
        for (accountIter.seekToFirst(); accountIter.isValid(); accountIter.next()) {
            String address = new String(accountIter.key());
            try {
                Account account = getObj("address", address, Account.class);
                if (account != null) {
                    accounts.add(account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

//    @Override
//    public void delAllAccountIps() throws Exception {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "id")));
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            delObj("id", new String(iterator.key()), AccountIp.class, true);
//        }
//    }
//
//    @Override
//    public List<AccountIp> listAccountIps() throws Exception {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "id")));
//        List<AccountIp> list = new ArrayList<>();
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            list.add(getObj("id", new String(iterator.key()), AccountIp.class));
//        }
//        return list;
//    }
//    @Override
//    public void localAddNewAccountIp(String address) throws Exception {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "ipAddr")));
//        int statu = 0;
//        String localIp = HttpUtils.getLocalHostLANAddress().getHostAddress();
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            String ipAddr = new String(iterator.value());
//            if (localIp.equals(ipAddr)) {
//                statu = Integer.parseInt(new String(
//                        rocksDB.get(handleMap.get(getColName("accountIp", "statu")), iterator.key())));
//                break;
//            }
//        }
//        AccountIp ipInfo = new AccountIp();
//        ipInfo.setAddress(address);
//        ipInfo.setIpAddr(localIp);
//        ipInfo.setStatu(statu);
//        ipInfo.setId();
//        addObj(ipInfo);
//    }
    @Override
    public List<Account> getNodeAccountList() {
        RocksIterator accountIter;
        accountIter = rocksDB.newIterator(handleMap.get(getColName("account", "address")));

        ArrayList<Account> accounts = new ArrayList<>();
        for (accountIter.seekToFirst(); accountIter.isValid(); accountIter.next()) {
            String address = new String(accountIter.key());
            try {
                Account account = getObj("address", address, Account.class);
                if (account != null &&
                        account.getPrivateKey() != null && !"".equals(account.getPrivateKey())
                        && account.getPassword() != null && !"".equals(account.getPassword())) {
                    accounts.add(account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

//    @Override
//    public boolean accountHasOnlineIp(String address) throws RocksDBException {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "address")));
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            String addr = new String(iterator.value());
//            byte[] keyByt = rocksDB.get(handleMap.get(getColName("accountIp", "statu")), iterator.key());
//            int statu = Integer.parseInt(new String(keyByt));
//            if (address.equals(addr) && statu == 1) {
//                byte[] ipByt = rocksDB.get(handleMap.get(getColName("accountIp", "ipAddr")), iterator.key());
//                if (ipByt != null && ipByt.length > 0 && !"".equals(new String(ipByt))) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    @Override
//    public void saveLocalAccountIpInfo() throws Exception {
//        List<Account> accounts = getNodeAccountList();
//        if (accounts == null) {
//            accounts = new ArrayList<>();
//        }
//        //添加一个默认的,因为
//        Account defaultAcc = new Account();
//        defaultAcc.setAddress("defaultLocalAcc");
//        accounts.add(defaultAcc);
//        saveIpAccountInfos(HttpUtils.getLocalHostLANAddress().getHostAddress(), accounts, channelsManager.getChannels().size() == 0 ? 1 : 0);
//    }
//
//    @Override
//    public List<AccountIp> delAccountIpByAddr(String ip) throws Exception {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "ipAddr")));
//        ArrayList<AccountIp> ips = new ArrayList<>();
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            String addr = new String(iterator.value());
//            String id = new String(iterator.key());
//            if (ip.equals(addr)) {
//                ips.add(getObj("id", id, AccountIp.class));
//                delObj("id", id, AccountIp.class, true);
//            }
//        }
//        return ips;
//    }
//
//    @Override
//    public int getLocalAccountIpStatu() throws Exception {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "ipAddr")));
//        int statu = 0;
//        String localIp = HttpUtils.getLocalHostLANAddress().getHostName();
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            String ipAddr = new String(iterator.value());
//            if (localIp.equals(ipAddr)) {
//                statu = Integer.parseInt(new String(
//                        rocksDB.get(handleMap.get(getColName("accountIp", "statu")), iterator.key())));
//                break;
//            }
//        }
//        return statu;
//    }
//
//    @Override
//    public void saveIpAccountInfos(String address, List<Account> accounts, int statu) throws Exception {
//        for (Account account : accounts) {
//            AccountIp accountIp = new AccountIp();
//            accountIp.setAddress(account.getAddress());
//            accountIp.setIpAddr(address);
//            accountIp.setStatu(statu);
//            accountIp.setId();
//            addObj(accountIp);
//        }
//    }
//
//    @Override
//    public void setIpAccountStatu(String ipAddr, int statu) throws RocksDBException {
//        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("accountIp", "ipAddr")));
//        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//            String addr = new String(iterator.value());
//            if (ipAddr.equals(addr)) {
//                rocksDB.put(handleMap.get(getColName("accountIp", "statu")), iterator.key(), (statu + "").getBytes());
//            }
//        }
//    }

    @Override
    public boolean putAccount(Account account) {
        try {
            addObj(account);
            //todo 要加那些字段作为索引
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Account> getAccount(String address) {
        try {
            Account account = getObj("address", address, Account.class);
            if (account.isNullContent()) {
                return Optional.absent();
            }
            return Optional.of(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public boolean putUnconfirmTransaction(Transaction transaction) {
        try {
            byte[] blockHeight = transaction.getBlockHeight();
            if (blockHeight == null || blockHeight.length == 0) {
                addObj(transaction);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Transaction> getUnconfirmTransaction(String txHash) {
        try {
            Transaction transaction = getObj("hash", txHash, Transaction.class);
            if (transaction != null) {
                if (transaction.getBlockHeight() == null || transaction.getBlockHeight().length == 0) {
                    if (transaction.isNullContent()) {
                        return Optional.absent();
                    }
                    return Optional.of(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public void deleteUnconfirmTransaction(String txHash) {
        Optional<Transaction> transaction = getUnconfirmTransaction(txHash);
        if (transaction.isPresent()) {
            try {
                delObj("hash", txHash, Transaction.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Transaction> listUnconfirmTransactions() {
//        if (transaction.isDeadlockDetect()) {
//        }
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
        List<Transaction> transactions = new ArrayList<>();
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            String hash = new String(iterator.key());
            try {
                Transaction transaction = getObj("hash", hash, Transaction.class);
                if (transaction.getBlockHeight() == null || transaction.getBlockHeight().length == 0) {
                    if (!transaction.isNullContent()) {
                        transactions.add(transaction);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }

    @Override
    public boolean putConfirmTransaction(Transaction transaction) {
        try {
            byte[] blockHeight = transaction.getBlockHeight();
            Optional<Block> blockOptional = getBlock(new String(blockHeight));
            if (blockOptional != null && blockOptional.isPresent()) {
                addObj(transaction);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Transaction> getConfirmTransaction(String txHash) {
        try {
            Transaction transaction = getObj("hash", txHash, Transaction.class);
            if (transaction != null) {
                if (transaction.getBlockHeight() != null && transaction.getBlockHeight().length > 0) {
                    if (transaction.isNullContent()) {
                        return Optional.absent();
                    }
                    return Optional.of(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public List<Transaction> getAllTrans() {
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
        List<Transaction> result = new ArrayList<>();
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            try {
                Transaction transaction = getObj("hash", iterator.key(), Transaction.class);
                if (!transaction.isNullContent()) {
                    result.add(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Optional<Account> getMinerAccount() {
        Optional<Object> getMinerAccount = get(MINERACCOUNT.getBytes()) == null || get(MINERACCOUNT.getBytes()).length == 0 ? Optional.absent() : Optional.of(SerializeUtils.unSerialize(get(MINERACCOUNT.getBytes())));
        if (getMinerAccount != null && getMinerAccount.isPresent()) {
            return Optional.of((Account) getMinerAccount.get());
        }
        return Optional.absent();
    }

    @Override
    public boolean putMinerAccount(Account account) {
        return put(MINERACCOUNT.getBytes(), SerializeUtils.serialize(account));
    }

    @Override
    public boolean putTrustee(Trustee trustee) {
        try {
            addObj(trustee);
            removeIndexesKey(handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.indexName),
                    (trustee.getVotes() + "").getBytes(), trustee.getAddress().getBytes());
            //添加索引
            putIndexesKey(handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.indexName),
                    (trustee.getVotes() + "").getBytes(), trustee.getAddress().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.overAndNextName),
                    (trustee.getVotes() + "").getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Trustee> getTrustee(String address) {
        try {
            Trustee trustee = getObj("address", address, Trustee.class);
            if (trustee.isNullContent()) {
                return Optional.absent();
            }
            return Optional.of(trustee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public List<Trustee> listTrustees() {
        return trusteePagination(Constant.TRUSTEES_INIT_NUM, 1, 0, null, null);
    }

    @Override
    public List<Trustee> listTrustees(int count) {
        return trusteePagination(count, 1, 0, null, null);
    }

    @Override
    public boolean putVoter(Voter voter) {
        try {
            addObj(voter);
            removeIndexesKey(handleMap.get(IndexColumnNames.VOTERNUMBEROFVOTE.indexName),
                    voter.getVoteNum().toString().getBytes(), voter.getAddress().getBytes());
            putIndexesKey(handleMap.get(IndexColumnNames.VOTERNUMBEROFVOTE.indexName),
                    voter.getVoteNum().toString().getBytes(), voter.getAddress().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.VOTERNUMBEROFVOTE.overAndNextName),
                    voter.getVoteNum().toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Voter> getVoter(String address) {
        try {
            Voter voter = getObj("address", address, Voter.class);
            if (voter.isNullContent()) {
                return Optional.absent();
            }
            return Optional.of(voter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public List<Trustee> getTrusteeOfRangeBeforeTime(long time) throws RocksDBException {
        List<Trustee> voters = new ArrayList<>();
        List<Trustee> allVoters = new ArrayList<>();
        //筛选/分组/求和
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("voteRecord", "id")));
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            byte[] timeByte = getByColumnFamilyHandle(handleMap.get(getColName("voteRecord", "time")), iterator.key());
            //time的筛选
            String address = new String(getByColumnFamilyHandle(handleMap.get(getColName("voteRecord", "receiptAddress")), iterator.key()));
            if (Long.parseLong(new String(timeByte)) <= time) {
                List<Trustee> trustees = listTrustees();
                for(Trustee trustee:trustees){
                    if(address.equals(trustee.getAddress())){
                        trustee.setStatus(1);
                        allVoters.add(trustee);
                    }
                }
//                Trustee trustee = new Trustee();
//                trustee.setVotes(0l);
//                trustee.setStatus(1);
//                trustee.setAddress(address);
//                int index = -1;
//                //address的分组
//                index = allVoters.indexOf(trustee);
//                if (index != -1) {
//                    trustee = allVoters.remove(index);
//                }
//                //求和
//                trustee.setVotes(trustee.getVotes() + Integer.parseInt(new String(getByColumnFamilyHandle(handleMap.get(getColName("voteRecord", "voteNum")), iterator.key()))));
//                if (!trustee.isNullContent()) {
//                    allVoters.add(trustee);
//                }
            }
        }
        //排序票数
        allVoters.sort(new Comparator<Trustee>() {
            @Override
            public int compare(Trustee o1, Trustee o2) {
                return o1.getVotes().longValue() > o2.getVotes().longValue() ? -1 : (o1.getVotes().longValue() == o2.getVotes().longValue() ? 0 : 1);
            }
        });

        //排序未启动出块节点排后面
        allVoters.sort(new Comparator<Trustee>() {
            @Override
            public int compare(Trustee o1, Trustee o2) {
                return o2.getState() - o1.getState() ;
            }
        });
        //获取前101个
        voters.addAll(allVoters.size() >= Constant.TRUSTEES_INIT_NUM ? allVoters.subList(0, Constant.TRUSTEES_INIT_NUM) : allVoters);
        return voters;
    }

    @Override
    public boolean putVoteRecord(VoteRecord voteRecord) {
        try {
            voteRecord.setId();
            addObj(voteRecord);
            removeIndexesKey(handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName),
                    voteRecord.getVoteNum().toString().getBytes(), voteRecord.getId().getBytes());
            putIndexesKey(handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName),
                    voteRecord.getVoteNum().toString().getBytes(), voteRecord.getId().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName),
                    voteRecord.getVoteNum().toString().getBytes());
            removeIndexesKey(handleMap.get(IndexColumnNames.VOTERECORDTIME.indexName),
                    voteRecord.getTime().toString().getBytes(), voteRecord.getId().getBytes());
            putIndexesKey(handleMap.get(IndexColumnNames.VOTERECORDTIME.indexName),
                    voteRecord.getTime().toString().getBytes(), voteRecord.getId().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.VOTERECORDTIME.overAndNextName), voteRecord.getTime().toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param time
     * @param type 0 =     1 >=     2 <=
     * @return
     */
    @Override
    public List<VoteRecord> listVoteRecords(long time, int type) {
        try {
            List<String> fields = new ArrayList<>();
            fields.add("time");
            List<byte[]> value = new ArrayList<>();
            value.add(("" + time).getBytes());
            List<Integer> types = new ArrayList<>();
            types.add(type);
            return getDtoListByField(fields, value, types, VoteRecord.class
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
                    , handleMap.get(getColName("voteRecord", "voteNum")), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param address 投票人/受托人的地址
     * @param type    投票人/受托人的字段名
     * @param time
     * @param scrType 0 =     1 >=     2 <=
     * @return
     */
    @Override
    public List<VoteRecord> listVoteRecords(String address, String type, long time, int scrType) {
        try {
            List<String> fields = new ArrayList<>();
            fields.add(type);
            fields.add("time");
            List<byte[]> values = new ArrayList<>();
            values.add(address.getBytes());
            values.add(("" + time).getBytes());
            List<Integer> screenType = new ArrayList<>();
            screenType.add(0);
            screenType.add(scrType);
            return getDtoListByField(fields, values, screenType, VoteRecord.class
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
                    , handleMap.get(getColName("voteRecord", "voteNum")), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<VoteRecord> listVoteRecords(String address, String type) {
        try {
            List<String> fields = new ArrayList<>();
            fields.add(type);
            List<byte[]> values = new ArrayList<>();
            values.add(address.getBytes());
            List<Integer> screenType = new ArrayList<>();
            screenType.add(0);
            return getDtoListByField(fields, values, screenType, VoteRecord.class
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
                    , handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
                    , handleMap.get(getColName("voteRecord", "voteNum")), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Block> blockPagination(int pageCount, int pageNumber) throws Exception {
        List<Block> blocks = new ArrayList<>();
        Optional<Object> curHeightOpt = getLastBlockHeight();
        if (curHeightOpt.isPresent() && (long) curHeightOpt.get() > 0) {
            long curHeight = (long) curHeightOpt.get();
            long end = curHeight - pageCount * (pageNumber - 1);
            long begin = curHeight - pageCount * pageNumber + 1;
            for (long cur = end; cur >= begin; cur--) {
                blocks.add(getObj("blockHeight", "" + cur, Block.class));
            }
        }
        return blocks;
    }

    @Override
    public List<Block> getBlocksByHeight(int blockHeight, int blockCount) throws Exception {
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < blockCount; i++) {
            int curBlockHeight = blockHeight + i;
            Block block = getObj("blockHeight", "" + curBlockHeight, Block.class);
            if (!block.isNullContent()) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    @Override
    public List<Transaction> transactionPagination(int pageCount, int pageNumber, int orderByType, List<String> screens, List<byte[][]> screenVals, int screenType) {
        List<ColumnFamilyHandle> screenHanles = new ArrayList<>();
        if (screens != null && screenVals != null) {
            for (int i = 0; i < screens.size(); i++) {
                screenHanles.add(handleMap.get(getColName("transaction", screens.get(i))));
            }
        }
        if (screenVals == null) {
            screenVals = new ArrayList<>();
        }
        try {
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(IndexColumnNames.TRANSTIMEINDEX.indexName)
                    , screenHanles, screenVals, screenType, handleMap.get(IndexColumnNames.TRANSTIMEINDEX.overAndNextName),
                    Transaction.class, orderByType, 150, 0, handleMap.get(getColName("transaction", "time")));

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> getTransactionByAddress(int pageCount, int pageNumber, int orderByType, String address) {
        List<ColumnFamilyHandle> screenHands = new ArrayList<>();
        screenHands.add(handleMap.get(getColName("transaction", "payAddress")));
        screenHands.add(handleMap.get(getColName("transaction", "receiptAddress")));
        List<byte[][]> vals = new ArrayList<>();
        byte[][] val = new byte[1][];
        val[0] = address.getBytes();
        vals.add(val);
        vals.add(val);
        try {
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(IndexColumnNames.TRANSTIMEINDEX.indexName),
                    screenHands, vals, 1, handleMap.get(IndexColumnNames.TRANSTIMEINDEX.overAndNextName),
                    Transaction.class, orderByType, 300, 0, handleMap.get(getColName("transaction", "time")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Transaction> getTransactionsByBlockHeight(long blockHeight) {
        Optional<Block> blockOpt = getBlock(blockHeight);
        if (blockOpt.isPresent()) {
            return blockOpt.get().getTransactions();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> getNewBlocksTransactions(int pageCount, int pageNumber, int nBlock) {
        List<ColumnFamilyHandle> screenHandles = new ArrayList<>();
        screenHandles.add(handleMap.get(getColName("transaction", "blockHeight")));
        Optional<Object> lastBlockHeightOpt = getLastBlockHeight();
        List<byte[][]> vals = new ArrayList<>();
        long lastBlockHeight = 0;
        if (lastBlockHeightOpt.isPresent()) {
            lastBlockHeight = Long.parseLong(lastBlockHeightOpt.get().toString());
        }
        int size = lastBlockHeight >= 0 ? (int) (lastBlockHeight <= nBlock ? lastBlockHeight : nBlock) : 1;
        byte[][] val = new byte[size][];
        for (int i = 0; lastBlockHeight >= 0 && i < nBlock; i++) {
            val[i] = (lastBlockHeight + "").getBytes();
            lastBlockHeight--;
        }
        vals.add(val);
        try {
            return getDtoOrderByHandle(pageCount, pageNumber,
                    handleMap.get(IndexColumnNames.TRANSBLOCKHEIGHTINDEX.indexName),
                    screenHandles, vals, 0,
                    handleMap.get(IndexColumnNames.TRANSBLOCKHEIGHTINDEX.overAndNextName), Transaction.class, 0, 300, 0,
                    handleMap.get(getColName("transaction", "blockHeight")));

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Trustee> trusteePagination(int pageCount, int pageNumber, int orderByType, List<String> screens, List<byte[][]> screenVals) {
        List<ColumnFamilyHandle> screenHanles = new ArrayList<>();
        if (screens != null && screenVals != null) {
            for (int i = 0; i < screens.size(); i++) {
                screenHanles.add(handleMap.get(getColName("transaction", screens.get(i))));
            }
        }
        if (screenVals == null) {
            screenVals = new ArrayList<>();
        }
        if (screens == null) {
            screens = new ArrayList<>();
        }
        try {
            return getDtoOrderByHandle(pageCount, pageNumber,
                    handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.indexName)
                    , screenHanles, screenVals, 0,
                    handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.overAndNextName),
                    Trustee.class, orderByType, 100, 0,
                    handleMap.get(getColName("trustee", "votes")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String censesData() throws RocksDBException {
        //获取当前最新高度，根据最新高度进行统计
        Optional lastHeightOpt = getLastBlockHeight();
        //总金额
        BigDecimal sumMoney = BigDecimal.ZERO;
        //总手续费
        BigDecimal sumFee = BigDecimal.ZERO;
        long timeSpli = NetworkTime.INSTANCE.getWebsiteDateTimeLong() - 60 * 60 * 1000;
        long count = 0l;
        long blockTimeDiff = 0l;
        Optional<Block> blockOptional = getLastBlock();
        if (blockOptional.isPresent()) {
            Block lastBlock = blockOptional.get();
            Optional<Block> block = getBlock(lastBlock.getBlockHeight() - 1);
            if (block.isPresent()) {
                blockTimeDiff = lastBlock.getBlockHeader().getTimeStamp();
                blockTimeDiff = blockTimeDiff - block.get().getBlockHeader().getTimeStamp();
            }
        }
        if (lastHeightOpt.isPresent()) {
            long lastHeight = (Long) lastHeightOpt.get();
            RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                byte[] hashKey = iterator.key();
                byte[] transHeight = rocksDB.get(handleMap.get(getColName("transaction", "blockHeight")), hashKey);
                if (transHeight != null && transHeight.length > 0) {
                    Long curHeight = Long.parseLong(new String(transHeight));
                    //获取高度进行判断，只统计最新高度的数据
                    if (curHeight <= lastHeight) {
                        byte[] transVal = rocksDB.get(handleMap.get(getColName("transaction", "value")), hashKey);
                        byte[] transUsed = rocksDB.get(handleMap.get(getColName("transaction", "eggUsed")), hashKey);
                        byte[] transPrice = rocksDB.get(handleMap.get(getColName("transaction", "eggPrice")), hashKey);
                        if (transVal != null && transVal.length != 0) {
                            sumMoney = sumMoney.add(new BigDecimal(new String(transVal)));
                        }
                        if (transUsed != null && transUsed.length != 0 && transPrice != null && transPrice.length != 0) {
                            sumFee = sumFee.add(new BigDecimal(new String(transUsed)).multiply(new BigDecimal(new String(transPrice))));
                        }
                    }

                    byte[] timeByt = rocksDB.get(handleMap.get(getColName("transaction", "time")), hashKey);
                    if (timeByt != null && timeByt.length != 0) {
                        long time = Long.parseLong(new String(timeByt));
                        if (time >= timeSpli) {
                            //一小时内的交易流水   进行统计
                            count++;
                        }
                    }
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("avgMoney", lastHeightOpt.isPresent() ? sumMoney.divide(new BigDecimal(lastHeightOpt.get().toString()), 3, BigDecimal.ROUND_DOWN) : sumMoney);
        jsonObject.put("avgFee", lastHeightOpt.isPresent() ? sumFee.divide(new BigDecimal(lastHeightOpt.get().toString()), 3, BigDecimal.ROUND_DOWN) : sumFee);
        jsonObject.put("countTrans", count);
        jsonObject.put("blockTimeDiff", blockTimeDiff);
        return jsonObject.toJSONString();
    }

    @Override
    public List<VoteRecord> votingRecord(String address, int pageCount, int pageNumber) {
        try {
            List<ColumnFamilyHandle> screenHands = new ArrayList<>();
            List<byte[][]> vals = new ArrayList<>();
            if (address != null && !"".equals(address)) {
                screenHands.add(handleMap.get(getColName("voteRecord", "payAddress")));
                screenHands.add(handleMap.get(getColName("voteRecord", "receiptAddress")));

                byte[] val = address.getBytes();
                vals.add(new byte[][]{val});
                vals.add(new byte[][]{val});
            }
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(IndexColumnNames.VOTERECORDTIME.indexName), screenHands, vals, 1, handleMap.get(IndexColumnNames.VOTERECORDTIME.overAndNextName), VoteRecord.class, 0, 300, 0, handleMap.get(getColName("voteRecord", "time")));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public long getTransCountByAddress(String address) throws RocksDBException {
        if (address == null || "".equalsIgnoreCase(address)) return 0;
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
        long count = 0l;
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            byte[] hashKey = iterator.key();
            byte[] fromByt = rocksDB.get(handleMap.get(getColName("transaction", "payAddress")), hashKey);
            byte[] toByt = rocksDB.get(handleMap.get(getColName("transaction", "receiptAddress")), hashKey);
            if ((fromByt != null && fromByt.length != 0 && address.equalsIgnoreCase(new String(fromByt))) || (toByt != null && toByt.length != 0 && address.equalsIgnoreCase(new String(toByt)))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean delOneBlock() throws Exception {
        Optional<Object> optional = getLastBlockHeight();
        //这里删除单个高度只能从最高高度开始删
        if (optional.isPresent()) {
            delObj(getKeyFieldByClass(Block.class), String.valueOf(optional.get()), Block.class, true);
            //todo 还需要删除索引、确认流水等信息
            return true;
        }
        return false;
    }

    @Override
    public boolean delBlocksByHeight(long beginHeight) throws Exception {
        Optional<Object> optional = getLastBlockHeight();
        long maxHeight = Long.parseLong(optional.get().toString());
        if (optional.isPresent() && beginHeight <= maxHeight) {

            for (long i = maxHeight; i >= beginHeight; i--) {
                //先删除流水 再删区块
                List<Transaction> transactionList = getBlock(beginHeight).get().getTransactions();
                for(Transaction transaction:transactionList){
                    delObj(getKeyFieldByClass(Transaction.class),String.valueOf(transaction.getHash()),Transaction.class,true);
                }
                delObj(getKeyFieldByClass(Block.class), String.valueOf(i), Block.class, true);

            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delUnconfiTrans() throws RocksDBException {
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            byte[] blockHeight = rocksDB.get(handleMap.get(getColName("transaction", "blockHeight")), iterator.key());
            if (blockHeight == null || blockHeight.length == 0) {
                String hash = new String(iterator.key());
                deleteUnconfirmTransaction(hash);
            }
        }
        return true;
    }

}
