package com.drumbeat.service.login;

import com.drumbeat.service.login.constant.ResultCode;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public interface ResultCallback<T> {
    void onSuccess(T succeed);

    void onFail(ResultCode resultCode);
}
