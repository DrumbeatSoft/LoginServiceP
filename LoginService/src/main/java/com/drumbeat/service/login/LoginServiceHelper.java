package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Intent;

import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.ui.ConfirmActivity;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class LoginServiceHelper {

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
        activity.startActivity(new Intent(activity, ConfirmActivity.class));
//        ProcessControl.scan(activity, callback);
    }


    /******************************************************************************************************************/

    private Builder mBuilder;

    private LoginServiceHelper(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    /**
     * 登录
     */
    public void login() {
//        mBuilder.activity.startActivity(new Intent(mBuilder.activity, LoginActivity.class));
    }

    public static class Builder {

        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Activity getActivity() {
            return activity;
        }

        /**
         * 开始构建
         *
         * @return
         */
        public LoginServiceHelper build() {
            return new LoginServiceHelper(this);
        }
    }
}
