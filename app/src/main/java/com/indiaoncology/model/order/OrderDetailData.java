package com.indiaoncology.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailData {

    @SerializedName("order_id")
    @Expose
    private String order_id;

    @SerializedName("booked_for")
    @Expose
    private String booked_for;

    @SerializedName("order_total")
    @Expose
    private String order_total;
    @SerializedName("order_status")
    @Expose
    private String order_status;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("pharmacist_call")
    @Expose
    private String pharmacistCall;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setBooked_for(String booked_for) {
        this.booked_for = booked_for;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPharmacistCall() {
        return pharmacistCall;
    }

    public void setPharmacistCall(String pharmacistCall) {
        this.pharmacistCall = pharmacistCall;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getBooked_for() {
        return booked_for;
    }

    public String getOrder_total() {
        return order_total;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getDate() {
        return date;
    }

}
