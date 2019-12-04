package com.drumbeat.sdk.qbar;

/**
 * 扫描结果
 * Created by ZuoHailong on 2019/8/28.
 */
public class ScanResult {
    private CodeType type;
    private String content;

    public CodeType getType() {
        return type;
    }

    public ScanResult setType(CodeType type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ScanResult setContent(String content) {
        this.content = content;
        return this;
    }
}
