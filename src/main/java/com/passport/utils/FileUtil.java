package com.passport.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileUtil
 * @Description TODO
 * @Author 岳东方
 * @Date 上午10:43
 **/
public class FileUtil {

    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtil.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }
    public static boolean writeFile(String filepath, String newstr) throws IOException {
        Boolean bool = false;
        String filein = newstr + "\r\n";
        String temp = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File ( filepath );
            if ( !file.exists ( ) ) {
                file.createNewFile ( );
            }
            fis = new FileInputStream ( file );
            isr = new InputStreamReader ( fis );
            br = new BufferedReader ( isr );
            StringBuffer buffer = new StringBuffer ( );
            for (int i = 0; (temp = br.readLine ( )) != null; i++) {
                buffer = buffer.append ( temp );
                buffer = buffer.append ( System.getProperty ( "line.separator" ) );
            }
            buffer = buffer.append ( filein );
            fos = new FileOutputStream ( file );
            pw = new PrintWriter ( fos );
            pw.write ( buffer.toString ( ).toCharArray ( ) );
            pw.flush ( );
            bool = true;
        } catch (Exception e) {
            e.printStackTrace ( );
        } finally {
            if ( pw != null ) {
                pw.close ( );
            }
            if ( fos != null ) {
                fos.close ( );
            }
            if ( br != null ) {
                br.close ( );
            }
            if ( isr != null ) {
                isr.close ( );
            }
            if ( fis != null ) {
                fis.close ( );
            }
        }
        return bool;
    }

    public static List<String> readFileByLines(String fileName) {
        File file = new File ( fileName );
        BufferedReader reader = null;
        List<String> list = new ArrayList<String>( );
        try {
            reader = new BufferedReader ( new FileReader ( file ) );
            String tempString = null;
            while ((tempString = reader.readLine ( )) != null) {
                list.add ( tempString );
            }
            reader.close ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        } finally {
            if ( reader != null ) {
                try {
                    reader.close ( );
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }


    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, String> traverseFolder(String path) {
        String encoding = "UTF-8";
        Map<String, String> resultMap = new HashMap<>( );
        File file = new File ( path );
        if ( !file.exists ( ) ) {
            file.mkdir ( );
        }
        File[] files = file.listFiles ( );
        if ( files == null || files.length == 0 ) {
        } else {
            for (File file2 : files) {
                String account = "";
                String address = "";
                if ( file2.isDirectory ( ) ) {
                    traverseFolder ( file2.getAbsolutePath ( ) );
                } else {
                    account = readAccountFileString ( file2 );
                    address = file2.getName ( );
                }
                if ( address.contains ( "px" ) ) {
                    resultMap.put ( address, account );
                }
            }
        }

        return resultMap;
    }

    public static <T extends Serializable> T clone(T obj) {
        T cloneObj = null;
        try {
            // 写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream ( );
            ObjectOutputStream obs = new ObjectOutputStream ( out );
            obs.writeObject ( obj );
            obs.close ( );

            // 分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream ( out.toByteArray ( ) );
            ObjectInputStream ois = new ObjectInputStream ( ios );
            // 返回生成的新对象
            cloneObj = (T) ois.readObject ( );
            ois.close ( );
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        return cloneObj;
    }

    public static String readAccountFileString(File file) {
        String encoding = "UTF-8";
        Long filelength = file.length ( ) - "\r\n".length ( );
        Long readLenght = filelength < 0 ? 0 : filelength;
        byte[] filecontent = new byte[readLenght.intValue ( )];
        try {
            FileInputStream in = new FileInputStream ( file );
            in.read ( filecontent );
            in.close ( );
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        try {
            return new String ( filecontent, encoding );
        } catch (UnsupportedEncodingException e) {
            System.err.println ( "The OS does not support " + encoding );
            e.printStackTrace ( );
            return null;
        }
    }
}
