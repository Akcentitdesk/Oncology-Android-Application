package com.indiaoncology.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

import java.util.List;

public class SearchingResponse extends BaseResponse {
    @SerializedName("search_data")
    @Expose
    private List<SearchingResultData> searchingResultDataList = null;

    public List<SearchingResultData> getSearchingResultDataList() {
        return searchingResultDataList;
    }

    public void setSearchingResultDataList(List<SearchingResultData> searchingResultDataList) {
        this.searchingResultDataList = searchingResultDataList;
    }
}
