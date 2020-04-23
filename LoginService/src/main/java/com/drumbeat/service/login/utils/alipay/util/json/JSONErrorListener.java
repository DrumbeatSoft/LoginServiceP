package com.drumbeat.service.login.utils.alipay.util.json;

public interface JSONErrorListener {
    void start(String text);

    void error(String message, int column);

    void end();
}
