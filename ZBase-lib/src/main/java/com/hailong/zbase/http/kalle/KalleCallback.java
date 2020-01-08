package com.hailong.zbase.http.kalle;

import com.blankj.utilcode.util.Utils;
import com.hailong.zbase.R;
import com.hailong.zbase.helper.LogHelper;
import com.yanzhenjie.kalle.exception.ConnectTimeoutError;
import com.yanzhenjie.kalle.exception.DownloadError;
import com.yanzhenjie.kalle.exception.HostError;
import com.yanzhenjie.kalle.exception.NetworkError;
import com.yanzhenjie.kalle.exception.ParseError;
import com.yanzhenjie.kalle.exception.ReadTimeoutError;
import com.yanzhenjie.kalle.exception.URLError;
import com.yanzhenjie.kalle.exception.WriteException;
import com.yanzhenjie.kalle.simple.Callback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZuoHailong on 2019/12/31.
 */
public abstract class KalleCallback<S> extends Callback<S, String> {

    @Override
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        return String.class;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onException(Exception e) {
        String message;
        if (e instanceof NetworkError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_network);
        } else if (e instanceof URLError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_url);
        } else if (e instanceof HostError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_host);
        } else if (e instanceof ConnectTimeoutError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_connect_timeout);
        } else if (e instanceof WriteException) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_write);
        } else if (e instanceof ReadTimeoutError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_read_timeout);
        } else if (e instanceof ParseError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_parse_error);
        } else if (e instanceof DownloadError) {
            message = Utils.getApp().getString(R.string.zbase_http_exception_download_error);
        } else {
            message = Utils.getApp().getString(R.string.zbase_http_exception_unknow_error);
        }
        LogHelper.e(e);
        onResponse(SimpleResponse.<S, String>newBuilder().failed(message).build());
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onEnd() {
    }
}
