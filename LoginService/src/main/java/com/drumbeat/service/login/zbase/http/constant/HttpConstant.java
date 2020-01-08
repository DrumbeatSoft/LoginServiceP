package com.drumbeat.service.login.zbase.http.constant;

/**
 * Created by ZuoHailong on 2019/12/30.
 */
public class HttpConstant {

    public final static String LOG_TAG = "HttpHelper";

    // 超时时间，单位秒
    public final static int CONNECTION_TIME_OUT = 300;
    public final static int READ_TIME_OUT = 300;

    // 要在 Request 中添加的 Headers（key,value） 在 SharePreference 中存储的键名
    public final static String SP_HEADERS = "sp_headers";

}
