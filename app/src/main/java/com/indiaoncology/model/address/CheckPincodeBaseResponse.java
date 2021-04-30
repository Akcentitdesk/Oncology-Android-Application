package com.indiaoncology.model.address;

import com.indiaoncology.service.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckPincodeBaseResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private CheckPincodeData data;

    public CheckPincodeData getData() {
        return data;
    }

    public void setData(CheckPincodeData data) {
        this.data = data;
    }
}
