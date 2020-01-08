package com.hailong.zbase.http.retrofit;

import com.hailong.zbase.http.HttpClientFactory;

/**
 * retrofit http 封装类，具体编码时将 abstract 去掉
 * Created by ZuoHailong on 2019/12/30.
 */
public abstract class RetrofitClient<T> implements HttpClientFactory<T> {

    private RetrofitClient() {
    }

    private static RetrofitClient retrofitClient;

    public static RetrofitClient getInstance() {
        if (retrofitClient == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitClient == null) {
//                    retrofitClient = new RetrofitClient();
                }
            }
        }
        return retrofitClient;
    }
    
}
