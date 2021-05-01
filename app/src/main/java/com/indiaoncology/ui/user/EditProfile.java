package com.indiaoncology.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityEditProfileBinding;
import com.indiaoncology.databinding.LayoutEnterOtpBinding;
import com.indiaoncology.model.user.LoginData;
import com.indiaoncology.model.user.UserBaseResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.startAndDashboard.Dashboard;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.RegexUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityEditProfileBinding binding;
    private String otp, mobile, email, user_id, device_token = "";
    boolean is_resend_mobile = false, is_resend_email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        context = EditProfile.this;
        activity = EditProfile.this;
        setViews();
    }

    private void setViews() {
        user_id = SharedPref.getStringPreferences(context, AppConstant.USER_ID);
        PrefManager prefManager = PrefManager.getInstance(context);
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        if (SharedPref.getStringPreferences(context, AppConstant.USER_EMAIL) != null && !SharedPref.getStringPreferences(context, AppConstant.USER_EMAIL).isEmpty()) {
            binding.etEmail.setEnabled(false);
            binding.etEmail.setText(SharedPref.getStringPreferences(context, AppConstant.USER_EMAIL));
            binding.changeEmail.setVisibility(View.VISIBLE);
        } else {
            binding.etEmail.setEnabled(true);
            binding.changeEmail.setVisibility(View.GONE);
            binding.sendOtpOnEmail.setVisibility(View.VISIBLE);
        }
        binding.changeEmail.setOnClickListener(this);
        binding.etMobile.setText(SharedPref.getStringPreferences(context, AppConstant.USER_MOBILE));
        binding.etMobile.setEnabled(false);

        binding.changeNumber.setOnClickListener(this);
        binding.btnUpdateEmail.setOnClickListener(this);
        binding.btnUpdateMobile.setOnClickListener(this);
        binding.sendOtpOnMobile.setOnClickListener(this);
        binding.sendOtpOnEmail.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvMobileError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvEmailError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setOTPViews(binding.enterOtpLayoutEmail);
        setOTPViews(binding.enterOtpLayoutVerify);
    }

    private void setOTPViews(LayoutEnterOtpBinding enterOtpLayout) {
        enterOtpLayout.tvResendCode.setOnClickListener(this);
        enterOtpLayout.etOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    enterOtpLayout.etOtp2.requestFocus();
                }
                enterOtpLayout.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        enterOtpLayout.etOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    enterOtpLayout.etOtp3.requestFocus();
                } else {
                    enterOtpLayout.etOtp1.requestFocus();
                }
                enterOtpLayout.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        enterOtpLayout.etOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    enterOtpLayout.etOtp4.requestFocus();
                } else {
                    enterOtpLayout.etOtp2.requestFocus();
                }
                enterOtpLayout.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        enterOtpLayout.etOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0) {
                    enterOtpLayout.etOtp3.requestFocus();
                }
                enterOtpLayout.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvResendCode:
                getOtpApi();
                break;
            case R.id.change_number:
                binding.etMobile.setEnabled(true);
                binding.etMobile.getText().clear();
                binding.etMobile.requestFocus();
                binding.changeNumber.setVisibility(View.GONE);
                binding.sendOtpOnMobile.setVisibility(View.VISIBLE);
                break;
            case R.id.change_email:
                binding.etEmail.setEnabled(true);
                binding.etEmail.getText().clear();
                binding.etEmail.requestFocus();
                binding.changeEmail.setVisibility(View.GONE);
                binding.llVerifyEmail.setVisibility(View.GONE);
                binding.sendOtpOnEmail.setVisibility(View.VISIBLE);
                break;
            case R.id.send_otp_on_mobile:
                if (isValidMobile())
                    checkUser("mobile");
                break;
            case R.id.send_otp_on_email:
                if (isValidEmail())
                    checkUser("email");
                break;
            case R.id.btnUpdateEmail:
                if (isValidOtp(binding.enterOtpLayoutEmail)) {
                    updateProfile("email");
                }
                break;
            case R.id.btnUpdateMobile:
                if (isValidOtp(binding.enterOtpLayoutVerify))
                    updateProfile("mobile");
                break;
            case R.id.ivBack:
                finish();
                break;
            default:
                break;

        }
    }

    private void getOtpApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                if (is_resend_email)
                    map.put("email", email);
                if (is_resend_mobile)
                    map.put("mobile", mobile);
                map.put("user_id", user_id);
                api.getOTP(map).enqueue(new Callback<UserBaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserBaseResponse> call, @NonNull retrofit2.Response<UserBaseResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            CommonUtils.showToastShort(context, response.body().getMessage());
                            if (response.body().getStatus().equals("1")) {


                            } else {
                                CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, response.body().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserBaseResponse> call, @NonNull Throwable t) {
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

    private void checkUser(String type) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "edit");
                if (type.equals("email"))
                    map.put("email", email);
                else
                    map.put("mobile", mobile);
                Api api = RequestController.createService(context, Api.class);
                api.checkUser(map).enqueue(new Callback<UserBaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserBaseResponse> call, @NonNull retrofit2.Response<UserBaseResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {
                                if (response.body().getIs_user().equals("0")) {
                                    //SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUser_id());
                                    //  user_id = response.body().getUser_id();
                                    if (response.body().getOtp() != null && !response.body().getOtp().isEmpty()) {
                                        if (type.equals("email")) {
                                            binding.llVerifyEmail.setVisibility(View.VISIBLE);
                                            binding.sendOtpOnEmail.setVisibility(View.GONE);
                                            binding.enterOtpLayoutEmail.etOtp1.requestFocus();
                                            binding.etEmail.setEnabled(false);
                                            binding.changeEmail.setVisibility(View.VISIBLE);
                                            is_resend_email = true;
                                        } else {
                                            binding.llVerifyMobile.setVisibility(View.VISIBLE);
                                            binding.sendOtpOnMobile.setVisibility(View.GONE);
                                            binding.enterOtpLayoutVerify.etOtp1.requestFocus();
                                            binding.etMobile.setEnabled(false);
                                            is_resend_mobile = true;
                                        }
                                    }
                                } else {
                                    if (type.equals("email"))
                                        CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, response.body().getMessage());
                                    else
                                        CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, response.body().getMessage());
                                }

                            } else {
                                CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, response.body().getMessage());

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserBaseResponse> call, @NonNull Throwable t) {
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

    private boolean isValidEmail() {
        email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        if (email.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, context.getResources().getString(R.string.empty_email));
        } else if (!RegexUtils.isValidEmail(email)) {
            CommonUtils.setErrorMessage(binding.etEmail, binding.tvEmailError, context.getResources().getString(R.string.invalid_email));
        } else
            return true;

        return false;
    }

    private boolean isValidMobile() {
        mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
        if (mobile.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, context.getResources().getString(R.string.empty_mobile));
        } else if (!RegexUtils.isValidMobileNumber(mobile)) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, context.getResources().getString(R.string.invalid_mobile));
        } else
            return true;

        return false;
    }

    private void updateProfile(String type) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                if (type.equals("email")) {
                    map.put("email", email);
                } else {
                    map.put("mobile", mobile);
                }
                map.put("otp", otp);
                map.put("user_id", user_id);
                map.put("type", type);
                map.put("device_token", device_token);
                api.verifyOtpSignup(map).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginData> call, @NonNull Response<LoginData> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {
                                if (response.body().getName() != null && !response.body().getName().isEmpty())
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, response.body().getName());
                                else
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, "User");

                                SharedPref.saveStringPreference(context, AppConstant.USER_MOBILE, response.body().getMobile());
                                SharedPref.saveStringPreference(context, AppConstant.USER_EMAIL, response.body().getEmail());
                                SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUserId());
                                ActivityController.startActivity(activity, Dashboard.class, true, true);
                            } else {

                                if (type.equals("mobile"))
                                    CommonUtils.setErrorMessage(binding.enterOtpLayoutVerify.etOtp1, binding.enterOtpLayoutVerify.tvOtpError, response.body().getMessage());
                                else
                                    CommonUtils.setErrorMessage(binding.enterOtpLayoutEmail.etOtp1, binding.enterOtpLayoutEmail.tvOtpError, response.body().getMessage());


                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginData> call, @NonNull Throwable t) {
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

    private boolean isValidOtp(LayoutEnterOtpBinding enterOtpLayout) {
        String otp1 = enterOtpLayout.etOtp1.getText().toString().trim();
        String otp2 = enterOtpLayout.etOtp2.getText().toString().trim();
        String otp3 = enterOtpLayout.etOtp3.getText().toString().trim();
        String otp4 = enterOtpLayout.etOtp4.getText().toString().trim();
        otp = otp1 + otp2 + otp3 + otp4;
        if (otp1.isEmpty()) {
            enterOtpLayout.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp2.isEmpty()) {
            enterOtpLayout.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp3.isEmpty()) {
            enterOtpLayout.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp4.isEmpty()) {
            enterOtpLayout.tvOtpError.setVisibility(View.VISIBLE);
        } else {
            return true;
        }
        return false;
    }
}