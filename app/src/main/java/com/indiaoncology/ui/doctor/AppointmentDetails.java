package com.indiaoncology.ui.doctor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityAppointmentDetailsBinding;
import com.indiaoncology.databinding.DialogBinding;
import com.indiaoncology.model.myAppointment.AppointmentData;
import com.indiaoncology.model.myAppointment.AppointmentResponse;
import com.indiaoncology.model.patient.PatientData;
import com.indiaoncology.model.order.SaveOrderResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.review.AddReview;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.Objects;

import retrofit2.Call;

import static com.indiaoncology.utils.CommonUtils.*;


public class AppointmentDetails extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityAppointmentDetailsBinding binding;
    private String appointment_ID, APPOINTMENT_ID;
    private String from = " ";
    private String doc_image, doc_name, doctor_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_details);
        context = AppointmentDetails.this;
        activity = AppointmentDetails.this;
        getData();
        setToolbar();
        //setViews();
    }


    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }

            if (bundle.getString("APPOINTMENT_ID") != null && !Objects.requireNonNull(bundle.getString("APPOINTMENT_ID")).isEmpty()) {
                appointment_ID = bundle.getString("APPOINTMENT_ID");
            }
        }
    }

    private void setViews() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvGetDirections.setOnClickListener(this);
        binding.btnShareExperience.setOnClickListener(this);
        if (from.equalsIgnoreCase(AppConstant.TYPE_APPOINTMENT_REVIEW)) {
            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.llMyAppointments.setVisibility(View.GONE);
            getAppointmentReviewApi();
        } else {
            binding.llMyAppointments.setVisibility(View.VISIBLE);
            binding.btnSubmit.setVisibility(View.GONE);
            getAppointmentDetailApi();
        }
    }

    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        if (from.equalsIgnoreCase(AppConstant.TYPE_APPOINTMENT_REVIEW)) {
            binding.menubar.tvTitle.setText(R.string.appointment_review);
        } else {
            binding.tvCancelAppointment.setOnClickListener(this);
            binding.menubar.tvTitle.setText(R.string.appointment_details);
        }
    }

    private void openDialog() {
        final DialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog, null, false);
        Dialog dialog = DialogUtils.createDialog(context, dialogBinding.getRoot(), 0);
        dialog.setCancelable(false);
        dialogBinding.tvHeading.setText("Cancel appointment");
        dialogBinding.tvDescription.setText(new StringBuilder("Are you sure you want to cancel the appointment with ").append(doc_name).append(" ?"));
        dialogBinding.tvNo.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.tvYes.setOnClickListener(v -> {
            cancelAppointment(appointment_ID);
            dialog.dismiss();
        });
    }

    private void cancelAppointment(String appointmentId) {
        if (isOnline(context)) {
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
                                        getAppointmentDetailApi();
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
            showToastShort(context, context.getResources().getString(R.string.no_internet));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvCancelAppointment:
                openDialog();
                break;
            case R.id.btnSubmit:
                confirmAppointment();
                break;
            case R.id.tvGetDirections:
                String uri = "http://maps.google.co.in/maps?q=" + binding.tvLocationName.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
                break;
            case R.id.btnShareExperience:
                Bundle bundle = new Bundle();
                bundle.putString("ITEM_NAME", doc_name);
                bundle.putString("IMAGE", doc_image);
                bundle.putString("ITEM_ID", doctor_Id);
                bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                ActivityController.startActivity(context, AddReview.class, bundle);
                break;
            default:
                break;
        }
    }

    private void confirmAppointment() {
        if (isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.confirmAppointment(SharedPref.getStringPreferences(context, AppConstant.USER_ID), APPOINTMENT_ID)
                        .enqueue(new BaseCallback<SaveOrderResponse>(context) {
                            @Override
                            public void onSuccess(SaveOrderResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        Intent intent = new Intent(context, OrderConfirmation.class);
                                        intent.putExtra(AppConstant.ORDERID, response.getSaveOrderData().getOrderId());
                                        context.startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<SaveOrderResponse> call, BaseResponse baseResponse) {
                                ProgressDialogUtils.dismiss();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showToastShort(context, context.getResources().getString(R.string.no_internet));
        }
    }

    private void getAppointmentDetailApi() {

        if (isOnline(context)) {
            try {
                binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
                Api api = RequestController.createService(context, Api.class);
                api.getAppointmentDetail(SharedPref.getStringPreferences(context, AppConstant.USER_ID), appointment_ID)
                        .enqueue(new BaseCallback<AppointmentResponse>(context) {
                            @Override
                            public void onSuccess(AppointmentResponse response) {
                                binding.llData.setVisibility(View.VISIBLE);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerViewContainer.setVisibility(View.GONE);

                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        if (response.getAppointmentData() != null) {
                                            AppointmentData appointmentData = response.getAppointmentData();
                                            setData(appointmentData);

                                            if (appointmentData.getComment() != null && !appointmentData.getComment().isEmpty()) {
                                                binding.tvStatusReason.setVisibility(View.VISIBLE);
                                                binding.tvStatusReason.setText(appointmentData.getComment());
                                            }
                                            if (appointmentData.getAppointment_status().equalsIgnoreCase("Cancelled")) {
                                                binding.tvCancelAppointment.setVisibility(View.GONE);
                                                binding.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
                                                setStatusDate(appointmentData.getCancelled_date(), "Cancelled on : ");
                                            } else if (appointmentData.getAppointment_status().equalsIgnoreCase("Rejected")) {
                                                binding.tvCancelAppointment.setVisibility(View.GONE);
                                                binding.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
                                                setStatusDate(appointmentData.getCancelled_date(), "Rejected on : ");
                                            } else if (appointmentData.getAppointment_status().equalsIgnoreCase("Attended")) {
                                                binding.tvCancelAppointment.setVisibility(View.GONE);
                                                binding.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.dark_green));
                                            } else if (appointmentData.getAppointment_status().equalsIgnoreCase("Confirmed")) {
                                                binding.tvCancelAppointment.setVisibility(View.VISIBLE);
                                                binding.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.dark_green));
                                            } else {
                                                binding.tvCancelAppointment.setVisibility(View.VISIBLE);
                                                binding.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorYellow));
                                            }

                                            binding.tvBookingStatus.setText(appointmentData.getAppointment_status());

                                            if (appointmentData.getIs_rated().equals("0") && !appointmentData.getAppointment_status().equalsIgnoreCase("Rejected")
                                                    && !appointmentData.getAppointment_status().equalsIgnoreCase("Cancelled")) {
                                                binding.btnShareExperience.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.btnShareExperience.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<AppointmentResponse> call, BaseResponse baseResponse) {
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.shimmerEffect.shimmerViewContainer.setVisibility(View.GONE);
            noInternetPopup();
        }

    }

    private void setData(AppointmentData appointmentData) {
        if (appointmentData.getDoctorProfile() != null && !appointmentData.getDoctorProfile().isEmpty()) {
            doc_image = appointmentData.getDoctorProfile();
            CommonUtils.setGlideImage(context, binding.ivDoctor, AppConstant.DOCTOR_URL + appointmentData.getDoctorProfile(), R.color.transparent);
        }
        binding.tvLocationName.setText(appointmentData.getDoctorLocation());
        binding.tvAppointmentId.setText(appointmentData.getAppointment_id());
        binding.tvClinicFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs)).append(" ").append(appointmentData.getDoctorFees()));
        binding.tvClinicName.setText(appointmentData.getClinic_name());
        String date = formatDate(appointmentData.getAppointment_date(), "yyyy-MM-dd", "dd MMM yyyy");
        binding.tvdate.setText(date);
        doc_name = appointmentData.getDoctorName();
        doctor_Id = appointmentData.getDoctorId();
        binding.tvDoctorName.setText(doc_name);
        binding.tvDocData.setText(appointmentData.getDoctor_experience() + " experience | " + appointmentData.getDoctor_category_name());
        binding.tvTime.setText(appointmentData.getAppointment_time());

        if (appointmentData.getSelectedPatientData() != null) {
            PatientData patientData = appointmentData.getSelectedPatientData();
            binding.tvUserAge.setText("Age: " + patientData.getAge());
            binding.tvUserGender.setText(patientData.getGender());
            binding.tvEmail.setText(patientData.getEmail());
            binding.tvPatientName.setText(patientData.getName());
            binding.tvMobile.setText(patientData.getMobile());
        }

    }

    private void setStatusDate(String date, String heading) {
        if (date != null && !date.isEmpty()) {
            String status_date = formatDate(date, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
            binding.tvStatusDate.setText(new StringBuilder(heading).append(status_date));
        }
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

    private void getAppointmentReviewApi() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        if (isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getAppointmentReview(SharedPref.getStringPreferences(context, AppConstant.USER_ID), appointment_ID)
                        .enqueue(new BaseCallback<AppointmentResponse>(context) {
                            @Override
                            public void onSuccess(AppointmentResponse response) {
                                binding.llData.setVisibility(View.VISIBLE);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);

                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        if (response.getAppointmentData() != null) {
                                            AppointmentData appointmentData = response.getAppointmentData();
                                            APPOINTMENT_ID = response.getAppointmentData().getAppointment_id();
                                            setData(appointmentData);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<AppointmentResponse> call, BaseResponse baseResponse) {
                                ProgressDialogUtils.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        setViews();
    }
}
