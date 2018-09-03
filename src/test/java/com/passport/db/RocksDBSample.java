package com.passport.db;
// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionType;
import org.rocksdb.Filter;
import org.rocksdb.HashLinkedListMemTableConfig;
import org.rocksdb.HashSkipListMemTableConfig;
import org.rocksdb.Options;
import org.rocksdb.PlainTableConfig;
import org.rocksdb.RateLimiter;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.SkipListMemTableConfig;
import org.rocksdb.Statistics;
import org.rocksdb.VectorMemTableConfig;
import org.rocksdb.util.SizeUnit;

public class RocksDBSample {

  static {
    RocksDB.loadLibrary();
  }

  public static void main(final String[] args) {
    //Options options = new Options();
    //options.setCreateIfMissing(true);
    RocksDB rocksDB = null;

    final Options options = new Options();
    final Filter bloomFilter = new BloomFilter(10);
    final ReadOptions readOptions = new ReadOptions()
        .setFillCache(false);
    final Statistics stats = new Statistics();
    final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);

    try {
      options.setCreateIfMissing(true)
          .setStatistics(stats)
          .setWriteBufferSize(8 * SizeUnit.KB)
          .setMaxWriteBufferNumber(3)
          .setMaxBackgroundCompactions(10)
          .setCompressionType(CompressionType.NO_COMPRESSION)
          .setCompactionStyle(CompactionStyle.UNIVERSAL);
    } catch (final IllegalArgumentException e) {
      assert (false);
    }

    options.setMemTableConfig(
        new HashSkipListMemTableConfig()
            .setHeight(4)
            .setBranchingFactor(4)
            .setBucketCount(2000000));

    options.setMemTableConfig(
        new HashLinkedListMemTableConfig()
            .setBucketCount(100000));

    options.setMemTableConfig(
        new VectorMemTableConfig().setReservedSize(10000));

    options.setMemTableConfig(new SkipListMemTableConfig());

    options.setTableFormatConfig(new PlainTableConfig());
    // Plain-Table requires mmap read
    options.setAllowMmapReads(true);

    options.setRateLimiter(rateLimiter);

    final BlockBasedTableConfig table_options = new BlockBasedTableConfig();
    table_options.setBlockCacheSize(64 * SizeUnit.KB)
        .setFilter(bloomFilter)
        .setCacheNumShardBits(6)
        .setBlockSizeDeviation(5)
        .setBlockRestartInterval(10)
        .setCacheIndexAndFilterBlocks(true)
        .setHashIndexAllowCollision(false)
        .setBlockCacheCompressedSize(64 * SizeUnit.KB)
        .setBlockCacheCompressedNumShardBits(10);

    options.setTableFormatConfig(table_options);

    try {
      rocksDB = RocksDB.open(options, "./passdb");
      rocksDB.put("hello".getBytes(), "world".getBytes());

      final byte[] value = rocksDB.get("hello".getBytes());
      assert ("world".equals(new String(value)));

      final String str = rocksDB.getProperty("rocksdb.stats");
      System.out.println(str);
    } catch (RocksDBException e) {
      e.printStackTrace();
    } finally {
      rocksDB.close();
    }
  }
}