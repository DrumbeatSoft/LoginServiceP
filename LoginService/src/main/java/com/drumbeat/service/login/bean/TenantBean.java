package com.drumbeat.service.login.bean;

import java.util.List;

/**
 * Created by ZuoHailong on 2020/1/7.
 */
public class TenantBean {

    /**
     * Result : [{"TenantId":"100000000000000000","Code":"auth","TenantName":"认证平台"}]
     * Success : true
     * Code : 1
     */

    private boolean Success;
    private int Code;
    private List<ResultBean> Result;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * TenantId : 100000000000000000
         * Code : auth
         * TenantName : 认证平台
         */

        private String TenantId;
        private String Code;
        private String TenantName;

        public String getTenantId() {
            return TenantId;
        }

        public void setTenantId(String TenantId) {
            this.TenantId = TenantId;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getTenantName() {
            return TenantName;
        }

        public void setTenantName(String TenantName) {
            this.TenantName = TenantName;
        }
    }
}
