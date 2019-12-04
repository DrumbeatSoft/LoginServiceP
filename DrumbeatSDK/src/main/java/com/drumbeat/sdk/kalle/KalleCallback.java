package com.drumbeat.sdk.kalle;

import com.blankj.utilcode.util.StringUtils;
import com.drumbeat.sdk.R;
import com.yanzhenjie.kalle.exception.ConnectException;
import com.yanzhenjie.kalle.exception.ConnectTimeoutError;
import com.yanzhenjie.kalle.exception.HostError;
import com.yanzhenjie.kalle.exception.NetworkError;
import com.yanzhenjie.kalle.exception.ReadTimeoutError;
import com.yanzhenjie.kalle.exception.URLError;
import com.yanzhenjie.kalle.exception.WriteException;
import com.yanzhenjie.kalle.simple.Callback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZuoHailong on 2019/12/2.
 */
public abstract class KalleCallback<T> extends Callback<T, String> {

    public KalleCallback() {
    }

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
    public void onResponse(SimpleResponse<T, String> response) {
        if (response.isSucceed()) {
            onSuccess(response.succeed());
        } else {
            onFailed(response.failed());
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onException(Exception e) {
        String message;
        if (e instanceof NetworkError) {
            message = StringUtils.getString(R.string.http_exception_network);
        } else if (e instanceof URLError) {
            message = StringUtils.getString(R.string.http_exception_url);
        } else if (e instanceof HostError) {
            message = StringUtils.getString(R.string.http_exception_host);
        } else if (e instanceof ConnectTimeoutError) {
            message = StringUtils.getString(R.string.http_exception_connect_timeout);
        } else if (e instanceof WriteException) {
            message = StringUtils.getString(R.string.http_exception_write);
        } else if (e instanceof ReadTimeoutError) {
            message = StringUtils.getString(R.string.http_exception_read_timeout);
        } else if (e instanceof ConnectException) {
            message = StringUtils.getString(R.string.http_exception_connect_fail);
        } else {
            message = StringUtils.getString(R.string.http_exception_unknow_error);
        }
        onResponse(SimpleResponse.<T, String>newBuilder().failed(message).build());
    }

    protected abstract void onSuccess(T succeed);

    protected abstract void onFailed(String failed);

}
