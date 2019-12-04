package com.drumbeat.service.login.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.ResultCallback;
import com.drumbeat.service.login.constant.ResultCode;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.bean.LoginResultBean;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginActivity extends AppCompatActivity {

    EditText etTenant;
    EditText etAccount;
    EditText etPwd;
    Button btnLogin;
    Button btnScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etTenant = findViewById(R.id.etTenant);
        etAccount = findViewById(R.id.etAccount);
        etPwd = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnScan = findViewById(R.id.btnScan);
        etTenant.setText(SPUtils.getInstance().getString("tenant"));
        etAccount.setText(SPUtils.getInstance().getString("account"));
        etPwd.setText(SPUtils.getInstance().getString("pwd"));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenant = etTenant.getEditableText().toString().trim();
                if (TextUtils.isEmpty(tenant)) {
                    ToastUtils.showShort("请输入租户");
                    return;
                }
                String account = etAccount.getEditableText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    ToastUtils.showShort("请输入账号");
                    return;
                }
                String pwd = etPwd.getEditableText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showShort("请输入密码");
                    return;
                }
                SPUtils.getInstance().put("tenant", etTenant.getEditableText().toString().trim());
                SPUtils.getInstance().put("account", etAccount.getEditableText().toString().trim());
                SPUtils.getInstance().put("pwd", etPwd.getEditableText().toString().trim());

                LoginService.setConfig(ServiceConfig.newBuilder()
                        .setAppId("0")
                        .setTenant(tenant)
                        .setBaseUrl("http://192.168.20.233:30060/")
                        .build());
                LoginService.login(account, pwd, new ResultCallback<LoginResultBean>() {
                    @Override
                    public void onSuccess(LoginResultBean succeed) {
                        ToastUtils.showShort("登录成功，现在可以扫码登录了");
                        btnScan.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFail(ResultCode resultCode) {

                    }
                });
            }
        });
        btnScan.setOnClickListener(view -> LoginService.scan(LoginActivity.this, new ResultCallback() {
            @Override
            public void onSuccess(Object succeed) {

            }

            @Override
            public void onFail(ResultCode resultCode) {

            }
        }));
    }

}
