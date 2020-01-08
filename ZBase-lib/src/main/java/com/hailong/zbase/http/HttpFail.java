package com.hailong.zbase.http;

/**
 * Created by ZuoHailong on 2019/12/30.
 */
public class HttpFail {
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public HttpFail setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public HttpFail setMessage(String message) {
        this.message = message;
        return this;
    }
}
