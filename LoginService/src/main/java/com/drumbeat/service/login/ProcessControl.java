package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.bean.ResultBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.constant.ResultCode;
import com.drumbeat.service.login.qbar.CodeType;
import com.drumbeat.service.login.qbar.OnScanListener;
import com.drumbeat.service.login.qbar.QBarHelper;
import com.drumbeat.service.login.qbar.ScanResult;
import com.drumbeat.service.login.ui.ConfirmActivity;
import com.drumbeat.service.login.zbase.helper.HttpHelper;
import com.drumbeat.service.login.zbase.http.HttpFail;
import com.drumbeat.service.login.zbase.http.callback.HttpCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.drumbeat.service.login.constant.APIInterface.CANCEL_LOGIN;
import static com.drumbeat.service.login.constant.APIInterface.CHECK_PASSWORD_EXPIRE;
import static com.drumbeat.service.login.constant.APIInterface.CONFIRM_LOGIN;
import static com.drumbeat.service.login.constant.APIInterface.GET_TENANT_URL;
import static com.drumbeat.service.login.constant.APIInterface.GET_USER_INFO;
import static com.drumbeat.service.login.constant.APIInterface.LOGIN_URL;
import static com.drumbeat.service.login.constant.APIInterface.MODIFY_PASSWORD;
import static com.drumbeat.service.login.constant.APIInterface.SCAN_CODE;
import static com.drumbeat.service.login.constant.Constant.SP_TOKEN;
import static com.drumbeat.service.login.constant.Constant.SP_USER_ID;
import static com.drumbeat.service.login.constant.ResultCode.CANCEL_LOGIN_QRCODE;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_CANCEL_LOGIN_QRCODE;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_CHECK_PASSWORD_EXPIRE;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_GET_TENANT;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_GET_USER_INFO;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_LOGIN_ACCOUNT;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_MODIFY_PASSWORD;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_ACCOUNT;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_APPID;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_PASSWORD;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_LOGIN;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_SCAN;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_VERIFY;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_TOKEN_INVALID;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class ProcessControl {

    private static Messenger mMessenger;

    /**
     * 从 ContentProvider 中获取 centralizerToken
     *
     * @param context
     * @return
     */
    static String getTokenFromCP(Context context) {
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
     * 查询租户
     */
    static void getTenantList(String account, ResultCallback<List<TenantBean.ResultBean>> callback) {
        ServiceConfig serviceConfig = LoginService.getConfig();

        Map<String, String> params = new HashMap<>();
        params.put("info", account);
        params.put("appId", serviceConfig.getAppId());

        HttpHelper.get(serviceConfig.getBaseUrl() + GET_TENANT_URL, params, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_GET_TENANT);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_GET_TENANT);
                    return;
                }
                TenantBean tenantBean = JSONObject.parseObject(baseBean.getEntity(), TenantBean.class);
                if (tenantBean == null) {
                    onFailCallback(callback, ERROR_GET_TENANT);
                    return;
                }
                onSuccessCallback(callback, tenantBean.getResult());
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_GET_TENANT);
            }
        });
    }

    /**
     * 账号密码登录
     */
    static void login(ServiceConfig serviceConfig, @NonNull String account, @NonNull String password, ResultCallback<LoginResultBean> callback) {
        serviceConfig = serviceConfig == null ? LoginService.getConfig() : serviceConfig;

        if (TextUtils.isEmpty(serviceConfig.getAppId())) {
            onFailCallback(callback, ERROR_NULL_APPID);
            return;
        }
        if (TextUtils.isEmpty(account)) {
            onFailCallback(callback, ERROR_NULL_ACCOUNT);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            onFailCallback(callback, ERROR_NULL_PASSWORD);
            return;
        }
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("TenantCode", LoginService.getTenantId());
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

        HttpHelper.post(serviceConfig.getBaseUrl() + LOGIN_URL, object, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
                    return;
                }
                LoginResultBean loginResultBean = JSONObject.parseObject(baseBean.getEntity(), LoginResultBean.class);
                if (loginResultBean == null || TextUtils.isEmpty(loginResultBean.getToken())) {
                    onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
                    return;
                }
                SPUtils.getInstance().put(SP_TOKEN, loginResultBean.getToken());
                onSuccessCallback(callback, loginResultBean);
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
            }
        });

    }

    /**
     * 检查账户密码是否过期，是否必须强制修改
     *
     * @param callback
     */
    public static void checkPasswordExpire(@NonNull String centralizerToken, ResultCallback<Boolean> callback) {
//        Map<String, String> params = new HashMap<>();
//        params.put("accountId", account);

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String id = userJSONObject.getString("AccountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", id);

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.addHeader("Authorization", centralizerToken);

        HttpHelper.post(serviceConfig.getBaseUrl() + CHECK_PASSWORD_EXPIRE, jsonObject, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_CHECK_PASSWORD_EXPIRE);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_CHECK_PASSWORD_EXPIRE);
                    return;
                }
                ResultBean resultBean = JSONObject.parseObject(baseBean.getEntity(), ResultBean.class);
                onSuccessCallback(callback, resultBean.isResult());
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_CHECK_PASSWORD_EXPIRE);
            }
        });
    }

    /**
     * 修改密码
     */
    static void modifyPwd(@NonNull String oldPwd, @NonNull String newPwd, @NonNull String centralizerToken, ResultCallback<ResultBean> callback) {

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

        HttpHelper.addHeader("Authorization", centralizerToken);

        HttpHelper.post(serviceConfig.getBaseUrl() + MODIFY_PASSWORD, object, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                    return;
                }
                // token失效
                if (baseBean.getStatusCode() == 401) {
                    onFailCallback(callback, ERROR_TOKEN_INVALID);
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                    return;
                }
                ResultBean resultBean = JSONObject.parseObject(baseBean.getEntity(), ResultBean.class);
                if (resultBean == null) {
                    onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                    return;
                }
                switch (resultBean.getCode()) {
                    case 1:
                        callback.onSuccess(resultBean);
                        break;
                    /*case 0:
                        callback.onFail("修改失败");
                        break;
                    case 2:
                        callback.onFail("旧密码错误");
                        break;
                    case 3:
                        callback.onFail("账户不存在");
                        break;
                    case 4:
                        callback.onFail("账户或密码错误");
                        break;
                    case 10:
                        callback.onFail("登录状态错误");
                        break;*/
                    default:
//                        callback.onFail("修改失败");
                        onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                        break;
                }
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_MODIFY_PASSWORD);
            }
        });

    }

    /**
     * 查询用户信息
     */
    static void getUserInfo(@NonNull String centralizerToken, ResultCallback<UserInfoBean.ResultBean> callback) {

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String accountId = userJSONObject.getString("AccountId");

        Map<String, String> map = new HashMap<>();
        map.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.addHeader("Authorization", centralizerToken);

        HttpHelper.get(serviceConfig.getBaseUrl() + GET_USER_INFO, map, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_GET_USER_INFO);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_GET_USER_INFO);
                    return;
                }
                // token失效
                if (baseBean.getStatusCode() == 401) {
                    onFailCallback(callback, ERROR_TOKEN_INVALID);
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_GET_USER_INFO);
                    return;
                }
                UserInfoBean userInfoBean = JSONObject.parseObject(baseBean.getEntity(), UserInfoBean.class);
                if (userInfoBean == null || userInfoBean.getResult() == null) {
                    onFailCallback(callback, ERROR_GET_USER_INFO);
                    return;
                }
                callback.onSuccess(userInfoBean.getResult());
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_MODIFY_PASSWORD);
            }
        });
    }

    /**
     * 拉起扫码
     */
    static void scan(Activity activity, ResultCallback callback) {
        new QBarHelper.Builder()
                .setCodeType(CodeType.QR_CODE)
                .build()
                .start(activity, new OnScanListener() {
                    @Override
                    public void onSuccess(ScanResult scanResult) {
                        if (scanResult == null || TextUtils.isEmpty(scanResult.getContent())) {
                            onFailCallback(callback, ERROR_QRCODE_SCAN);
                        } else {
                            // 扫码得到二维码数据，下一步验证二维码数据，进行登录
                            SPUtils.getInstance().put(SP_USER_ID, scanResult.getContent());
                            verifyQRCode(activity, callback);
                        }
                    }

                    @Override
                    public void onFail() {
                        onFailCallback(callback, ERROR_QRCODE_SCAN);
                    }
                });
    }

    /**
     * 使用二维码扫出来的数据，访问server，获取待登录的用户信息
     */
    private static void verifyQRCode(final Activity activity, ResultCallback callback) {

        Map<String, String> map = new HashMap<>();
        map.put("id", SPUtils.getInstance().getString(SP_USER_ID));

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.addHeader("Authorization", SPUtils.getInstance().getString(SP_TOKEN));

        HttpHelper.get(serviceConfig.getBaseUrl() + SCAN_CODE, map, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_QRCODE_VERIFY);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_QRCODE_VERIFY);
                    return;
                }
                // token失效
                if (baseBean.getStatusCode() == 401) {
                    onFailCallback(callback, ERROR_TOKEN_INVALID);
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_QRCODE_VERIFY);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    onFailCallback(callback, ERROR_QRCODE_VERIFY);
                    return;
                }
                /*
                 * 扫码成功，下一步登录确认
                 * */
                // 扫码的回调消息
                mMessenger = new Messenger(activity, (Messenger.Message message) -> {
                    if (message.getResultCode() == ResultCode.SUCCEES) {
                        onSuccessCallback(callback, message.getData());
                    } else {
                        onFailCallback(callback, message.getResultCode());
                    }
                    mMessenger.unRegister();
                });
                mMessenger.register();
                activity.startActivity(new Intent(activity, ConfirmActivity.class));
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_QRCODE_VERIFY);
            }
        });

    }

    /**
     * 扫码后确认登录
     */
    public static void login(Activity activity, ResultCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("id", SPUtils.getInstance().getString(SP_USER_ID));

        HttpHelper.addHeader("Authorization", SPUtils.getInstance().getString(SP_TOKEN));

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.get(serviceConfig.getBaseUrl() + CONFIRM_LOGIN, params, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                // token失效
                if (baseBean.getStatusCode() == 401) {
                    onFailCallback(callback, ERROR_TOKEN_INVALID);
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                onSuccessCallback(callback, success);
                activity.finish();
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_QRCODE_LOGIN);
            }
        });

    }

    /**
     * 取消登录
     */
    public static void cancelLogin(Activity activity, ResultCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString(SP_TOKEN));
        Map<String, String> map = new HashMap<>();
        map.put("id", SPUtils.getInstance().getString(SP_USER_ID));

        HttpHelper.addHeader("Authorization", SPUtils.getInstance().getString(SP_TOKEN));

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.get(serviceConfig.getBaseUrl() + CANCEL_LOGIN, map, new HttpCallback<String>() {
            @Override
            public void onSuccess(String success) {
                if (TextUtils.isEmpty(success)) {
                    onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(success, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
                    return;
                }
                // token失效
                if (baseBean.getStatusCode() == 401) {
                    onFailCallback(callback, ERROR_TOKEN_INVALID);
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getEntity())) {
                    onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
                    return;
                }
                // 用户手动取消扫码登录，走失败回调
                onFailCallback(callback, CANCEL_LOGIN_QRCODE);
                activity.finish();
            }

            @Override
            public void onFail(HttpFail fail) {
                onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
            }
        });
    }

    private static void onSuccessCallback(ResultCallback callback, Object succeed) {
        if (callback != null) {
            callback.onSuccess(succeed);
        }
    }

    private static void onFailCallback(ResultCallback callback, ResultCode resultCode) {
        if (callback != null) {
            callback.onFail(resultCode);
        }
    }
}
