package com.passport.proto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Lin
 * @Description:
 * @Date:9:58 2018/1/2
 * @Modified by:
 */
public class GenerateProto {
    public static void main(String[] args) throws IOException {
        String protoPath = System.getProperty("user.dir") + "\\src\\main\\proto";
        List<String> protoFileList = new ArrayList<String>();
        File f = new File(protoPath);
        File fa[] = f.listFiles();
        for (File fs : fa) {
            if (fs.isFile()) {
                protoFileList.add(fs.getName());
            }
        }
        for (String protoFile : protoFileList) {
            String strCmd = "protoc --java_out=../../java " + protoFile;
            Runtime.getRuntime().exec(strCmd, null, new File(protoPath));
            System.out.println("protoPath:"+protoPath+":"+strCmd);
        }
    }

}