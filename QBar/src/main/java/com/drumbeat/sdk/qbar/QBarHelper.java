package com.drumbeat.sdk.qbar;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;

import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;
import cn.bertsir.zbar.view.ScanLineView;

/**
 * 图形码的扫描帮助类
 * Created by ZuoHailong on 2019/8/28.
 */
public class QBarHelper {

    private Builder mBuilder;

    private QBarHelper(Builder Builder) {
        this.mBuilder = Builder;
    }

    /**
     * 开始扫描图形码
     *
     * @param activity Activity
     * @param listener 扫描监听
     */
    public void start(Activity activity, OnScanListener listener) {

        QrConfig qrConfig = new QrConfig.Builder()
                .setTitleBackgroudColor(Color.TRANSPARENT) //状态栏背景色
                .setShowTitle(true) //显示title
                .setTitleText(TextUtils.isEmpty(mBuilder.title) ? activity.getString(R.string.qbar_scan_qrcode) : mBuilder.title) //title文字
//                .setTitleTextColor(Color.BLACK)//设置Title文字颜色
                .setShowDes(false) //显示扫描框下面文字
//                .setDesText("(识别二维码)") //扫描框下文字
                .setShowLight(true) //显示手电筒按钮
                .setCornerColor(Color.WHITE) //扫描框颜色
                .setLineColor(Color.WHITE) //扫描线颜色
                .setLineSpeed(QrConfig.LINE_MEDIUM) //扫描线速度
                .setScanType(CodeType.QR_CODE.equals(mBuilder.codeType) ? QrConfig.TYPE_QRCODE : QrConfig.TYPE_BARCODE) //图形码类型
                .setScanViewType(CodeType.QR_CODE.equals(mBuilder.codeType) ? QrConfig.SCANVIEW_TYPE_QRCODE : QrConfig.SCANVIEW_TYPE_BARCODE) //扫描框类型
                .setIsOnlyCenter(true)//是否只识别框中内容(默认为全屏识别)
                .setPlaySound(true) //扫描成功后bi~的声音
//                .setDingPath(R.raw.test)//设置提示音(不设置为默认的Ding~)
                .setShowAlbum(false) //显示从相册选择按钮
//                .setNeedCrop(true)//从相册选择二维码之后再次截取二维码
//                .setOpenAlbumText("选择要识别的图片")//打开相册的文字
                .setScreenOrientation(QrConfig.SCREEN_PORTRAIT) //设置屏幕方向
//                .setShowZoom(false)//是否手动调整焦距
//                .setAutoZoom(false)//是否自动调整焦距
//                .setFingerZoom(false)//是否开始双指缩放
//                .setLooperScan(true)//是否连续扫描二维码
//                .setLooperWaitTime(1000)//连续扫描间隔时间
                .setScanLineStyle(ScanLineView.style_gridding)//扫描动画样式
                .setAutoLight(true) //自动灯光
                .setShowVibrator(true) //是否震动提醒
                .create();

        QrManager.getInstance().init(qrConfig).startScan(activity, result -> {
            if (result != null) {
                com.drumbeat.sdk.qbar.ScanResult scanResult = new com.drumbeat.sdk.qbar.ScanResult();
                scanResult.setType(result.getType() == 1 ? CodeType.QR_CODE : CodeType.BAR_CODE).setContent(result.getContent());
                listener.onSuccess(scanResult);
            } else {
                listener.onFail();
            }
        });
    }

    /**
     * 二维码构造器，用于初始化及构建QBarHelper
     */
    public static class Builder {

        private CodeType codeType;
        private String title;

        /**
         * 设置图形码类型
         *
         * @param codeType 图形码枚举类型{@link CodeType CodeType}
         * @return
         */
        public Builder setCodeType(CodeType codeType) {
            this.codeType = codeType;
            return this;
        }

        /**
         * 标题栏文字内容
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public QBarHelper build() {
            return new QBarHelper(this);
        }
    }

}
