package com.simplechat.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static String readTextFile(FileInputStream fis){
        byte[] buf = new byte[1024];
        int length = 0;
        //循环读取文件内容
        StringBuilder sb = new StringBuilder();
        try {
            while((length = fis.read(buf)) != -1){
                sb.append(new String(buf, 0, length));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
