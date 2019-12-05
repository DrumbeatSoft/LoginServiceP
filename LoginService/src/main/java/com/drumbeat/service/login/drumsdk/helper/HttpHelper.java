package com.drumbeat.service.login.drumsdk.helper;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.Utils;
import com.drumbeat.service.login.BuildConfig;
import com.drumbeat.service.login.drumsdk.kalle.DrumInterceptor;
import com.drumbeat.service.login.drumsdk.kalle.KalleCallback;
import com.drumbeat.service.login.drumsdk.kalle.NetCallback;
import com.yanzhenjie.kalle.JsonBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.KalleConfig;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.connect.BroadcastNetwork;
import com.yanzhenjie.kalle.cookie.DBCookieStore;
import com.yanzhenjie.kalle.simple.Converter;
import com.yanzhenjie.kalle.simple.SimpleBodyRequest;
import com.yanzhenjie.kalle.simple.SimpleUrlRequest;
import com.yanzhenjie.kalle.simple.cache.CacheMode;
import com.yanzhenjie.kalle.simple.cache.DiskCacheStore;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.yanzhenjie.kalle.Headers.KEY_CONTENT_TYPE;
import static com.yanzhenjie.kalle.Headers.VALUE_APPLICATION_JSON;

public class HttpHelper {

    /**
     * 初始化Http网络请求库
     */
    public static void init() {
        Kalle.setConfig(KalleConfig.newBuilder()
//                .connectFactory(OkHttpConnectFactory.newBuilder().build())
                .connectionTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .cookieStore(DBCookieStore.newBuilder(Utils.getApp()).build())
                .cacheStore(DiskCacheStore.newBuilder(PathUtils.getExternalAppCachePath()).build())
                .network(new BroadcastNetwork(Utils.getApp()))
                .setHeader(KEY_CONTENT_TYPE, VALUE_APPLICATION_JSON)
                .addInterceptor(new DrumInterceptor("Kalle", BuildConfig.DEBUG))
                .converter(Converter.DEFAULT)
                .build());
    }

    public static void post(String url, Map<String, String> headers, JSONObject jsonObject, NetCallback netCallback) {
        SimpleBodyRequest.Api postApi = Kalle.post(url);

        // 添加header
        if (headers != null && !headers.isEmpty()) {
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                String value = headers.get(key);
                postApi.addHeader(key, value);
            }
        }

        postApi.cacheMode(CacheMode.NETWORK)
                .body(new JsonBody(jsonObject.toJSONString()))
                .perform(new KalleCallback<String>() {
                    @Override
                    protected void onSuccess(String succeed) {
                        netCallback.onSuccess(succeed);
                    }

                    @Override
                    protected void onFailed(String failed) {
                        netCallback.onFailed(failed);
                    }
                });
    }

    public static void get(String url, Map<String, String> headers, Map<String, String> params, NetCallback netCallback) {
        SimpleUrlRequest.Api getApi = Kalle.get(url);
        // 添加header
        if (headers != null && !headers.isEmpty()) {
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                String value = headers.get(key);
                getApi.addHeader(key, value);
            }
        }
        // 包装参数
        Params.Builder builder = Params.newBuilder();
        if (params != null && !params.isEmpty()) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                String value = params.get(key);
                builder.add(key, value);
            }
        }
        getApi.params(builder.build())
                .cacheMode(CacheMode.NETWORK)
                // Kalle会为get请求设置默认的Content-Type
                .removeHeader(KEY_CONTENT_TYPE)
                .addHeader(KEY_CONTENT_TYPE, VALUE_APPLICATION_JSON)
                .perform(new KalleCallback<String>() {
                    @Override
                    protected void onSuccess(String succeed) {
                        netCallback.onSuccess(succeed);
                    }

                    @Override
                    protected void onFailed(String failed) {
                        netCallback.onFailed(failed);
                    }
                });
    }

}
