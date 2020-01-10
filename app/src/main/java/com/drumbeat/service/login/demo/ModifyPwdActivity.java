package com.drumbeat.service.login.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.drumbeat.service.login.LoginService;
import com.drumbeat.service.login.bean.BooleanResultBean;
import com.drumbeat.service.login.callback.FailureBean;
import com.drumbeat.service.login.constant.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPwdActivity extends AppCompatActivity {

    @BindView(R.id.etOldPwd)
    EditText etOldPwd;
    @BindView(R.id.etNewPwd)
    EditText etNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.BtnSubmit)
    public void onViewClicked() {
        modifyPwd();
    }

    private void modifyPwd() {
        LoginService.modifyPassword(etOldPwd.getEditableText().toString().trim(),
                etNewPwd.getEditableText().toString().trim(),
                SPUtils.getInstance().getString(Constant.SP_TOKEN),
                new LoginService.Callback<BooleanResultBean>() {
                    @Override
                    public void onSuccess(BooleanResultBean succeed) {
                        ToastUtils.showShort("密码修改成功，改为了 " + etNewPwd.getEditableText().toString().trim());
                        finish();
                    }

                    @Override
                    public void onFailure(FailureBean failure) {
                        ToastUtils.showShort(failure.getMsg());
                    }
                });
    }

}
