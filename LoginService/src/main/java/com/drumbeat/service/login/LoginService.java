package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
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
        if (context == null) {
            return null;
        }

        String centralizerToken = null;

        Uri uri = Uri.parse("content://com.drumbeat.appmanager.app.provider/app");
        String column_appliaction_id = "applicationId";
        String column_token = "token";
        String currentApplicationId = AppUtils.getAppPackageName();

        Cursor appCursor = context.getContentResolver().query(uri, new String[]{"_id", column_appliaction_id, column_token}, null, null, null);
        while (appCursor.moveToNext()) {
            int anInt = appCursor.getInt(0);
            String applicationId = appCursor.getString(1);
            String token = appCursor.getString(2);
            if (!TextUtils.isEmpty(applicationId) && applicationId.equals(currentApplicationId)) {
                centralizerToken = token;
                break;
            }
        }

        return centralizerToken;

    }
}
