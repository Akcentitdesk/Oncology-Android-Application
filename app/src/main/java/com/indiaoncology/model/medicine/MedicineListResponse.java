package com.indiaoncology.model.medicine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.Pagination;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class MedicineListResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<MedicineDataList> dataList = null;

    public List<MedicineDataList> getDataList() {
        return dataList;
    }

    public void setDataList(List<MedicineDataList> dataList) {
        this.dataList = dataList;
    }

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
