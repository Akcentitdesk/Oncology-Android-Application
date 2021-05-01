package com.indiaoncology.model.medicine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicineDataList {
    @SerializedName("image")
    @Expose
    private String image_;
    @SerializedName("discount")
    @Expose
    private String discount_percentage;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getImage_() {
        return image_;
    }

    public void setImage_(String image_) {
        this.image_ = image_;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
