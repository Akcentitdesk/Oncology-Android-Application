package com.indiaoncology.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class PrescriptionResponse extends BaseResponse {
    @SerializedName("report_data")
    @Expose
    private List<PrescriptionData> prescriptionDataList = null;

    public List<PrescriptionData> getPrescriptionDataList() {
        return prescriptionDataList;
    }

    public void setPrescriptionDataList(List<PrescriptionData> prescriptionDataList) {
        this.prescriptionDataList = prescriptionDataList;
    }
}
