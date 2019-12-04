package com.drumbeat.service.login;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public interface ResultCallback<T> {
    void onSuccess(T succeed);

    void onFail(ResultCode resultCode);
}
