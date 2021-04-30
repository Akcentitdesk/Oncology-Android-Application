package com.indiaoncology.model.doctor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

public class DoctorResponse extends BaseResponse {

    @SerializedName("doctor_data")
    @Expose
    private DoctorData DoctorData;

    public DoctorData getDoctorData() {
        return DoctorData;
    }

    public void setDoctorData(DoctorData doctorData) {
        DoctorData = doctorData;
    }
}
