package com.hailong.zbase.helper;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.hailong.zbase.http.callback.DownloadCallback;
import com.hailong.zbase.http.callback.HttpCallback;
import com.hailong.zbase.http.HttpClientFactory;
import com.hailong.zbase.http.constant.HttpClient;
import com.hailong.zbase.http.kalle.KalleClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网络帮助类
 * Created by ZuoHailong on 2019/12/30.
 */
public class HttpHelper {

    private static HttpClientFactory httpClient;

    /**
     * Initialize HttpClient
     *
     * @param httpClientEnum Enum object for
     */
    public static void init(HttpClient httpClientEnum) {
        switch (httpClientEnum) {
            case KALLE:
                httpClient = KalleClient.getInstance();
                break;
            default:
                httpClient = KalleClient.getInstance();
                break;
        }
        httpClient.init();
    }

    /**
     * Add a header.
     *
     * @param key   not null
     * @param value
     */
    public static void addHeader(@NonNull String key, String value) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(key, value);
        addHeaders(headers);
    }

    /**
     * Add a few headers.
     *
     * @param headers Map object for headers.
     */
    public static void addHeaders(Map<String, String> headers) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        httpClient.addHeaders(headers);
    }

    /**
     * Remove a header.
     *
     * @param key not null
     */
    public static void removeHeader(@NonNull String key) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        httpClient.removeHeader(key);
    }

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param urlParams    LinkedHashMap object of path params for url.
     * @param httpCallback
     * @param <T>
     */
    public static <T> void get(@NonNull String url, LinkedHashMap<String, String> urlParams, HttpCallback<T> httpCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        get(url, urlParams, null, httpCallback);
    }

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param params       Map object for params.
     * @param httpCallback
     * @param <T>
     */
    public static <T> void get(@NonNull String url, Map<String, String> params, HttpCallback<T> httpCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        get(url, null, params, httpCallback);
    }

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param urlParams    LinkedHashMap object of path params for url.
     * @param params       Map object for params.
     * @param httpCallback
     * @param <T>
     */
    public static <T> void get(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, HttpCallback<T> httpCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        httpClient.get(url, urlParams, params, httpCallback);
    }

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param requestBody  [Alibaba-fastjson] JSONObject object for requestBody.
     * @param httpCallback
     */
    public static <T> void post(@NonNull String url, JSONObject requestBody, HttpCallback<T> httpCallback) {
        post(url, null, requestBody, httpCallback);
    }

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param params       Map object for params.
     * @param httpCallback
     */
    /*public static <T> void post(@NonNull String url, Map<String, String> params, HttpCallback<T> httpCallback) {
        post(url, null, params, httpCallback);
    }*/

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param urlParams    LinkedHashMap object of path params for url.
     * @param params       Map object for params.
     * @param httpCallback
     * @param <T>
     */
    /*public static <T> void post(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, HttpCallback<T> httpCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        httpClient.post(url, urlParams, params, httpCallback);
    }*/

    /**
     * Send a post request.
     *
     * @param url          Full url.
     * @param urlParams    LinkedHashMap object of path params for url.
     * @param requestBody  [Alibaba-fastjson] JSONObject object for requestBody.
     * @param httpCallback
     * @param <T>
     */
    public static <T> void post(@NonNull String url, LinkedHashMap<String, String> urlParams, JSONObject requestBody, HttpCallback<T> httpCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        httpClient.post(url, urlParams, requestBody, httpCallback);
    }

    /**
     * Send a download request.
     *
     * @param url              file url
     * @param directory        local file directory
     * @param fileName         local file name
     * @param downloadCallback download callback
     */
    public static void download(@NonNull String url, @NonNull String directory, @NonNull String fileName, @NonNull DownloadCallback downloadCallback) {
        if (httpClient == null) {
            init(HttpClient.KALLE);
        }
        httpClient.download(url, directory, fileName, downloadCallback);
    }

}
