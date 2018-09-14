package com.passport.db.transaction;


import com.passport.utils.FileUtil;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName DbBackResult
 * @Description TODO
 * @Author 岳东方
 * @Date 下午4:53
 **/
@Component
public class RocksdbTransaction {

    private Set<String> KeysSet;
    private RocksDB rocksDB;
    private Snapshot snapshot ;
    private RocksBackup rocksBackup = new RocksBackup();
    private Map<String,ColumnFamilyHandle> handleMap = new HashMap<>();
    @Value("${db.dataDir}")
    private String dataDir;

    public RocksBackup getRocksBackup() {
        return rocksBackup;
    }

    public void setRocksBackup(RocksBackup rocksBackup) {
        this.rocksBackup = rocksBackup;
    }

    public RocksDB getRocksDB() {
        return rocksDB;
    }

    public void setRocksDB(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Set<String> getKeysSet() {
        return KeysSet;
    }

    public Map<String, ColumnFamilyHandle> getHandleMap() {
        return handleMap;
    }

    public void setHandleMap(Map<String, ColumnFamilyHandle> handleMap) {
        this.handleMap = handleMap;
    }

    public void setKeysSet(Set<String> keysSet) {
        KeysSet = keysSet;
    }

    private RocksdbTransaction() {

    }
    /**
     * @Description: 打开事务
     * @Params
     * @Return TODO
     * @Author: 岳东方
     * @Date: 2018/9/11 下午8:48
     **/
    /**
     * 初始化RocksDB
     */
//    @PostConstruct
    public void initRocksDB() {
            rocksBackup.setUrl(System.getProperty("user.dir") + "/" + dataDir+File.separator+"backup.obj");
            this.setKeysSet(new HashSet<>());
    }

    /**
     * @Description: 关闭事务
     * @Params
     * @Return TODO
     * @Author: 岳东方
     * @Date: 2018/9/11 下午8:48
     **/
    public void close() {
        //在一个事务的最后调用该方法,由切面决定调用时间(当事务注解的方法完成的时候执行)
        //关闭DB对象实例,情况Keyset,快照
        //todo 接下来还要加入清空实例化对象文件, 记录keySet对象的实例化对象文件之后如果事务正确执行完毕, 则清除该文件,如果执行过程中出现 问题,则在启动的时候逐一回复, 逐一回复的会后应从linkedList最后开始一次恢复
        rocksDB.close();
        setKeysSet(new HashSet());
        setRocksBackup(new RocksBackup());
        setSnapshot(null);
        FileUtil.delete(rocksBackup.getUrl());

    }

    public byte[] get(byte[] key) throws RocksDBException {
        return rocksDB.get(key);
    }

    public boolean add(byte[] key, byte[] value) throws RocksDBException {
        this.getKeysSet().add(new String(new String(new String(key)+",default")));
        backup();//备份该次操作的key
        rocksDB.put(key, value);
        return true;
    }
    public boolean add(String columnFamily,byte[] key, byte[] value) throws RocksDBException {
        this.getKeysSet().add(new String(new String(key)+","+columnFamily));
        backup();//备份该次操作的key
        ColumnFamilyHandle columnFamilyHandle= handleMap.get(columnFamily)!=null?handleMap.get(columnFamily):
                rocksDB.createColumnFamily(new ColumnFamilyDescriptor(columnFamily.getBytes()));
        rocksDB.put(columnFamilyHandle,key, value);
        if (columnFamilyHandle == null) {
            handleMap.put(columnFamily, columnFamilyHandle);
        }
        return true;
    }

    public boolean delete(byte[] key) throws RocksDBException {
        this.getKeysSet().add(new String(new String(new String(key)+",default")));
//        backup();//备份该次操作的key
        rocksDB.delete(key);
        return true;
    }
    public boolean delete(String columnFamily,byte[] key) throws RocksDBException {
        this.getKeysSet().add(new String(new String(key)+","+columnFamily));
        ColumnFamilyHandle columnFamilyHandle= handleMap.get(columnFamily)!=null?handleMap.get(columnFamily):
                rocksDB.createColumnFamily(new ColumnFamilyDescriptor(columnFamily.getBytes()));
//        backup();//备份该次操作的key
        rocksDB.delete(columnFamilyHandle,key);
        if (columnFamilyHandle == null) {
            handleMap.put(columnFamily, columnFamilyHandle);
        }
        return true;
    }


    public void backup(){

        rocksBackup.getKeySets().add(getKeysSet());

        try {
            FileOutputStream fos = new FileOutputStream(rocksBackup.getUrl());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rocksBackup);
            oos.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
//    /**
//     * @Description: 新增数据
//     * @Params
//     * @Return TODO
//     * @Author: 岳东方
//     * @Date: 2018/9/11 下午8:48
//     **/
//    public static void insert(Map map) {
//        // 新增操作支持批量, 先将map对象转换为WriteBatch对象
//        // 再将对象写入数据库, 提交的时候将BatchResult 对象情况
//        WriteBatch writeBatch = instance.getDbImpl().createWriteBatch();
//        Iterator<Map.Entry<String, Object>> iter =  map.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, Object> entry = iter.next();
//            writeBatch.put(entry.getKey().getBytes(), ObejctUtil.toByteArray(entry.getValue()));
//        }
//        BatchResult.setResultWriteBath(writeBatch);
//        BatchResult.setResultMap(map);
//        instance.commit();
////        BatchResult.commit();
//    }
//
//    /**
//     * @Description: 提交事务
//     * @Params
//     * @Return TODO
//     * @Author: 岳东方
//     * @Date: 2018/9/11 下午8:48
//     **/
//    public static void commit() {
//        //先将结果批量写入库,在结果批量插入成功之后,记录该次操作的键值,注意多次键值对象是List<Set>类型的,用于一个事务里面存在多持久化
//        //操作的时候,方便依次回滚,提交并记录键值之后, 销毁BatchResult 以继续一次事务中接下来的持久化操作
//        instance.getDbImpl().write(BatchResult.getResultWriteBath());
//        // todo  插入成功之后还要讲keySet 实例化到本地文件
//        instance.getKeysSet().add(BatchResult.getResultMap().keySet());
//        BatchResult.destroy();
//    }
//    /**
//     * @Description: 回滚事务
//     * @Params
//     * @Return TODO
//     * @Author: 岳东方
//     * @Date: 2018/9/11 下午8:48
//     **/
//    public static void rollBack() {
//        //事务的回滚总共有三种情况: 1. 比较原来新增的 2. 比较原来修改的 3. 和原来没有变的
//        //对比快照之后如果该键值是新增的, 则执行删除操作, 如果是修改和不变的则put成原来的值
//        //todo 该处如果考虑不仅仅存储字符串的话就要进行数据类型的判断
//        for (Set<String> list : instance.getKeysSet()) {
//            for (String s : list) {
//                byte[] value = instance.getDbImpl().get(s.getBytes(), new ReadOptions().snapshot(instance.getSnapshot()));
//                if (value == null) {
//                    instance.getDbImpl().delete(s.getBytes());
//                } else if(value.equals(s.getBytes())){
//                    instance.getDbImpl().put(s.getBytes(), value);
//                }
//            }
//        }
//    }
//
//
//    /**
//     * @Description: 创建数据库
//     * @Params
//     * @Return TODO  待实现
//     * @Author: 岳东方
//     * @Date: 2018/9/11 下午8:48
//     **/
}
