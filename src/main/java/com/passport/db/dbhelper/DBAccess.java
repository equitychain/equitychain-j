package com.passport.db.dbhelper;

import com.google.common.base.Optional;
import com.passport.core.*;
import org.rocksdb.ColumnFamilyHandle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public interface DBAccess {

	/**
	 * 往数据库添加|更新一条数据
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(byte[] key, byte[] value);

	/**
	 * 往数据库添加|更新一条数据,指定列族
	 * @param columnFamilyHandle
	 * @param key
	 * @param value
	 * @return
	 */
	boolean putByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle, byte[] key, byte[] value);

	/**
	 * 获取某一条指定的数据
	 * @param key
	 * @return
	 */
	byte[] get(byte[] key);

	/**
	 * 获取指定列族的value
	 * @param columnFamilyHandle
	 * @param key
	 * @return
	 */
	byte[] getByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle,byte[] key);

	/**
	 * 删除一条数据
	 * @param key
	 * @return
	 */
	boolean delete(byte[] key);
	/**
	 * 删除一条数据
	 * @param key
	 * @return
	 */
	boolean deleteByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle,byte[] key);

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
     * 获取某一条指定的数据(通过列族)
     * @param columFamily
     * @param key
     * @return
     */
    Optional<Object> get(String columFamily ,String key);

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

    void addObj(Object obj) throws Exception;

    <T> void delObj(String keyField,String fieldVale,Class<T> dtoClazz,boolean deleteCase)throws Exception;

    <T> T getObj(String keyField, String fieldValue, Class<T> dtoClazz) throws Exception;
	/**
	 * 获取投票记录列表
     * type      0 =     1 >=     2 <=
	 * @return
	 */
	List<VoteRecord> listVoteRecords(long time,int type);
	/**
	 * 根据 投票人/受托人 获取投票记录列表
	 * @param   address   投票人/受托人的地址
	 * @param   type      投票人/受托人的字段名
     *                    screenType      0 =     1 >=     2 <=
	 * @return  返回需要的投票集合
	 */
    List<VoteRecord> listVoteRecords(String address, String type,long time, int screenType);

    List<VoteRecord> listVoteRecords(String address, String typee);
	/**
	 * 区块分页查询
	 * @param pageCount：每页记录数
	 * @param pageNumber：页码
	 * @return
	 */
	List<Block> blockPagination(int pageCount, int pageNumber) throws Exception;
	/**
	 * 交易流水分页查询
	 * @param pageCount：每页记录数
	 * @param pageNumber：页码
	 * @param orderByType：排序类型
	 * @param screens      筛选字段
	 * @param  screenVals  筛选字段对应的值
	 * @return
	 */
	List<Transaction> transactionPagination(int pageCount, int pageNumber, int orderByType,List<String> screens,List<byte[][]> screenVals);

    /**
     * 根据地址查询流水
     * @param pageCount
     * @param pageNumber
     * @param orderByType
     * @param address
     * @return
     */
	List<Transaction> getTransactionByAddress(int pageCount, int pageNumber, int orderByType,String address);

	List<Transaction> getTransactionsByBlockHeight(long blockHeight);
    /**
     * 查询前100个区块的流水
     * @param pageCount
     * @param pageNumber
     * @return
     */
	List<Transaction> getNewBlocksTransactions(int pageCount, int pageNumber);
	/**
	 * 委托人分页查询
	 * @param pageCount：每页记录数
	 * @param pageNumber：页码
	 * @param orderByType：排序类型
	 * @param screens      筛选字段
	 * @param  screenVals  筛选字段对应的值
	 * @return
	 */
	List<Trustee> trusteePagination(int pageCount, int pageNumber, int orderByType,List<String> screens,List<byte[][]> screenVals);

    /**
     * 筛选排序查询    不分页
     * @param fields                   筛选的字段
     * @param values                   筛选字段所对应的值
     * @param tClass                   查询dto的class字节码
     * @param overAndNextHandle       排序索引的关系handle
     * @param indexHandle              排序索引的indexhandle
     * @param orderByFieldHandle       排序字段的handle
     * @param orderType                 排序类型  1升序,0降序
     * @param screenType                0 =     1 >=     2 <=
     * @param <T>                       dto
     * @return                             list
     * @throws Exception
     */
	<T> List<T> getDtoListByField(List<String> fields, List<byte[]> values,List<Integer> screenType, Class<T> tClass,ColumnFamilyHandle overAndNextHandle,ColumnFamilyHandle indexHandle,ColumnFamilyHandle orderByFieldHandle, int orderType)throws Exception;

    /**
     * @param pageCount         分页的每页条数
     * @param pageNumber        分页的当前页数
     * @param indexHandle       排序字段的索引Handle
     * @param screenHands       筛选字段的Handle集合
     * @param vals              筛选字段的值集合   可以有多个值
     *@param screenType        筛选类型   0 and   1 or  （注意，要么全是and，要么全是or）
     * @param overAndNextHandle 索引字段的排序关系handle
     * @param tClass            对象的字节码
     * @param keyFiledName      主键字段的名
     * @param orderByType       排序类型 1升序,0降序
     * @param flushSize         排序字段区间的缓存
     * @param dtoType           对象主键的类型  根据这个类型匹配不同的排序方法
     * @param <T>               对象
     * @return 该页的数据
     * @throws Exception
     */
    <T> ArrayList<T> getDtoOrderByHandle(int pageCount, int pageNumber,
                                         ColumnFamilyHandle indexHandle, List<ColumnFamilyHandle> screenHands,
                                         List<byte[][]> vals, int screenType,
                                         ColumnFamilyHandle overAndNextHandle, Class<T> tClass, String keyFiledName,
                                         int orderByType, int flushSize, int dtoType, ColumnFamilyHandle orderByFieldHandle) throws Exception;
}
