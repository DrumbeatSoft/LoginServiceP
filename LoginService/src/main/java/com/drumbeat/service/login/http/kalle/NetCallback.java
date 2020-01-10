package com.drumbeat.service.login.http.kalle;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public interface NetCallback {
    void onSuccess(String success);

    void onFailure(String failure);
}
