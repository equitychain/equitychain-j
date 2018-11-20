package com.passport.db;

import com.passport.db.exception.NullNameException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Wu Created by SKINK on 2018/6/30.
 */
public class RocksDataSource implements MapDataSource<byte[]> {

  private static final Logger logger = LoggerFactory.getLogger(RocksDataSource.class);

  private String dbname;
  private RocksDB rocksDB;
  private ReadOptions readOptions;
  private boolean alive;

  /**
   * reset lock
   * **/
  private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


  static {
    RocksDB.loadLibrary();
  }

  public RocksDataSource() {
  }

  public RocksDataSource(String dbname) {
    this.dbname = dbname;
    logger.info("create RocksDB[{}]",dbname);
  }

  @Override
  public void defaultInit() {
    specialInit(DataSourceSettings.getInstance());
  }

  @Override
  public void specialInit(DataSourceSettings sourceSettings) {
    logger.info("RocksDB[{}] init",dbname);
    readWriteLock.writeLock().lock();
    try {
      if (isAlive()) return;
      if (StringUtils.isEmpty(this.dbname)) throw new NullNameException("name is not null");

      Options options = new Options();

      // general options
      options.setCreateIfMissing(true);
      options.setCompressionType(CompressionType.NO_COMPRESSION);
      options.setCompactionStyle(CompactionStyle.UNIVERSAL);
      options.setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION);
      options.setLevelCompactionDynamicLevelBytes(true);
      options.setMaxOpenFiles(sourceSettings.getMaxOpenFiles());
      options.setIncreaseParallelism(sourceSettings.getMaxThreads());

      // key prefix for state node lookups
      //options.useFixedLengthPrefixExtractor(NodeKeyCompositor.PREFIX_BYTES);

      // table options
      final BlockBasedTableConfig tableCfg;
      options.setTableFormatConfig(tableCfg = new BlockBasedTableConfig());
      tableCfg.setBlockSize(16 * 1024);
      tableCfg.setBlockCacheSize(32 * 1024 * 1024);
      tableCfg.setCacheIndexAndFilterBlocks(true);
      tableCfg.setPinL0FilterAndIndexBlocksInCache(true);
      tableCfg.setFilter(new BloomFilter(10, false));

      // read options
      readOptions = new ReadOptions().setPrefixSameAsStart(true).setVerifyChecksums(false);




    } finally {
      readWriteLock.writeLock().unlock();
    }

  }

  @Override
  public void setDBName(String name) {

  }

  @Override
  public String getDBName() {
    return null;
  }

  @Override
  public boolean isAlive() {
    return false;
  }

  @Override
  public void reset() {

  }

  @Override
  public void deInit() {

  }

  @Override
  public Set<byte[]> keySet() {
    return null;
  }

  @Override
  public void batchUpdate(Map<byte[], byte[]> datas) {

  }

  @Override
  public void put(byte[] key, byte[] value) {

  }

  @Override
  public byte[] get(byte[] key) {
    return new byte[0];
  }

  @Override
  public void delete(byte[] key) {

  }

  @Override
  public boolean flushData() {
    return false;
  }
}
