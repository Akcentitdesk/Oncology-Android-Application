package com.indiaoncology.ui.review;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.ReviewAdapter;
import com.indiaoncology.databinding.ActivityAllReviewsBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.review.ReviewData;
import com.indiaoncology.model.review.ReviewResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class AllReviews extends AppCompatActivity {
    private Context context;
    private Activity activity;
    private ActivityAllReviewsBinding binding;
    private String product_id = "", from = "", blog_id = "", title = "", doctor_id = "", lab_id = "", blog_title = "", blog_image = "";
    private List<ReviewData> reviewDataList = new ArrayList<>();
    private List<ReviewData> blogCommentList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private ReviewAdapter commentAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private Api api;
    private Dialog dialog;
    private int TOTAL_PAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_reviews);
        context = AllReviews.this;
        activity = AllReviews.this;
        api = RequestController.createService(context, Api.class);
        getData();
        CommonUtils.setToolBar(binding.menuBar, title, activity);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
        if (from.equalsIgnoreCase(AppConstant.TYPE_BLOG)) {
            binding.menuBar.ivRight.setVisibility(View.GONE);
            setCommentsAdapter();
            getArticleCommentsApi();
        } else {
            setAdapter();
            getAllDoctorReviewsApi();
        }

        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            reviewAdapter.clear();
            if (from.equalsIgnoreCase(AppConstant.TYPE_BLOG)) {
                setCommentsAdapter();
                getArticleCommentsApi();
            } else {
                setAdapter();
                getAllDoctorReviewsApi();
            }

        });
    }

    private void getArticleCommentsApi() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();

        if (CommonUtils.isOnline(context)) {
            try {
                api.getBlogComments(currentPage, blog_id)
                        .enqueue(new BaseCallback<ReviewResponse>(context) {
                            @Override
                            public void onSuccess(ReviewResponse response) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        binding.llData.setVisibility(View.VISIBLE);
                                        if (response.getPagination() != null) {
                                            TOTAL_PAGES = response.getPagination().getMaxPage();
                                            currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                                        }
                                        if (response.getReviewDataList() != null && !response.getReviewDataList().isEmpty()) {
                                            blogCommentList.clear();
                                            blogCommentList.addAll(response.getReviewDataList());
                                            commentAdapter.notifyDataSetChanged();
                                        } else {
                                            blogCommentList.clear();
                                            commentAdapter.notifyDataSetChanged();
                                        }

                                        if (currentPage < TOTAL_PAGES)
                                            commentAdapter.addLoadingFooter();
                                        else isLastPage = true;
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<ReviewResponse> call, BaseResponse baseResponse) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
            noInternetPopup();
        }
    }

    private void setCommentsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvAllReviews.setLayoutManager(layoutManager);
        binding.rvAllReviews.hasFixedSize();
        binding.rvAllReviews.setItemAnimator(new DefaultItemAnimator());
        commentAdapter = new ReviewAdapter(context, blogCommentList, "full");
        binding.rvAllReviews.setAdapter(commentAdapter);
        binding.rvAllReviews.addOnScrollListener(new PaginationScrollListener(layoutManager, "LINEAR") {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPageBlog();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadNextPageBlog() {
        api.getBlogComments(currentPage, blog_id)
                .enqueue(new BaseCallback<ReviewResponse>(context) {
                    @Override
                    public void onSuccess(ReviewResponse response) {
                        if (response != null) {
                            if (response.getPagination() != null) {
                                currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                                TOTAL_PAGES = response.getPagination().getMaxPage();
                            }

                            commentAdapter.removeLoadingFooter();
                            isLoading = false;

                            List<ReviewData> result = response.getReviewDataList();
                            if (result == null)
                                return;
                            else
                                commentAdapter.addAll(result);
                            binding.swipeRefreshingLayout.setRefreshing(false);

                            if (currentPage < TOTAL_PAGES)
                                commentAdapter.addLoadingFooter();
                            else isLastPage = true;
                        }
                    }

                    @Override
                    public void onFail(Call<ReviewResponse> call, BaseResponse baseResponse) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                    }
                });
    }

    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }

            if (bundle.getString("DOCTOR_ID") != null && !Objects.requireNonNull(bundle.getString("DOCTOR_ID")).isEmpty()) {
                doctor_id = bundle.getString("DOCTOR_ID");
            }
            if (bundle.getString("BLOG_ID") != null && !Objects.requireNonNull(bundle.getString("BLOG_ID")).isEmpty()) {
                blog_id = bundle.getString("BLOG_ID");
            }

            if (bundle.getString("TITLE") != null && !Objects.requireNonNull(bundle.getString("TITLE")).isEmpty()) {
                title = bundle.getString("TITLE");
            }

        }
    }


    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }


    public void noInternetPopup() {
        binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
        binding.swipeRefreshingLayout.setVisibility(View.GONE);
        binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
        binding.emptyLayout.tvHeading.setText(getResources().getString(R.string.no_internet_connection));
        binding.emptyLayout.ivImage.setImageResource(R.drawable.no_internet);
        binding.emptyLayout.tvSubHeading.setText(R.string.no_internet);
        binding.emptyLayout.btnSubmit.setText(getResources().getString(R.string.retry));
        binding.emptyLayout.btnSubmit.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
    }

    private void getAllDoctorReviewsApi() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();

        if (CommonUtils.isOnline(context)) {
            try {
                api.getDoctorReviews(currentPage, doctor_id)
                        .enqueue(new BaseCallback<ReviewResponse>(context) {
                            @Override
                            public void onSuccess(ReviewResponse response) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        // binding.rvAllReviews.setVisibility(View.VISIBLE);
                                        binding.llData.setVisibility(View.VISIBLE);
                                        TOTAL_PAGES = response.getPagination().getMaxPage();
                                        currentPage = Integer.parseInt(response.getPagination().getCurrentPage());

                                        if (response.getReviewDataList() != null && !response.getReviewDataList().isEmpty()) {
                                            reviewDataList.clear();
                                            reviewDataList.addAll(response.getReviewDataList());
                                            reviewAdapter.notifyDataSetChanged();
                                        } else {
                                            reviewDataList.clear();
                                            reviewAdapter.notifyDataSetChanged();
                                        }

                                        if (currentPage < TOTAL_PAGES)
                                            reviewAdapter.addLoadingFooter();
                                        else isLastPage = true;
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<ReviewResponse> call, BaseResponse baseResponse) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
            noInternetPopup();
        }
    }

    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvAllReviews.setLayoutManager(layoutManager);
        binding.rvAllReviews.hasFixedSize();
        binding.rvAllReviews.setItemAnimator(new DefaultItemAnimator());
        reviewAdapter = new ReviewAdapter(context, reviewDataList, "full");
        binding.rvAllReviews.setAdapter(reviewAdapter);
        binding.rvAllReviews.addOnScrollListener(new PaginationScrollListener(layoutManager, "LINEAR") {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPageDoctor();

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadNextPageDoctor() {
        api.getDoctorReviews(currentPage, doctor_id)
                .enqueue(new BaseCallback<ReviewResponse>(context) {
                    @Override
                    public void onSuccess(ReviewResponse response) {
                        if (response != null) {
                            if (response.getPagination() != null) {
                                currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                                TOTAL_PAGES = response.getPagination().getMaxPage();
                            }

                            reviewAdapter.removeLoadingFooter();
                            isLoading = false;

                            List<ReviewData> result = response.getReviewDataList();
                            if (result == null)
                                return;
                            else
                                reviewAdapter.addAll(result);
                            binding.swipeRefreshingLayout.setRefreshing(false);

                            if (currentPage < TOTAL_PAGES)
                                reviewAdapter.addLoadingFooter();
                            else isLastPage = true;
                        }
                    }

                    @Override
                    public void onFail(Call<ReviewResponse> call, BaseResponse baseResponse) {

                    }
                });

    }


}
