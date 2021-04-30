package com.indiaoncology.model.review;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.Pagination;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class ReviewResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private List<ReviewData> reviewDataList = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }


    public List<ReviewData> getReviewDataList() {
        return reviewDataList;
    }

    public void setReviewDataList(List<ReviewData> reviewDataList) {
        this.reviewDataList = reviewDataList;
    }
}
