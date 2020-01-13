package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.BooleanResultBean;
import com.drumbeat.service.login.bean.FailureBean;
import com.drumbeat.service.login.bean.LoginBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.http.HttpHelper;
import com.drumbeat.service.login.http.kalle.NetCallback;
import com.drumbeat.service.login.ui.ConfirmActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.drumbeat.service.login.config.UrlConfig.CANCEL_LOGIN;
import static com.drumbeat.service.login.config.UrlConfig.CHECK_PASSWORD_EXPIRE;
import static com.drumbeat.service.login.config.UrlConfig.CONFIRM_LOGIN;
import static com.drumbeat.service.login.config.UrlConfig.GET_TENANT_URL;
import static com.drumbeat.service.login.config.UrlConfig.GET_USER_INFO;
import static com.drumbeat.service.login.config.UrlConfig.LOGIN_URL;
import static com.drumbeat.service.login.config.UrlConfig.MODIFY_PASSWORD;
import static com.drumbeat.service.login.config.UrlConfig.SCAN_CODE;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class ProcessControl {


    /**
     * 从 ContentProvider 中获取 centralizerToken
     *
     * @param context
     * @return
     */
    static String getTokenFromCP(@Nullable Context context) {
        context = context == null ? ActivityUtils.getTopActivity() : context;
        if (context == null) {
            return null;
        }
        String centralizerToken = null;

        Uri uri = Uri.parse("content://com.drumbeat.appmanager.app.provider/app");
        String column_appliaction_id = "applicationId";
        String column_token = "token";
        // 当前运行的应用：宿主APP/插件APP
        String currentApplicationId = AppUtils.getAppPackageName();

        Cursor appCursor = context.getContentResolver().query(uri, new String[]{"_id", column_appliaction_id, column_token}, null, null, null);
        if (appCursor == null) {
            return null;
        }
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

    /**
     * 查询租户列表
     */
    static void getTenantList(String account, @NonNull LoginService.Callback<List<TenantBean.ResultBean>> callback) {
        ServiceConfig serviceConfig = LoginService.getConfig();
        Map<String, String> params = new HashMap<>();
        params.put("info", account);
        params.put("appId", serviceConfig.getAppId());

        HttpHelper.get(serviceConfig.getBaseUrl() + GET_TENANT_URL, null, params, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                try {
                    if (TextUtils.isEmpty(success)) {
                        callback.onSuccess(null);
                        return;
                    }
                    BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                    if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
                        callback.onSuccess(null);
                        return;
                    }
                    TenantBean bean = JSONObject.parseObject(baseBean.getEntity(), TenantBean.class);
                    if (bean == null) {
                        callback.onSuccess(null);
                        return;
                    }
                    // 处理业务code
                    switch (bean.getCode()) {
                        case 1:
                            callback.onSuccess(bean.getResult());
                            break;
                        default:
                        /*dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());*/
                            callback.onSuccess(null);
                            break;
                    }
                } catch (Exception e) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String failure) {
//                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
                callback.onSuccess(null);
            }
        });
    }

    /**
     * 账号密码登录
     */
    static void login(ServiceConfig serviceConfig, String account, String password, @NonNull LoginService.Callback<LoginBean> callback) {
        serviceConfig = serviceConfig == null ? LoginService.getConfig() : serviceConfig;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TenantCode", "");
        jsonObject.put("DeviceId", "");
        jsonObject.put("TenantId", LoginService.getTenantId());
        jsonObject.put("AppId", serviceConfig.getAppId());
        jsonObject.put("Device", 20);//20 android
        jsonObject.put("TokenType", 0);
        jsonObject.put("AccountName", account);
        jsonObject.put("Password", password);
        jsonObject.put("AppSecurityCode", "");
        jsonObject.put("OperatorAccountId", 0);
        jsonObject.put("OperatorAccountName", "");

        JSONObject object = new JSONObject();
        object.put("input", jsonObject);

        HttpHelper.post(serviceConfig.getBaseUrl() + LOGIN_URL, null, object, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                LoginBean bean = dispatchSuccessData(callback, success, LoginBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getResult()) {
                    case 1:
                        callback.onSuccess(bean);
                        break;
                    case 10:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_10_login);
                        break;
                    case 11:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_11);
                        break;
                    case 12:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_12);
                        break;
                    case 13:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_13);
                        break;
                    case 14:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_14);
                        break;
                    case 15:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_15);
                        break;
                    case 16:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_16);
                        break;
                    case 17:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_17);
                        break;
                    case 18:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_18);
                        break;
                    case 20:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_20);
                        break;
                    case 21:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_21);
                        break;
                    case 22:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_22);
                        break;
                    case 23:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_23);
                        break;
                    case 30:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_30);
                        break;
                    case 80:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_80);
                        break;
                    case 81:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_81);
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getResult());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 检查账户密码是否过期，是否必须强制修改
     *
     * @param callback
     */
    static void checkPasswordExpire(String centralizerToken, @NonNull LoginService.Callback<Boolean> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String id = userJSONObject.getString("AccountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", id);

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.post(serviceConfig.getBaseUrl() + CHECK_PASSWORD_EXPIRE, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BooleanResultBean bean = dispatchSuccessData(callback, success, BooleanResultBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    case 1:
                        callback.onSuccess(bean.getResult());
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 修改密码
     */
    static void modifyPwd(String centralizerToken, String oldPwd, String newPwd, @NonNull LoginService.Callback<Boolean> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String id = userJSONObject.getString("AccountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OldPassword", oldPwd);
        jsonObject.put("Password", newPwd);
        jsonObject.put("Id", id);

        JSONObject object = new JSONObject();
        object.put("input", jsonObject);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + MODIFY_PASSWORD, headers, object, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BooleanResultBean bean = dispatchSuccessData(callback, success, BooleanResultBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    case 1:
                        callback.onSuccess(bean.getResult());
                        break;
                    case 2:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_2);
                        break;
                    case 3:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_3);
                        break;
                    case 4:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_4);
                        break;
                    case 10:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_10_modifypwd);
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 查询用户信息
     */
    static void getUserInfo(String centralizerToken, @NonNull LoginService.Callback<UserInfoBean.ResultBean> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String accountId = userJSONObject.getString("AccountId");

        Map<String, String> map = new HashMap<>();
        map.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.get(serviceConfig.getBaseUrl() + GET_USER_INFO, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                UserInfoBean bean = dispatchSuccessData(callback, success, UserInfoBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    case 1:
                        callback.onSuccess(bean.getResult());
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 使用二维码扫出来的数据，访问server，获取待登录的用户信息
     */
    static void loginQrcode(@NonNull Activity activity, String centralizerToken, String userId, @NonNull LoginService.Callback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);
        Map<String, String> map = new HashMap<>();
        map.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.get(serviceConfig.getBaseUrl() + SCAN_CODE, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BooleanResultBean bean = dispatchSuccessData(callback, success, BooleanResultBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    /*
                     * 1：扫码成功，下一步登录确认
                     * */
                    case 1:
                        Intent intent = new Intent(activity, ConfirmActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("centralizerToken", centralizerToken);
                        activity.startActivity(intent);
                        break;
                    case 0:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_0_scancode);
                        break;
                    case 200:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_200);
                        break;
                    case 201:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_201);
                        break;
                    case 400:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_400);
                        break;
                    case 500:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_500);
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 扫码后确认登录
     */
    public static void login(String centralizerToken, String userId, @NonNull LoginService.Callback<Boolean> callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);
        Map<String, String> params = new HashMap<>();
        params.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.get(serviceConfig.getBaseUrl() + CONFIRM_LOGIN, headers, params, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BooleanResultBean bean = dispatchSuccessData(callback, success, BooleanResultBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    case 1:
                        callback.onSuccess(bean.getResult());
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 取消登录
     */
    public static void cancelLogin(String centralizerToken, String userId, @NonNull LoginService.Callback<Boolean> callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);
        Map<String, String> map = new HashMap<>();
        map.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.get(serviceConfig.getBaseUrl() + CANCEL_LOGIN, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BooleanResultBean bean = dispatchSuccessData(callback, success, BooleanResultBean.class);
                if (bean == null) {
                    return;
                }
                // 处理业务code
                switch (bean.getCode()) {
                    // 取消登录成功
                    case 1:
                        callback.onSuccess(bean.getResult());
                        break;
                    default:
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + bean.getCode());
                        break;
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 处理 onSuccess 数据，已处理返回true，未处理返回false
     *
     * @param callback 回调
     * @param success  onSuccess 数据
     * @param cls      要转换的结果实体类
     * @return
     */
    private static <T> T dispatchSuccessData(LoginService.Callback callback, String success, Class<T> cls) {
        T t;
        try {
            if (TextUtils.isEmpty(success) || JSONObject.parseObject(success, BaseBean.class) == null) {
                // 外层数据实体是空的
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
                return null;
            }
            BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
            if (baseBean.getStatusCode() != 200 || TextUtils.isEmpty(baseBean.getEntity())) {
                // 特殊的错误码，需要开发者处理，如401、415等
                dispatchFailureData(callback, baseBean.getStatusCode(),
                        Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getStatusCode());
                return null;
            }
            t = JSON.parseObject(baseBean.getEntity(), cls);
        } catch (JSONException e) {
            // json转换异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_json);
            return null;
        } catch (Exception e) {
            // 未知异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_unknow);
            return null;
        }
        // 内层数据实体是空的
        if (t == null) {
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
            return null;
        }
        return t;
    }

    /**
     * 处理 onFailure 数据
     *
     * @param callback 回调
     * @param code     错误码
     * @param resId    错误信息资源ID
     */
    private static void dispatchFailureData(LoginService.Callback callback, int code, @StringRes int resId) {
        dispatchFailureData(callback, code, Utils.getApp().getString(resId));
    }

    /**
     * 处理 onFailure 数据
     *
     * @param callback 回调
     * @param code     错误码
     * @param msg      错误信息
     */
    private static void dispatchFailureData(LoginService.Callback callback, int code, String msg) {
        if (callback == null) {
            return;
        }
        callback.onFailure(
                new FailureBean()
                        .setCode(code)
                        .setMsg(msg));
    }
}
