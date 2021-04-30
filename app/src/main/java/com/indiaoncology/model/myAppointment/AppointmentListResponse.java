package com.indiaoncology.model.myAppointment;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.Pagination;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class AppointmentListResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<AppointmentData> appointmentListData = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<AppointmentData> getAppointmentListData() {
        return appointmentListData;
    }

    public void setAppointmentListData(List<AppointmentData> appointmentListData) {
        this.appointmentListData = appointmentListData;
    }


}