package com.indiaoncology.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comm100.livechat.ChatActivity;
import com.comm100.livechat.VisitorClientInterface;
import com.indiaoncology.R;
import com.indiaoncology.adaptar.AndroidImageAdapter;
import com.indiaoncology.adaptar.dashboard.DashboardAdapter;
import com.indiaoncology.adaptar.doctor.CategoryAdapter;
import com.indiaoncology.databinding.FragmentDoctorBinding;
import com.indiaoncology.model.dashboard.IndexData;
import com.indiaoncology.model.dashboard.IndexResponse;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.model.type.TypeResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.SearchActivity;

import com.indiaoncology.ui.SubmitQuery;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;


public class FragmentDoctor extends Fragment implements View.OnClickListener {
    private static Context context;
    private FragmentDoctorBinding binding;
    private List<IndexData> doctorDataList = new ArrayList<>();
    private List<Data> categoryDataList = new ArrayList<>();
    private DashboardAdapter dashboardAdapter;
    private CategoryAdapter categoryAdapter;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Api api;
    private List<Data> bannerDataList = new ArrayList<>();

    public FragmentDoctor() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor, container, false);
        View view = binding.getRoot();
        context = getActivity();
        setToolbar();
        getBanner();
        getdoctorCategory();
        getIndexData();
        binding.swipeRefreshingLayout.setOnRefreshListener(() -> getIndexData());
        return view;
    }

    private void getIndexData() {
        if (CommonUtils.isOnline(context)) {
            try {
                api = RequestController.createService(context, Api.class);
                api.dtpl().enqueue(new BaseCallback<IndexResponse>(context) {
                    @Override
                    public void onSuccess(IndexResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response.getStatus().equals("1")) {
                            if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                doctorDataList.clear();
                                doctorDataList.addAll(response.getDataList());
                            } else
                                doctorDataList.clear();

                            setMainAdapter();
                        }
                    }

                    @Override
                    public void onFail(Call<IndexResponse> call, BaseResponse baseResponse) {
                   binding.swipeRefreshingLayout.setRefreshing(false);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void setMainAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvDoctors.setLayoutManager(layoutManager);
        binding.rvDoctors.hasFixedSize();
        binding.rvDoctors.setItemAnimator(new DefaultItemAnimator());
        dashboardAdapter = new DashboardAdapter(getActivity(), doctorDataList);
        binding.rvDoctors.setAdapter(dashboardAdapter);
    }

    private void getdoctorCategory() {
        if (CommonUtils.isOnline(context)) {
            try {
                api = RequestController.createService(context, Api.class);
                api.getCategory().enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response.getStatus().equals("1")) {
                            if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                categoryDataList.clear();
                                categoryDataList.addAll(response.getDataList());
                            } else
                                categoryDataList.clear();

                            setCategoryAdapter();
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
        }
    }

    private void setCategoryAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvcategory.setLayoutManager(layoutManager);
        binding.rvcategory.hasFixedSize();
        binding.rvcategory.setItemAnimator(new DefaultItemAnimator());
        categoryAdapter = new CategoryAdapter(context, categoryDataList);
        binding.rvcategory.setAdapter(categoryAdapter);
    }

    private void setToolbar() {
        binding.menubar.ivBack.setVisibility(View.GONE);
        binding.menubar.ivLogo.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setText("Oncologist");
        binding.menubar.ivRight.setOnClickListener(this);
        binding.menubar.ivSecond.setOnClickListener(this);
        binding.menubar.ivRight.setVisibility(View.VISIBLE);
        binding.menubar.ivSecond.setVisibility(View.VISIBLE);
        binding.menubar.ivSecond.setOnClickListener(v -> {
            int siteId = 40100011;
            String planId = "94fcdecc-e382-49b1-98db-4ec441aa2c16";
            String chatServer = "https://entchatserver.comm100.com";
            VisitorClientInterface.setChatUrl(chatServer + "/chatWindow.aspx?planId=" + planId + "&siteId=" + siteId);
            Intent intent = new Intent(context, ChatActivity.class);
            startActivity(intent);
        });
        binding.btnGetOpinion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetOpinion:
                ActivityController.startActivity(context, SubmitQuery.class);
                break;
            case R.id.ivRight:
                ActivityController.startActivity(context, SearchActivity.class);
                break;
        }
    }

    private void getBanner() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getbanner("main").enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    bannerDataList.clear();
                                    bannerDataList.addAll(response.getDataList());
                                } else {
                                    bannerDataList.clear();
                                }
                                setBannerData(bannerDataList);
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
        }
    }

    // setting banner data
    private void setBannerData(List<Data> list) {
        binding.pager.setAdapter(new AndroidImageAdapter(getActivity(), list));
        binding.tabLayout.setupWithViewPager(binding.pager);
        NUM_PAGES = list.size();
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            binding.pager.setCurrentItem(currentPage++, true);
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }
}