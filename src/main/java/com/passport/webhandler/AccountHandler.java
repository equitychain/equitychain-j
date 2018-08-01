package com.passport.webhandler;

import com.passport.core.Account;
import com.passport.db.dbhelper.DBAccess;
import com.passport.listener.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountHandler.class);

    @Autowired
    private DBAccess dbAccess;

    //广播event用的
    @Autowired
    private ApplicationContextProvider provider;

    /**
     * 新增账号
     * @return 账号
     */
    public Account newAccount(){
        Account account = new Account();
        account.newAccount();
        if(dbAccess.putAccount(account)) {
            Optional<Account> minerAccount = dbAccess.getMinerAccount();
            if(!minerAccount.isPresent()){
                dbAccess.putMinerAccount(account);
            }
            return account;
        }
        return null;
    }
    //用户设置挖矿账号
    public Account setMinerAccount(String address){
        Optional<Account> addAccount = dbAccess.getAccount(address);
        if(addAccount.isPresent()){
            Account account = addAccount.get();
            if(dbAccess.putMinerAccount(account)){
                return account;
            }
        }
        return null;
    }
}
