package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.*;
import com.passport.utils.SerializeUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class BaseDBRocksImpl extends BaseDBAccess {
    //存放节点列表
    private static final String CLIENT_NODES_LIST_KEY = "client-node-list";
    private static final String MINERACCOUNT = "miner_account";
    @Value("${db.dataDir}")
    private String dataDir;

    public BaseDBRocksImpl() {

    }

    @Override
    @PostConstruct
    protected void initDB(){
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
        RocksIterator heightIter = rocksDB.newIterator(handle);
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
            KeysSet.add(key);//存储key到文件
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
                Optional.of(SerializeUtils.unSerialize(objByt));
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public Optional<Object> get(String columnFamily,String key) {
        byte[] objByt = new byte[100];
        try {
            objByt = rocksDB.get(super.handleMap.get(columnFamily),key.getBytes());
//            if (objByt != null) {
//                Optional.of(SerializeUtils.unSerialize(objByt));

        } catch (RocksDBException e) {
//            e.printStackTrace();
        }
//        return Optional.absent();
        System.out.println(new String(objByt)+"-----------!");
        return null;
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
    public <T> List<T> seekByKey(String keyPrefix) {
        ArrayList<T> ts = new ArrayList<>();
        ReadOptions options = new ReadOptions();
        options.setPrefixSameAsStart(true);
        RocksIterator iterator = rocksDB.newIterator(options);
        byte[] key = keyPrefix.getBytes();
        for (iterator.seek(key); iterator.isValid(); iterator.next()) {
            if (!new String(iterator.key()).startsWith(keyPrefix)) continue;
            ts.add((T) SerializeUtils.unSerialize(iterator.value()));
        }
        return ts;
    }

    @Override
    public List<Account> listAccounts() {
        RocksIterator accountIter = rocksDB.newIterator(handleMap.get(getColName("account", "address")));
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
            return Optional.of(getObj("address", address, Account.class));
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
        if(transaction.isPresent()){
            try {
                delObj("hash",txHash,Transaction.class,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Transaction> listUnconfirmTransactions() {
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
        List<Transaction> transactions = new ArrayList<>();
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            String hash = new String(iterator.key());
            try {
                Transaction transaction = getObj("hash", hash, Transaction.class);
                if (transaction.getBlockHeight() == null || transaction.getBlockHeight().length == 0) {
                    transactions.add(transaction);
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
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction","hash")));
        List<Transaction> result = new ArrayList<>();
        for (iterator.seekToFirst();iterator.isValid();iterator.next()){
            try {
                result.add(getObj("hash",iterator.key(),Transaction.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Optional<Account> getMinerAccount(){
        Optional<Object> getMinerAccount = (Optional<Object>) SerializeUtils.unSerialize(get(MINERACCOUNT.getBytes()));
        if(getMinerAccount != null && getMinerAccount.isPresent()){
            return Optional.of((Account) getMinerAccount.get());
        }
        return Optional.absent();
    }

    @Override
    public boolean putMinerAccount(Account account) {
        return put(MINERACCOUNT.getBytes(),SerializeUtils.serialize(Optional.of(account)));
    }

    @Override
    public boolean putTrustee(Trustee trustee) {
        try {
            addObj(trustee);
            //添加索引
            putSuoyinKey(handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.indexName),
                    (trustee.getVotes()+"").getBytes(),trustee.getAddress().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.overAndNextName),
                    (trustee.getVotes()+"").getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Trustee> getTrustee(String address) {
        try {
            Trustee trustee = getObj("address",address,Trustee.class);
            return Optional.of(trustee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public List<Trustee> listTrustees() {
        return trusteePagination(101,1,0,null,null);
    }

    @Override
    public boolean putVoter(Voter voter) {
        try {
            addObj(voter);
            putSuoyinKey(handleMap.get(IndexColumnNames.VOTERNUMBEROFVOTE.indexName),
                    voter.getVoteNum().toString().getBytes(),voter.getAddress().getBytes());
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
            Voter voter = getObj("address",address,Voter.class);
            return Optional.of(voter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public List<Trustee> getTrusteeOfRangeBeforeTime(long time) {
        List<Trustee> voters = new ArrayList<>();
        List<Trustee> allVoters = new ArrayList<>();
        //筛选/分组/求和
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("voteRecord","id")));
        for (iterator.seekToFirst();iterator.isValid();iterator.next()){
            byte[] timeByte = getByColumnFamilyHandle(handleMap.get(getColName("voteRecord","time")),iterator.key());
            //time的筛选
            if(Long.parseLong(new String(timeByte)) <= time){
                Trustee trustee = new Trustee();
                trustee.setVotes(0l);
                trustee.setStatus(1);
                trustee.setAddress(new String(getByColumnFamilyHandle(handleMap.get(getColName("voteRecord","receiptAddress")),iterator.key())));
                int index = -1;
                //address的分组
                index = allVoters.indexOf(trustee);
                if(index != -1) {
                    trustee = allVoters.remove(index);
                }
                //求和
                trustee.setVotes(trustee.getVotes()+Integer.parseInt(new String(getByColumnFamilyHandle(handleMap.get(getColName("voteRecord","voteNum")),iterator.key()))));
                allVoters.add(trustee);
            }
        }
        //排序
        allVoters.sort(new Comparator<Trustee>() {
            @Override
            public int compare(Trustee o1, Trustee o2) {
                return o1.getVotes().longValue()>o2.getVotes().longValue()?-1:(o1.getVotes().longValue()==o2.getVotes().longValue()?0:1);
            }
        });
        //获取前101个
        voters.addAll(allVoters.size()>=101?allVoters.subList(0,100):allVoters);
        return voters;
    }

    @Override
    public boolean putVoteRecord(VoteRecord voteRecord) {
        try {
            addObj(voteRecord);
            putSuoyinKey(handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName),
                    voteRecord.getVoteNum().toString().getBytes(),voteRecord.getPayAddress().getBytes());
            putOverAndNext(handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName),
                    voteRecord.getVoteNum().toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param time
     * @param type      0 =     1 >=     2 <=
     * @return
     */
    @Override
    public List<VoteRecord> listVoteRecords(long time,int type) {
        try {
            List<String> fields = new ArrayList<>();
            fields.add("time");
            List<byte[]> value = new ArrayList<>();
            value.add((""+time).getBytes());
            List<Integer> types = new ArrayList<>();
            types.add(type);
            return getDtoListByField(fields,value,types,VoteRecord.class
            ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
            ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
            ,handleMap.get(getColName("voteRecord","voteNum")),0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param   address   投票人/受托人的地址
     * @param   type      投票人/受托人的字段名
     * @param time
     * @param scrType 0 =     1 >=     2 <=
     * @return
     */
    @Override
    public List<VoteRecord> listVoteRecords(String address, String type,long time,int scrType) {
        try {
            List<String> fields = new ArrayList<>();
            fields.add(type);
            fields.add("time");
            List<byte[]> values = new ArrayList<>();
            values.add(address.getBytes());
            values.add((""+time).getBytes());
            List<Integer> screenType = new ArrayList<>();
            screenType.add(0);
            screenType.add(scrType);
            return getDtoListByField(fields,values,screenType,VoteRecord.class
                    ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
                    ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
                    ,handleMap.get(getColName("voteRecord","voteNum")),0);
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
            return getDtoListByField(fields,values,screenType,VoteRecord.class
                    ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.overAndNextName)
                    ,handleMap.get(IndexColumnNames.VOTERECORDVOTENUMBER.indexName)
                    ,handleMap.get(getColName("voteRecord","voteNum")),0);
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
            for (long cur = end; cur >= begin; cur++) {
                blocks.add(getObj("blockHeight", "" + cur, Block.class));
            }
        }
        return blocks;
    }

    @Override
    public List<Transaction> transactionPagination(int pageCount, int pageNumber, int orderByType, List<String> screens, List<byte[][]> screenVals) {
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
                    , screenHanles, screenVals,0, handleMap.get(IndexColumnNames.TRANSTIMEINDEX.overAndNextName),
                    Transaction.class, "hash", orderByType, 150, 0,handleMap.get(getColName("transaction","time")));

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
            return getDtoOrderByHandle(pageCount,pageNumber,handleMap.get(IndexColumnNames.TRANSTIMEINDEX.indexName),
                    screenHands,vals,1,handleMap.get(IndexColumnNames.TRANSTIMEINDEX.overAndNextName),
                    Transaction.class,"hash",orderByType,300,0,handleMap.get(getColName("transaction","time")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Transaction> getTransactionsByBlockHeight(long blockHeight){
        Optional<Block> blockOpt = getBlock(blockHeight);
        if(blockOpt.isPresent()){
            return blockOpt.get().getTransactions();
        }else{
            return new ArrayList<>();
        }
    }
    @Override
    public List<Transaction> getNewBlocksTransactions(int pageCount, int pageNumber) {
        List<ColumnFamilyHandle> screenHandles = new ArrayList<>();
        screenHandles.add(handleMap.get(getColName("transaction", "blockHeight")));
        Optional<Object> lastBlockHeightOpt = getLastBlockHeight();
        List<byte[][]> vals = new ArrayList<>();
        long lastBlockHeight = 0;
        if (lastBlockHeightOpt.isPresent()) {
            lastBlockHeight = Long.parseLong(lastBlockHeightOpt.get().toString());
        }
        int size = lastBlockHeight >= 0 ? (int) (lastBlockHeight <= 100 ? lastBlockHeight : 100) : 1;
        byte[][] val = new byte[size][];
        for (int i = 0; lastBlockHeight >= 0 && i < 100; i++) {
            val[i] = (lastBlockHeight + "").getBytes();
            lastBlockHeight--;
        }
        vals.add(val);
        try {
            return getDtoOrderByHandle(pageCount,pageNumber,
                    handleMap.get(IndexColumnNames.TRANSBLOCKHEIGHTINDEX.indexName),
                    screenHandles,vals,0,
                    handleMap.get(IndexColumnNames.TRANSBLOCKHEIGHTINDEX.overAndNextName),Transaction.class,
                    "hash",0,300,0,
                    handleMap.get(getColName("transaction","blockHeight")));

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
                    , screenHanles, screenVals,0,
                    handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.overAndNextName),
                    Trustee.class, "votes", orderByType, 100, 0,
                    handleMap.get(getColName("trustee", "votes")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
