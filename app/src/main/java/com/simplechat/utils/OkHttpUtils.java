package com.simplechat.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 使用单例模式构造一个OkHttpClient实例并初始化相关操作
 */
public class OkHttpUtils {
    private static OkHttpClient instance;
    public static OkHttpClient getInstance(){
        if (instance == null) {
            synchronized (OkHttpClient.class) {
                if (instance == null) {
                    instance = (new OkHttpClient()).newBuilder()
                            .connectTimeout(100, TimeUnit.SECONDS)
                            .readTimeout(100, TimeUnit.SECONDS)
                            .writeTimeout(300, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return instance;
    }
}
