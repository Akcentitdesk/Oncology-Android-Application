package com.indiaoncology.ui.medicine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.indiaoncology.R;
import com.indiaoncology.adaptar.medicine.ListingAdapter;
import com.indiaoncology.databinding.ActivityListingBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.medicine.MedicineDataList;
import com.indiaoncology.model.medicine.MedicineListResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.others.SearchActivity;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class Listing extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityListingBinding binding;
    private List<MedicineDataList> dataList = new ArrayList<>();
    private ListingAdapter adapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private Api api;
    private int TOTAL_PAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing);
        context = Listing.this;
        activity = Listing.this;
        api = RequestController.createService(context, Api.class);
        setViews();
        setAdapter();
        getData();
        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();
            setAdapter();
            getData();
        });
    }

    private void setViews() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setText(getResources().getString(R.string.medicine));
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivRight.setOnClickListener(this);
    }

    private void getData() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        if (CommonUtils.isOnline(context)) {
            try {
                api.getList(currentPage).enqueue(new BaseCallback<MedicineListResponse>(context) {
                    @Override
                    public void onSuccess(MedicineListResponse response) {
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
                                    dataList.clear();
                                    dataList.addAll(response.getDataList());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    setEmptyLayout(true);
                                    dataList.clear();
                                    adapter.notifyDataSetChanged();
                                }

                                if (currentPage < TOTAL_PAGES)
                                    adapter.addLoadingFooter();
                                else isLastPage = true;

                            } else {
                                setEmptyLayout(true);
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<MedicineListResponse> call, BaseResponse baseResponse) {
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
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        binding.rvData.setLayoutManager(layoutManager);
        binding.rvData.hasFixedSize();
        binding.rvData.setItemAnimator(new DefaultItemAnimator());
        adapter = new ListingAdapter(activity, dataList);
        binding.rvData.setAdapter(adapter);
        binding.rvData.addOnScrollListener(new PaginationScrollListener(layoutManager, "GRID") {
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

    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.swipeRefreshingLayout.setVisibility(View.GONE);
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
            binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
            binding.emptyLayout.tvSubHeading.setText("No medicines found!");
            //  binding.emptyLayout.ivImage.setImageResource(R.drawable.search_doctors);
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.swipeRefreshingLayout.setVisibility(View.VISIBLE);
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

    private void loadNextPageData() {
        api.getList(currentPage).enqueue(new BaseCallback<MedicineListResponse>(context) {
            @Override
            public void onSuccess(MedicineListResponse response) {
                if (response != null) {
                    if (response.getPagination() != null) {
                        currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                        TOTAL_PAGES = Integer.parseInt(String.valueOf(response.getPagination().getMaxPage()));
                    }

                    adapter.removeLoadingFooter();
                    isLoading = false;

                    List<MedicineDataList> result = response.getDataList();
                    if (result == null)
                        return;
                    else
                        adapter.addAll(result);
                    binding.swipeRefreshingLayout.setRefreshing(false);

                    if (currentPage < TOTAL_PAGES)
                        adapter.addLoadingFooter();
                    else isLastPage = true;
                }


            }

            @Override
            public void onFail(Call<MedicineListResponse> call, BaseResponse baseResponse) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivRight:
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.FROM, AppConstant.MEDICINE);
                ActivityController.startActivity(activity, SearchActivity.class, bundle, false, false);
                break;
            default:
                break;

        }
    }
}