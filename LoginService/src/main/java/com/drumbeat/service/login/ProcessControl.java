package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.drumbeat.sdk.helper.HttpHelper;
import com.drumbeat.sdk.kalle.NetCallback;
import com.drumbeat.sdk.qbar.CodeType;
import com.drumbeat.sdk.qbar.OnScanListener;
import com.drumbeat.sdk.qbar.QBarHelper;
import com.drumbeat.sdk.qbar.ScanResult;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.ui.ConfirmActivity;

import java.util.HashMap;
import java.util.Map;

import static com.drumbeat.service.login.APIInterface.BASE_URL;
import static com.drumbeat.service.login.APIInterface.CANCEL_LOGIN;
import static com.drumbeat.service.login.APIInterface.CONFIRM_LOGIN;
import static com.drumbeat.service.login.APIInterface.LOGIN_URL;
import static com.drumbeat.service.login.APIInterface.SCAN_CODE;
import static com.drumbeat.service.login.DBLoginSPUtil.SP_USER_ID;
import static com.drumbeat.service.login.ResultCode.CANCEL_LOGIN_QRCODE;
import static com.drumbeat.service.login.ResultCode.ERROR_LOGIN_ACCOUNT;
import static com.drumbeat.service.login.ResultCode.ERROR_NULL_ACCOUNT;
import static com.drumbeat.service.login.ResultCode.ERROR_NULL_APPID;
import static com.drumbeat.service.login.ResultCode.ERROR_NULL_PASSWORD;
import static com.drumbeat.service.login.ResultCode.ERROR_QRCODE_LOGIN;
import static com.drumbeat.service.login.ResultCode.ERROR_QRCODE_SCAN;
import static com.drumbeat.service.login.ResultCode.ERROR_QRCODE_VERIFY;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class ProcessControl {

    /**
     * 账号密码登录
     */
    static void login(@NonNull String account, @NonNull String password, ResultCallback<LoginResultBean> callback) {
        ServiceConfig serviceConfig = LoginServiceHelper.getConfig();

        if (TextUtils.isEmpty(serviceConfig.getAppId())) {
            callback.onFail(ERROR_NULL_APPID);
            return;
        }
        if (TextUtils.isEmpty(account)) {
            callback.onFail(ERROR_NULL_ACCOUNT);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            callback.onFail(ERROR_NULL_PASSWORD);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TenantCode", serviceConfig.getTenant());
        jsonObject.put("DeviceId", "");
        jsonObject.put("TenantId", 0);
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
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    callback.onFail(ERROR_LOGIN_ACCOUNT);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
                    callback.onFail(ERROR_LOGIN_ACCOUNT);
                    return;
                }
                LoginResultBean loginResultBean = JSONObject.parseObject(baseBean.getEntity(), LoginResultBean.class);
                if (loginResultBean == null || TextUtils.isEmpty(loginResultBean.getToken())) {
                    callback.onFail(ERROR_LOGIN_ACCOUNT);
                    return;
                }
                SPUtils.getInstance().put("token", loginResultBean.getToken());
                callback.onSuccess(loginResultBean);
            }

            @Override
            public void onFailed(String failed) {
                callback.onFail(ERROR_LOGIN_ACCOUNT);
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
                            if (callback != null)
                                callback.onFail(ERROR_QRCODE_SCAN);
                        } else {
                            // 扫码得到二维码数据，下一步验证二维码数据，进行登录
                            DBLoginSPUtil.newInstance(activity).put(SP_USER_ID, scanResult.getContent());
                            verifyQRCode(activity, callback);
                        }
                    }

                    @Override
                    public void onFail() {
                        if (callback != null)
                            callback.onFail(ERROR_QRCODE_SCAN);
                    }
                });
    }

    /**
     * 使用二维码扫出来的数据，访问server，获取待登录的用户信息
     */
    private static void verifyQRCode(final Activity activity, ResultCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString("token"));
        Map<String, String> map = new HashMap<>();
        map.put("id", DBLoginSPUtil.newInstance(activity).getString(SP_USER_ID));
        HttpHelper.get(BASE_URL + SCAN_CODE, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    callback.onFail(ERROR_QRCODE_VERIFY);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
                    callback.onFail(ERROR_QRCODE_VERIFY);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    callback.onFail(ERROR_QRCODE_VERIFY);
                    return;
                }
                // 扫码成功，下一步登录确认
                activity.startActivity(new Intent(activity, ConfirmActivity.class));
            }

            @Override
            public void onFailed(String failed) {
                if (callback != null)
                    callback.onFail(ERROR_QRCODE_VERIFY);

            }
        });
    }

    /**
     * 扫码后确认登录
     */
    public static void login(final Activity activity, ResultCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString("token"));
        Map<String, String> params = new HashMap<>();
        params.put("id", DBLoginSPUtil.newInstance(activity).getString(SP_USER_ID));
        HttpHelper.get(BASE_URL + CONFIRM_LOGIN, headers, params, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    callback.onFail(ERROR_QRCODE_LOGIN);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
                    callback.onFail(ERROR_QRCODE_LOGIN);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    callback.onFail(ERROR_QRCODE_LOGIN);
                    return;
                }
                callback.onSuccess(succeed);
            }

            @Override
            public void onFailed(String failed) {
                if (callback != null)
                    callback.onFail(ERROR_QRCODE_LOGIN);

            }
        });
    }

    /**
     * 取消登录
     */
    public static void cancelLogin(final Activity activity, ResultCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString("token"));
        Map<String, String> map = new HashMap<>();
        map.put("id", DBLoginSPUtil.newInstance(activity).getString(SP_USER_ID));
        HttpHelper.get(BASE_URL + CANCEL_LOGIN, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    callback.onFail(CANCEL_LOGIN_QRCODE);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
                    callback.onFail(CANCEL_LOGIN_QRCODE);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    callback.onFail(CANCEL_LOGIN_QRCODE);
                    return;
                }
                callback.onSuccess(succeed);
            }

            @Override
            public void onFailed(String failed) {
                if (callback != null)
                    callback.onFail(CANCEL_LOGIN_QRCODE);

            }
        });
    }

}
