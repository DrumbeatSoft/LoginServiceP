package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2020/1/7.
 */
public class TenantBean {
    /**
     * tenantId : 100000000000000000
     * code : auth
     * tenantName : 认证平台
     */

    private String tenantId;
    private String code;
    private String tenantName;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String TenantId) {
        this.tenantId = TenantId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String Code) {
        this.code = Code;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String TenantName) {
        this.tenantName = TenantName;
    }
}
