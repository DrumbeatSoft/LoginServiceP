package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.Utils;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.FailureBean;
import com.drumbeat.service.login.bean.LoginBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.bean.WeChatBean;
import com.drumbeat.service.login.code.CodeEnum;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.http.HttpHelper;
import com.drumbeat.service.login.http.TokenInterceptor;
import com.drumbeat.service.login.http.kalle.NetCallback;
import com.drumbeat.service.login.ui.ConfirmActivity;
import com.drumbeat.service.login.utils.SignUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.drumbeat.service.login.config.UrlConfig.CANCEL_LOGIN;
import static com.drumbeat.service.login.config.UrlConfig.CHECK_PASSWORD_EXPIRE;
import static com.drumbeat.service.login.config.UrlConfig.CHECK_SMS_CODE;
import static com.drumbeat.service.login.config.UrlConfig.CONFIRM_LOGIN;
import static com.drumbeat.service.login.config.UrlConfig.FORGOT_PASSWORD;
import static com.drumbeat.service.login.config.UrlConfig.GET_FACE_FEATURES;
import static com.drumbeat.service.login.config.UrlConfig.GET_SMS_CODE;
import static com.drumbeat.service.login.config.UrlConfig.GET_TENANT_URL;
import static com.drumbeat.service.login.config.UrlConfig.GET_USER_INFO;
import static com.drumbeat.service.login.config.UrlConfig.LOGIN_URL;
import static com.drumbeat.service.login.config.UrlConfig.LOGIN_WITH_FACE;
import static com.drumbeat.service.login.config.UrlConfig.MODIFY_PASSWORD;
import static com.drumbeat.service.login.config.UrlConfig.SAVE_FACE_FEATURES;
import static com.drumbeat.service.login.config.UrlConfig.SCAN_CODE;

/**
 * @author ZuoHailong
 * @date 2019/10/17
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
    static void getTenantList(String account, @NonNull LoginService.Callback<List<TenantBean>> callback) {
        ServiceConfig serviceConfig = LoginService.getConfig();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("info", account);
        jsonObject.put("appId", serviceConfig.getAppId());

        HttpHelper.post(serviceConfig.getBaseUrl() + GET_TENANT_URL, null, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<List<TenantBean>> baseBean = dispatchSuccessDataToList(callback, success, TenantBean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onSuccess(baseBean.getData());
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
//                callback.onSuccess(null);
            }
        });
    }

    /**
     * 账号密码登录
     */
    static void login(ServiceConfig serviceConfig, String account, String password, String tenantId, @NonNull LoginService.Callback<LoginBean> callback) {
        serviceConfig = serviceConfig == null ? LoginService.getConfig() : serviceConfig;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tenantCode", "");
        jsonObject.put("deviceId", "");
        jsonObject.put("tenantId", tenantId);
        jsonObject.put("appId", serviceConfig.getAppId());
        //20 android
        jsonObject.put("device", 20);
        jsonObject.put("tokenType", 0);
        jsonObject.put("accountName", account);
        jsonObject.put("password", password);
        jsonObject.put("appSecurityCode", "");
        jsonObject.put("operatorAccountId", 0);
        jsonObject.put("operatorAccountName", "");

        JSONObject object = new JSONObject();
        object.put("input", jsonObject);

        HttpHelper.post(serviceConfig.getBaseUrl() + LOGIN_URL, null, object, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<LoginBean> baseBean = dispatchSuccessDataToBean(callback, success, LoginBean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
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

        String accountId = null;
        String[] split = centralizerToken.split("\\.");
        if (split.length > 0) {
            String base64 = split[1];
            String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
            JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
            accountId = userJSONObject.getString("accountId");
            if (TextUtils.isEmpty(accountId)) {
                accountId = userJSONObject.getString("AccountId");
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.post(serviceConfig.getBaseUrl() + CHECK_PASSWORD_EXPIRE, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
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

        String accountId = null;
        String[] split = centralizerToken.split("\\.");
        if (split.length > 0) {
            String base64 = split[1];
            String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
            JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
            accountId = userJSONObject.getString("accountId");
            if (TextUtils.isEmpty(accountId)) {
                accountId = userJSONObject.getString("AccountId");
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oldPassword", oldPwd);
        jsonObject.put("password", newPwd);
        jsonObject.put("id", accountId);

        JSONObject object = new JSONObject();
        object.put("input", jsonObject);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + MODIFY_PASSWORD, headers, object, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
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
    static void getUserInfo(String centralizerToken, @NonNull LoginService.Callback<UserInfoBean> callback) {

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", centralizerToken);

        String accountId = null;
        String[] split = centralizerToken.split("\\.");
        if (split.length > 0) {
            String base64 = split[1];
            String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
            JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
            accountId = userJSONObject.getString("accountId");
            if (TextUtils.isEmpty(accountId)) {
                accountId = userJSONObject.getString("AccountId");
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + GET_USER_INFO, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<UserInfoBean> baseBean = dispatchSuccessDataToBean(callback, success, UserInfoBean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
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
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", centralizerToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + SCAN_CODE, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                Intent intent = new Intent(activity, ConfirmActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("centralizerToken", centralizerToken);
                activity.startActivity(intent);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + CONFIRM_LOGIN, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + CANCEL_LOGIN, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 获取短信验证码
     *
     * @param mobile   手机号/账号/邮箱号/身份证号
     * @param callback
     */
    static void getSmsCode(ServiceConfig serviceConfig, @NonNull String mobile,
                           @NonNull String privateKey, @NonNull LoginService.Callback<Boolean> callback) {
        if (!RegexUtils.isMobileSimple(mobile)) {
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                    Utils.getApp().getString(R.string.dblogin_fail_mobile_illegal));
            return;
        }
        getTenantList(mobile, new LoginService.Callback<List<TenantBean>>() {
            @Override
            public void onSuccess(List<TenantBean> success) {
                // 未查到租户
                if (success == null || success.size() == 0) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_1001));
                    return;
                }

                try {
                    // 租户存在，查询短信验证码
                    String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L - 1);

                    String requestStr = timeStamp + "mobilePhone=" + mobile + "&verifyCodeType=1";
                    String sign = SignUtil.signRsa2(requestStr, privateKey);

                    Map<String, String> headers = new HashMap<>(3);
                    headers.put("AppId", serviceConfig.getAppId());
                    headers.put("TimeStamp", timeStamp);
                    headers.put("Sign", sign);

                    LinkedHashMap<String, String> params = new LinkedHashMap<>(2);
                    params.put("mobilePhone", mobile);
                    params.put("verifyCodeType", "1");

                    HttpHelper.get(serviceConfig.getBaseUrl() + GET_SMS_CODE, headers, params, new NetCallback() {
                        @Override
                        public void onSuccess(String success) {
                            BaseBean<Object> baseBean = dispatchSuccessDataToBean(callback, success, Object.class);
                            // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                            if (baseBean == null) {
                                return;
                            }
                            callback.onSuccess(true);
                        }

                        @Override
                        public void onFailure(String failure) {
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT, e.getMessage());
                }
            }

            @Override
            public void onFailure(FailureBean failure) {
                callback.onFailure(failure);
            }
        });
    }

    /**
     * 验证短信验证码
     *
     * @param mobile
     * @param smsCode
     * @param privateKey
     * @param callback
     */
    static void checkSmsCode(ServiceConfig serviceConfig, @NonNull String mobile, @NonNull String smsCode,
                             @NonNull String privateKey, @NonNull LoginService.Callback<Boolean> callback) {
        if (!RegexUtils.isMobileSimple(mobile)) {
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                    Utils.getApp().getString(R.string.dblogin_fail_mobile_illegal));
            return;
        }

        try {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L - 1);

            String requestStr = timeStamp + "mobilePhone=" + mobile + "&verifyCodeType=1" + "&verifyCode=" + smsCode;
            String sign = SignUtil.signRsa2(requestStr, privateKey);

            Map<String, String> headers = new HashMap<>(3);
            headers.put("AppId", serviceConfig.getAppId());
            headers.put("TimeStamp", timeStamp);
            Log.d("loginS", timeStamp);
            headers.put("Sign", sign);

            LinkedHashMap<String, String> params = new LinkedHashMap<>(3);
            params.put("mobilePhone", mobile);
            params.put("verifyCodeType", "1");
            params.put("verifyCode", smsCode);

            HttpHelper.get(serviceConfig.getBaseUrl() + CHECK_SMS_CODE, headers, params, new NetCallback() {
                @Override
                public void onSuccess(String success) {
                    Log.d("loginS", success);
                    BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                    // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                    if (baseBean == null) {
                        return;
                    }
                    callback.onSuccess(true);
                }

                @Override
                public void onFailure(String failure) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, Utils.getApp().getString(R.string.dblogin_fail_unknow));
        }
    }


    /**
     * 忘记密码，新设置密码
     *
     * @param serviceConfig
     * @param mobile
     * @param smsCode
     * @param newPassword
     * @param privateKey
     * @param callback
     */
    static void forgotPassword(ServiceConfig serviceConfig, @NonNull String mobile, @NonNull String smsCode,
                               @NonNull String newPassword, @NonNull String privateKey, @NonNull LoginService.Callback<Boolean> callback) {
        if (!RegexUtils.isMobileSimple(mobile)) {
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                    Utils.getApp().getString(R.string.dblogin_fail_mobile_illegal));
            return;
        }

        try {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L - 1);

            String requestStr = timeStamp + "mobilePhone=" + mobile
                    + "&verifyCode=" + smsCode
                    + "&appId=" + serviceConfig.getAppId()
                    + "&password=" + newPassword;

            String sign = SignUtil.signRsa2(requestStr, privateKey);

            Map<String, String> headers = new HashMap<>(3);
            headers.put("AppId", serviceConfig.getAppId());
            headers.put("TimeStamp", timeStamp);
            headers.put("Sign", sign);

            LinkedHashMap<String, String> params = new LinkedHashMap<>(4);
            params.put("mobilePhone", mobile);
            params.put("verifyCode", smsCode);
            params.put("appId", serviceConfig.getAppId());
            params.put("password", newPassword);

            HttpHelper.get(serviceConfig.getBaseUrl() + FORGOT_PASSWORD, headers, params, new NetCallback() {
                @Override
                public void onSuccess(String success) {
                    BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                    // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                    if (baseBean == null) {
                        return;
                    }
                    callback.onSuccess(true);
                }

                @Override
                public void onFailure(String failure) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, Utils.getApp().getString(R.string.dblogin_fail_unknow));
        }
    }


    /**
     * 保存人脸特征
     *
     * @param centralizerToken
     * @param featureData
     * @param callback
     */
    static void saveFaceFeatures(@NonNull String centralizerToken, @NonNull float[] featureData, @NonNull LoginService.Callback<Boolean> callback) {

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", centralizerToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("features", Arrays.toString(featureData));

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + SAVE_FACE_FEATURES, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                callback.onSuccess(baseBean.getData());
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 查询人脸特征
     *
     * @param centralizerToken
     * @param callback
     */
    static void getFaceFeatures(@NonNull String centralizerToken, @NonNull LoginService.Callback<float[]> callback) {

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", centralizerToken);

        String accountId = null;
        String[] split = centralizerToken.split("\\.");
        if (split.length > 0) {
            String base64 = split[1];
            String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
            JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
            accountId = userJSONObject.getString("accountId");
            if (TextUtils.isEmpty(accountId)) {
                accountId = userJSONObject.getString("AccountId");
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + GET_FACE_FEATURES, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<String> baseBean = dispatchSuccessDataToBean(callback, success, String.class);
                // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                if (baseBean == null) {
                    return;
                }
                if (TextUtils.isEmpty(baseBean.getData())) {
                    // 人脸特征为空，不可以报错，业务上接下来要录入人脸
//                    dispatchFailureData(callback, baseBean.getCode(), Utils.getApp().getString(R.string.dblogin_fail_1020));
                    callback.onSuccess(null);
                    return;
                }
                String[] split = baseBean.getData().replace("[", "").replace("]", "").split(",");
                if (split.length > 0) {
                    float[] features = new float[split.length];
                    for (int i = 0; i < split.length; i++) {
                        features[i] = Float.parseFloat(split[i]);
                    }
                    callback.onSuccess(features);
                } else {
                    dispatchFailureData(callback, baseBean.getCode(), Utils.getApp().getString(R.string.dblogin_fail_1020));
                }
            }

            @Override
            public void onFailure(String failure) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
            }
        });
    }

    /**
     * 人脸登录
     *
     * @param accountId
     * @param privateKey
     * @param callback
     */
    static void loginWithFace(@NonNull String accountId, @NonNull String privateKey, String tenantId, @NonNull LoginService.Callback<LoginBean> callback) {
        try {

            ServiceConfig serviceConfig = LoginService.getConfig();
            if (serviceConfig == null) return;

            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L - 1);

            String requestStr = timeStamp + "accountId=" + accountId + "&appId=" + serviceConfig.getAppId() + "&tenantId=" + tenantId;
            String sign = SignUtil.signRsa2(requestStr, privateKey);

            Map<String, String> headers = new HashMap<>(3);
            headers.put("AppId", serviceConfig.getAppId());
            headers.put("TimeStamp", timeStamp);
            headers.put("Sign", sign);

            LinkedHashMap<String, String> params = new LinkedHashMap<>(3);
            params.put("accountId", accountId);
            params.put("appId", serviceConfig.getAppId());
            params.put("tenantId", tenantId);

            HttpHelper.get(serviceConfig.getBaseUrl() + LOGIN_WITH_FACE, headers, params, new NetCallback() {
                @Override
                public void onSuccess(String success) {
                    BaseBean<LoginBean> baseBean = dispatchSuccessDataToBean(callback, success, LoginBean.class);
                    // baseBean == null 在方法 dispatchSuccessDataToXXX() 已处理
                    if (baseBean == null) {
                        return;
                    }
                    callback.onSuccess(baseBean.getData());
                }

                @Override
                public void onFailure(String failure) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT, failure);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, Utils.getApp().getString(R.string.dblogin_fail_unknow));
        }
    }

    /**
     * 绑定微信
     *
     * @param centralizerToken
     * @param weChatBean
     * @param callback
     */
    static void bindWeChat(@NonNull String centralizerToken, @NonNull WeChatBean weChatBean, @NonNull LoginService.Callback<String> callback) {
    }

    /**
     * 处理 onSuccess 数据，已处理返回true，未处理返回false
     *
     * @param callback 回调
     * @param success  onSuccess 数据
     * @param cls      要转换的结果实体类
     * @return
     */
    private static <
            T> BaseBean<T> dispatchSuccessDataToBean(LoginService.Callback callback, String success, Class<T> cls) {
        try {
            if (TextUtils.isEmpty(success)) {
                // 外层数据实体是空的
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
                return null;
            }
            BaseBean<T> baseBean = JSON.parseObject(success, new TypeReference<BaseBean<T>>(cls) {
            });

            if (baseBean == null) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_unknow);
                return null;
            }
            // 统一处理401 Token失效
            if (baseBean.getCode() == 401) {
                TokenInterceptor tokenInterceptor = LoginService.getConfig().getTokenInterceptor();
                if (tokenInterceptor != null) {
                    tokenInterceptor.onInvalid();
                } else {
                    dispatchFailureData(callback, baseBean.getCode(),
                            Utils.getApp().getString(R.string.dblogin_fail_401) + baseBean.getCode());
                }
                return null;
            }
            // 统一处理401 Token失效
            if (baseBean.getCode() == 412) {
                dispatchFailureData(callback, baseBean.getCode(),
                        Utils.getApp().getString(R.string.dblogin_fail_412) + baseBean.getCode());
                return null;
            }
            // 统一处理601 业务数据为空
            if (baseBean.getCode() == 601) {
                return baseBean;
            }
            if (baseBean.getCode() != 200) {
                int stringResId = CodeEnum.valueOf(baseBean.getCode()).getStringResId();
                // 未处理的错误码，向上抛出
                if (stringResId == R.string.dblogin_fail_uncontrolled_code) {
                    dispatchFailureData(callback, baseBean.getCode(),
                            Utils.getApp().getString(CodeEnum.valueOf(baseBean.getCode()).getStringResId()) + baseBean.getCode());
                } else {
                    dispatchFailureData(callback, baseBean.getCode(), CodeEnum.valueOf(baseBean.getCode()).getStringResId());
                }
                return null;
            }
            return baseBean;
        } catch (JSONException e) {
            // json转换异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_json);
            return null;
        } catch (Exception e) {
            // 未知异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_unknow);
            return null;
        }
    }

    /**
     * 处理 onSuccess 数据，已处理返回true，未处理返回false
     *
     * @param callback 回调
     * @param success  onSuccess 数据
     * @param cls      要转换的结果实体类
     * @return
     */
    private static <
            T> BaseBean<List<T>> dispatchSuccessDataToList(LoginService.Callback callback, String success, Class<T> cls) {
        try {
            if (TextUtils.isEmpty(success)) {
                // 外层数据实体是空的
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
                return null;
            }
            BaseBean<List<T>> baseBean = JSONObject.parseObject(success, new TypeReference<BaseBean<List<T>>>(cls) {
            });
            if (baseBean == null) {
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
                return null;
            }
            // 统一处理401 Token失效
            if (baseBean.getCode() == 401) {
                TokenInterceptor tokenInterceptor = LoginService.getConfig().getTokenInterceptor();
                if (tokenInterceptor != null) {
                    tokenInterceptor.onInvalid();
                } else {
                    dispatchFailureData(callback, baseBean.getCode(),
                            Utils.getApp().getString(R.string.dblogin_fail_401) + baseBean.getCode());
                }
                return null;
            }
            // 统一处理401 Token失效
            if (baseBean.getCode() == 412) {
                dispatchFailureData(callback, baseBean.getCode(),
                        Utils.getApp().getString(R.string.dblogin_fail_412) + baseBean.getCode());
                return null;
            }
            // 统一处理601 业务数据为空
            if (baseBean.getCode() == 601) {
                return baseBean;
            }
            // 普通错误码的统一处理
            if (baseBean.getCode() != 200) {
                int stringResId = CodeEnum.valueOf(baseBean.getCode()).getStringResId();
                // 未处理的错误码，向上抛出
                if (stringResId == R.string.dblogin_fail_uncontrolled_code) {
                    dispatchFailureData(callback, baseBean.getCode(),
                            Utils.getApp().getString(CodeEnum.valueOf(baseBean.getCode()).getStringResId()) + baseBean.getCode());
                } else {
                    dispatchFailureData(callback, baseBean.getCode(), CodeEnum.valueOf(baseBean.getCode()).getStringResId());
                }
                return null;
            }
            return baseBean;
        } catch (JSONException e) {
            // json转换异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_json);
            return null;
        } catch (Exception e) {
            // 未知异常
            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_unknow);
            return null;
        }
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
