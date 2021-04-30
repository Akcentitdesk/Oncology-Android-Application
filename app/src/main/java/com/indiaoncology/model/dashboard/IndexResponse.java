package com.indiaoncology.model.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class IndexResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<IndexData> dataList = null;

    public List<IndexData> getDataList() {
        return dataList;
    }

    public void setDataList(List<IndexData> dataList) {
        this.dataList = dataList;
    }
}
