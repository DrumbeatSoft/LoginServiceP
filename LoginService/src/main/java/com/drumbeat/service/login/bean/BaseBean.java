package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public class BaseBean<T> {
    /**
     * IsSucceed : true
     * Message : string
     * StatusCode : 0
     */

    private String Entity;
    private boolean IsSucceed;
    private String Message;
    /*
     * 成功条件：==200 && Entity != null
     * 失败条件：!= 200 || Entity == null
     * */
    private int StatusCode;

    public String getEntity() {
        return Entity;
    }

    public BaseBean<T> setEntity(String entity) {
        Entity = entity;
        return this;
    }

    public boolean getIsSucceed() {
        return IsSucceed;
    }

    public void setIsSucceed(boolean IsSucceed) {
        this.IsSucceed = IsSucceed;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int StatusCode) {
        this.StatusCode = StatusCode;
    }

    public static class EntityBean {
        /**
         * Result : true
         * Success : true
         * Code : 0
         */

        private boolean Result;
        private boolean Success;
        private int Code;

        public boolean isResult() {
            return Result;
        }

        public void setResult(boolean Result) {
            this.Result = Result;
        }

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
    }
}
