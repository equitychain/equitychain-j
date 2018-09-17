package com.passport.db;
// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

import com.passport.utils.SerializeUtils;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.util.ArrayList;
import java.util.List;

public class RocksDBSample {

    static {
        RocksDB.loadLibrary();
    }

    public static void main(final String[] args) {
        //Options options = new Options();
        //options.setCreateIfMissing(true);
        RocksDB rocksDB = null;


        final Options options = new Options();
        final WriteOptions writeOptions = new WriteOptions();
//        writeOptions.setSync(true).setDisableWAL(true);

        final Filter bloomFilter = new BloomFilter(10);
        final ReadOptions readOptions = new ReadOptions()
                .setFillCache(false);
        final Statistics stats = new Statistics();
//        final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);

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

//        options.setRateLimiter(rateLimiter);

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
        WriteBatch writeBatch = new WriteBatch();
        writeBatch.setSavePoint();
        try {
//            OptimisticTransactionDB transactionDB = OptimisticTransactionDB.open(new Options(),"d:/dfs");
            rocksDB = RocksDB.open(options, "./passdb");
            Snapshot snapshot = rocksDB.getSnapshot();

            TransactionDB transactionDB = TransactionDB.open(new Options(),new TransactionDBOptions(),"./passdb");
            for(int i = 20;i<30;i++){
                writeBatch.put(("test_"+i).getBytes(),(i+"").getBytes());
                writeBatch.delete("".getBytes());
                if(i == 25){
                    throw new Exception("");
                }
            }

            rocksDB.write(writeOptions,writeBatch);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                writeBatch.rollbackToSavePoint();
            }catch (Exception e1){
                e1.printStackTrace();
            }
        } finally {
            RocksIterator iterator = rocksDB.newIterator();
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                System.out.println(new String(iterator.key())+":"+new String(iterator.value()));
            }
            rocksDB.close();
        }
    }
    public <T> List<T> seekByKey(RocksDB rocksDB, String keyPrefix) {
        ArrayList<T> ts = new ArrayList<>();
        ReadOptions options = new ReadOptions();
        options.setPrefixSameAsStart(true);
        RocksIterator iterator = rocksDB.newIterator(options);
        byte[] key = keyPrefix.getBytes();
        for (iterator.seek(key); iterator.isValid(); iterator.next()) {
            if (!new String(iterator.key()).startsWith(keyPrefix)) continue;
//		for (iterator.seek(key); iterator.isValid() && String.valueOf(iterator.key()).startsWith(keyPrefix); iterator.next()) {
            ts.add((T) SerializeUtils.unSerialize(iterator.value()));
        }
        return ts;
    }
}