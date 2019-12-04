package com.drumbeat.service.login;

import android.app.Activity;

import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.config.ServiceConfig;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class LoginService {

    private static ServiceConfig sConfig;

    public static void setConfig(ServiceConfig config) {
        if (sConfig == null) {
            synchronized (ServiceConfig.class) {
                if (sConfig == null) {
                    sConfig = config == null ? ServiceConfig.newBuilder().build() : config;
                }
            }
        }
    }

    static ServiceConfig getConfig() {
        // 保证sConfig不是null
        setConfig(null);
        return sConfig;
    }

    /**
     * 登录中台
     */
    public static void login(String account, String password, ResultCallback<LoginResultBean> callback) {
        ProcessControl.login(account, password, callback);
    }

    /**
     * 扫码登录，目前用于web页的登录
     */
    public static void scan(Activity activity, ResultCallback callback) {
        ProcessControl.scan(activity, callback);
    }
}
