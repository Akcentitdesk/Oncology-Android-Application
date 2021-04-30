package com.indiaoncology.model.medicine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.feedback.FeedbackData;
import com.indiaoncology.service.BaseResponse;

public class MedicineResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private MedicineData medicineData;


    public MedicineData getMedicineData() {
        return medicineData;
    }

    public void setMedicineData(MedicineData medicineData) {
        this.medicineData = medicineData;
    }
}
