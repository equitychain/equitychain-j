package com.passport.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.passport.crypto.eth.WalletFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class StoryFileUtil {
    protected File keyStoryDir;
    protected ObjectMapper objectMapper = new ObjectMapper();
    //             filepath   addressfileinfo
    protected Map<String, WalletFile> sotryFiles = new HashMap<>();
    {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    private StoryFileUtil(File keyStoryDir){
        this.keyStoryDir = keyStoryDir;
    }
    private static StoryFileUtil storyFileUtil;
    public static synchronized StoryFileUtil getStoryFileUtil(File keyStoryDir)throws Exception{
        if(storyFileUtil == null){
            storyFileUtil = new StoryFileUtil(keyStoryDir);
            storyFileUtil.reload();
        }
        if(keyStoryDir.exists()){
            throw new Exception(keyStoryDir.getPath()+" file is not exists");
        }
        if(!storyFileUtil.keyStoryDir.getPath().equals(keyStoryDir.getPath())){
            storyFileUtil.keyStoryDir = keyStoryDir;
            storyFileUtil.reload();
        }
        return storyFileUtil;
    }
    protected void reload(){
        sotryFiles.clear();
        File[] addressFiles = keyStoryDir.listFiles();
        if(addressFiles == null) return;
        for(int i = 0; i < addressFiles.length; i ++){
            File addressFile = addressFiles[i];
            if(addressFile.isFile() && addressFile.canRead()){
                try {
                    WalletFile fileInfo = objectMapper.readValue(addressFile,WalletFile.class);
                    sotryFiles.put(addressFile.getPath(),fileInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            continue;
        }
    }
    public void flush(){
        File[] addressFiles = keyStoryDir.listFiles();
        for(int i = 0; i < addressFiles.length; i ++){
            File addressFile = addressFiles[i];
            if(addressFile.isFile() && addressFile.canRead()){
                try {
                    WalletFile fileInfo = sotryFiles.get(addressFile.getPath());
                    if(fileInfo == null) {
                        fileInfo =objectMapper.readValue(addressFile, WalletFile.class);
                        sotryFiles.put(addressFile.getPath(),fileInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            continue;
        }
    }
    public Set<String> getAddresses(){
        Collection<WalletFile> collection = sotryFiles.values();
        Set<String> addresses = new HashSet<>();
        WalletFile[] file = new WalletFile[1];
        collection.forEach((f)->{
            addresses.add(f.getAddress());
        });
        return addresses;
    }

    public WalletFile getAddressInfo(String address){
        Collection<WalletFile> collection = sotryFiles.values();
        WalletFile[] file = new WalletFile[1];
        collection.forEach((f)->{
            if(f != null && f.getAddress().equals(address)){
                file[0] = f;
            }
        });
        return file[0];
    }

}
