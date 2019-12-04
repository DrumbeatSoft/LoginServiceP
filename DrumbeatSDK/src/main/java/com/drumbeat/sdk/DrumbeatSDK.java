package com.drumbeat.sdk;

import android.content.Context;

import com.drumbeat.sdk.helper.HttpHelper;

/**
 * Created by ZuoHailong on 2019/12/2.
 */
public class DrumbeatSDK {

    private DrumbeatSDK() {
    }

    public static DrumbeatSDK newInstance() {
        return InstaneceHelper.instance;
    }

    public void init() {
        HttpHelper.init();
    }

    private static class InstaneceHelper {

        private static DrumbeatSDK instance = new DrumbeatSDK();

    }

}
