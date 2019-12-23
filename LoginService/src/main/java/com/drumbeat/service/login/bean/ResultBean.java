package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/23.
 */
public class ResultBean {
    private int Code;
    private boolean Success;
    private boolean Result;

    public int getCode() {
        return Code;
    }

    public ResultBean setCode(int code) {
        Code = code;
        return this;
    }

    public boolean isSuccess() {
        return Success;
    }

    public ResultBean setSuccess(boolean success) {
        Success = success;
        return this;
    }

    public boolean isResult() {
        return Result;
    }

    public ResultBean setResult(boolean result) {
        Result = result;
        return this;
    }
}
