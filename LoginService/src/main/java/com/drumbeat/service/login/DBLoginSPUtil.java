package com.drumbeat.service.login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
class DBLoginSPUtil {

    static final String SP_TOKEN = "sp_token";
    static final String SP_USER_ID = "sp_user_id";

    private static final String SP_NAME = "dblogin_sp";
    private static SharedPreferences mSharedPreferences;

    private static DBLoginSPUtil instance;

    // 私有构造，不允许外部通过构造实例化 Singleton.class
    private DBLoginSPUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    static DBLoginSPUtil newInstance(Context context) {
        if (instance == null) {
            synchronized (DBLoginSPUtil.class) {
                if (instance == null) {
                    instance = new DBLoginSPUtil(context);
                }
            }
        }
        return instance;
    }

    void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

}
