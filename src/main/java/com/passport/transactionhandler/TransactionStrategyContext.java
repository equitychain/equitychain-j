package com.passport.transactionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xujianfeng
 * @create: 2018-09-10 17:39
 **/
@Component
public class TransactionStrategyContext {
    @Autowired
    private Map<String, TransactionStrategy> strategyMap = new ConcurrentHashMap<>();

    public void setStrategyMap(Map<String, TransactionStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public TransactionStrategy getTransactionStrategy(String tradeType) {
        return strategyMap.get(tradeType);
    }
}
