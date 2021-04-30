package com.indiaoncology.model.type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.Pagination;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class TypeResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<Data> dataList = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
