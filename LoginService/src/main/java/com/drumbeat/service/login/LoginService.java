package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.bean.ResultBean;
import com.drumbeat.service.login.bean.UserInfoBean;
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
     *
     * @param account
     * @param password
     * @param callback
     */
    public static void login(String account, String password, ResultCallback<LoginResultBean> callback) {
        login(LoginService.getConfig(), account, password, callback);
    }

    /**
     * 登录中台
     *
     * @param serviceConfig 可选，一次性参数
     * @param account
     * @param password
     * @param callback
     */
    public static void login(ServiceConfig serviceConfig, String account, String password, ResultCallback<LoginResultBean> callback) {
        String centralizerToken = getCentralizerToken();
        // 已有token，直接返回，不再登录
        if (!TextUtils.isEmpty(centralizerToken)) {
            callback.onSuccess(new LoginResultBean().setToken(centralizerToken));
            return;
        }
        ProcessControl.login(serviceConfig, account, password, callback);
    }

    /**
     * 修改密码
     */
    public static void modifyPassword(@NonNull String oldPwd, @NonNull String newPwd, @NonNull String centralizerToken, ResultCallback<ResultBean> callback) {
        ProcessControl.modifyPwd(oldPwd, newPwd, centralizerToken, callback);
    }

    /**
     * 查询用户信息
     */
    public static void getUserInfo(@NonNull String centralizerToken, ResultCallback<UserInfoBean.ResultBean> callback) {
        ProcessControl.getUserInfo(centralizerToken, callback);
    }

    /**
     * 扫码登录，目前用于web页的登录
     */
    public static void scan(Activity activity, ResultCallback callback) {
        ProcessControl.scan(activity, callback);
    }

    /**
     * 获取centralizerToken（中台appToken），供ghostAPP使用，独立APP调用此方法无法获取到centralizerToken
     */
    public static String getCentralizerToken() {
        return getCentralizerToken(ActivityUtils.getTopActivity());
    }

    /**
     * 获取centralizerToken（中台appToken），供ghostAPP使用，独立APP调用此方法无法获取到centralizerToken
     */
    public static String getCentralizerToken(Context context) {
        return ProcessControl.getTokenFromCP(context);
    }
}
