package com.drumbeat.service.login.zbase.http.callback;

import com.drumbeat.service.login.zbase.http.HttpFail;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZuoHailong on 2019/12/30.
 */
public abstract class HttpCallback<T> {

    /**
     * 获取传入泛型的Type
     *
     * @return
     */
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public void onStart() {
    }

    public void onCancel() {
    }

    public abstract void onSuccess(T success);

    public abstract void onFail(HttpFail fail);

}
