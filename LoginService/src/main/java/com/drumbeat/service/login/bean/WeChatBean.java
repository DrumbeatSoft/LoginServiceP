package com.drumbeat.service.login.bean;

/**
 * @author ZuoHailong
 * @date 2020/7/30
 */
public class WeChatBean {
    private String unionId;
    private String nickName;
    private String sex;
    private String city;
    private String province;
    private String country;
    private String headimgurl;

    public String getUnionId() {
        return unionId;
    }

    public WeChatBean setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public WeChatBean setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public WeChatBean setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getCity() {
        return city;
    }

    public WeChatBean setCity(String city) {
        this.city = city;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public WeChatBean setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public WeChatBean setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public WeChatBean setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
        return this;
    }
}
