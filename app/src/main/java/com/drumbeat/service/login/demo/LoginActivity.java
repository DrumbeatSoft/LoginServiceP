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

    EditText etAccount;
    EditText etPwd;
    Button btnLogin;
    Button btnModify;
    Button btnScan;
    Button btnCheckPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etAccount = findViewById(R.id.etAccount);
        etPwd = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnModify = findViewById(R.id.btnModify);
        btnScan = findViewById(R.id.btnScan);
        btnCheckPwd = findViewById(R.id.btnCheckPwd);
        String tenant = SPUtils.getInstance().getString("tenant");
        String account = SPUtils.getInstance().getString("account");
        String pwd = SPUtils.getInstance().getString("pwd");
        etAccount.setText(TextUtils.isEmpty(account) ? "GD020" : account);
        etPwd.setText(TextUtils.isEmpty(pwd) ? "MM20200108" : pwd);
        btnLogin.setOnClickListener(view -> {
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
            SPUtils.getInstance().put("account", etAccount.getEditableText().toString().trim());
            SPUtils.getInstance().put("pwd", etPwd.getEditableText().toString().trim());

            LoginService.setConfig(ServiceConfig.newBuilder()
//                    .setAppId("125438260305469440")//认证平台
                    .setAppId("121535616969084928")//体验店
//                    .setBaseUrl("http://192.168.20.233:30060/")
                    .setBaseUrl("http://api.drumbeatsoft.com/")
                    .build());
            LoginService.setTenantId("123480379607748607");//河南OPPO

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

        btnCheckPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginService.checkPasswordExpire(SPUtils.getInstance().getString(Constant.SP_TOKEN), new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean succeed) {
                        ToastUtils.showLong(succeed ? "失效" : "未失效");
                    }

                    @Override
                    public void onFail(ResultCode resultCode) {
                        ToastUtils.showLong("检查失败");
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
