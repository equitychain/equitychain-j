package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.*;

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
	 * 添加一条未确认流水记录
	 * @param transaction
	 * @return
	 */
	boolean putUnconfirmTransaction(Transaction transaction);

	/**
	 * 获取指定未确认流水
	 * @param txHash
	 * @return
	 */
	Optional<Transaction> getUnconfirmTransaction(String txHash);

	/**
	 * 删除未确认流水
	 * @param txHash
	 */
	void deleteUnconfirmTransaction(String txHash);

	/**
	 * 未确认流水列表
	 * @return
	 */
	public List<Transaction> listUnconfirmTransactions();

	/**
	 * 添加一条已确认流水记录
	 * @param transaction
	 * @return
	 */
	boolean putConfirmTransaction(Transaction transaction);

	/**
	 * 获取指定已确认流水
	 * @param txHash
	 * @return
	 */
	Optional<Transaction> getConfirmTransaction(String txHash);

	Optional<Account> getMinerAccount();

	boolean putMinerAccount(Account account);

	/**
	 * 添加一个受托人
	 * @param trustee
	 * @return
	 */
	boolean putTrustee(Trustee trustee);

	/**
	 * 获取指定的委托人
	 * @param address
	 * @return
	 */
	Optional<Trustee> getTrustee(String address);

	/**
	 * 获取受托人列表
	 * @return
	 */
	List<Trustee> listTrustees();

	/**
	 * 添加一个投票人
	 * @param voter
	 * @return
	 */
	boolean putVoter(Voter voter);

	/**
	 * 获取指定的投票人
	 * @param address
	 * @return
	 */
	Optional<Voter> getVoter(String address);

	/**
	 * 获取投票人列表
	 * @return
	 */
	List<Voter> listVoters();

	/**
	 * 添加一条投票记录
	 * @param voteRecord
	 * @return
	 */
	boolean putVoteRecord(VoteRecord voteRecord);

	/**
	 * 获取投票记录列表
	 * @return
	 */
	List<VoteRecord> listVoteRecords();

	/**
	 * 区块分页查询
	 * @param pageCount：每页记录数
	 * @param pageNumber：页码
	 * @param orderByType：排序类型
	 * @return
	 */
	List<Block> blockPagination(int pageCount, int pageNumber, int orderByType) throws Exception;
	/**
	 * 交易流水分页查询
	 * @param pageCount：每页记录数
	 * @param pageNumber：页码
	 * @param orderByType：排序类型
	 * @param screens      筛选字段
	 * @param  screenVals  筛选字段对应的值
	 * @return
	 */
	List<Transaction> transactionPagination(int pageCount, int pageNumber, int orderByType,List<String> screens,List<byte[]> screenVals);
}
