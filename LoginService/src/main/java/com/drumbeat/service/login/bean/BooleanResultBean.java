package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/23.
 */
public class BooleanResultBean {
    private int Code;
    private boolean Success;
    private boolean Result;

    public int getCode() {
        return Code;
    }

    public BooleanResultBean setCode(int code) {
        Code = code;
        return this;
    }

    public boolean getSuccess() {
        return Success;
    }

    public BooleanResultBean setSuccess(boolean success) {
        Success = success;
        return this;
    }

    public boolean getResult() {
        return Result;
    }

    public BooleanResultBean setResult(boolean result) {
        Result = result;
        return this;
    }
}
