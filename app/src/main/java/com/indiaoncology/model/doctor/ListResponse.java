package com.indiaoncology.model.doctor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.Pagination;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class ListResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<DoctorData> dataList = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public List<DoctorData> getDataList() {
        return dataList;
    }

    public void setDataList(List<DoctorData> dataList) {
        this.dataList = dataList;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
