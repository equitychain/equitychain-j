package com.passport.aop;


import com.passport.db.dbhelper.BaseDBAccess;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;


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
    BaseDBAccess dbAccess;

    long beginTime ;

    public static ReentrantLock lock = new ReentrantLock();

    @Pointcut("@annotation(com.passport.annotations.RocksTransaction)")
    private void sig() {
    }//切入点签名

    @Before("sig()")//snapshot
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        beginTime = System.currentTimeMillis();
        lock.lock();
//        System.out.println(Thread.currentThread().getName()+"加锁(开启事务)=============!~"+DateFormatUtils.format(new Date(),"yyyy-MM-dd hh:mm:ss"));
        dbAccess.transaction = dbAccess.rocksDB.beginTransaction(new WriteOptions());
    }

    /**
     * 异常通知:当事务执行报异常的时候执行该回滚操作, 如果是服务器宕机等无法通过程序立即恢复的情况, 则在下一次服务启动的时候
     * 通过实例化的文件来启动
     */
    @AfterThrowing(value = "sig()", throwing = "e")//传到 after snapshot
    public void throwingMethod(Exception e) throws RocksDBException {
        dbAccess.transaction.rollback();
        lock.unlock();
//        System.out.println("解锁(回滚)=============!~"+DateFormatUtils.format(new Date(),"yyyy-MM-dd hh:mm:ss"));
    }
    @AfterReturning(returning = "ret", pointcut = "sig()")
    public void doAfterReturning(Object ret) throws Throwable {
        dbAccess.transaction.commit();
        lock.unlock();
//        System.err.println("解锁(提交)=============!~"+DateFormatUtils.format(new Date(),"yyyy-MM-dd hh:mm:ss")+"===耗时"+(System.currentTimeMillis()-beginTime));
    }



}
