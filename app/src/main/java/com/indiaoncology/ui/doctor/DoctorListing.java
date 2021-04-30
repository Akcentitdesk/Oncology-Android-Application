package com.indiaoncology.ui.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.doctor.FindDoctorAdapter;
import com.indiaoncology.databinding.ActivityDoctorListingBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.model.doctor.ListResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class DoctorListing extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityDoctorListingBinding binding;
    private List<DoctorData> doctorDataList = new ArrayList<>();
    private FindDoctorAdapter doctorAdapter;
    private String doctor_Category_ID, title, tag_id;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private Api api;
    private int TOTAL_PAGES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_listing);
        context = DoctorListing.this;
        activity = DoctorListing.this;
        api = RequestController.createService(context, Api.class);
        getData();
        CommonUtils.setToolBar(binding.menubar, title, activity);
        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            doctorAdapter.clear();
            setData();
        });
    }

    public void setData() {
        setAdapter();
        getDoctorListing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        setData();
    }

    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("Doctor_Category_ID") != null && !intent.getStringExtra("Doctor_Category_ID").isEmpty()) {
                doctor_Category_ID = intent.getStringExtra("Doctor_Category_ID");
            }

            if (intent.getStringExtra("TAG_ID") != null && !intent.getStringExtra("TAG_ID").isEmpty()) {
                tag_id = intent.getStringExtra("TAG_ID");
            }

            if (intent.getStringExtra("TITLE") != null && !intent.getStringExtra("TITLE").isEmpty()) {
                title = intent.getStringExtra("TITLE");
            }
        }

    }


    private void setAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvDoctors.setLayoutManager(layoutManager);
        binding.rvDoctors.hasFixedSize();
        binding.rvDoctors.setItemAnimator(new DefaultItemAnimator());
        doctorAdapter = new FindDoctorAdapter(activity, doctorDataList);
        binding.rvDoctors.setAdapter(doctorAdapter);
        binding.rvDoctors.addOnScrollListener(new PaginationScrollListener(layoutManager, "LINEAR") {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPageData();
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


    private void loadNextPageData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", currentPage);
        if (doctor_Category_ID != null && !doctor_Category_ID.isEmpty())
            map.put("doctor_category_id", doctor_Category_ID);
        if (tag_id != null && !tag_id.isEmpty())
            map.put("tag_id", tag_id);
        api.getDoctorsList(map).enqueue(new BaseCallback<ListResponse>(context) {
            @Override
            public void onSuccess(ListResponse response) {
                if (response != null) {
                    if (response.getPagination() != null) {
                        currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                        TOTAL_PAGES = Integer.parseInt(String.valueOf(response.getPagination().getMaxPage()));
                    }

                    doctorAdapter.removeLoadingFooter();
                    isLoading = false;

                    List<DoctorData> result = response.getDataList();
                    if (result == null)
                        return;
                    else
                        doctorAdapter.addAll(result);
                    binding.swipeRefreshingLayout.setRefreshing(false);

                    if (currentPage < TOTAL_PAGES)
                        doctorAdapter.addLoadingFooter();
                    else isLastPage = true;
                }


            }

            @Override
            public void onFail(Call<ListResponse> call, BaseResponse baseResponse) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }

    private void getDoctorListing() {
        if (CommonUtils.isOnline(context)) {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("page", currentPage);

                if (doctor_Category_ID != null && !doctor_Category_ID.isEmpty())
                    map.put("doctor_category_id", doctor_Category_ID);

                if (tag_id != null && !tag_id.isEmpty())
                    map.put("tag_id", tag_id);

                api.getDoctorsList(map).enqueue(new BaseCallback<ListResponse>(context) {
                    @Override
                    public void onSuccess(ListResponse response) {
                        binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                        binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                        binding.swipeRefreshingLayout.setRefreshing(false);

                        if (response != null) {
                            if (response.getStatus().equals("1")) {

                                if (response.getPagination() != null) {
                                    currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                                    TOTAL_PAGES = Integer.parseInt(String.valueOf(response.getPagination().getMaxPage()));
                                }
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    setEmptyLayout(false);
                                    doctorDataList.clear();
                                    doctorDataList.addAll(response.getDataList());
                                    doctorAdapter.notifyDataSetChanged();
                                } else {
                                    setEmptyLayout(true);
                                    doctorDataList.clear();
                                    doctorAdapter.notifyDataSetChanged();
                                }

                                if (currentPage < TOTAL_PAGES)
                                    doctorAdapter.addLoadingFooter();
                                else isLastPage = true;

                            } else {
                                setEmptyLayout(true);
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<ListResponse> call, BaseResponse baseResponse) {
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

    public void noInternetPopup() {
        binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
        binding.swipeRefreshingLayout.setVisibility(View.GONE);
        binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
        binding.emptyLayout.tvHeading.setText(getResources().getString(R.string.no_internet_connection));
        //binding.emptyLayout.ivImage.setImageResource(R.drawable.no_internet);
        binding.emptyLayout.tvSubHeading.setText(R.string.no_internet);
        binding.emptyLayout.btnSubmit.setText(getResources().getString(R.string.retry));
        binding.emptyLayout.btnSubmit.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
    }


    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.swipeRefreshingLayout.setVisibility(View.GONE);
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
            binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
            binding.emptyLayout.tvSubHeading.setText("No doctors found!");
            //  binding.emptyLayout.ivImage.setImageResource(R.drawable.search_doctors);
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.swipeRefreshingLayout.setVisibility(View.VISIBLE);
        }
    }
}
