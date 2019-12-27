package com.drumbeat.service.login.bean;

/**
 * Created by ZuoHailong on 2019/12/23.
 */
public class UserInfoBean {
    /**
     * Result : {"Account":"岳真真","FullName":"岳真真","MobilePhone":"19939133884","Email":"19939133884","IdCard":"19939133884","ModifyAccountId":121589783771226110,"ModifyAccountName":"岳真真","ModifyDate":"2019-12-23T19:02:22","CreateAccountId":0,"CreateAccountName":"系统","CreateDate":"2019-12-02T18:55:24","Id":"121589783771226112"}
     * Success : true
     * Code : 1
     */

    private ResultBean Result;
    private boolean Success;
    private int Code;

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
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

    public static class ResultBean {
        /**
         * Account : 岳真真
         * FullName : 岳真真
         * MobilePhone : 19939133884
         * Email : 19939133884
         * IdCard : 19939133884
         * ModifyAccountId : 121589783771226110
         * ModifyAccountName : 岳真真
         * ModifyDate : 2019-12-23T19:02:22
         * CreateAccountId : 0
         * CreateAccountName : 系统
         * CreateDate : 2019-12-02T18:55:24
         * Id : 121589783771226112
         */

        private String Account;
        private String FullName;
        private String MobilePhone;
        private String Email;
        private String IdCard;
        private String ModifyAccountId;
        private String ModifyAccountName;
        private String ModifyDate;
        private String CreateAccountId;
        private String CreateAccountName;
        private String CreateDate;
        private String Id;

        public String getAccount() {
            return Account;
        }

        public void setAccount(String Account) {
            this.Account = Account;
        }

        public String getFullName() {
            return FullName;
        }

        public void setFullName(String FullName) {
            this.FullName = FullName;
        }

        public String getMobilePhone() {
            return MobilePhone;
        }

        public void setMobilePhone(String MobilePhone) {
            this.MobilePhone = MobilePhone;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public String getIdCard() {
            return IdCard;
        }

        public void setIdCard(String IdCard) {
            this.IdCard = IdCard;
        }

        public String getModifyAccountName() {
            return ModifyAccountName;
        }

        public void setModifyAccountName(String ModifyAccountName) {
            this.ModifyAccountName = ModifyAccountName;
        }

        public String getModifyDate() {
            return ModifyDate;
        }

        public void setModifyDate(String ModifyDate) {
            this.ModifyDate = ModifyDate;
        }

        public String getModifyAccountId() {
            return ModifyAccountId;
        }

        public ResultBean setModifyAccountId(String modifyAccountId) {
            ModifyAccountId = modifyAccountId;
            return this;
        }

        public String getCreateAccountId() {
            return CreateAccountId;
        }

        public ResultBean setCreateAccountId(String createAccountId) {
            CreateAccountId = createAccountId;
            return this;
        }

        public String getCreateAccountName() {
            return CreateAccountName;
        }

        public void setCreateAccountName(String CreateAccountName) {
            this.CreateAccountName = CreateAccountName;
        }

        public String getCreateDate() {
            return CreateDate;
        }

        public void setCreateDate(String CreateDate) {
            this.CreateDate = CreateDate;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
}
