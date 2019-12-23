package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.bean.ResultBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.constant.ResultCode;
import com.drumbeat.service.login.drumsdk.helper.HttpHelper;
import com.drumbeat.service.login.drumsdk.kalle.NetCallback;
import com.drumbeat.service.login.qbar.CodeType;
import com.drumbeat.service.login.qbar.OnScanListener;
import com.drumbeat.service.login.qbar.QBarHelper;
import com.drumbeat.service.login.qbar.ScanResult;
import com.drumbeat.service.login.ui.ConfirmActivity;

import java.util.HashMap;
import java.util.Map;

import static com.drumbeat.service.login.constant.APIInterface.BASE_URL;
import static com.drumbeat.service.login.constant.APIInterface.CANCEL_LOGIN;
import static com.drumbeat.service.login.constant.APIInterface.CONFIRM_LOGIN;
import static com.drumbeat.service.login.constant.APIInterface.GET_USER_INFO;
import static com.drumbeat.service.login.constant.APIInterface.LOGIN_URL;
import static com.drumbeat.service.login.constant.APIInterface.MODIFY_PASSWORD;
import static com.drumbeat.service.login.constant.APIInterface.SCAN_CODE;
import static com.drumbeat.service.login.constant.Constant.SP_TOKEN;
import static com.drumbeat.service.login.constant.Constant.SP_USER_ID;
import static com.drumbeat.service.login.constant.ResultCode.CANCEL_LOGIN_QRCODE;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_CANCEL_LOGIN_QRCODE;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_GET_USER_INFO;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_LOGIN_ACCOUNT;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_MODIFY_PASSWORD;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_ACCOUNT;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_APPID;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_NULL_PASSWORD;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_LOGIN;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_SCAN;
import static com.drumbeat.service.login.constant.ResultCode.ERROR_QRCODE_VERIFY;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class ProcessControl {

    private static Messenger mMessenger;

    /**
     * 账号密码登录
     */
    static void login(@NonNull String account, @NonNull String password, ResultCallback<LoginResultBean> callback) {
        ServiceConfig serviceConfig = LoginService.getConfig();

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
                    onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
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
            public void onFail(String failed) {
                onFailCallback(callback, ERROR_LOGIN_ACCOUNT);
            }
        });
    }

    /**
     * 修改密码
     */
    static void modifyPwd(@NonNull String oldPwd, @NonNull String newPwd, @NonNull String centralizerToken, ResultCallback<ResultBean> callback) {

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
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    onFailCallback(callback, ERROR_MODIFY_PASSWORD);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
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
            public void onFail(String failed) {
                onFailCallback(callback, ERROR_MODIFY_PASSWORD);
            }
        });
    }


    /**
     * 修改密码
     */
    static void getUserInfo(@NonNull String centralizerToken, ResultCallback<UserInfoBean.ResultBean> callback) {

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
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    onFailCallback(callback, ERROR_GET_USER_INFO);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null || TextUtils.isEmpty(baseBean.getEntity())) {
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
            public void onFail(String failed) {
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
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString(SP_TOKEN));
        Map<String, String> map = new HashMap<>();
        map.put("id", SPUtils.getInstance().getString(SP_USER_ID));
        HttpHelper.get(BASE_URL + SCAN_CODE, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    onFailCallback(callback, ERROR_QRCODE_VERIFY);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
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
            public void onFail(String failed) {
                onFailCallback(callback, ERROR_QRCODE_VERIFY);
            }
        });
    }

    /**
     * 扫码后确认登录
     */
    public static void login(Activity activity, ResultCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", SPUtils.getInstance().getString(SP_TOKEN));
        Map<String, String> params = new HashMap<>();
        params.put("id", SPUtils.getInstance().getString(SP_USER_ID));
        HttpHelper.get(BASE_URL + CONFIRM_LOGIN, headers, params, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(baseBean.getEntity());
                if (jsonObject == null || !jsonObject.getBoolean("Success")) {
                    onFailCallback(callback, ERROR_QRCODE_LOGIN);
                    return;
                }
                onSuccessCallback(callback, succeed);
                activity.finish();
            }

            @Override
            public void onFail(String failed) {
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
        HttpHelper.get(BASE_URL + CANCEL_LOGIN, headers, map, new NetCallback() {
            @Override
            public void onSuccess(String succeed) {
                if (TextUtils.isEmpty(succeed)) {
                    onFailCallback(callback, ERROR_CANCEL_LOGIN_QRCODE);
                    return;
                }
                BaseBean baseBean = JSONObject.parseObject(succeed, BaseBean.class);
                if (baseBean == null) {
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
            public void onFail(String failed) {
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
