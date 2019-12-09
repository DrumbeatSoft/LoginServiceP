package com.drumbeat.service.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;

import com.drumbeat.service.login.constant.ResultCode;

import java.io.Serializable;

/**
 * 页面间消息传递
 * Created by ZuoHailong on 2019/12/9.
 */
public class Messenger extends BroadcastReceiver {

    private static final String ACTION = "com.drumbeat.service.login";

    public static void send(Context context) {
        send(context, new Message().setResultCode(ResultCode.SUCCEES));
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

        private ResultCode resultCode;
        private String data;

        public ResultCode getResultCode() {
            return resultCode;
        }

        public Message setResultCode(ResultCode resultCode) {
            this.resultCode = resultCode;
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
