package com.drumbeat.service.login.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.bean.FailureBean;
import com.drumbeat.service.login.bean.LoginBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.demo.qbar.CodeType;
import com.drumbeat.service.login.demo.qbar.OnScanListener;
import com.drumbeat.service.login.demo.qbar.QBarHelper;
import com.drumbeat.service.login.demo.qbar.ScanResult;
import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;

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
    @BindView(R.id.etSmsCode)
    EditText etSmsCode;
    @BindView(R.id.tvTenantNull)
    TextView tvTenantNull;
    @BindView(R.id.tvName)
    TextView tvName;

    private boolean initFaceSuccess = false;
    private boolean isUploadFace = false;
    private boolean isCompareFace = false;

    private float[] faceFeaturesInDb;

    private String accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        String account = SPUtils.getInstance().getString("account");
        String pwd = SPUtils.getInstance().getString("pwd");

        account = TextUtils.isEmpty(account) ? "admin" : account;
        etAccount.setText(account);
        getTenant();
        etPwd.setText(TextUtils.isEmpty(pwd) ? "admin" : pwd);

        etAccount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!TextUtils.isEmpty(etAccount.getEditableText().toString().trim()) && !hasFocus) {
                getTenant();
            }
        });
        queryResourceFace();
    }

    @OnClick({R.id.btnLogin, R.id.btnSaveFace, R.id.btnGetFace, R.id.btnCompareFace, R.id.btnCheckPwd, R.id.btnModify, R.id.btnScan, R.id.btnGetUserInfo, R.id.btngetSmsCode, R.id.btnCheckSmsCode, R.id.btnForgotPassword})
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
            case R.id.btngetSmsCode:
                LoginService.getSmsCode(etAccount.getEditableText().toString().trim(), KeyConstant.privateKey, new LoginService.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            ToastUtils.showShort("短信验证码获取成功");
                        } else {
                            ToastUtils.showShort("短信验证码获取失败");
                        }
                    }

                    @Override
                    public void onFailure(FailureBean failure) {
                        ToastUtils.showShort(failure.getMsg());
                    }
                });
                break;
            case R.id.btnCheckSmsCode:
                LoginService.checkSmsCode(etAccount.getEditableText().toString().trim(),
                        etSmsCode.getEditableText().toString().trim(), KeyConstant.privateKey, new LoginService.Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean success) {
                                if (success) {
                                    ToastUtils.showShort("验证码正确");
                                } else {
                                    ToastUtils.showShort("短信验证码获取失败");
                                }
                            }

                            @Override
                            public void onFailure(FailureBean failure) {
                                ToastUtils.showShort(failure.getMsg());
                            }
                        });
                break;
            case R.id.btnForgotPassword:
                LoginService.forgotPassword(etAccount.getEditableText().toString().trim(), etSmsCode.getEditableText().toString().trim(),
                        etPwd.getEditableText().toString().trim(), KeyConstant.privateKey, new LoginService.Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean success) {
                                if (success) {
                                    ToastUtils.showShort("新密码设置成功");
                                } else {
                                    ToastUtils.showShort("新密码设置失败");
                                }
                            }

                            @Override
                            public void onFailure(FailureBean failure) {
                                ToastUtils.showShort(failure.getMsg());
                            }
                        });
                break;
            case R.id.btnSaveFace:
                isUploadFace = false;
                if (initFaceSuccess) {
                    recognizerFace(this::saveFaceFeatures);
                }
                break;
            case R.id.btnGetFace:
                LoginService.getFaceFeatures(SPUtils.getInstance().getString(SPConfig.SP_TOKEN), new LoginService.Callback<float[]>() {
                    @Override
                    public void onSuccess(float[] success) {
                        if (success == null || success.length <= 0) {
                            showToast("未查询到人脸特征信息");
                            return;
                        }
                        showToast("查询成功");
                        faceFeaturesInDb = success;
                        findViewById(R.id.btnCompareFace).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(FailureBean failure) {
                        ToastUtils.showShort(failure.getMsg());
                    }
                });
                break;
            case R.id.btnCompareFace:
                isCompareFace = false;
                if (initFaceSuccess) {
                    recognizerFace(featureData -> {
                        if (isCompareFace) return;
                        isCompareFace = true;
                        ZFace.with(this).recognizer().compare(faceFeaturesInDb, featureData, new CompareListener() {
                            @Override
                            public void onSuccess(float faceSimilar) {
                                ZFace.with(LoginActivity.this).recognizer().close();
                                showToast("比对成功，开始登录");
                                LoginService.loginWithFace(accountId, KeyConstant.privateKey, new LoginService.Callback<LoginBean>() {
                                    @Override
                                    public void onSuccess(LoginBean success) {
                                        showToast("登录成功");
                                        SPUtils.getInstance().put(SPConfig.SP_TOKEN, success.getToken());
                                    }

                                    @Override
                                    public void onFailure(FailureBean failure) {
                                        showToast(failure.getMsg());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(ErrorCode errorCode, String errorMsg) {
                                ZFace.with(LoginActivity.this).recognizer().close();
                                showToast(errorMsg);
                            }
                        });
                    });
                }
                break;
            default:
                break;
        }
    }

    private void getTenant() {
        LoginService.getTenantList(etAccount.getEditableText().toString().trim(), new LoginService.Callback<List<TenantBean>>() {
            @Override
            public void onSuccess(List<TenantBean> succeed) {
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

        LoginService.login(account1, pwd1, new LoginService.Callback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean succeed) {
                ToastUtils.showShort("登录成功");
                findViewById(R.id.btnScan).setVisibility(View.VISIBLE);
                findViewById(R.id.btnModify).setVisibility(View.VISIBLE);
                findViewById(R.id.btnCheckPwd).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGetUserInfo).setVisibility(View.VISIBLE);
                findViewById(R.id.btnSaveFace).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGetFace).setVisibility(View.VISIBLE);
                SPUtils.getInstance().put(SPConfig.SP_TOKEN, succeed.getToken());

                // 提取accountId备用
                String[] split = succeed.getToken().split("\\.");
                String base64 = split[1];
                String userBeanStr = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
                JSONObject userJSONObject = JSONObject.parseObject(userBeanStr);
                accountId = userJSONObject.getString("accountId");
                if (TextUtils.isEmpty(accountId)) {
                    accountId = userJSONObject.getString("AccountId");
                }
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showShort(failure.getMsg());
            }
        });
    }

    private void checkPwd() {
        LoginService.checkPasswordExpire(SPUtils.getInstance().getString(SPConfig.SP_TOKEN), new LoginService.Callback<Boolean>() {
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
        LoginService.loginQrcode(LoginActivity.this, SPUtils.getInstance().getString(SPConfig.SP_TOKEN), userId, new LoginService.Callback() {
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
        LoginService.getUserInfo(SPUtils.getInstance().getString(SPConfig.SP_TOKEN), new LoginService.Callback<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean success) {
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

    private void saveFaceFeatures(float[] featureData) {
        if (isUploadFace) return;
        isUploadFace = true;
        LoginService.saveFaceFeatures(SPUtils.getInstance().getString(SPConfig.SP_TOKEN), featureData, new LoginService.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success) {
                    ToastUtils.showShort("人脸特征保存成功");
                } else {
                    ToastUtils.showShort("人脸特征保存失败");
                }
            }

            @Override
            public void onFailure(FailureBean failure) {
                ToastUtils.showShort(failure.getMsg());
            }
        });
    }

    private void queryResourceFace() {
        showToast("正在查询……");
        ZFace.with(this).resource().query(needDownload -> {
            if (needDownload) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("需要下载面部识别所需资源文件，是否立即开始下载？")
                        .setPositiveButton("立即开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ZFace.with(LoginActivity.this).resource().download(new DownloadListener() {
                                    @Override
                                    public void onSuccess() {
                                        showToast("资源文件下载完成");
                                        initFace();
                                    }

                                    @Override
                                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                                        showToast("资源文件下载失败，错误码：" + errorCode);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            } else {
                initFace();
            }
        });
    }

    private void initFace() {
        showToast("正在初始化……");
        ZFace.with(this).recognizer().init(new InitListener() {
            @Override
            public void onSuccess() {
                initFaceSuccess = true;
                showToast("初始化成功");
            }

            @Override
            public void onFailure(ErrorCode errorCode, String errorMsg) {
                showToast("面部识别模块初始化失败，错误码：" + errorCode);
            }
        });
    }

    private void recognizerFace(RecognizerFaceCallback callback) {
        ZFace.with(this)
                .recognizer()
                .recognize(new RecognizeListener() {
                    @Override
                    public void onSuccess(float[] featureData) {
                        if (featureData != null && featureData.length > 0) {
                            callback.onRecognizer(featureData);
                            // 处理完成后，关闭检测器
                            ZFace.with(LoginActivity.this).recognizer().close();
                        } else {
                            showToast("未获取到人脸特征数据，请再次识别");
                        }
                    }

                    @Override
                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                        showToast("人脸识别失败，错误码：" + errorCode);
                        // 处理完成后，关闭检测器
                        ZFace.with(LoginActivity.this).recognizer().close();
                    }
                });
    }

    private void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public interface RecognizerFaceCallback {
        void onRecognizer(float[] featureData);
    }

}
