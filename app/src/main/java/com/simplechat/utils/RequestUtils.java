package com.simplechat.utils;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Request buildRequestForGet(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }


    public static Request buildRequestForPostByJson(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static Request buildRequestForPostByForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry :
                    params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }
}
