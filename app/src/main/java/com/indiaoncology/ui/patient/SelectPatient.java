package com.indiaoncology.ui.patient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivitySelectPatientBinding;
import com.indiaoncology.databinding.PopupAddReminderBinding;
import com.indiaoncology.model.patient.PatientBaseResponse;
import com.indiaoncology.model.patient.PatientData;
import com.indiaoncology.adaptar.schedule.SelectPatientAdapter;
import com.indiaoncology.model.order.SaveOrderResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.Dashboard;
import com.indiaoncology.ui.doctor.AppointmentDetails;
import com.indiaoncology.ui.doctor.OrderConfirmation;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class SelectPatient extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivitySelectPatientBinding binding;
    private SelectPatientAdapter patientAdapter;
    private List<PatientData> patientData = new ArrayList<>();
    private String from = " ", selectedTime = " ", selectedDate = " ",
            selectedDay = " ", selectedLocationId = "", selectedDoctorId = " ", doc_fees = " ",
            SelectedPatientID, orderId, cancer_type, stage, condition;
    ArrayList<String> report_ids = new ArrayList<>();
    ArrayList<String> trestments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_patient);
        context = SelectPatient.this;
        activity = SelectPatient.this;
        getData();
        setToolbar();
        binding.swipeRefreshingLayout.setOnRefreshListener(this::getAllPatientApi);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        getAllPatientApi();
        setAdapter();
    }

    private void getData() {
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty())
                from = bundle.getString(AppConstant.FROM);

            if (bundle.getString("Selected_Date") != null && !Objects.requireNonNull(bundle.getString("Selected_Date")).isEmpty())
                selectedDate = bundle.getString("Selected_Date");

            if (bundle.getString("Selected_Day") != null && !Objects.requireNonNull(bundle.getString("Selected_Day")).isEmpty())
                selectedDay = bundle.getString("Selected_Day");

            if (bundle.getString("Selected_Doctor_Id") != null && !Objects.requireNonNull(bundle.getString("Selected_Doctor_Id")).isEmpty())
                selectedDoctorId = bundle.getString("Selected_Doctor_Id");

            if (bundle.getString("Selected_Location_Id") != null && !Objects.requireNonNull(bundle.getString("Selected_Location_Id")).isEmpty())
                selectedLocationId = bundle.getString("Selected_Location_Id");

            if (bundle.getString("Selected_Time") != null && !Objects.requireNonNull(bundle.getString("Selected_Time")).isEmpty())
                selectedTime = bundle.getString("Selected_Time");

            if (bundle.getString("fees") != null && !Objects.requireNonNull(bundle.getString("fees")).isEmpty())
                doc_fees = bundle.getString("fees");

            if (bundle.getString("patient_condition") != null && !Objects.requireNonNull(bundle.getString("patient_condition")).isEmpty())
                cancer_type = bundle.getString("patient_condition");
            if (bundle.getString("cancer_stage") != null && !Objects.requireNonNull(bundle.getString("cancer_stage")).isEmpty())
                stage = bundle.getString("cancer_stage");
            if (bundle.getString("patient_condition") != null && !Objects.requireNonNull(bundle.getString("patient_condition")).isEmpty())
                condition = bundle.getString("patient_condition");

            if (bundle.getStringArrayList("report_id") != null && !Objects.requireNonNull(bundle.getStringArrayList("report_id")).isEmpty())
                report_ids = bundle.getStringArrayList("report_id");

            if (bundle.getStringArrayList("treatments") != null && !Objects.requireNonNull(bundle.getStringArrayList("treatments")).isEmpty())
                trestments = bundle.getStringArrayList("treatments");


        }


    }

    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.llData.setVisibility(View.GONE);
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.emptyLayout.ivImage.setImageResource(R.drawable.ic_add_patient);
            binding.emptyLayout.btnSubmit.setText(getResources().getString(R.string.add_patient));
            binding.emptyLayout.tvHeading.setText(getResources().getString(R.string.no_patient));
            binding.emptyLayout.btnSubmit.setOnClickListener(this);
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.llData.setVisibility(View.VISIBLE);
            if (from.equalsIgnoreCase(AppConstant.PROFILE)) {
                binding.btnSelectPatient.setVisibility(View.GONE);
            } else {
                binding.btnSelectPatient.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(binding.llPatientData.getLayoutParams());
                layoutParams.setMargins(0, 200, 0, 160);
                binding.llPatientData.setLayoutParams(layoutParams);
            }
        }
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvPatient.setLayoutManager(linearLayoutManager);
        binding.rvPatient.hasFixedSize();
        binding.rvPatient.setItemAnimator(new DefaultItemAnimator());
        patientAdapter = new SelectPatientAdapter(context, patientData, (view, position) -> {
            switch (view.getId()) {
                case R.id.tvYes:
                    deletePatientApi(position);
                    break;
                case R.id.tvEdit:
                    Bundle bundle = new Bundle();
                    bundle.putString("Patient_Age", patientData.get(position).getAge());
                    bundle.putString("Patient_Name", patientData.get(position).getName());
                    bundle.putString("Patient_Mobile", patientData.get(position).getMobile());
                    bundle.putString("Patient_Email", patientData.get(position).getEmail());
                    bundle.putString("Patient_Gender", patientData.get(position).getGender());
                    bundle.putString("Patient_ID", patientData.get(position).getPatientId());
                    bundle.putString("is_self_patient", patientData.get(position).getIs_self_patient());
                    bundle.putString(AppConstant.FROM, AppConstant.TYPE_EDIT);
                    ActivityController.startActivity(activity, AddPatient.class, bundle, false, false);
                    break;
                default:
                    break;
            }
        });
        if (from.equalsIgnoreCase(AppConstant.PROFILE)) {
            patientAdapter.hide(true);
        }
        binding.rvPatient.setAdapter(patientAdapter);
        patientAdapter.notifyDataSetChanged();
    }

    private void setToolbar() {
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        if (from.equalsIgnoreCase(AppConstant.PROFILE)) {
            binding.menubar.tvTitle.setText(R.string.saved_patient);
        } else {
            binding.menubar.tvTitle.setText(R.string.select_patient);
        }
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.tvAddNewPatient.setOnClickListener(this);
        binding.btnSelectPatient.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btn_submit:
            case R.id.tvAddNewPatient:
                ActivityController.startActivity(context, AddPatient.class, from);
                break;
            case R.id.btnSelectPatient:
                if (from.equalsIgnoreCase(AppConstant.FROM_ENQUIRY))
                    submitData();
                else
                    addAppointment();
                break;
            default:
                break;
        }
    }

    private void submitData() {

        if (CommonUtils.isOnline(context)) {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("patient_condition", condition);
                map.put("cancer_stage", stage);
                map.put("cancer_type", cancer_type);
                map.put("report_id", report_ids);
                map.put("treatments", trestments);
                map.put("patient_id", SharedPref.getStringPreferences(context, AppConstant.Selected_Patient_ID));
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.enquiry(map).enqueue(new BaseCallback<BaseResponse>(context) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ProgressDialogUtils.dismiss();
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                openResponseDialog(response.getMessage());

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
        }
    }

    private void openResponseDialog(String heading) {
        final PopupAddReminderBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_add_reminder, null, false);
        Dialog dialogSubmit = DialogUtils.createDialog(context, dataBinding.getRoot(), 0);
        dialogSubmit.setCancelable(false);
        dataBinding.subHeading.setText(heading);

        dataBinding.btnOk.setOnClickListener(v -> {
            dialogSubmit.dismiss();
            ActivityController.startActivity(activity, Dashboard.class, true);
        });


    }

    private void addAppointment() {
        if (CommonUtils.isOnline(context)) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("selected_patient_id", SharedPref.getStringPreferences(context, AppConstant.Selected_Patient_ID));
                map.put("doctor_id", selectedDoctorId);
                map.put("type", "app");
              /*  map.put("location_id",selectedLocationId);
                map.put("date",selectedDate);
                map.put("time",selectedTime);
                map.put("day",selectedDay);
                map.put("fees",doc_fees);*/
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.addAppointment(map).enqueue(new BaseCallback<SaveOrderResponse>(context) {
                    @Override
                    public void onSuccess(SaveOrderResponse response) {
                        ProgressDialogUtils.dismiss();
                        if (response != null) {
                            if (response.getStatus().equals("1")) {

                                if (response.getSaveOrderData().getOrderId() != null && !response.getSaveOrderData().getOrderId().isEmpty()) {
                                    Intent intent = new Intent(context, OrderConfirmation.class);
                                    intent.putExtra(AppConstant.ORDERID, response.getSaveOrderData().getOrderId());
                                    context.startActivity(intent);
                                }


                                       /* String appointmentId = response.getSaveOrderData().getOrderId();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("APPOINTMENT_ID", appointmentId);
                                        bundle.putString(AppConstant.FROM, AppConstant.TYPE_APPOINTMENT_REVIEW);
                                        ActivityController.startActivity(activity, AppointmentDetails.class, bundle, false, false);
*/
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
            CommonUtils.showToastShort(context, context.getResources().getString(R.string.no_internet));
        }

    }

    private void deletePatientApi(int position) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.deletePatient(SharedPref.getStringPreferences(context, AppConstant.USER_ID), patientData.get(position).getPatientId())
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        getAllPatientApi();
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

    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }

    private void getAllPatientApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getPatientList(SharedPref.getStringPreferences(context, AppConstant.USER_ID))
                        .enqueue(new BaseCallback<PatientBaseResponse>(context) {
                            @Override
                            public void onSuccess(PatientBaseResponse response) {
                                binding.swipeRefreshingLayout.setRefreshing(false);
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);

                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        if (response.getPatientData() != null && !response.getPatientData().isEmpty()) {
                                            setEmptyLayout(false);
                                            patientData.clear();
                                            patientData.addAll(response.getPatientData());
                                            patientAdapter.notifyDataSetChanged();
                                        } else {
                                            patientData.clear();
                                            patientAdapter.notifyDataSetChanged();
                                            setEmptyLayout(true);
                                        }

                                    } else {
                                        patientData.clear();
                                        patientAdapter.notifyDataSetChanged();
                                        setEmptyLayout(true);
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<PatientBaseResponse> call, BaseResponse baseResponse) {
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
}

