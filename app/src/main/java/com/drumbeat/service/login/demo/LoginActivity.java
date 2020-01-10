package com.drumbeat.service.login.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.bean.LoginResultBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.callback.FailureBean;
import com.drumbeat.service.login.constant.Constant;
import com.drumbeat.service.login.demo.qbar.CodeType;
import com.drumbeat.service.login.demo.qbar.OnScanListener;
import com.drumbeat.service.login.demo.qbar.QBarHelper;
import com.drumbeat.service.login.demo.qbar.ScanResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etAccount)
    EditText etAccount;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnGetUserInfo)
    Button btnGetUserInfo;
    @BindView(R.id.btnModify)
    Button btnModify;
    @BindView(R.id.btnCheckPwd)
    Button btnCheckPwd;
    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.tvTenantNull)
    TextView tvTenantNull;
    @BindView(R.id.tvName)
    TextView tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        String account = SPUtils.getInstance().getString("account");
        String pwd = SPUtils.getInstance().getString("pwd");

        account = TextUtils.isEmpty(account) ? "GD020" : account;
        etAccount.setText(account);
        getTenant();
        etPwd.setText(TextUtils.isEmpty(pwd) ? "MM20200108" : pwd);

        etAccount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!TextUtils.isEmpty(etAccount.getEditableText().toString().trim()) && !hasFocus) {
                getTenant();
            }
        });
    }

    @OnClick({R.id.btnLogin, R.id.btnCheckPwd, R.id.btnModify, R.id.btnScan, R.id.btnGetUserInfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnCheckPwd:
                checkPwd();
                break;
            case R.id.btnModify:
                startActivity(new Intent(this, ModifyPwdActivity.class));
                break;
            case R.id.btnScan:
                scan();
                break;
            case R.id.btnGetUserInfo:
                getUserInfo();
                break;
        }
    }

    private void getTenant() {
        LoginService.getTenantList(etAccount.getEditableText().toString().trim(), new LoginService.Callback<List<TenantBean.ResultBean>>() {
            @Override
            public void onSuccess(List<TenantBean.ResultBean> succeed) {
                if (succeed != null && succeed.size() > 0) {
                    ToastUtils.showLong(String.valueOf(succeed.get(0).getTenantId()));
                    LoginService.setTenantId(succeed.get(0).getTenantId());
                    tvTenantNull.setText("tenantId：" + succeed.get(0).getTenantId());
                } else {
                    tvTenantNull.setText("未查询到租户信息");
                }
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showLong(failure.getMsg());
                tvTenantNull.setText("未查询到租户信息");
            }

        });

    }

    private void login() {
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

        LoginService.login(account1, pwd1, new LoginService.Callback<LoginResultBean>() {
            @Override
            public void onSuccess(LoginResultBean succeed) {
                ToastUtils.showShort("登录成功");
                btnScan.setVisibility(View.VISIBLE);
                btnModify.setVisibility(View.VISIBLE);
                btnCheckPwd.setVisibility(View.VISIBLE);
                btnGetUserInfo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showShort(failure.getMsg());
            }
        });
    }

    private void checkPwd() {
        LoginService.checkPasswordExpire(SPUtils.getInstance().getString(Constant.SP_TOKEN), new LoginService.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean succeed) {
                ToastUtils.showLong(succeed ? "失效" : "未失效");
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showLong(failure.getMsg());
            }

        });
    }

    private void scan() {
        new QBarHelper.Builder()
                .setCodeType(CodeType.QR_CODE)
                .build()
                .start(this, new OnScanListener() {
                    @Override
                    public void onSuccess(ScanResult scanResult) {
                        if (scanResult == null || TextUtils.isEmpty(scanResult.getContent())) {
                            ToastUtils.showShort(R.string.fail_scancode);
                        } else {
                            // 扫码得到二维码数据，下一步验证二维码数据，进行登录
                            loginQrcode(scanResult.getContent());
                        }
                    }

                    @Override
                    public void onFail() {
                        ToastUtils.showShort(R.string.fail_scancode);
                    }
                });
    }

    private void loginQrcode(String userId) {
        LoginService.loginQrcode(LoginActivity.this, userId, new LoginService.Callback() {
            @Override
            public void onSuccess(Object success) {
                ToastUtils.showShort("扫码登录成功");
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showShort(failure.getMsg());
            }
        });
    }

    private void getUserInfo() {
        LoginService.getUserInfo(SPUtils.getInstance().getString(Constant.SP_TOKEN), new LoginService.Callback<UserInfoBean.ResultBean>() {
            @Override
            public void onSuccess(UserInfoBean.ResultBean success) {
                if (success != null) {
                    tvName.setText(StringUtils.null2Length0(success.getFullName()));
                } else {
                    ToastUtils.showShort("查询失败");
                }
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showShort(failure.getMsg());
            }
        });
    }

}
