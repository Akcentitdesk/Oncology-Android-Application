package com.indiaoncology.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

public class SaveOrderResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private SaveOrderData saveOrderData;

    public SaveOrderData getSaveOrderData() {
        return saveOrderData;
    }

    public void setSaveOrderData(SaveOrderData saveOrderData) {
        this.saveOrderData = saveOrderData;
    }
}
