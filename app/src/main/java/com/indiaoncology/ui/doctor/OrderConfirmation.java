package com.indiaoncology.ui.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityOrderConfirmationBinding;
import com.indiaoncology.model.order.OrderDetailData;
import com.indiaoncology.model.order.OrderDetailResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.Dashboard;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.SharedPref;


import retrofit2.Call;


public class OrderConfirmation extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private String from = "", ORDER_ID = "", order_total = "";
    private ActivityOrderConfirmationBinding binding;
    private String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_confirmation);
        context = OrderConfirmation.this;
        activity = OrderConfirmation.this;
        getData();
        setData();
    }

    private void setData() {
        binding.btnGoHome.setOnClickListener(this);
        // binding.btnAppointmentDetails.setOnClickListener(this);
        binding.tvEmail.setText(SharedPref.getStringPreferences(context, AppConstant.COMPANY_EMAIL));
        binding.tvMobile.setText(SharedPref.getStringPreferences(context, AppConstant.COMPANY_MOBILE_NUMBER));
        //getAppointmentOrderDetail();
    }


    private void getAppointmentOrderDetail() {
        if (CommonUtils.isOnline(context)) {
            try {
                binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
                Api api = RequestController.createService(context, Api.class);
                api.getappointmentorderdetail(SharedPref.getStringPreferences(context, AppConstant.USER_ID), ORDER_ID)
                        .enqueue(new BaseCallback<OrderDetailResponse>(context) {
                            @Override
                            public void onSuccess(OrderDetailResponse response) {
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                        binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                        if (response.getOrderDetailData() != null) {
                                            binding.llMainLayout.setVisibility(View.VISIBLE);
                                            binding.btnGoHome.setVisibility(View.VISIBLE);
                                            OrderDetailData data = response.getOrderDetailData();
                                            binding.tvPatientName.setText(data.getBooked_for());
                                            binding.docFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs)).append(data.getOrder_total()));
                                            binding.tvAppointmentId.setText(new StringBuffer("Appointment ID: ").append(data.getOrder_id()));
                                            String date = CommonUtils.formatDate(data.getDate(), "yyyy-MM-dd", "dd MMM yyyy");
                                            binding.tvDateAppointment.setText(date);
                                            binding.tvTimeAppointment.setText(data.getTime());
                                            ORDER_ID = response.getOrderDetailData().getOrder_id();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<OrderDetailResponse> call, BaseResponse baseResponse) {

                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
        }
    }


    private void getData() {

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra(AppConstant.ORDERID) != null && !intent.getStringExtra(AppConstant.ORDERID).isEmpty()) {
                ORDER_ID = intent.getStringExtra(AppConstant.ORDERID);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGoHome:
                ActivityController.startActivity(activity, Dashboard.class, true, true);
                break;
            case R.id.btnAppointmentDetails:
                Bundle bundle = new Bundle();
                bundle.putString("APPOINTMENT_ID", ORDER_ID);
                bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                ActivityController.startActivity(activity, AppointmentDetails.class, bundle, false, false);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityController.startActivity(activity, Dashboard.class, true, true);
    }
}
