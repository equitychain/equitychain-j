package com.passport.db.dbhelper;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.passport.core.*;
import com.passport.core.Transaction;
import com.passport.utils.SerializeUtils;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
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
    protected void initDB() {
        try {
            //数据库目录不存在就创建
            File directory = new File(System.getProperty("user.dir") + "/" + dataDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            List<String> fields = new ArrayList<>();
            IndexColumnNames[] indexColumnNames = IndexColumnNames.values();
            //TODO 添加dto的字节码
            fields.addAll(getClassCols(new Transaction().getClass()));
            fields.addAll(getClassCols(new Block().getClass()));
            fields.addAll(getClassCols(new Trustee().getClass()));
            dtoClasses.add(new Transaction().getClass());
            dtoClasses.add(new Block().getClass());
            dtoClasses.add(new Trustee().getClass());
            try {
                rocksDB = RocksDB.open(new Options().setCreateIfMissing(true), dataDir);
                System.out.println("========create fields=========");
                //添加默认的列族
                handleMap.put("default", rocksDB.getDefaultColumnFamily());
                for (String field : fields) {
                    ColumnFamilyDescriptor descriptor = new ColumnFamilyDescriptor(field.getBytes());
                    ColumnFamilyHandle handle = rocksDB.createColumnFamily(descriptor);
                    handleMap.put(field, handle);
                    System.out.println("====field:" + field);
                }
                for(IndexColumnNames columnNames : indexColumnNames){
                    ColumnFamilyHandle indexNameHandle = rocksDB.createColumnFamily(columnNames.getIndexName());
                    ColumnFamilyHandle indexOverHandle = rocksDB.createColumnFamily(columnNames.getOverAndNextName());
                    handleMap.put(columnNames.indexName, indexNameHandle);
                    handleMap.put(columnNames.overAndNextName,indexOverHandle);
                }
            } catch (Exception e) {
                //列集合
                List<ColumnFamilyDescriptor> descriptorList = new ArrayList<>();
                ColumnFamilyDescriptor defaultDescriptor = new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY);
                descriptorList.add(defaultDescriptor);
                System.out.println("========load fields=========");
                for (String s : fields) {
                    ColumnFamilyDescriptor descriptor = new ColumnFamilyDescriptor(s.getBytes());
                    descriptorList.add(descriptor);
                    System.out.println("====field:" + s);
                }
                for(IndexColumnNames names : indexColumnNames){
                    descriptorList.add(names.getIndexName());
                    descriptorList.add(names.getOverAndNextName());
                }
                //打开数据库
                List<ColumnFamilyHandle> handleList = new ArrayList<>();
                rocksDB = RocksDB.open(new DBOptions().setCreateIfMissing(true), dataDir, descriptorList, handleList);
                handleList.forEach((handler) -> {
                    String name = new String(handler.getName());
                    handleMap.put(name, handler);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 放置最后一个区块高度，过期
     * 插入区块时会自动的
     *
     * @param lastBlock
     * @return
     */
    @Override
    @Deprecated
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
//            putSuoyinKey(handleMap.get(getColName(BLOCK_HEIGHT_COLNAME, SUFFIX_INDEX)), block.getBlockHeight().toString().getBytes(), block.getBlockHeight().toString().getBytes());
//            putOverAndNext(handleMap.get(getColName(BLOCK_HEIGHT_RELA_COLNAME, SUFFIX_RELA)), block.getBlockHeight().toString().getBytes());
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
        try {
            byte[] objByt = rocksDB.get(CLIENT_NODES_LIST_KEY.getBytes());
            if (objByt != null) {
                List<String> nodeList = (List<String>) SerializeUtils.unSerialize(objByt);
                if (nodeList != null) {
                    return Optional.of(nodeList);
                }
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
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
    @Deprecated
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
            if(blockHeight == null || blockHeight.length == 0) {
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
            Transaction transaction = getObj("hash",txHash,Transaction.class);
            if(transaction != null){
                if(transaction.getBlockHeight() == null || transaction.getBlockHeight().length == 0){
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
//        Optional<Transaction> optionalTran = getUnconfirmTransaction(txHash);
//        if(optionalTran.isPresent()){
//
//        }
    }

    @Override
    public List<Transaction> listUnconfirmTransactions() {
        RocksIterator iterator = rocksDB.newIterator(handleMap.get(getColName("transaction","hash")));
        List<Transaction> transactions = new ArrayList<>();
        for (iterator.seekToFirst();iterator.isValid();iterator.next()){
            String hash = new String(iterator.key());
            try {
                Transaction transaction = getObj("hash",hash,Transaction.class);
                if(transaction.getBlockHeight() == null || transaction.getBlockHeight().length == 0){
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
            if(blockOptional != null && blockOptional.isPresent()){
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
            Transaction transaction = getObj("hash",txHash,Transaction.class);
            if(transaction != null){
                if(transaction.getBlockHeight() != null && transaction.getBlockHeight().length > 0){
                    return Optional.of(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    @Override
    public Optional<Account> getMinerAccount() {
        return null;
    }

    @Override
    public boolean putMinerAccount(Account account) {
        return false;
    }

    @Override
    public boolean putTrustee(Trustee trustee) {
        return false;
    }

    @Override
    public Optional<Trustee> getTrustee(String address) {
        return null;
    }

    @Override
    public List<Trustee> listTrustees() {
        return null;
    }

    @Override
    public boolean putVoter(Voter voter) {
        return false;
    }

    @Override
    public Optional<Voter> getVoter(String address) {
        return null;
    }

    @Override
    public List<Voter> listVoters() {
        return null;
    }

    @Override
    public boolean putVoteRecord(VoteRecord voteRecord) {
        return false;
    }

    @Override
    public List<VoteRecord> listVoteRecords() {
        return null;
    }

    @Override
    public List<VoteRecord> listVoteRecords(String address, String type) {
        return null;
    }

    @Override
    public List<Block> blockPagination(int pageCount, int pageNumber) throws Exception {
        List<Block> blocks = new ArrayList<>();
        Optional<Object> curHeightOpt = getLastBlockHeight();
        if(curHeightOpt.isPresent() && (long)curHeightOpt.get() > 0) {
            long curHeight = (long)curHeightOpt.get();
            long end = curHeight-pageCount * (pageNumber - 1);
            long begin = curHeight-pageCount * pageNumber+1;
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
        screenHands.add(handleMap.get(getColName("transaction","payAddress")));
        screenHands.add(handleMap.get(getColName("transaction","receiptAddress")));
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

    @Override
    public List<Transaction> getNewBlocksTransactions(int pageCount, int pageNumber) {
        List<ColumnFamilyHandle> screenHandles = new ArrayList<>();
        screenHandles.add(handleMap.get(getColName("transaction","blockHeight")));
        Optional<Object> lastBlockHeightOpt = getLastBlockHeight();
        List<byte[][]> vals = new ArrayList<>();
        long lastBlockHeight = 0;
        int size = lastBlockHeight>=0? (int) (lastBlockHeight <= 100 ? lastBlockHeight : 100) :1;
        byte[][] val = new byte[size][];
        if(lastBlockHeightOpt.isPresent()){
            lastBlockHeight = Long.parseLong(lastBlockHeightOpt.get().toString());
        }
        for(int i = 0;lastBlockHeight >=0 && i < 100;i ++){
            val[i] = (lastBlockHeight+"").getBytes();
            lastBlockHeight --;
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
        if(screens == null){
            screens = new ArrayList<>();
        }
        try {
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.indexName)
                    , screenHanles, screenVals,0, handleMap.get(IndexColumnNames.TRUSTEEVOTESINDEX.overAndNextName), Trustee.class, "votes", orderByType, 100, 0,
                    handleMap.get(getColName("trustee", "votes")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
