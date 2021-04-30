package com.indiaoncology.model.doctor.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeArray {
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

}
