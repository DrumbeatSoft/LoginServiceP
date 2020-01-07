package com.drumbeat.service.login.constant;

import java.io.Serializable;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public enum ResultCode implements Serializable {
    /**
     * 成功
     */
    SUCCEES,
    /**
     * 查询租户失败
     */
    ERROR_GET_TENANT,
    /**
     * 账号密码登录失败
     */
    ERROR_LOGIN_ACCOUNT,
    /**
     * 二维码扫描失败
     */
    ERROR_QRCODE_SCAN,
    /**
     * 二维码数据验证失败
     */
    ERROR_QRCODE_VERIFY,
    /**
     * 扫码登录失败
     */
    ERROR_QRCODE_LOGIN,
    /**
     * 用户取消扫码登录
     */
    CANCEL_LOGIN_QRCODE,
    /**
     * 取消扫码登录操作失败
     */
    ERROR_CANCEL_LOGIN_QRCODE,

    /**
     * 修改密码失败
     */
    ERROR_MODIFY_PASSWORD,
    /**
     * 获取用户信息失败
     */
    ERROR_GET_USER_INFO,
    /**
     * token失效
     */
    ERROR_TOKEN_INVALID,


    /********************************************************** 入参验证 ********************************************************/
    /**
     * appId is null
     */
    ERROR_NULL_APPID,
    /**
     * account is null
     */
    ERROR_NULL_ACCOUNT,
    /**
     * password is null
     */
    ERROR_NULL_PASSWORD,
}
