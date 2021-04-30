package com.indiaoncology.ui.doctor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.doctor.MyAppointmentAdapter;
import com.indiaoncology.databinding.ActivityMyOrderBinding;
import com.indiaoncology.databinding.DialogBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.myAppointment.AppointmentData;
import com.indiaoncology.model.myAppointment.AppointmentListResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrder extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityMyOrderBinding binding;
    private String from = "";
    private Dialog dialog;
    private MyAppointmentAdapter myAppointmentAdapter;
    private List<AppointmentData> appointmentData = new ArrayList<>();
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int TOTAL_PAGES;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order);
        context = MyOrder.this;
        activity = MyOrder.this;
        api = RequestController.createService(context, Api.class);
        getData();
        setToolbar();
        setData();

        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            myAppointmentAdapter.clear();
            setAppointmentAdapter();
            getMyAppointments();
        });
    }

    public void noInternetPopup() {
        binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
        binding.llData.setVisibility(View.GONE);
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

    private void setData() {
        setAppointmentAdapter();
        getMyAppointments();
    }


    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }
        }
    }

    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.menubar.tvTitle.setText(getResources().getString(R.string.my_appointment));

    }

    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.llData.setVisibility(View.GONE);
            binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
            binding.emptyLayout.ivImage.setImageResource(R.drawable.confirm_appointment);
            binding.emptyLayout.tvHeading.setText(getResources().getString(R.string.no_appointment_booked));
            binding.emptyLayout.tvSubHeading.setText(getResources().getString(R.string.order_desc_appointment));
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.llData.setVisibility(View.VISIBLE);
        }
    }

    private void setAppointmentAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvMyOrders.setLayoutManager(layoutManager);
        binding.rvMyOrders.hasFixedSize();
        binding.rvMyOrders.setItemAnimator(new DefaultItemAnimator());
        myAppointmentAdapter = new MyAppointmentAdapter(activity, appointmentData, (view, position, selected) -> {
            switch (view.getId()) {
                case R.id.tvCancel:
                    openDialog(position);
                    break;
                default:
                    break;
            }
        });
        binding.rvMyOrders.setAdapter(myAppointmentAdapter);
        myAppointmentAdapter.notifyDataSetChanged();
        binding.rvMyOrders.setOnScrollListener(new PaginationScrollListener(layoutManager, "LINEAR") {
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

    private void openDialog(final int position) {
        final DialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog, null, false);
        dialog = DialogUtils.createDialog(context, dialogBinding.getRoot(), 0);
        dialog.setCancelable(false);
        dialogBinding.tvHeading.setText("Cancel appointment");
        dialogBinding.tvDescription.setText("Are you sure you want to cancel the appointment with " + appointmentData.get(position).getDoctorName() + " ?");
        dialogBinding.tvNo.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.tvYes.setOnClickListener(v -> {
            cancelAppointment(appointmentData.get(position).getAppointment_id());
            dialog.dismiss();
        });
    }

    private void cancelAppointment(String appointmentId) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.deleteAppointment(SharedPref.getStringPreferences(context, AppConstant.USER_ID), appointmentId)
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        currentPage = PAGE_START;
                                        isLastPage = false;
                                        myAppointmentAdapter.clear();
                                        setAppointmentAdapter();
                                        getMyAppointments();
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
            CommonUtils.showToastShort(context, context.getResources().getString(R.string.no_internet));
        }

    }

    private void loadNextPageData() {
        api.getAllApointment(SharedPref.getStringPreferences(context, AppConstant.USER_ID), currentPage)
                .enqueue(new Callback<AppointmentListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AppointmentListResponse> call, @NonNull Response<AppointmentListResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getPagination() != null) {
                                currentPage = Integer.parseInt(response.body().getPagination().getCurrentPage());
                                TOTAL_PAGES = response.body().getPagination().getMaxPage();
                            }

                            myAppointmentAdapter.removeLoadingFooter();
                            isLoading = false;

                            List<AppointmentData> result = response.body().getAppointmentListData();
                            if (result == null)
                                return;
                            else
                                myAppointmentAdapter.addAll(result);
                            binding.swipeRefreshingLayout.setRefreshing(false);

                            if (currentPage < TOTAL_PAGES)
                                myAppointmentAdapter.addLoadingFooter();
                            else isLastPage = true;
                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<AppointmentListResponse> call, @NonNull Throwable t) {
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            default:
                break;
        }
    }


    private void getMyAppointments() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        if (CommonUtils.isOnline(context)) {
            try {
                api = RequestController.createService(context, Api.class);
                api.getAllApointment(SharedPref.getStringPreferences(context, AppConstant.USER_ID), currentPage)
                        .enqueue(new Callback<AppointmentListResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<AppointmentListResponse> call, @NonNull Response<AppointmentListResponse> response) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("1")) {
                                        if (response.body().getPagination() != null) {
                                            currentPage = Integer.parseInt(response.body().getPagination().getCurrentPage());
                                            TOTAL_PAGES = response.body().getPagination().getMaxPage();
                                        }
                                        if (response.body().getAppointmentListData() != null && !response.body().getAppointmentListData().isEmpty()) {
                                            setEmptyLayout(false);
                                            appointmentData.clear();
                                            appointmentData.addAll(response.body().getAppointmentListData());
                                            myAppointmentAdapter.notifyDataSetChanged();
                                        } else {
                                            setEmptyLayout(true);
                                            appointmentData.clear();
                                            myAppointmentAdapter.notifyDataSetChanged();
                                        }
                                        if (currentPage < TOTAL_PAGES)
                                            myAppointmentAdapter.addLoadingFooter();
                                        else isLastPage = true;

                                    } else {
                                        setEmptyLayout(true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppointmentListResponse> call, @NonNull Throwable t) {
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


    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }

}
