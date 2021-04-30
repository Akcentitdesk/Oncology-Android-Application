package com.indiaoncology.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityAddPatientBinding;
import com.indiaoncology.databinding.ActivityContactBinding;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.patient.AddPatient;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.RegexUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.Objects;

import retrofit2.Call;

public class Contact extends AppCompatActivity implements View.OnClickListener {
    private ActivityContactBinding binding;
    int flag = 0;
    private Context context;
    private Activity activity;
    private String name, email, mobile, age, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);
        context = Contact.this;
        activity = Contact.this;
        CommonUtils.setToolBar(binding.menuBar, "Contact", activity);
        binding.menuBar.ivRight.setVisibility(View.GONE);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
        setViews();

    }

    private void setViews() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvEmail.setText(SharedPref.getStringPreferences(context, AppConstant.COMPANY_EMAIL));
        binding.tvMobile.setText(SharedPref.getStringPreferences(context, AppConstant.COMPANY_MOBILE_NUMBER));
        binding.tvAddress.setText(SharedPref.getStringPreferences(context, AppConstant.COMPANY_ADDRESS));
        binding.etmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.tvMessageError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etName.addTextChangedListener(new TextWatcher() {
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
                    saveData();
                }
                break;
            default:
                break;
        }
    }

    private boolean isValid() {
        name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
        message = Objects.requireNonNull(binding.etmessage.getText()).toString().trim();


        if (name.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etName, binding.tvNameError, getResources().getString(R.string.empty_name));
        } else if (name.length() < 2) {
            CommonUtils.setErrorMessage(binding.etName, binding.tvNameError, getResources().getString(R.string.invalid_name));
        } else if (email.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, getResources().getString(R.string.empty_email));
        } else if (!RegexUtils.isValidEmail(email)) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, getResources().getString(R.string.invalid_email));
        } else if (mobile.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, getResources().getString(R.string.empty_mobile));
        } else if (!RegexUtils.isValidMobileNumber(mobile)) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, getResources().getString(R.string.invalid_mobile));
        } else if (message.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etmessage, binding.tvMessageError, getResources().getString(R.string.empty_message));
        } else {
            return true;
        }
        return false;
    }

    private void saveData() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.contact(name, email, mobile, message).enqueue(new BaseCallback<BaseResponse>(context) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ProgressDialogUtils.dismiss();
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                CommonUtils.showToastShort(context, response.getMessage());
                                Intent intent = new Intent(context, Dashboard.class);
                                startActivity(intent);
                                finishAffinity();
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