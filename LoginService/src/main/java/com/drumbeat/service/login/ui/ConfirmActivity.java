package com.drumbeat.service.login.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.BarUtils;
import com.drumbeat.service.login.Messenger;
import com.drumbeat.service.login.ProcessControl;
import com.drumbeat.service.login.R;
import com.drumbeat.service.login.callback.Callback;
import com.drumbeat.service.login.callback.FailureBean;
import com.drumbeat.service.login.constant.ResultCode;

/**
 * 扫描二维码后的确认登录页
 * Created by ZuoHailong on 2020/1/9.
 */
public class ConfirmActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dblogin_activity_confirm_login);

        BarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0), false);
        BarUtils.setStatusBarLightMode(this, true);
        ConstraintLayout layoutRoot = findViewById(R.id.layoutRoot);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutRoot.getLayoutParams();
        layoutParams.topMargin = BarUtils.getStatusBarHeight();
        layoutRoot.setLayoutParams(layoutParams);

        findViewById(R.id.tvLogin).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvClose).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tvLogin) {
            ProcessControl.login(this, new Callback() {
                @Override
                public void onSuccess(Object success) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setCode(Messenger.CODE_SUCCESS).setData((String) success));
                    finish();
                }

                @Override
                public void onFailure(FailureBean failure) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setCode(Messenger.CODE_FAILURE));
                    finish();
                }

            });
        } else if (viewId == R.id.tvCancel) {
            ProcessControl.cancelLogin(this, new Callback() {
                @Override
                public void onSuccess(Object success) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setCode(Messenger.CODE_SUCCESS).setData((String) success));
                    finish();
                }

                @Override
                public void onFailure(FailureBean failure) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setCode(Messenger.CODE_FAILURE));
                    finish();
                }
            });
        } else if (viewId == R.id.tvClose) {
            finish();
        }
    }

}
