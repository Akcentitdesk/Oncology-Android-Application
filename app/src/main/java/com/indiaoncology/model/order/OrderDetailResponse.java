package com.indiaoncology.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

public class OrderDetailResponse extends BaseResponse {

    @SerializedName("order_data")
    @Expose
    private OrderDetailData orderDetailData;

    public OrderDetailData getOrderDetailData() {
        return orderDetailData;
    }

    public void setOrderDetailData(OrderDetailData orderDetailData) {
        this.orderDetailData = orderDetailData;
    }
}
