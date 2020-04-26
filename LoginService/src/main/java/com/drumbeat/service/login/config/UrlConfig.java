package com.drumbeat.service.login.config;

/**
 * 中台接口
 * Created by ZuoHailong on 2019/10/17.
 */
public interface UrlConfig {
    /**
     * 查询租户列表
     */
    String GET_TENANT_URL = "gateway/api/accounttenantrelation/querybyinfo";
    /**
     * 账号密码登录
     */
    String LOGIN_URL = "gateway/api/oauth2/token";
    /**
     * 扫码，下一步确认登录
     */
    String SCAN_CODE = "gateway/api/account/scancode";

    /**
     * 确认登录
     */
    String CONFIRM_LOGIN = "gateway/api/account/confirmscancodelogin";

    /**
     * 取消扫码登录
     */
    String CANCEL_LOGIN = "gateway/api/account/cancelscancodelogin";

    /**
     * 修改密码
     */
    String MODIFY_PASSWORD = "gateway/api/account/modifypassword";

    /**
     * 查询用户信息
     */
    String GET_USER_INFO = "gateway/api/account/getbyid";

    /**
     * 检查密码是否失效需要重置
     */
    String CHECK_PASSWORD_EXPIRE = "gateway/api/account/checkpasswordexpire";

    /**
     * 获取短信验证码
     */
    String GET_SMS_CODE = "v2-gateway/technical/sms/sendunverifycode";

    /**
     * 验证短信验证码
     */
    String CHECK_SMS_CODE = "v2-gateway/technical/sms/checkunverifycode";

    /**
     * 忘记密码，修改密码
     */
    String FORGOT_PASSWORD = "v2-gateway/api/account/forgetpassword";
}
