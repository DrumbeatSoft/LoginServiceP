package com.hailong.zbase.http;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.hailong.zbase.http.callback.DownloadCallback;
import com.hailong.zbase.http.callback.HttpCallback;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http 工厂类
 * Created by ZuoHailong on 2019/12/30.
 */
public interface HttpClientFactory<T> {

    void init();

    /**
     * 添加多个Header
     *
     * @param headers
     */
    void addHeaders(Map<String, String> headers);

    /**
     * 移除某个Header
     *
     * @param key not null
     */
    void removeHeader(@NonNull String key);

    void get(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, HttpCallback<T> httpCallback);

//    void post(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, JSONObject body, HttpCallback<T> httpCallback);

//    void post(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, HttpCallback<T> httpCallback);

    void post(@NonNull String url, LinkedHashMap<String, String> urlParams, JSONObject body, HttpCallback<T> httpCallback);

    void download(String url, String directory, String fileName, DownloadCallback downloadCallback);

    void cancel();

}
