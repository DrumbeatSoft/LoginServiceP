
# LoginServiceP

鼓点·中台·登录Service

[![](https://jitpack.io/v/ZuoHailong/LoginServiceP.svg)](https://jitpack.io/#ZuoHailong/LoginServiceP)
[![api](https://img.shields.io/badge/API-19+-brightgreen.svg)](https://android-arsenal.com/api?level=19)
[![csdn](https://img.shields.io/badge/CSDN-ZuoHailong-green.svg)](https://blog.csdn.net/hailong0529)

### 引入
```

implementation 'com.github.ZuoHailong:LoginServiceP:latest.release'

```

### 1、初始化配置

要放在Application的onCreate()中。

```

void LoginService.setConfig(ServiceConfig.newBuilder()
                        .setAppId(String appId)
                        .setTenant(String tenant)
                        .setBaseUrl(String baseUrl)
                        .build());
```
* appId：应用标识，公司各应用有各自的appId
* tenant：租户标识，河南OPPO用“Auth”
* baseUrl：要访问的中台服务器的baseUrl，形如："http://192.168.20.233:30060/" （测试时用此url）

### 2、调用中台登录接口
```
/**
     * 登录中台
     *
     * @param account
     * @param password
     * @param callback
     */
void LoginService.login(String account, String password, ResultCallback<LoginResultBean> callback);

    /**
     * 登录中台
     *
     * @param serviceConfig 可选，一次性参数
     * @param account
     * @param password
     * @param callback
     */
void LoginService.login(ServiceConfig serviceConfig, String account, String password, ResultCallback<LoginResultBean> callback);

```
* account：中台统一后的账户
* password：中台统一后的账户密码
* callback：回调接口
* LoginResultBean：登录成功得到的实体类数据，形如：

```

public class LoginResultBean {
    private int Result;
    private String Token;
    private String AbsExpire;
    private String Data;

    ……
    
    getXxx();
    
    ……

}

```

### 3、拉起扫码登录页面（用于支持Web端管理系统的扫码登陆）
```

void LoginService.scan(Activity activity, ResultCallback callback);

```

### 4、Ghost APP 获取中台token
插件APP中使用
```

String LoginService.getCentralizerToken();

String LoginService.getCentralizerToken(Context context);

```

### 5、修改密码
```

void LoginService.modifyPassword(String oldPwd, String newPwd, String centralizerToken, ResultCallback<ResultBean> callback);

```

### 6、获取用户信息
```

void getUserInfo(String centralizerToken, ResultCallback<UserInfoBean.ResultBean> callback);

```

### 错误码枚举类

##### ResultCallback 回调函数回调 onFail(ResultCode resultCode) 函数时会返回错误码

```

public enum ResultCode {
    /**
     * 成功
     */
    SUCCEES,
    /**
     * 账号密码登录失败
     */
    ERROR_LOGIN_ACCOUNT,
    /**
     * 二维码扫描失败
     */
    ERROR_QRCODE_SCAN,
    /**
     * 二维码数据验证失败
     */
    ERROR_QRCODE_VERIFY,
    /**
     * 扫码登录失败
     */
    ERROR_QRCODE_LOGIN,
    /**
     * 用户取消扫码登录
     */
    CANCEL_LOGIN_QRCODE,
    /**
     * 取消扫码登录操作失败
     */
    ERROR_CANCEL_LOGIN_QRCODE,

    /**
     * 修改密码失败
     */
    ERROR_MODIFY_PASSWORD,
    /**
     * 获取用户信息失败
     */
    ERROR_GET_USER_INFO,


    /**************************************** 入参验证 ****************************************/
    /**
     * appId is null
     */
    ERROR_NULL_APPID,
    /**
     * account is null
     */
    ERROR_NULL_ACCOUNT,
    /**
     * password is null
     */
    ERROR_NULL_PASSWORD,
}

```

