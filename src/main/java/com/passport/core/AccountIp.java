package com.passport.core;

import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;

@EntityClaz(name = "accountIp")
public class AccountIp {
    @KeyField
    @FaildClaz(name = "id",type = String.class)
    private String id;
    @FaildClaz(name = "address",type = String.class)
    private String address;
    @FaildClaz(name = "ipAddr",type = String.class)
    private String ipAddr;
    @FaildClaz(name = "statu",type = int.class)
    private int statu = 0;

    public void setId() {
        id = address+"-"+ipAddr;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }
}
