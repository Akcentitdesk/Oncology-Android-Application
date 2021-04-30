package com.indiaoncology.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("login_type")
    @Expose
    private String login_type;
    @SerializedName("social_token")
    @Expose
    private String social_token;

    @SerializedName("is_verified")
    @Expose
    private String isVerified;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("company_email")
    @Expose
    private String company_email;
    @SerializedName("company_mobile")
    @Expose
    private String company_mobile;
    @SerializedName("company_address")
    @Expose
    private String company_address;
    @SerializedName("privacy_policy_url")
    @Expose
    private String privacy_policy_url;
    @SerializedName("about_us_url")
    @Expose
    private String about_us_url;
    @SerializedName("term_condition_url")
    @Expose
    private String term_condition_url;

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email;
    }

    public String getCompany_mobile() {
        return company_mobile;
    }

    public void setCompany_mobile(String company_mobile) {
        this.company_mobile = company_mobile;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getPrivacy_policy_url() {
        return privacy_policy_url;
    }

    public void setPrivacy_policy_url(String privacy_policy_url) {
        this.privacy_policy_url = privacy_policy_url;
    }

    public String getAbout_us_url() {
        return about_us_url;
    }

    public void setAbout_us_url(String about_us_url) {
        this.about_us_url = about_us_url;
    }

    public String getTerm_condition_url() {
        return term_condition_url;
    }

    public void setTerm_condition_url(String term_condition_url) {
        this.term_condition_url = term_condition_url;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getSocial_token() {
        return social_token;
    }

    public void setSocial_token(String social_token) {
        this.social_token = social_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
