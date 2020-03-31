package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public class BaseBean<T> {
    /*
     * 成功条件：==200
     * 失败条件：!= 200
     * */
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public BaseBean<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseBean<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseBean<T> setData(T data) {
        this.data = data;
        return this;
    }
}
