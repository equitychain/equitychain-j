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
import java.util.List;

@Component
public class BaseDBRocksImpl extends BaseDBAccess {
    //存放节点列表
    private static final String CLIENT_NODES_LIST_KEY = "client-node-list";
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
                //todo 索引的添加
                //索引分类
                //流水时间索引
                String suoyinStr = getColName("transcationTime", "index");
                ColumnFamilyDescriptor descriptor = new ColumnFamilyDescriptor(suoyinStr.getBytes());
                ColumnFamilyHandle suoyinHeight = rocksDB.createColumnFamily(descriptor);
                handleMap.put(suoyinStr, suoyinHeight);
                //区块高度索引
                String blockHeight = getColName("blockHeight", "index");
                ColumnFamilyDescriptor blockHeightDescriptor = new ColumnFamilyDescriptor(blockHeight.getBytes());
                ColumnFamilyHandle blockHeightHandle = rocksDB.createColumnFamily(blockHeightDescriptor);
                handleMap.put(blockHeight, blockHeightHandle);

                //索引关系
                String suoyin_indexStr = getColName("transcationTimeIndex", "overAndNext");
                ColumnFamilyDescriptor descriptor1 = new ColumnFamilyDescriptor(suoyin_indexStr.getBytes());
                ColumnFamilyHandle handle = rocksDB.createColumnFamily(descriptor1);
                handleMap.put(suoyin_indexStr, handle);
                //区块高度关系
                String blockNextHeight = getColName("blockHeightIndex", "overAndNext");
                ColumnFamilyDescriptor blockHeightIndexDescriptor = new ColumnFamilyDescriptor(blockNextHeight.getBytes());
                ColumnFamilyHandle blockHeightNextHand = rocksDB.createColumnFamily(blockHeightIndexDescriptor);
                handleMap.put(blockNextHeight, blockHeightNextHand);

                //todo 存量数据的索引put  测试数据
//                for (int i = 1; i < 10000; i++) {
//                    Block block = new Block();
//                    block.setBlockHeight(Long.parseLong("" + i));
//                    block.setBlockSize(Long.parseLong("10" + i % 1000));
//                    block.setTransactionCount(0);
//                    addObj(block);
//                    putSuoyinKey(handleMap.get(getColName("blockHeight", "index")), (block.getBlockHeight() + "").getBytes(), (block.getBlockHeight() + "").getBytes());
//                    putOverAndNext(handleMap.get(getColName("blockHeightIndex", "overAndNext")), (block.getBlockHeight() + "").getBytes());
//                }

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
                //todo 加载已创建的索引列族
                descriptorList.add(new ColumnFamilyDescriptor(getColName("transcationTime", "index").getBytes()));
                descriptorList.add(new ColumnFamilyDescriptor(getColName("blockHeight", "index").getBytes()));
                descriptorList.add(new ColumnFamilyDescriptor(getColName("transcationTimeIndex", "overAndNext").getBytes()));
                descriptorList.add(new ColumnFamilyDescriptor(getColName("blockHeightIndex", "overAndNext").getBytes()));
                //打开数据库
                List<ColumnFamilyHandle> handleList = new ArrayList<>();
                rocksDB = RocksDB.open(new DBOptions().setCreateIfMissing(true), dataDir, descriptorList, handleList);
                handleList.forEach((handler) -> {
                    String name = new String(handler.getName());
                    handleMap.put(name, handler);
                });
            }
            /*int index = 0;
            for (int i = 1; i <= 600 * 10000; i++) {
                Transaction transaction = new Transaction();
                transaction.setHash((i + "").getBytes());
                if (i % 360 * 1000 == 0) {
                    index++;
                }
                long time = System.currentTimeMillis() + index * 3600 * 1000;
                transaction.setTime((time + "").getBytes());
                transaction.setEggMax(("test" + i).getBytes());
                transaction.setSignature("xxx".getBytes());
                addObj(transaction);
                putSuoyinKey(handleMap.get(getColName("transcationTime", "index")), transaction.getTime(), transaction.getHash());
                putOverAndNext(handleMap.get(getColName("transcationTimeIndex", "overAndNext")), transaction.getTime());
                System.out.println(i);
            }
            List<String> screens = new ArrayList<>();
            screens.add("eggMax");
            List<byte[]> vals = new ArrayList<>();
            vals.add(("test" + 100).getBytes());
            long t1 = System.currentTimeMillis();
            List<Transaction> transactionList = transactionPagination(100, 1, 0, null, null);
            long t2 = System.currentTimeMillis();
            System.out.println("transactionList.TIME:" + (t2 - t1));
            System.out.println("transactionList.size:" + transactionList.size());
            RocksIterator countIt = rocksDB.newIterator(handleMap.get(getColName("transaction", "hash")));
            RocksIterator timeQuIt = rocksDB.newIterator(handleMap.get(getColName("transcationTimeIndex", "overAndNext")));
            int count = 0;
            int quCount = 0;
            for (countIt.seekToFirst(); countIt.isValid(); countIt.next()) {
                count++;
            }
            for (timeQuIt.seekToFirst(); timeQuIt.isValid(); timeQuIt.next()) {
                quCount++;
            }
            System.out.println("集合大小：" + transactionList.size() + "  总数据：" + count + "  区间数：" + quCount);*/


            //测试
          String keyIndex = getColName("trusteeVotes", "index");
            String keyOverAndNext = getColName("trusteeVotesIndex", "overAndNext");
            ColumnFamilyHandle columnFamilyHandleIndex = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(keyIndex.getBytes()));
            ColumnFamilyHandle columnFamilyHandleOverAndNext = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(keyOverAndNext.getBytes()));
            handleMap.put(keyIndex, columnFamilyHandleIndex);
            handleMap.put(keyOverAndNext, columnFamilyHandleOverAndNext);
              for (int i = 1; i <= 1000; i++) {
                Trustee trustee = new Trustee();
                trustee.setAddress(i + "0x0000000000000");
                trustee.setGenerateRate(0.1f);
                trustee.setIncome(BigDecimal.ONE);
                trustee.setStatus(1);
                trustee.setVotes(new Long(i));
                addObj(trustee);
                //添加索引
                putSuoyinKey(handleMap.get(keyIndex), (trustee.getVotes() + "").getBytes(), trustee.getAddress().getBytes());
                putOverAndNext(handleMap.get(keyOverAndNext), (trustee.getVotes() + "").getBytes());
                System.out.println(i);
            }
            List<Trustee> trustees = trusteePagination(101, 1, 0, null, null);
            System.out.println("trustees.size:" + trustees.size());
            for (Trustee trustee : trustees) {
                System.out.println("trustee:" + JSON.toJSONString(trustee));
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
    @Deprecated
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
    @Deprecated
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
        return false;
    }

    @Override
    public Optional<Transaction> getUnconfirmTransaction(String txHash) {
        return null;
    }

    @Override
    public void deleteUnconfirmTransaction(String txHash) {

    }

    @Override
    public List<Transaction> listUnconfirmTransactions() {
        return null;
    }

    @Override
    public boolean putConfirmTransaction(Transaction transaction) {
        return false;
    }

    @Override
    public Optional<Transaction> getConfirmTransaction(String txHash) {
        return null;
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
    public List<Block> blockPagination(int pageCount, int pageNumber, int orderByType) throws Exception {
        ColumnFamilyHandle indexHandle = handleMap.get(getColName("blockHeight", "index"));
        ColumnFamilyHandle overAndNextHandle = handleMap.get(getColName("blockHeightIndex", "overAndNext"));
        List<Block> blocks = super.getDtoOrderByHandle(pageCount, pageNumber, indexHandle, null, null, overAndNextHandle, Block.class, "blockHeight", orderByType, 150, 0, handleMap.get(getColName("block", "blockHeight")));
        return blocks;
    }

    @Override
    public List<Transaction> transactionPagination(int pageCount, int pageNumber, int orderByType, List<String> screens, List<byte[]> screenVals) {
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
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(getColName("transcationTime", "index"))
                    , screenHanles, screenVals, handleMap.get(getColName("transcationTimeIndex", "overAndNext")),
                    Transaction.class, "hash", orderByType, 100000, 0, handleMap.get(getColName("transaction", "time")));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Trustee> trusteePagination(int pageCount, int pageNumber, int orderByType, List<String> screens, List<byte[]> screenVals) {
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
            return getDtoOrderByHandle(pageCount, pageNumber, handleMap.get(getColName("trusteeVotes", "index"))
                    , screenHanles, screenVals, handleMap.get(getColName("trusteeVotesIndex", "overAndNext")), Trustee.class, "votes", orderByType, 100, 0,
                    handleMap.get(getColName("trustee", "votes")));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
