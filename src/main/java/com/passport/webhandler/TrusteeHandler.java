package com.passport.webhandler;

import com.passport.db.dbhelper.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrusteeHandler {
    private static final Logger logger = LoggerFactory.getLogger(TrusteeHandler.class);

    @Autowired
    private DBAccess dbAccess;

}
