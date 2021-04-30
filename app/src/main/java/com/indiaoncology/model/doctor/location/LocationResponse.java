package com.indiaoncology.model.doctor.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class LocationResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<LocationResponseData> data = null;

    public List<LocationResponseData> getData() {
        return data;
    }

    public void setData(List<LocationResponseData> data) {
        this.data = data;
    }
}
