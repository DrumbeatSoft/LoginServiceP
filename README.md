
# LoginServiceP

鼓点·中台·登录Service

<br>

### 引入
```java

implementation 'com.github.ZuoHailong:LoginServiceP:0.1.5'

```

<br>

### 1、初始化配置
```java

LoginService.setConfig(ServiceConfig.newBuilder()
                        .setAppId(String appId)
                        .setTenant(String tenant)
                        .setBaseUrl(String baseUrl)
                        .build());
                        
```

* appId：【必须】应用标识，公司各应用有各自的appId
* tenant：租户标识
* baseUrl：要访问的中台服务器的baseUrl，形如："http://192.168.20.233:30060/" （测试时用此url）

<br>

### 2、调用中台登录接口
```java

LoginService.login(String account, String password, ResultCallback<LoginResultBean> callback);

```
* account：中台统一后的账户
* password：中台统一后的账户密码
* callback：回调接口
* LoginResultBean：登录成功得到的实体类数据，形如：

```java

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
<br>

### 3、拉起扫码登录页面（用于支持Web端管理系统的扫码登陆）
```java

LoginService.scan(Activity activity, ResultCallback callback);

```
<br>

### 4、Ghost APP 获取中台token
```java

LoginService.getCentralizerToken();

```
<br>

### 错误码枚举类
##### ResultCallback 回调函数回调 onFail(ResultCode resultCode) 函数时会返回错误码
<br>

```java

public enum ResultCode {
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
     * 取消扫码登录
     */
    CANCEL_LOGIN_QRCODE,


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

