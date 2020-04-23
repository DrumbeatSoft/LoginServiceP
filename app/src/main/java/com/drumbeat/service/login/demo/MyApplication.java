package com.drumbeat.service.login.demo;

import android.app.Application;

import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.config.ServiceConfig;

/**
 * Created by ZuoHailong on 2020/1/10.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoginService.setConfig(ServiceConfig.newBuilder()
                .setAppId("125438260305469440")//认证平台
//                .setAppId("121535616969084928")//体验店
                .setBaseUrl("http://192.168.20.233:30060/")
//                .setBaseUrl("http://api.drumbeatsoft.com/")
                .setTokenInterceptor(() -> ToastUtils.showShort("token失效啦啦啦啦"))
                .build());
    }
}
