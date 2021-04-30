package com.indiaoncology.model.blogDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.service.BaseResponse;

public class BlogDetailResponse extends BaseResponse {
    @SerializedName("blog_data")
    @Expose
    private BlogDetail blogDetail;

    public BlogDetail getBlogDetail() {
        return blogDetail;
    }

    public void setBlogDetail(BlogDetail blogDetail) {
        this.blogDetail = blogDetail;
    }
}
