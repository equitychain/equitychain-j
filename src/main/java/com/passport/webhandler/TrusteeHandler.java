package com.passport.webhandler;

import com.google.common.base.Optional;
import com.passport.core.Trustee;
import com.passport.db.dbhelper.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrusteeHandler {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeHandler.class);

    @Autowired
    private DBAccess dbAccess;

    /**
     * 改变已经出块人的状态
     * @param trustee
     * @param blockCycle
     */
    public void changeStatus(Trustee trustee, int blockCycle) {
        Optional<Object> objectOptional = dbAccess.get(String.valueOf(blockCycle));
        if(objectOptional.isPresent()){
            List<Trustee> list = (List<Trustee>)objectOptional.get();
            for(Trustee tee : list){
                if(tee.getAddress().equals(trustee.getAddress())){
                    tee.setStatus(0);//状态设置为已出场
                    break;
                }
            }
            dbAccess.put(String.valueOf(blockCycle), list);
        }
    }
}
