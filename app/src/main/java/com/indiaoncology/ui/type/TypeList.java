package com.indiaoncology.ui.type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.indiaoncology.R;
import com.indiaoncology.adaptar.TypeAdapter;
import com.indiaoncology.adaptar.doctor.FindDoctorAdapter;
import com.indiaoncology.databinding.ActivityDoctorListingBinding;
import com.indiaoncology.databinding.ActivityTypeListBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.model.doctor.ListResponse;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.model.type.TypeResponse;
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

public class TypeList extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityTypeListBinding binding;
    private List<Data> typeList = new ArrayList<>();
    private TypeAdapter typeAdapter;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private Api api;
    private int TOTAL_PAGES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_type_list);
        context = TypeList.this;
        activity = TypeList.this;
        api = RequestController.createService(context, Api.class);
        setToolbar();

        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            typeAdapter.clear();
            setData();
        });
    }

    public void setData() {
        setAdapter();
        getTypeList();
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

    private void setAdapter() {
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        binding.rvTypes.setLayoutManager(layoutManager);
        binding.rvTypes.hasFixedSize();
        binding.rvTypes.setItemAnimator(new DefaultItemAnimator());
        typeAdapter = new TypeAdapter(activity, typeList, "grid");
        binding.rvTypes.setAdapter(typeAdapter);
        binding.rvTypes.addOnScrollListener(new PaginationScrollListener(layoutManager, "GRID") {
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
        api.getTypeList(currentPage).enqueue(new BaseCallback<TypeResponse>(context) {
            @Override
            public void onSuccess(TypeResponse response) {
                if (response != null) {
                    if (response.getPagination() != null) {
                        currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                        TOTAL_PAGES = Integer.parseInt(String.valueOf(response.getPagination().getMaxPage()));
                    }

                    typeAdapter.removeLoadingFooter();
                    isLoading = false;

                    List<Data> result = response.getDataList();
                    if (result == null)
                        return;
                    else
                        typeAdapter.addAll(result);
                    binding.swipeRefreshingLayout.setRefreshing(false);

                    if (currentPage < TOTAL_PAGES)
                        typeAdapter.addLoadingFooter();
                    else isLastPage = true;
                }


            }

            @Override
            public void onFail(Call<TypeResponse> call, BaseResponse baseResponse) {

            }
        });
    }


    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.menubar.ivRight.setOnClickListener(this);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.tvTitle.setText("Type of Cancer");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivRight:
                break;
            case R.id.ivSecond:
                break;
            default:
                break;
        }
    }

    private void getTypeList() {
        if (CommonUtils.isOnline(context)) {
            try {

                api.getTypeList(currentPage).enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
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
                                    typeList.clear();
                                    typeList.addAll(response.getDataList());
                                    typeAdapter.notifyDataSetChanged();
                                } else {
                                    setEmptyLayout(true);
                                    typeList.clear();
                                    typeAdapter.notifyDataSetChanged();
                                }

                                if (currentPage < TOTAL_PAGES)
                                    typeAdapter.addLoadingFooter();
                                else isLastPage = true;

                            } else {
                                setEmptyLayout(true);
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<TypeResponse> call, BaseResponse baseResponse) {
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
