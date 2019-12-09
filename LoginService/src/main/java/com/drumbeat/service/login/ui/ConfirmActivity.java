package com.drumbeat.service.login.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.Messenger;
import com.drumbeat.service.login.ProcessControl;
import com.drumbeat.service.login.R;
import com.drumbeat.service.login.ResultCallback;
import com.drumbeat.service.login.constant.ResultCode;

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
            ProcessControl.login(this, new ResultCallback() {
                @Override
                public void onSuccess(Object succeed) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setResultCode(ResultCode.SUCCEES).setData((String) succeed));
                    finish();
                }

                @Override
                public void onFail(ResultCode resultCode) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setResultCode(resultCode));
                    finish();
                }
            });
        } else if (viewId == R.id.tvCancel) {
            ProcessControl.cancelLogin(this, new ResultCallback() {
                @Override
                public void onSuccess(Object succeed) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setResultCode(ResultCode.SUCCEES).setData((String) succeed));
                    finish();
                }

                @Override
                public void onFail(ResultCode resultCode) {
                    Messenger.send(ConfirmActivity.this, new Messenger.Message().setResultCode(resultCode));
                    finish();
                }
            });
        } else if (viewId == R.id.tvClose) {
            finish();
        }
    }

}
