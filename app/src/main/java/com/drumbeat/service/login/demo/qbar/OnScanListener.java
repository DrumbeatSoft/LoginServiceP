package com.drumbeat.service.login.demo.qbar;

/**
 * 扫描监听
 * Created by ZuoHailong on 2019/8/28.
 */
public interface OnScanListener {
    void onSuccess(ScanResult scanResult);

    void onFail();
}
