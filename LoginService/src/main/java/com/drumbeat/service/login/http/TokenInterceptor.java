package com.drumbeat.service.login.http;

/**
 * Token拦截器，用于处理token失效问题
 * Created by ZuoHailong on 2020/1/13.
 */
public interface TokenInterceptor {
    /**
     * token失效
     */
    void onInvalid();
}
