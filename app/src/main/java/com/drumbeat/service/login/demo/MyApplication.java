package com.drumbeat.service.login.demo;

import android.app.Application;

import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.ZFaceConfig;

/**
 * Created by ZuoHailong on 2020/1/10.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoginService.setConfig(ServiceConfig.newBuilder()
//                .setAppId("125438260305469440")//认证平台
                .setAppId("101913605049421824")//供应链
                .setBaseUrl("http://192.168.20.233:30060/")
                .setTokenInterceptor(() -> ToastUtils.showShort("token失效啦啦啦啦"))
                .build());
        ZFace.setConfig(ZFaceConfig.newBuilder()
                .setResource_model_download_base_url("") // model文件baseurl
                .setResource_so_download_base_url("") // so文件baseurl
                .build());
    }
}
