package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginBean {
    private String token;
    private String absExpire;
    private String data;

    public String getToken() {
        return token;
    }

    public LoginBean setToken(String token) {
        this.token = token;
        return this;
    }

    public String getAbsExpire() {
        return absExpire;
    }

    public LoginBean setAbsExpire(String absExpire) {
        this.absExpire = absExpire;
        return this;
    }

    public String getData() {
        return data;
    }

    public LoginBean setData(String data) {
        this.data = data;
        return this;
    }
}
