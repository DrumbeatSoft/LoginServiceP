package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2020/1/9.
 */
public class FailureBean {

    public static final int CODE_DEFAULT = 0;
    /**
     * 错误码<br>
     * 1、FailureBean.CODE_DEFAULT：多是业务相关的异常，具体的异常原因在 msg 中，msg可直接提示给用户；<br>
     * 2、其他：特殊的异常，如401、415等，msg 统一是“失败”，开发者需分析具体的code，并对某些code做特殊的处理。
     */
    private int code;
    /**
     * 异常原因<br>
     * 1、业务异常：会有具体的异常原因；<br>
     * 2、网络异常：链接超时、发送数据失败等；<br>
     * 3、未知异常：是由未处理的Exception引起；<br>
     * 4、未知异常+错误码：业务引起，由未处理的业务异常code引起。
     */
    private String msg;

    public int getCode() {
        return code;
    }

    public FailureBean setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public FailureBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "FailureBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
