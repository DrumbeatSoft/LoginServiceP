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
import com.drumbeat.service.login.bean.ResultBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.constant.Constant;
import com.drumbeat.service.login.constant.ResultCode;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.bean.LoginResultBean;

import java.util.List;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginActivity extends AppCompatActivity {

    EditText etTenant;
    EditText etAccount;
    EditText etPwd;
    Button btnLogin;
    Button btnModify;
    Button btnScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etTenant = findViewById(R.id.etTenant);
        etAccount = findViewById(R.id.etAccount);
        etPwd = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnModify = findViewById(R.id.btnModify);
        btnScan = findViewById(R.id.btnScan);
        String tenant = SPUtils.getInstance().getString("tenant");
        String account = SPUtils.getInstance().getString("account");
        String pwd = SPUtils.getInstance().getString("pwd");
        etTenant.setText(TextUtils.isEmpty(tenant) ? "Auth" : tenant);
        etAccount.setText(TextUtils.isEmpty(account) ? "岳真真" : account);
        etPwd.setText(TextUtils.isEmpty(pwd) ? "MM2019" : pwd);
        btnLogin.setOnClickListener(view -> {
            String tenant1 = etTenant.getEditableText().toString().trim();
            if (TextUtils.isEmpty(tenant1)) {
                ToastUtils.showShort("请输入租户");
                return;
            }
            String account1 = etAccount.getEditableText().toString().trim();
            if (TextUtils.isEmpty(account1)) {
                ToastUtils.showShort("请输入账号");
                return;
            }
            String pwd1 = etPwd.getEditableText().toString().trim();
            if (TextUtils.isEmpty(pwd1)) {
                ToastUtils.showShort("请输入密码");
                return;
            }
            SPUtils.getInstance().put("tenant", etTenant.getEditableText().toString().trim());
            SPUtils.getInstance().put("account", etAccount.getEditableText().toString().trim());
            SPUtils.getInstance().put("pwd", etPwd.getEditableText().toString().trim());

            LoginService.setConfig(ServiceConfig.newBuilder()
                    .setAppId("125438260305469440")
                    .setBaseUrl("http://192.168.20.233:30060/")
                    .build());
            LoginService.setTenantId(etTenant.getEditableText().toString().trim());

            LoginService.getTenantList(etAccount.getEditableText().toString().trim(), new ResultCallback<List<TenantBean.ResultBean>>() {
                @Override
                public void onSuccess(List<TenantBean.ResultBean> succeed) {
                    if (succeed != null && succeed.size() > 0) {
                        LoginService.setTenantId(succeed.get(0).getTenantId());
                        login(account1, pwd1);
                    }
                }

                @Override
                public void onFail(ResultCode resultCode) {

                }
            });


        });
        btnScan.setOnClickListener(view ->
                LoginService.scan(LoginActivity.this, new ResultCallback() {
                    @Override
                    public void onSuccess(Object succeed) {
                        ToastUtils.showShort("扫码登录成功");
                    }

                    @Override
                    public void onFail(ResultCode resultCode) {
                        ToastUtils.showShort("扫码登录失败：" + resultCode.name());
                    }
                }));

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginService.modifyPassword("MM2019", "MM2020", SPUtils.getInstance().getString(Constant.SP_TOKEN), new ResultCallback<ResultBean>() {
                    @Override
                    public void onSuccess(ResultBean succeed) {
                        ToastUtils.showShort("密码修改成功，改为了 MM2020");
                    }

                    @Override
                    public void onFail(ResultCode resultCode) {
                        ToastUtils.showShort("修改失败");
                    }
                });
            }
        });
    }

    private void login(String account1, String pwd1) {
        LoginService.login(account1, pwd1, new ResultCallback<LoginResultBean>() {
            @Override
            public void onSuccess(LoginResultBean succeed) {
                ToastUtils.showShort("登录成功，现在可以扫码登录了");
                btnScan.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(ResultCode resultCode) {
                ToastUtils.showShort("登录失败：" + resultCode.name());
            }
        });
    }

}
