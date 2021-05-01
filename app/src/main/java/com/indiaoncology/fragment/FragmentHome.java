package com.indiaoncology.fragment;

import android.app.Dialog;
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
import com.indiaoncology.adaptar.images.AndroidImageAdapter;
import com.indiaoncology.adaptar.article.ArticleAdapter;
import com.indiaoncology.adaptar.doctor.DoctorAdapter;
import com.indiaoncology.adaptar.TypeAdapter;
import com.indiaoncology.databinding.FragmentHomeBinding;
import com.indiaoncology.databinding.PopupAddReminderBinding;
import com.indiaoncology.listener.onFragmentChange;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.model.doctor.ListResponse;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.model.type.TypeResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.others.Contact;
import com.indiaoncology.ui.others.SearchActivity;
import com.indiaoncology.ui.others.SubmitQuery;
import com.indiaoncology.ui.doctor.DoctorListing;
import com.indiaoncology.ui.article.ArticleList;
import com.indiaoncology.ui.type.TypeList;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;


public class FragmentHome extends Fragment implements View.OnClickListener {
    private static FragmentHomeBinding binding;
    private static Context context;
    private String device_token = "";
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private List<Data> typeDataList = new ArrayList<>();
    private List<Data> blogDataList = new ArrayList<>();
    private List<DoctorData> doctorDataList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private ArticleAdapter articleAdapter;
    private DoctorAdapter doctorAdapter;
    private TypeAdapter typeAdapter;
    private List<Data> bannerDataList = new ArrayList<>();
    private onFragmentChange listener;

    public FragmentHome() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        context = getActivity();
        PrefManager prefManager = PrefManager.getInstance(getActivity());
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        setToolbar(context.getResources().getString(R.string.app_name));
        setViews();
        getDoctors();
        getTypes();
        getArticles();
        getBanner();
        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            getDoctors();
            getTypes();
            getArticles();
        });
        return view;
    }

    private void getBanner() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getbanner("main").enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
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

    private void setViews() {
        binding.btnContactUs.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnGetOpinion.setOnClickListener(this);
        binding.btnFindDoctor.setOnClickListener(this);
        binding.btnViewAllArticle.setOnClickListener(this);
        binding.btnViewAllTypes.setOnClickListener(this);
        binding.llrequestcallback.setOnClickListener(this);
        binding.llgetopinion.setOnClickListener(this);
        binding.llbookappointment.setOnClickListener(this);
    }

    private void setToolbar(String title) {
        binding.menuBar.ivBack.setVisibility(View.GONE);
        binding.menuBar.ivLogo.setVisibility(View.VISIBLE);
        binding.menuBar.tvTitle.setVisibility(View.VISIBLE);
        binding.menuBar.tvTitle.setText(title);
        binding.menuBar.ivRight.setOnClickListener(this);
        binding.menuBar.ivSecond.setOnClickListener(this);
        binding.menuBar.ivRight.setVisibility(View.VISIBLE);
        binding.menuBar.ivSecond.setVisibility(View.VISIBLE);
        binding.menuBar.ivSecond.setOnClickListener(v -> {
            int siteId = 40100011;
            String planId = "94fcdecc-e382-49b1-98db-4ec441aa2c16";
            String chatServer = "https://entchatserver.comm100.com";
            VisitorClientInterface.setChatUrl(chatServer + "/chatWindow.aspx?planId=" + planId + "&siteId=" + siteId);
            Intent intent = new Intent(context, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void openResponseDialog(String heading, String subheading) {
        final PopupAddReminderBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_add_reminder, null, false);
        Dialog dialogSubmit = DialogUtils.createDialog(context, dataBinding.getRoot(), 0);
        dialogSubmit.setCancelable(false);
        dataBinding.heading.setText(heading);
        dataBinding.subHeading.setText(subheading);
        dataBinding.btnOk.setOnClickListener(v -> {
            dialogSubmit.dismiss();
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llrequestcallback:
                requestCallBack();
                break;
            case R.id.ivSecond:
                break;
            case R.id.btnCall:
                CommonUtils.openDialer(context, SharedPref.getStringPreferences(context, AppConstant.COMPANY_MOBILE_NUMBER));
                break;
            case R.id.llbookappointment:
            case R.id.ivRight:
                ActivityController.startActivity(context, SearchActivity.class);
                break;
            case R.id.btnContactUs:
                ActivityController.startActivity(context, Contact.class);
                break;
            case R.id.llgetopinion:
            case R.id.btnGetOpinion:
                ActivityController.startActivity(context, SubmitQuery.class);
                break;
            case R.id.btnViewAllTypes:
                ActivityController.startActivity(context, TypeList.class);
                break;
            case R.id.btnViewAllArticle:
                ActivityController.startActivity(context, ArticleList.class);
                break;
            case R.id.btnFindDoctor:
                //  ActivityController.startActivity(context, SearchActivity.class);

                /*  listener.sendData("Doctor");*/

                Intent intent = new Intent(context, DoctorListing.class);
                intent.putExtra("TITLE", "Top Doctors");
                context.startActivity(intent);
                break;
        }
    }

    private void getTypes() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getTypeList(1).enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    typeDataList.clear();
                                    typeDataList.addAll(response.getDataList());
                                } else {
                                    typeDataList.clear();
                                }
                                setTypeAdapter();
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

    private void getDoctors() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, Object> map = new HashMap<>();
                map.put("page", 1);
                api.getDoctorsList(map).enqueue(new BaseCallback<ListResponse>(context) {
                    @Override
                    public void onSuccess(ListResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    doctorDataList.clear();
                                    doctorDataList.addAll(response.getDataList());
                                } else {
                                    doctorDataList.clear();
                                }
                                setDoctorAdapter();
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
        }
    }

    private void getArticles() {
        if (CommonUtils.isOnline(context)) {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("page", 1);
                Api api = RequestController.createService(context, Api.class);
                api.getBlogList(map).enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    blogDataList.clear();
                                    blogDataList.addAll(response.getDataList());
                                } else {
                                    blogDataList.clear();
                                }
                                setBlogAdapter();
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

    private void setBlogAdapter() {
        layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvArticle.setLayoutManager(layoutManager);
        binding.rvArticle.hasFixedSize();
        binding.rvArticle.setItemAnimator(new DefaultItemAnimator());
        articleAdapter = new ArticleAdapter(context, blogDataList);
        binding.rvArticle.setAdapter(articleAdapter);
    }

    private void setTypeAdapter() {
        layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvTypes.setLayoutManager(layoutManager);
        binding.rvTypes.hasFixedSize();
        binding.rvTypes.setItemAnimator(new DefaultItemAnimator());
        typeAdapter = new TypeAdapter(getActivity(), typeDataList, "list");
        binding.rvTypes.setAdapter(typeAdapter);
    }

    private void setDoctorAdapter() {
        layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvDoctors.setLayoutManager(layoutManager);
        binding.rvDoctors.hasFixedSize();
        binding.rvDoctors.setItemAnimator(new DefaultItemAnimator());
        doctorAdapter = new DoctorAdapter(context, doctorDataList);
        binding.rvDoctors.setAdapter(doctorAdapter);
    }

    private void requestCallBack() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.requestCallback(SharedPref.getStringPreferences(context, AppConstant.USER_ID))
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        openResponseDialog("Thank You", "We have received your request, one of our Care Managers will give you a call to help you further.");
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<BaseResponse> call, BaseResponse baseResponse) {
                                ProgressDialogUtils.dismiss();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showToastShort(context, getResources().getString(R.string.no_internet));
        }
    }
}