package com.indiaoncology.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckPincodeData {
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("expected_delivery")
    @Expose
    private String expectedDelivery;
    @SerializedName("is_delivery_available")
    @Expose
    private boolean isDeliveryAvailable;

    public String getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(String expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public boolean isDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        isDeliveryAvailable = deliveryAvailable;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
