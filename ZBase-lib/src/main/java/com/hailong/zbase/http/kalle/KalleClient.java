package com.hailong.zbase.http.kalle;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.Utils;
import com.hailong.zbase.BuildConfig;
import com.hailong.zbase.helper.SPStaticHelper;
import com.hailong.zbase.http.callback.DownloadCallback;
import com.hailong.zbase.http.callback.HttpCallback;
import com.hailong.zbase.http.HttpClientFactory;
import com.hailong.zbase.http.HttpFail;
import com.hailong.zbase.http.constant.HttpConstant;
import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.JsonBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.KalleConfig;
import com.yanzhenjie.kalle.OkHttpConnectFactory;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.connect.BroadcastNetwork;
import com.yanzhenjie.kalle.cookie.DBCookieStore;
import com.yanzhenjie.kalle.download.Callback;
import com.yanzhenjie.kalle.download.Download;
import com.yanzhenjie.kalle.simple.Converter;
import com.yanzhenjie.kalle.simple.SimpleBodyRequest;
import com.yanzhenjie.kalle.simple.SimpleResponse;
import com.yanzhenjie.kalle.simple.SimpleUrlRequest;
import com.yanzhenjie.kalle.simple.cache.CacheMode;
import com.yanzhenjie.kalle.simple.cache.DiskCacheStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Kalle的Http帮助类，用于初始化、添加header、发起网络请求等
 * Created by ZuoHailong on 2019/12/30.
 */
public class KalleClient<T> implements HttpClientFactory<T> {

    private KalleClient() {
    }

    private static KalleClient kalleClient;

    public static KalleClient getInstance() {
        if (kalleClient == null) {
            synchronized (KalleClient.class) {
                if (kalleClient == null) {
                    kalleClient = new KalleClient();
                }
            }
        }
        return kalleClient;
    }

    @Override
    public void init() {
        KalleConfig config = KalleConfig.newBuilder()
                .connectionTimeout(HttpConstant.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HttpConstant.READ_TIME_OUT, TimeUnit.SECONDS)
//                .addHeader(Headers.KEY_CONTENT_TYPE, Headers.VALUE_APPLICATION_JSON)
                .setHeader(Headers.KEY_CONTENT_TYPE, Headers.VALUE_APPLICATION_JSON)
                // 让 Kalle 检查网络是否可用
                .network(new BroadcastNetwork(Utils.getApp()))
                // 配置连接工厂，URLConnectionFactory/OkHttpConnectFactory，默认 URLConnectionFactory
                .connectFactory(OkHttpConnectFactory.newBuilder().build())
                // 配置全局CookieStore，用来增删改查Cookie
                .cookieStore(DBCookieStore.newBuilder(Utils.getApp()).build())
                // 全局缓存
                .cacheStore(DiskCacheStore.newBuilder(PathUtils.getInternalAppCachePath()).build())
                // 日志拦截器，可以添加多个拦截器，分别做参数签名、Token/Cookie失效时登录重试、失败后重试、Log打印、重定向等
                .addInterceptor(new LoggerInterceptor(HttpConstant.LOG_TAG, BuildConfig.DEBUG))
                // Request 加签拦截器
                .addInterceptor(new SignInterceptor())
                // 数据转换器
                .converter(Converter.DEFAULT)
                .build();
        Kalle.setConfig(config);
    }

    @Override
    public void removeHeader(@NonNull String key) {
        // 添加null值的header，会在SignInterceptor中移除
        Map<String, String> headers = new HashMap<>();
        headers.put(key, "");
        addHeaders(headers);
    }

    @Override
    public void addHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        // 先获取原有Header
//        Set<String> headerSet = new HashSet<>(SPStaticHelper.getStringSet(HttpConstant.SP_HEADERS));
        Set<String> headerSet = new HashSet<>();

        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            // null value 也要放入集合，removeHeader() 需要此逻辑
            String value = (TextUtils.isEmpty(headers.get(key)) ? "" : headers.get(key));
            headerSet.add(key + "," + value);
        }
        // 这里存起来，会在 HttpClient 的加签拦截器添加到 Request 中
        SPStaticHelper.put(HttpConstant.SP_HEADERS, headerSet);
    }

    @Override
    public void get(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, final HttpCallback<T> httpCallback) {

        SimpleUrlRequest.Api getApi = Kalle.get(url);

        // urlParams
        if (urlParams != null && !urlParams.isEmpty()) {
            Set<String> keySet = urlParams.keySet();
            for (String key : keySet) {
                getApi.path(key).path(urlParams.get(key));
            }
        }
        // params
        if (params != null && !params.isEmpty()) {
            Params.Builder builder = Params.newBuilder();
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                builder.add(key, params.get(key));
            }
            getApi.params(builder.build());
        }
        // send request
        getApi.cacheMode(CacheMode.NETWORK)
                .perform(new KalleCallback<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        httpCallback.onStart();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        httpCallback.onCancel();
                    }

                    @Override
                    public void onResponse(SimpleResponse<String, String> response) {
                        if (response.isSucceed()) {
                            httpCallback.onSuccess(JSON.parseObject(response.succeed(), httpCallback.getSucceed()));
                        } else {
                            httpCallback.onFail(new HttpFail().setCode(response.code()).setMessage(response.failed()));
                        }
                    }
                });
    }

    /*@Override
    public void post(@NonNull String url, LinkedHashMap<String, String> urlParams, Map<String, String> params, HttpCallback<T> httpCallback) {

        SimpleBodyRequest.Api postApi = Kalle.post(url);

        // urlParams
        if (urlParams != null && !urlParams.isEmpty()) {
            Set<String> keySet = urlParams.keySet();
            for (String key : keySet) {
                postApi.urlParam(key, urlParams.get(key));
            }
        }
        // params
        if (params != null && !params.isEmpty()) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                postApi.param(key, params.get(key));
            }
        }
        // send request
        postApi.cacheMode(CacheMode.NETWORK)
                .perform(new KalleCallback<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        httpCallback.onStart();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        httpCallback.onCancel();
                    }

                    @Override
                    public void onResponse(SimpleResponse<String, String> response) {
                        if (response.isSucceed()) {
                            httpCallback.onSuccess(JSON.parseObject(response.succeed(), httpCallback.getSucceed()));
                        } else {
                            httpCallback.onFail(new HttpFail().setCode(response.code()).setMessage(response.failed()));
                        }
                    }
                });
    }*/

    @Override
    public void post(@NonNull String url, LinkedHashMap<String, String> urlParams, JSONObject body, HttpCallback<T> httpCallback) {

        SimpleBodyRequest.Api postApi = Kalle.post(url);

        // urlParams
        if (urlParams != null && !urlParams.isEmpty()) {
            Set<String> keySet = urlParams.keySet();
            for (String key : keySet) {
                postApi.urlParam(key, urlParams.get(key));
            }
        }
        // requestBody
        if (body != null) {
            postApi.body(new JsonBody(body.toJSONString()));
        }
        // send request
        postApi.cacheMode(CacheMode.NETWORK)
                .perform(new KalleCallback<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        httpCallback.onStart();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        httpCallback.onCancel();
                    }

                    @Override
                    public void onResponse(SimpleResponse<String, String> response) {
                        if (response.isSucceed()) {
                            httpCallback.onSuccess(JSON.parseObject(response.succeed(), httpCallback.getSucceed()));
                        } else {
                            httpCallback.onFail(new HttpFail().setCode(response.code()).setMessage(response.failed()));
                        }
                    }
                });
    }

    @Override
    public void download(@NonNull String url, @NonNull String directory, @NonNull String fileName, @NonNull DownloadCallback downloadCallback) {
        Kalle.Download.get(url)
                .directory(directory)
                .fileName(fileName)
                .onProgress((progress, byteCount, speed) -> downloadCallback.onProgress(progress, byteCount, speed))
                .perform(new Callback() {
                    @Override
                    public void onStart() {
                        downloadCallback.onStart();
                    }

                    @Override
                    public void onFinish(String path) {
                        downloadCallback.onSuccess(path);
                    }

                    @Override
                    public void onException(Exception e) {
                        downloadCallback.onFail(new HttpFail().setCode(-1).setMessage(e.getMessage()));
                    }

                    @Override
                    public void onCancel() {
                        downloadCallback.onCancel();
                    }

                    @Override
                    public void onEnd() {

                    }
                });
    }

    @Override
    public void cancel() {

    }


}
