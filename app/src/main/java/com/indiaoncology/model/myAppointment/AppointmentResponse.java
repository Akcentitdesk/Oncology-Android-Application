package com.indiaoncology.model.myAppointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

public class AppointmentResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private AppointmentData appointmentData;

    public AppointmentData getAppointmentData() {
        return appointmentData;
    }

    public void setAppointmentData(AppointmentData appointmentData) {
        this.appointmentData = appointmentData;
    }
}

