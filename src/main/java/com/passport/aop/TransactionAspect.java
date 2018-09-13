package com.passport.aop;


import com.passport.db.dbhelper.BaseDBAccess;
import com.passport.db.dbhelper.BaseDBRocksImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.rocksdb.Snapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @ClassName TransactionAspect
 * @Description TODO
 * @Author 岳东方
 * @Date 下午4:16
 **/

@Aspect
@Component
public class TransactionAspect {

    @Autowired
    BaseDBAccess baseDBAccess;

    @Pointcut("@annotation(com.passport.annotations.RocksTransaction)")
    private void sig() {
    }//切入点签名

    @Before("sig()")//snapshot
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        baseDBAccess.setSnapshot(baseDBAccess.getCurrentSnapshot());
        // 记录下请求内容
    }
    /**
     * 异常通知:当事务执行报异常的时候执行该回滚操作, 如果是服务器宕机等无法通过程序立即恢复的情况, 则在下一次服务启动的时候
     * 通过实例化的文件来启动
     */
    @AfterThrowing(value = "sig()", throwing = "e")//传到 after snapshot
    public void throwingMethod(Exception e) {
        System.err.println("--------------事务出错 开始回滚--------------");
        Snapshot snapshot = baseDBAccess.getCurrentSnapshot();
        org.rocksdb.ReadOptions options = new org.rocksdb.ReadOptions();
        options.setSnapshot(snapshot);
        try {
//            for(String s : baseDBAccess.getKeysSet() ){
//                System.out.println("事务key："+s);
//                byte[] old = baseDBAccess.getRocksDB().get(options, s.getBytes());
//                if(old!=null){
//                    baseDBAccess.getRocksDB().put(s.getBytes(), old);
//                }else {
//                    baseDBAccess.getRocksDB().delete(s.getBytes());
//                }
//            }
//            RocksIterator iterator = baseDBAccess.getRocksDB().newIterator(options);
//            for(iterator.seekToFirst();iterator.isValid();iterator.next()){
//                System.out.println(("快照数据：")+new String(iterator.key())+"---"+new String(iterator.value()));
//            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
//        RocksIterator iterator = baseDBAccess.seekByKey("".getBytes());
//        for(iterator.seekToFirst();iterator.isValid();iterator.next()){
//            System.out.println(("真实数据：")+new String(iterator.key())+"---"+new String(iterator.value()));
//        }
        baseDBAccess.close();
        System.err.println("--------------事务出错 回滚成功--------------");
    }
    @AfterReturning(returning = "ret", pointcut = "sig()")
    public void doAfterReturning(Object ret) throws Throwable {
        //测试查询
        System.out.println("测试AfterReturning");
//        RocksDB rocksDB = baseDBAccess.getRocksDB();
//        try {
//            RocksIterator iterator = rocksDB.newIterator(new ReadOptions());
//            for(iterator.seekToFirst();iterator.isValid();iterator.next()){
//                System.out.println(new String(iterator.key())+"---"+new String(iterator.value()));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        // 处理完请求，关闭close
        baseDBAccess.close();

    }



}
