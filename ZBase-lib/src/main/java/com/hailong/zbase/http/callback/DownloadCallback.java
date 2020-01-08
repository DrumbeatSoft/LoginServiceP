package com.hailong.zbase.http.callback;

import com.hailong.zbase.http.HttpFail;

/**
 * 文件下载回调
 * Created by ZuoHailong on 2020/1/6.
 */
public abstract class DownloadCallback {

    public abstract void onStart();

    public abstract void onProgress(int progress, long byteCount, long speed);

    public abstract void onSuccess(String path);

    public abstract void onFail(HttpFail fail);

    public void onCancel() {
    }
}
