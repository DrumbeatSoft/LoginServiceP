package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/3.
 */
public class LoginBean {
    private int Result;
    private String Token;
    private String AbsExpire;
    private String Data;

    public String getToken() {
        return Token;
    }

    public LoginBean setToken(String token) {
        Token = token;
        return this;
    }

    public int getResult() {
        return Result;
    }

    public LoginBean setResult(int result) {
        Result = result;
        return this;
    }

    public String getAbsExpire() {
        return AbsExpire;
    }

    public LoginBean setAbsExpire(String absExpire) {
        AbsExpire = absExpire;
        return this;
    }

    public String getData() {
        return Data;
    }

    public LoginBean setData(String data) {
        Data = data;
        return this;
    }
}
