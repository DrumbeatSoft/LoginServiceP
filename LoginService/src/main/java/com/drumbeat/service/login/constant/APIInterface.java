package com.drumbeat.service.login.constant;

/**
 * 中台接口
 * Created by ZuoHailong on 2019/10/17.
 */
public interface APIInterface {
    /**
     * 基础URL
     */
    String BASE_URL = "http://192.168.20.233:30060/";
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
}
