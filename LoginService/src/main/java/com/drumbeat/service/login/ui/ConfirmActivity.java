package com.drumbeat.service.login.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.ProcessControl;
import com.drumbeat.service.login.R;
import com.drumbeat.service.login.bean.FailureBean;

/**
 * 扫描二维码后的确认登录页
 * Created by ZuoHailong on 2020/1/9.
 */
public class ConfirmActivity extends Activity implements View.OnClickListener {

    private String userId;
    private String centralizerToken;

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

        userId = getIntent().getStringExtra("userId");
        centralizerToken = getIntent().getStringExtra("centralizerToken");
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tvLogin) {
            ProcessControl.login(centralizerToken, userId, new LoginService.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if (success) {
                        ToastUtils.showShort(R.string.dblogin_login_success);
                        finish();
                    } else {
                        ToastUtils.showShort(R.string.dblogin_login_fail);
                    }
                }

                @Override
                public void onFailure(FailureBean failure) {
                    ToastUtils.showShort(R.string.dblogin_login_fail);
                }

            });
        } else if (viewId == R.id.tvCancel) {
            ProcessControl.cancelLogin(centralizerToken, userId, new LoginService.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if (success) {
                        ToastUtils.showShort(R.string.dblogin_cancel_login_success);
                        finish();
                    } else {
                        ToastUtils.showShort(R.string.dblogin_cancel_login_fail);
                    }
                }

                @Override
                public void onFailure(FailureBean failure) {
                    ToastUtils.showShort(R.string.dblogin_cancel_login_fail);
                }
            });
        } else if (viewId == R.id.tvClose) {
            ProcessControl.cancelLogin(centralizerToken, userId, new LoginService.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                }

                @Override
                public void onFailure(FailureBean failure) {
                }
            });
            finish();
        }
    }

}
