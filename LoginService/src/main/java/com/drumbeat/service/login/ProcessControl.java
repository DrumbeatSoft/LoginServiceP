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
import com.alibaba.fastjson.TypeReference;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.drumbeat.service.login.bean.BaseBean;
import com.drumbeat.service.login.bean.FailureBean;
import com.drumbeat.service.login.bean.LoginBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.http.HttpHelper;
import com.drumbeat.service.login.http.TokenInterceptor;
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
    static void getTenantList(String account, @NonNull LoginService.Callback<List<TenantBean>> callback) {
        ServiceConfig serviceConfig = LoginService.getConfig();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("info", account);
        jsonObject.put("appId", serviceConfig.getAppId());

        HttpHelper.post(serviceConfig.getBaseUrl() + GET_TENANT_URL, null, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<List<TenantBean>> baseBean = dispatchSuccessDataToList(callback, success, TenantBean.class);
                callback.onSuccess(baseBean == null || baseBean.getCode() != 200 ? null : baseBean.getData());
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
        jsonObject.put("tenantCode", "");
        jsonObject.put("deviceId", "");
        jsonObject.put("tenantId", LoginService.getTenantId());
        jsonObject.put("appId", serviceConfig.getAppId());
        jsonObject.put("device", 20);//20 android
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
                if (baseBean == null) {
                    return;
                }
                if (baseBean.getCode() != 200) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                    return;
                }
                LoginBean loginBean = baseBean.getData();
                // 处理业务code
                switch (loginBean.getResult()) {
                    case 1:
                        callback.onSuccess(loginBean);
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
                        dispatchFailureData(callback, FailureBean.CODE_DEFAULT, Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + loginBean.getResult());
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
        String id = userJSONObject.getString("accountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", id);

        ServiceConfig serviceConfig = LoginService.getConfig();

        HttpHelper.post(serviceConfig.getBaseUrl() + CHECK_PASSWORD_EXPIRE, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                if (baseBean == null) {
                } else if (baseBean.getCode() != 200) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                } else {
                    callback.onSuccess(baseBean.getData());
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
        String id = userJSONObject.getString("accountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oldPassword", oldPwd);
        jsonObject.put("password", newPwd);
        jsonObject.put("id", id);

        JSONObject object = new JSONObject();
        object.put("input", jsonObject);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + MODIFY_PASSWORD, headers, object, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                if (baseBean == null) {
                } else {
                    // 处理业务code
                    switch (baseBean.getCode()) {
                        case 200:
                            callback.onSuccess(baseBean.getData());
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
                                    Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                            break;
                    }
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
    static void getUserInfo(String centralizerToken, @NonNull LoginService.Callback<UserInfoBean> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", centralizerToken);

        String[] split = centralizerToken.split("\\.");
        String base64 = split[1];
        String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
        String accountId = userJSONObject.getString("accountId");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);

        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + GET_USER_INFO, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<UserInfoBean> baseBean = dispatchSuccessDataToBean(callback, success, UserInfoBean.class);
                if (baseBean == null) {
                } else if (baseBean.getCode() != 200) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                } else {
                    callback.onSuccess(baseBean.getData());
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + SCAN_CODE, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                if (baseBean == null) {
                } else {
                    // 处理业务code
                    switch (baseBean.getCode()) {
                        /*
                         * 1：扫码成功，下一步登录确认
                         * */
                        case 200:
                            Intent intent = new Intent(activity, ConfirmActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("centralizerToken", centralizerToken);
                            activity.startActivity(intent);
                            break;
                        case 0:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_0_scancode);
                            break;
                        case 101:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_201);
                            break;
                        case 102:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_200);
                            break;
                        case 103:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_400);
                            break;
                        case 104:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_500);
                            break;
                        default:
                            dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                                    Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                            break;
                    }
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + CONFIRM_LOGIN, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                if (baseBean == null) {
                } else if (baseBean.getCode() != 200) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                } else {
                    callback.onSuccess(baseBean.getData());
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        ServiceConfig serviceConfig = LoginService.getConfig();
        HttpHelper.post(serviceConfig.getBaseUrl() + CANCEL_LOGIN, headers, jsonObject, new NetCallback() {
            @Override
            public void onSuccess(String success) {
                BaseBean<Boolean> baseBean = dispatchSuccessDataToBean(callback, success, Boolean.class);
                if (baseBean == null) {
                } else if (baseBean.getCode() != 200) {
                    dispatchFailureData(callback, FailureBean.CODE_DEFAULT,
                            Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                } else {
                    callback.onSuccess(baseBean.getData());
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
    private static <T> BaseBean<T> dispatchSuccessDataToBean(LoginService.Callback callback, String success, Class<T> cls) {
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
            /*if (baseBean.getCode() != 200) {
                // 特殊的错误码，需要开发者处理，如415等
                dispatchFailureData(callback, baseBean.getCode(),
                        Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
                return null;
            }*/
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
    private static <T> BaseBean<List<T>> dispatchSuccessDataToList(LoginService.Callback callback, String success, Class<T> cls) {
        try {
            if (TextUtils.isEmpty(success)) {
                // 外层数据实体是空的
                dispatchFailureData(callback, FailureBean.CODE_DEFAULT, R.string.dblogin_fail_nodata);
                return null;
            }
            BaseBean<List<T>> baseBean = JSON.parseObject(success, new TypeReference<BaseBean<List<T>>>(cls) {
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
            if (baseBean.getCode() != 200) {
                // 特殊的错误码，需要开发者处理，如415等
                dispatchFailureData(callback, baseBean.getCode(),
                        Utils.getApp().getString(R.string.dblogin_fail_unknow_with_code) + baseBean.getCode());
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
