package com.drumbeat.service.login.zbase.helper;

import android.util.Log;

import java.util.Locale;

/**
 * 日志帮助类
 * Created by ZuoHailong on 2019/12/31.
 */
public class LogHelper {

    private static final String ERROR_MESSAGE = "An exception occurs.";

    private static String sTag = "ZBase";
    private static boolean sEnable = false;

    public static void setEnable(boolean enable) {
        LogHelper.sEnable = enable;
    }

    public static void v(Object message) {
        if (sEnable) Log.v(sTag, String.valueOf(message));
    }

    public static void v(Throwable e) {
        if (sEnable) Log.v(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void v(Object message, Throwable e) {
        if (sEnable) Log.v(sTag, buildMessage(message), e);
    }

    public static void i(Object message) {
        if (sEnable) Log.i(sTag, String.valueOf(message));
    }

    public static void i(Throwable e) {
        if (sEnable) Log.i(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void i(Object message, Throwable e) {
        if (sEnable) Log.i(sTag, buildMessage(message), e);
    }

    public static void d(Object message) {
        if (sEnable) Log.d(sTag, String.valueOf(message));
    }

    public static void d(Throwable e) {
        if (sEnable) Log.d(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void d(Object message, Throwable e) {
        if (sEnable) Log.d(sTag, buildMessage(message), e);
    }

    public static void w(Object message) {
        if (sEnable) Log.w(sTag, String.valueOf(message));
    }

    public static void w(Throwable e) {
        if (sEnable) Log.w(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void w(Object message, Throwable e) {
        if (sEnable) Log.w(sTag, buildMessage(message), e);
    }

    public static void e(Object message) {
        if (sEnable) Log.e(sTag, String.valueOf(message));
    }

    public static void e(Throwable e) {
        if (sEnable) Log.e(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void e(Object message, Throwable e) {
        if (sEnable) Log.e(sTag, buildMessage(message), e);
    }

    private static String buildMessage(Object message) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogHelper.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, String.valueOf(message));
    }

}
