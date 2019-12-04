package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginResultBean {
    private int Result;
    private String Token;
    private String AbsExpire;
    private String Data;

    public String getToken() {
        return Token;
    }

    public LoginResultBean setToken(String token) {
        Token = token;
        return this;
    }

    public int getResult() {
        return Result;
    }

    public LoginResultBean setResult(int result) {
        Result = result;
        return this;
    }

    public String getAbsExpire() {
        return AbsExpire;
    }

    public LoginResultBean setAbsExpire(String absExpire) {
        AbsExpire = absExpire;
        return this;
    }

    public String getData() {
        return Data;
    }

    public LoginResultBean setData(String data) {
        Data = data;
        return this;
    }
}
