package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.Account;
import com.passport.core.Block;
import com.passport.core.Transaction;

import java.util.List;

/**
 *
 */
public interface DBAccess {

	/**
	 * 更新最新一个区块的Hash值
	 * @param lastBlock
	 * @return
	 */
	boolean putLastBlockHeight(Object lastBlock);

	/**
	 * 获取最新一个区块的Hash值
	 * @return
	 */
	Optional<Object> getLastBlockHeight();

	/**
	 * 保存区块
	 * @param block
	 * @return
	 */
	boolean putBlock(Block block);

	/**
	 * 获取指定的区块, 根据区块高度去获取
	 * @param blockHeight
	 * @return
	 */
	Optional<Block> getBlock(Object blockHeight);

	/**
	 * 获取最后（最大高度）一个区块
	 * @return
	 */
	Optional<Block> getLastBlock();

	/**
	 * 获取客户端节点列表
	 * @return
	 */
	Optional<List<String>> getNodeList();

	/**
	 * 保存客户端节点列表
	 * @param nodes
	 * @return
	 */
	boolean putNodeList(List<String> nodes);

	/**
	 * 往数据库添加|更新一条数据
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(String key, Object value);

	/**
	 * 获取某一条指定的数据
	 * @param key
	 * @return
	 */
	Optional<Object> get(String key);

	/**
	 * 删除一条数据
	 * @param key
	 * @return
	 */
	boolean delete(String key);

	/**
	 * 根据前缀搜索
	 * @param keyPrefix
	 * @return
	 */
	<T> List<T> seekByKey(String keyPrefix);

	/**
	 * 获取账户列表
	 * @return
	 */
	List<Account> listAccounts();

	/**
	 * 添加一个钱包账户
	 * @param account
	 * @return
	 */
	boolean putAccount(Account account);

	/**
	 * 获取指定的钱包账户
	 * @param address
	 * @return
	 */
	Optional<Account> getAccount(String address);

	/**
	 * 添加一条流水记录
	 * @param transaction
	 * @return
	 */
	boolean putTransaction(Transaction transaction);

	/**
	 * 获取指定流水
	 * @param txHash
	 * @return
	 */
	Optional<Transaction> getTransaction(String txHash);
}
