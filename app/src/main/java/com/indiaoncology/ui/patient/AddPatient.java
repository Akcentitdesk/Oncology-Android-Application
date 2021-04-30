package com.indiaoncology.ui.patient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityAddPatientBinding;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.RegexUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.Objects;

import retrofit2.Call;

public class AddPatient extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddPatientBinding binding;
    int flag = 0;
    private Context context;
    private Activity activity;
    private String name, email, mobile, age, selected_gender, is_self_patient = " ", patient_ID;
    private String from = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_patient);
        context = AddPatient.this;
        activity = AddPatient.this;
        getData();
        setData();
        setViews();

    }

    private void setData() {
        if (from.equalsIgnoreCase(AppConstant.TYPE_EDIT)) {
            binding.etAge.setText(age);
            binding.etEmail.setText(email);
            binding.etMobile.setText(mobile);
            binding.etPatientName.setText(name);
            if (selected_gender.equalsIgnoreCase("Female")) {
                selected_gender = setViewColor(binding.tvFemale, binding.tvMale);
            } else {
                selected_gender = setViewColor(binding.tvMale, binding.tvFemale);
            }
            binding.btnSubmit.setText(getResources().getString(R.string.update_patient));

            if (is_self_patient != null && !is_self_patient.isEmpty() && is_self_patient.equals("1"))
                binding.cbSelfPatient.setChecked(true);
        } else {
            binding.etEmail.setText(SharedPref.getStringPreferences(context, AppConstant.USER_EMAIL));
            binding.etMobile.setText(SharedPref.getStringPreferences(context, AppConstant.USER_MOBILE));
        }
    }

    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("is_self_patient") != null && !Objects.requireNonNull(bundle.getString("is_self_patient")).isEmpty())
                is_self_patient = bundle.getString("is_self_patient");

            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }
            if (bundle.getString("Patient_Age") != null && !Objects.requireNonNull(bundle.getString("Patient_Age")).isEmpty()) {
                age = bundle.getString("Patient_Age");
            }
            if (bundle.getString("Patient_Name") != null && !Objects.requireNonNull(bundle.getString("Patient_Name")).isEmpty()) {
                name = bundle.getString("Patient_Name");
            }
            if (bundle.getString("Patient_Mobile") != null && !Objects.requireNonNull(bundle.getString("Patient_Mobile")).isEmpty()) {
                mobile = bundle.getString("Patient_Mobile");
            }
            if (bundle.getString("Patient_Email") != null && !Objects.requireNonNull(bundle.getString("Patient_Email")).isEmpty()) {
                email = bundle.getString("Patient_Email");
            }
            if (bundle.getString("Patient_Gender") != null && !Objects.requireNonNull(bundle.getString("Patient_Gender")).isEmpty()) {
                selected_gender = bundle.getString("Patient_Gender");
            }
            if (bundle.getString("Patient_ID") != null && !Objects.requireNonNull(bundle.getString("Patient_ID")).isEmpty()) {
                patient_ID = bundle.getString("Patient_ID");
            }
        }

    }

    private void setViews() {
        if (from.equalsIgnoreCase(AppConstant.TYPE_EDIT)) {
            CommonUtils.setToolBar(binding.menubar, getResources().getString(R.string.update_patient), activity);
        } else {
            CommonUtils.setToolBar(binding.menubar, getResources().getString(R.string.add_patient), activity);
        }
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);

        binding.btnSubmit.setOnClickListener(this);
        binding.tvFemale.setOnClickListener(this);
        binding.tvMale.setOnClickListener(this);
        binding.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.tvAgeError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etPatientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.tvNameError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.tvMobileError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.tvEmailError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnSubmit:
                if (isValid()) {
                    if (from.equalsIgnoreCase(AppConstant.TYPE_EDIT)) {
                        updatePatient();
                    } else {
                        savePatient();
                    }
                }
                break;
            case R.id.tvMale:
                flag = 1;
                selected_gender = setViewColor(binding.tvMale, binding.tvFemale);
                binding.tvGenderError.setVisibility(View.INVISIBLE);
                break;
            case R.id.tvFemale:
                flag = 2;
                selected_gender = setViewColor(binding.tvFemale, binding.tvMale);
                binding.tvGenderError.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


    private boolean isValid() {
        name = Objects.requireNonNull(binding.etPatientName.getText()).toString().trim();
        age = Objects.requireNonNull(binding.etAge.getText()).toString().trim();
        email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();


        if (name.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etPatientName, binding.tvNameError, getResources().getString(R.string.empty_patient_name));
        } else if (name.length() < 2) {
            CommonUtils.setErrorMessage(binding.etPatientName, binding.tvNameError, getResources().getString(R.string.invalid_name));
        } else if (age.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etAge, binding.tvAgeError, getResources().getString(R.string.empty_patient_age));
        } else if (flag == 0) {
            binding.tvGenderError.setVisibility(View.VISIBLE);
            binding.tvGenderError.setText(getResources().getString(R.string.empty_gender));
        } else if (mobile.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, getResources().getString(R.string.empty_mobile));
        } else if (!RegexUtils.isValidMobileNumber(mobile)) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, getResources().getString(R.string.invalid_mobile));
        } else if (email.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, getResources().getString(R.string.empty_email));
        } else if (!RegexUtils.isValidEmail(email)) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, getResources().getString(R.string.invalid_email));
        } else {
            return true;
        }
        return false;
    }

    private String setViewColor(TextView tvSelected, TextView tvUnselected) {
        tvSelected.setBackground(getResources().getDrawable(R.drawable.border_main));
        tvUnselected.setBackground(getResources().getDrawable(R.drawable.border_gray));
        tvSelected.setTextColor(getResources().getColor(R.color.colorAccent));
        tvUnselected.setTextColor(getResources().getColor(R.color.lightGray));
        return tvSelected.getText().toString().trim();
    }

    private void savePatient() {
        String value = "0";
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                if (binding.cbSelfPatient.isChecked()) {
                    value = "1";
                }
                Api api = RequestController.createService(context, Api.class);
                api.addPatient(SharedPref.getStringPreferences(context, AppConstant.USER_ID),
                        name, mobile, email, age, selected_gender, value).enqueue
                        (new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        finish();
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
            CommonUtils.showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));
        }
    }

    private void updatePatient() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.updatePatient(SharedPref.getStringPreferences(context, AppConstant.USER_ID), patient_ID, name, mobile, email, age, selected_gender).enqueue
                        (new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        finish();
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
            CommonUtils.showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));
        }
    }

}

