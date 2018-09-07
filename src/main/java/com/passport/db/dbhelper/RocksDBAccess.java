package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.Transaction;
import com.passport.utils.SerializeUtils;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
//@Component
public class RocksDBAccess implements DBAccess {
	static Logger logger = LoggerFactory.getLogger(RocksDBAccess.class);

	//区块数据存储hash桶前缀
	public static final String BLOCKS_BUCKET_PREFIX = "blocks_";
	//最后一个区块的区块高度
	public static final String LAST_BLOCK_HEIGHT = BLOCKS_BUCKET_PREFIX+"last_block";
	//存放节点列表
	private static final String CLIENT_NODES_LIST_KEY = "client-node-list";
	//钱包数据存储hash桶前缀
	public static final String WALLETS_BUCKET_PREFIX = "wallets_";
	//未确认交易流水存储hash桶前缀
	public static final String UNCONFIRM_TRANSACTIONS_BUCKET_PREFIX = "unconfirm_transactions_";
	//已确认交易流水存储hash桶前缀
	public static final String CONFIRM_TRANSACTIONS_BUCKET_PREFIX = "confirm_transactions_";
    //用户自己设置的挖矿账号
    public static final String MINERACCOUNT = "miner-account";

	@Value("${db.dataDir}")
	private String dataDir;

	private RocksDB rocksDB;

	public RocksDBAccess() {
	}
	/**
	 * 初始化RocksDB
	 */
//	@PostConstruct
	public void initRocksDB() {
		try {
			//如果数据库路径不存在，则创建路径
			File directory = new File(System.getProperty("user.dir")+"/"+dataDir);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			rocksDB = RocksDB.open(new Options().setCreateIfMissing(true), dataDir);
		} catch (RocksDBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean putLastBlockHeight(Object lastBlock) {
		return this.put(LAST_BLOCK_HEIGHT, lastBlock);
	}

	@Override
	public Optional<Object> getLastBlockHeight() {
		return this.get(LAST_BLOCK_HEIGHT);
	}

	@Override
	public boolean putBlock(Block block) {
		return this.put(BLOCKS_BUCKET_PREFIX + block.getBlockHeight(), block);
	}

	@Override
	public Optional<Block> getBlock(Object blockHeight) {
		Optional<Object> object = this.get(BLOCKS_BUCKET_PREFIX + blockHeight);
		if (object.isPresent()) {
			return Optional.of((Block) object.get());
		}
		return Optional.absent();
	}

	@Override
	public Optional<Block> getLastBlock() {
		Optional<Object> blockHeight = getLastBlockHeight();
		if (blockHeight.isPresent()) {
			return this.getBlock(blockHeight.get().toString());
		}
		return Optional.absent();
	}

	@Override
	public Optional<List<String>> getNodeList() {
		Optional<Object> nodes = this.get(CLIENT_NODES_LIST_KEY);
		if (nodes.isPresent()) {
			return Optional.of((List<String>) nodes.get());
		}
		return Optional.absent();
	}

	@Override
	public boolean putNodeList(List<String> nodes) {
		return this.put(CLIENT_NODES_LIST_KEY, nodes);
	}

	@Override
	public boolean put(String key, Object value) {
		try {
			rocksDB.put(key.getBytes(), SerializeUtils.serialize(value));
			return true;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.error("ERROR for RocksDB : {}", e);
			}
			return false;
		}
	}

	@Override
	public Optional<Object> get(String key) {
		try {
			return Optional.of(SerializeUtils.unSerialize(rocksDB.get(key.getBytes())));
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.error("ERROR for RocksDB : {}", e);
			}
			return Optional.absent();
		}
	}

	@Override
	public boolean delete(String key) {
		try {
			rocksDB.delete(key.getBytes());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public <T> List<T> seekByKey(String keyPrefix) {
		ArrayList<T> ts = new ArrayList<>();
		ReadOptions options = new ReadOptions();
		options.setPrefixSameAsStart(true);
		RocksIterator iterator = rocksDB.newIterator(options);
		byte[] key = keyPrefix.getBytes();
		for (iterator.seek(key); iterator.isValid(); iterator.next()) {
			if(!new String(iterator.key()).startsWith(keyPrefix)) continue;
//		for (iterator.seek(key); iterator.isValid() && String.valueOf(iterator.key()).startsWith(keyPrefix); iterator.next()) {
			ts.add((T) SerializeUtils.unSerialize(iterator.value()));
		}
		return ts;
	}

	@Override
	public List<Account> listAccounts() {

		List<Object> objects = seekByKey(WALLETS_BUCKET_PREFIX);
		List<Account> accounts = new ArrayList<>();
		for (Object o : objects) {
			accounts.add((Account) o);
		}
		return accounts;
	}

	@Override
	public boolean putAccount(Account account) {
		return this.put(WALLETS_BUCKET_PREFIX + account.getAddress(), account);
	}

	@Override
	public Optional<Account> getAccount(String address) {

		Optional<Object> object = this.get(WALLETS_BUCKET_PREFIX + address);
		if (object.isPresent()) {
			return Optional.of((Account) object.get());
		}
		return Optional.absent();
	}

	@Override
	public boolean putUnconfirmTransaction(Transaction transaction) {
		return this.put(UNCONFIRM_TRANSACTIONS_BUCKET_PREFIX + new String(transaction.getHash()), transaction);
	}

	@Override
	public Optional<Transaction> getUnconfirmTransaction(String txHash) {
		Optional<Object> object = this.get(UNCONFIRM_TRANSACTIONS_BUCKET_PREFIX + txHash);
		if (object.isPresent()) {
			return Optional.of((Transaction) object.get());
		}
		return Optional.absent();
	}

	@Override
	public void deleteUnconfirmTransaction(String txHash) {
		delete(UNCONFIRM_TRANSACTIONS_BUCKET_PREFIX + txHash);
	}

	@Override
	public List<Transaction> listUnconfirmTransactions() {
		List<Object> objects = seekByKey(UNCONFIRM_TRANSACTIONS_BUCKET_PREFIX);
		List<Transaction> transactions = new ArrayList<>();
		for (Object o : objects) {
			transactions.add((Transaction) o);
		}
		return transactions;
	}

	@Override
	public boolean putConfirmTransaction(Transaction transaction) {
		return this.put(CONFIRM_TRANSACTIONS_BUCKET_PREFIX + transaction.getHash().toString(), transaction);
	}

	@Override
	public Optional<Transaction> getConfirmTransaction(String txHash) {
		Optional<Object> object = this.get(CONFIRM_TRANSACTIONS_BUCKET_PREFIX + txHash);
		if (object.isPresent()) {
			return Optional.of((Transaction) object.get());
		}
		return Optional.absent();
	}

	public Optional<Account> getMinerAccount(){
	    Optional<Object> getMinerAccount = get(MINERACCOUNT);
	    if(getMinerAccount.isPresent()){
	        return Optional.of((Account) getMinerAccount.get());
        }
        return Optional.absent();
    }

    public boolean putMinerAccount(Account account){
		return this.put(MINERACCOUNT, account);
	}
}
