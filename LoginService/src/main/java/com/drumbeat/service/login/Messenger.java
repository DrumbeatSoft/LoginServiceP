package com.drumbeat.service.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.drumbeat.service.login.constant.ResultCode;

import java.io.Serializable;

/**
 * 页面间消息传递
 * Created by ZuoHailong on 2019/12/9.
 */
public class Messenger extends BroadcastReceiver {

    public static final int CODE_SUCCESS = 100;
    public static final int CODE_FAILURE = 101;
    private static final String ACTION = "com.drumbeat.service.login";

    public static void send(Context context) {
        send(context, new Message().setCode(CODE_SUCCESS));
    }

    public static void send(Context context, Message message) {
        Intent broadcast = new Intent(ACTION);
        broadcast.putExtra("message", message);
        context.sendBroadcast(broadcast);
    }

    private final Context mContext;
    private final Callback mCallback;

    public Messenger(Context context, Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void register() {
        IntentFilter filter = new IntentFilter(ACTION);
        mContext.registerReceiver(this, filter);
    }

    public void unRegister() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        mCallback.onCallback(message);
    }

    public interface Callback {
        void onCallback(Message message);
    }

    public static class Message implements Serializable {

        private int code;
        private String data;

        public int getCode() {
            return code;
        }

        public Message setCode(int code) {
            this.code = code;
            return this;
        }

        public String getData() {
            return data;
        }

        public Message setData(String data) {
            this.data = data;
            return this;
        }
    }
}
