package com.indiaoncology.model.doctor.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Slot {
    @SerializedName("shift")
    @Expose
    private String shift;
    @SerializedName("time_array")
    @Expose
    private List<TimeArray> timeArray = null;

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public List<TimeArray> getTimeArray() {
        return timeArray;
    }

    public void setTimeArray(List<TimeArray> timeArray) {
        this.timeArray = timeArray;
    }
}
