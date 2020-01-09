package com.drumbeat.service.login.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public abstract class Callback<T> {

    /**
     * 获取传入泛型的Type
     *
     * @return
     */
    public Type getSuccessType() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public abstract void onSuccess(T success);

    public abstract void onFailure(FailureBean failure);
}
