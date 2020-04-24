package com.drumbeat.service.login.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author ZuoHailong
 * @date 2020/1/6
 */
public class LocalSpUtil {
    private static final String FILE_NAME = "sp_name_loginservice";

    private static LocalSpUtil mLocalSpUtil;

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;

    //构造方法
    private LocalSpUtil(Context context) {
        mPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    //单例模式
    public static LocalSpUtil getInstance(Context context) {
        if (mLocalSpUtil == null) {
            mLocalSpUtil = new LocalSpUtil(context);
        }
        return mLocalSpUtil;
    }

    /**
     * 向SP中存储String，可选是否长久存储（账户无关长久存储，账户相关退出清除）
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * 从SP中获取string，账户相关数据，退出账户后清除此数据
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * 从SP中获取string，账户相关数据，退出账户后清除此数据
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }


}
