package com.indiaoncology.model.doctor.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationDatum {
    @SerializedName("fees")
    @Expose
    private String fees;
    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("clinic_name")
    @Expose
    private String clinicName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("timings")
    @Expose
    private String timings;
    @SerializedName("banner")
    @Expose
    private List<String> banner = null;

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public List<String> getBanner() {
        return banner;
    }

    public void setBanner(List<String> banner) {
        this.banner = banner;
    }

}
