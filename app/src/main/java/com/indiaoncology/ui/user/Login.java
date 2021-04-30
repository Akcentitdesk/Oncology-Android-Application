package com.indiaoncology.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityLoginBinding;
import com.indiaoncology.databinding.LayoutEnterOtpBinding;
import com.indiaoncology.model.user.LoginData;
import com.indiaoncology.model.user.UserBaseResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.Dashboard;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.RegexUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private Context context;
    private ActivityLoginBinding binding;
    private String mobile;
    private String password;
    private String otp, otp_new;
    private String name;
    private String email;
    private String pass;
    private String phone;
    private String new_pass;
    private String fullName;
    private String facebook_id;
    private String device_token = "";
    private String user_id = "";
    private String social_token_;
    private String social_login_type_;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    private boolean is_social_login_verification = false, is_forgot_password = false, is_signup_flow = false;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        context = Login.this;
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setViews();
        addGoogle();
        addFacebook();
    }

    private void setViews() {
        PrefManager prefManager = PrefManager.getInstance(context);
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        //user_id = SharedPref.getStringPreferences(context, AppConstant.USER_ID);

        Intent intent = getIntent();
        if (intent != null) {
            String login_type = intent.getStringExtra(AppConstant.LOGIN_TYPE);
            if (login_type != null) {
                if (login_type.equalsIgnoreCase(AppConstant.TYPE_SIGNUP)) {
                    setSignUpView();
                } else if (login_type.equalsIgnoreCase(AppConstant.TYPE_LOGIN)) {
                    setLoginView(false);
                }
            }
        }

        setSpannable(binding.tvNewUser, "Don't have an account? Sign Up", 23, 30);
        setSpannable(binding.tvAlreadyUser, "Have an account? Login", 17, 22);
        //setSpannable(binding.tvResendCode, "Didn't get the code? Resend code", 21, 32);

        //  binding.etMobile.requestFocus();

        binding.ivBack.setOnClickListener(this);
        binding.changeNumber.setOnClickListener(this);
        binding.forgotPassword.setOnClickListener(this);
        binding.usePassword.setOnClickListener(this);
        binding.useOtp.setOnClickListener(this);
        binding.tvUseOtp.setOnClickListener(this);
        //binding.tvUsePassword.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.btnUpdateMobile.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);
        binding.btnLoginOtp.setOnClickListener(this);
        binding.btnResetPassword.setOnClickListener(this);
        binding.btnVerifyOTP.setOnClickListener(this);
        binding.tvNewUser.setOnClickListener(this);
        binding.tvAlreadyUser.setOnClickListener(this);
        binding.tvResendCode.setOnClickListener(this);
        binding.google.setOnClickListener(this);
        binding.facebookButton.setOnClickListener(this);

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
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvPasswprdError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etcnfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvCnfPasswordError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etMobile2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvMobileError2.setVisibility(View.INVISIBLE);
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
        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvNameError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvPasswordError2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvnewPasseroor.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etCnfNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvCnfNewPassError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setOTPViews(binding.enterOtpLayoutLogin);
        binding.etOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    binding.etOtp2.requestFocus();
                }
                binding.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    binding.etOtp3.requestFocus();
                } else {
                    binding.etOtp1.requestFocus();
                }
                binding.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    binding.etOtp4.requestFocus();
                } else {
                    binding.etOtp2.requestFocus();
                }
                binding.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.etOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0) {
                    binding.etOtp3.requestFocus();
                }
                binding.tvOtpError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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


    private void setSpannable(TextView textView, String span_text, int start, int end) {
        SpannableString span_str = new SpannableString(span_text);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent));
        span_str.setSpan(foregroundColorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(span_str);
    }

    private void setSignUpView() {
        binding.heading.setText("Create account.");
        binding.subHeading.setText("Please sign up to continue");
        binding.llSignupView.setVisibility(View.VISIBLE);
        binding.llLoginView.setVisibility(View.GONE);
        binding.llMobile.setVisibility(View.GONE);
        binding.bottomView.setVisibility(View.VISIBLE);
        binding.btnResetPassword.setVisibility(View.GONE);
    }

    private void setLoginView(boolean isUpdateMobile) {
        binding.llSignupView.setVisibility(View.GONE);
        binding.llForgotView.setVisibility(View.GONE);
        binding.llMobile.setVisibility(View.VISIBLE);
        binding.bottomView.setVisibility(View.GONE);
        binding.btnResetPassword.setVisibility(View.GONE);
        binding.llVerifyOtpView.setVisibility(View.GONE);
        if (isUpdateMobile) {
            binding.heading.setText("Update Mobile.");
            binding.subHeading.setText("Verify your mobile number");
            binding.btnUpdateMobile.setVisibility(View.VISIBLE);
            binding.llLoginView.setVisibility(View.GONE);
        } else {
            // binding.heading.setText("Login to your account.");
            // binding.subHeading.setText("Please sign in to continue");
            binding.heading.setText("Sign In / Sign Up");
            binding.subHeading.setText("Please sign in / sign up to continue.");
            binding.llLoginView.setVisibility(View.VISIBLE);
            binding.layoutSelection.setVisibility(View.VISIBLE);
        }

    }


    private void setOtpVerificationView() {
        binding.heading.setText("OTP Verification.");
        binding.subHeading.setText("Verify your mobile number");
        binding.llVerifyOtpView.setVisibility(View.VISIBLE);
        binding.llSignupView.setVisibility(View.GONE);
        binding.llForgotView.setVisibility(View.GONE);
        binding.llLoginView.setVisibility(View.GONE);
        binding.llMobile.setVisibility(View.GONE);
        binding.bottomView.setVisibility(View.GONE);
        binding.btnResetPassword.setVisibility(View.GONE);
        binding.etOtp1.requestFocus();
    }

    private void setUpdateMobileView(boolean isVerify) {
        binding.llSignupView.setVisibility(View.GONE);
        binding.llForgotView.setVisibility(View.GONE);
        binding.llLoginView.setVisibility(View.GONE);
        binding.bottomView.setVisibility(View.GONE);
        binding.btnResetPassword.setVisibility(View.GONE);
        if (isVerify) {
            binding.heading.setText("OTP Verification.");
            binding.llVerifyOtpView.setVisibility(View.VISIBLE);
            //binding.tvResendCode.setVisibility(View.GONE);
            binding.llMobile.setVisibility(View.VISIBLE);
            binding.etOtp1.requestFocus();
            binding.btnUpdateMobile.setVisibility(View.GONE);
            binding.etMobile.setEnabled(false);
            binding.changeNumber.setVisibility(View.VISIBLE);
            is_social_login_verification = true;
        } else {
            binding.etMobile.setEnabled(true);
            binding.etMobile.getText().clear();
            binding.heading.setText("Update Mobile.");
            binding.llVerifyOtpView.setVisibility(View.GONE);
            binding.llMobile.setVisibility(View.VISIBLE);
            binding.btnUpdateMobile.setVisibility(View.VISIBLE);
        }
        binding.subHeading.setText("Verify your mobile number");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.facebook_button:
                if (v == binding.facebookButton)
                    binding.facebook.performClick();
                break;
            case R.id.google:
                if (v == binding.google)
                    signIn();
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvNewUser:
                setSignUpView();
                break;
            case R.id.tvAlreadyUser:
                setLoginView(false);
                break;
           /* case R.id.use_password:
                if (isValidMobile())
                    // checkUserApi("password", true);
                    break;
            case R.id.tv_use_password:
                if (isValidMobile())
                    // checkUserApi("password", false);
                    break;*/
            case R.id.use_otp:  // send otp on mobile for login
                if (isValidMobile())
                    checkUserApi("otp", true);
                break;
           /* case R.id.tv_use_otp: // send otp on mobile for login
                if (isValidMobile())
                    //checkUserApi("otp", false);
                    break;*/
            case R.id.change_number:
                binding.etMobile.setEnabled(true);
                binding.etMobile.getText().clear();
                binding.etMobile.requestFocus();
                binding.changeNumber.setVisibility(View.GONE);
                if (is_social_login_verification)
                    setLoginView(true);
                else {
                    setLoginView(false);
                    binding.layoutOtp.setVisibility(View.GONE);
                    binding.layoutPassword.setVisibility(View.GONE);
                    binding.layoutSelection.setVisibility(View.VISIBLE);
                }
                break;
            /*case R.id.btn_login: // login using mobile | password
                if (isValidPassword())
                    // login("password");
                    break;*/
            case R.id.btn_login_otp: // login using mobile | otp
                if (isValidOtp(binding.enterOtpLayoutLogin))
                    login("otp");
                break;
           /* case R.id.btn_signup: // send otp for signup
                if (isValidForm())
                    getSignUpApi();
                break;*/
            case R.id.btnUpdateMobile: // send otp on mobile for social login
                if (isValidMobile())
                    getOtpApi(false, true, false);
                break;
            case R.id.btnVerifyOTP:// verify otp for signup | Social login
                if (isValidOtpForm())
                    //Toast.makeText(context, "huki", Toast.LENGTH_SHORT).show();
                    verifyOtpApi();
                break;
            case R.id.tvResendCode:// resend otp for signup process | forgot password | login through otp | social login mobile verification
                getOtpApi(is_forgot_password, is_social_login_verification, is_signup_flow);
                System.out.println(is_signup_flow + "\n" + is_forgot_password + "\n" + is_social_login_verification);
                break;
            case R.id.forgot_password:
                is_forgot_password = true;
                getOtpApi(true, false, false);

                break;
            /*case R.id.btn_reset_password:// reset password
                if (isValidOtp(binding.enterOtpLayoutForgot)) {
                    if (isValidSetPassword())
                        resetPassword();
                }
                break;*/
            default:
                break;
        }
    }

    private boolean isValidOtpForm() {
        String otp1 = binding.etOtp1.getText().toString().trim();
        String otp2 = binding.etOtp2.getText().toString().trim();
        String otp3 = binding.etOtp3.getText().toString().trim();
        String otp4 = binding.etOtp4.getText().toString().trim();
        otp_new = otp1 + otp2 + otp3 + otp4;
        if (otp1.isEmpty()) {
            binding.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp2.isEmpty()) {
            binding.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp3.isEmpty()) {
            binding.tvOtpError.setVisibility(View.VISIBLE);
        } else if (otp4.isEmpty()) {
            binding.tvOtpError.setVisibility(View.VISIBLE);
        } else {
            return true;
        }
        return false;
    }

    private boolean isValidSetPassword() {
        new_pass = Objects.requireNonNull(binding.etNewPassword.getText()).toString().trim();
        String cnf_new_pass = Objects.requireNonNull(binding.etCnfNewPass.getText()).toString().trim();
        if (new_pass.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etNewPassword, binding.tvnewPasseroor, getResources().getString(R.string.empty_password));
        } else if (new_pass.length() < 8 || new_pass.length() > 16) {
            CommonUtils.setErrorMessage(binding.etNewPassword, binding.tvnewPasseroor, getResources().getString(R.string.invalid_password));
        } else if (!RegexUtils.isPasswordMatch(new_pass, cnf_new_pass)) {
            CommonUtils.setErrorMessage(binding.etCnfNewPass, binding.tvCnfNewPassError, getResources().getString(R.string.password_not_match));
        } else {
            return true;
        }
        return false;
    }

    private void setView(RelativeLayout layoutShow, RelativeLayout layoutGone, EditText et_focus) {
        layoutShow.setVisibility(View.VISIBLE);
        layoutGone.setVisibility(View.GONE);
        binding.etMobile.setEnabled(false);
        binding.changeNumber.setVisibility(View.VISIBLE);
        et_focus.requestFocus();
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

    private void gotoHome() {
        Intent intent = new Intent(context, Dashboard.class);
        intent.putExtra(AppConstant.FROM, AppConstant.TYPE_LOGIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }


    /* VERIFY OTP FOR SIGNUP | SOCIAL LOGIN  */
    private void verifyOtpApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                if (is_social_login_verification) {
                    map.put("mobile", mobile);
                    map.put("type", "social_login");
                    map.put("user_id", user_id);
                } else {
                    map.put("mobile", mobile);
                    // map.put("email", email);
                }
                map.put("otp", otp_new);
                map.put("device_token", device_token);
                api.verifyOtpSignup(map).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginData> call, @NonNull Response<LoginData> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {

                                SharedPref.saveBooleanPreferences(context, AppConstant.IS_LOGIN, true);
                                if (response.body().getName() != null && !response.body().getName().isEmpty())
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, response.body().getName());
                                else
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, "User");

                                SharedPref.saveStringPreference(context, AppConstant.USER_MOBILE, response.body().getMobile());
                                SharedPref.saveStringPreference(context, AppConstant.USER_EMAIL, response.body().getEmail());
                                SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUserId());


                                if (is_social_login_verification) {
                                    is_social_login_verification = false;
                                    if (response.body().getLogin_type() != null && !response.body().getLogin_type().isEmpty())
                                        SharedPref.saveStringPreference(context, AppConstant.LOGIN_TYPE, response.body().getLogin_type());
                                    if (response.body().getSocial_token() != null && !response.body().getSocial_token().isEmpty())
                                        SharedPref.saveStringPreference(context, AppConstant.SOCIAL_TOKEN, response.body().getSocial_token());
                                } else if (is_signup_flow)
                                    is_signup_flow = false;
                                else
                                    SharedPref.saveStringPreference(context, AppConstant.LOGIN_TYPE, AppConstant.TYPE_APPLICATION);

                                gotoHome();
                            } else {
                                CommonUtils.setErrorMessage(binding.etOtp1, binding.tvOtpError, response.body().getMessage());
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

    /*  CHECK USER FOR LOGIN */
    private void checkUserApi(String type, boolean isSelection) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("type", type);
                map.put("mobile", mobile);
                api.checkUser(map).enqueue(new Callback<UserBaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserBaseResponse> call, @NonNull retrofit2.Response<UserBaseResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {

                                if (response.body().getOtp() != null && !response.body().getOtp().isEmpty()) {
                                    if (response.body().getIs_user().equals("1")) {
                                        SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUser_id());
                                        user_id = response.body().getUser_id();
                                        binding.llLoginView.setVisibility(View.VISIBLE);
                                        binding.enterOtpLayoutLogin.etOtp1.getText().clear();
                                        binding.enterOtpLayoutLogin.etOtp2.getText().clear();
                                        binding.enterOtpLayoutLogin.etOtp3.getText().clear();
                                        binding.enterOtpLayoutLogin.etOtp4.getText().clear();
                                        setView(binding.layoutOtp, binding.layoutSelection, binding.enterOtpLayoutLogin.etOtp1);
                                    } else {
                                        is_signup_flow = true;
                                        binding.llVerifyOtpView.setVisibility(View.VISIBLE);
                                        binding.llLoginView.setVisibility(View.VISIBLE);
                                        binding.layoutSelection.setVisibility(View.GONE);
                                        binding.etOtp1.requestFocus();
                                        binding.changeNumber.setVisibility(View.VISIBLE);
                                        binding.etMobile.setEnabled(false);
                                    }




                                       /*if (isSelection) {
                                            setView(binding.layoutOtp, binding.layoutSelection, binding.enterOtpLayoutLogin.etOtp1);
                                        } else {
                                            setView(binding.layoutOtp, binding.layoutPassword, binding.enterOtpLayoutLogin.etOtp1);
                                        }*/
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

    /*  LOGIN THROUGH OTP OR PASSWORD */
    private void login(String type) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("type", type);
                map.put("user_id", user_id);
                map.put("mobile", mobile);
                map.put("device_token", device_token);
                if (type.equals("password"))
                    map.put("password", password);
                else
                    map.put("otp", otp);
                api.login(map).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginData> call, @NonNull retrofit2.Response<LoginData> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {
                                Toast.makeText(context, " " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                SharedPref.saveBooleanPreferences(context, AppConstant.IS_LOGIN, true);
                                SharedPref.saveStringPreference(context, AppConstant.LOGIN_TYPE, AppConstant.TYPE_APPLICATION);
                                if (response.body().getName() != null && !response.body().getName().isEmpty())
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, response.body().getName());
                                else
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, "User");

                                SharedPref.saveStringPreference(context, AppConstant.USER_MOBILE, response.body().getMobile());
                                SharedPref.saveStringPreference(context, AppConstant.USER_EMAIL, response.body().getEmail());
                                SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUserId());
                                gotoHome();

                            } else {
                                if (type.equals("otp")) {
                                    CommonUtils.setErrorMessage(binding.enterOtpLayoutLogin.etOtp1, binding.enterOtpLayoutLogin.tvOtpError, response.body().getMessage());
                                } /*else {
                                    CommonUtils.setErrorMessage(binding.etPassword, binding.tvPasswprdError, response.body().getMessage());
                                }*/
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

    /*  GET OTP FOR FORGOT PASSWORD| RESEND OTP | UPDATE MOBILE */
    private void getOtpApi(boolean is_forgot_password, boolean isUpdateMobile, boolean isSignup) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                if (is_forgot_password) {
                    map.put("mobile", mobile);
                    map.put("user_id", user_id);
                } else if (isUpdateMobile) {
                    map.put("mobile", mobile);
                    map.put("user_id", user_id);
                    map.put("type", "social_login");
                } else if (isSignup) {
                    map.put("mobile", mobile);
                    //map.put("email", email);
                } else {
                    map.put("mobile", mobile);
                    map.put("user_id", user_id);
                }
                api.getOTP(map).enqueue(new Callback<UserBaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserBaseResponse> call, @NonNull retrofit2.Response<UserBaseResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            CommonUtils.showToastShort(context, response.body().getMessage());
                            if (response.body().getStatus().equals("1")) {
                                if (is_forgot_password) {
                                    //setForgetPasswordView();
                                    // setCounter(binding.enterOtpLayoutForgot.tvTimer, binding.enterOtpLayoutForgot.tvResendCode);
                                }
                                if (isUpdateMobile) {
                                    setUpdateMobileView(true);
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


    /* SOCIAL LOGIN API  ( facebook/gmail ) */
    private void getSocialLogin(String SocialToken, String social_user_id, String name, String email, final String type) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.getSocialLogin(SocialToken, social_user_id, email, name, device_token, type).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginData> call, @NonNull Response<LoginData> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {
                                SharedPref.saveStringPreference(context, AppConstant.USER_ID, response.body().getUserId());
                                user_id = response.body().getUserId();
                                if (response.body().getIsVerified().equalsIgnoreCase("1")) {
                                    SharedPref.saveStringPreference(context, AppConstant.USER_NAME, response.body().getName());
                                    SharedPref.saveStringPreference(context, AppConstant.USER_MOBILE, response.body().getMobile());
                                    SharedPref.saveStringPreference(context, AppConstant.USER_EMAIL, response.body().getEmail());
                                    SharedPref.saveBooleanPreferences(context, AppConstant.IS_LOGIN, true);
                                    SharedPref.saveStringPreference(context, AppConstant.LOGIN_TYPE, type);
                                    SharedPref.saveStringPreference(context, AppConstant.SOCIAL_TOKEN, SocialToken);
                                    gotoHome();
                                } else {
                                    // OPEN UPDATE MOBILE VIEW
                                    setUpdateMobileView(false);
                                }
                            } else {
                                CommonUtils.showToastShort(context, response.body().getMessage());
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



    /* GMAIL INTEGRATION CODE*/

    private void addGoogle() {
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                System.out.println("user data : " + account.toString());
                String idToken = account.getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential, account);
            }
        } else {
            Log.e(TAG, "Login Unsuccessful. " + result);
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(final AuthCredential credential, final GoogleSignInAccount account) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        String fullName = account.getDisplayName();
                        String first_name = account.getGivenName();
                        String last_name = account.getFamilyName();
                        String email = account.getEmail();
                        Log.d(TAG, "full name : " + fullName + "\nfirst Name : " + first_name + "\nLast Name : " + last_name + "\nEmail : " + email);
                        social_login_type_ = AppConstant.TYPE_GMAIL;
                        social_token_ = account.getIdToken();
                        getSocialLogin(social_token_, account.getId(), fullName, email, social_login_type_);
                    } else {
                        Log.w(TAG, "signInWithCredential" + Objects.requireNonNull(task.getException()).getMessage());
                        task.getException().printStackTrace();
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }

                });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /* FACEBOOK INTEGRATION */

    private void addFacebook() {
        List<String> permissionNeeds = Arrays.asList("email", "public_profile");
        binding.facebook.setReadPermissions(permissionNeeds);
        binding.facebook.setLoginBehavior(LoginBehavior.WEB_ONLY);
        callbackManager = CallbackManager.Factory.create();

        binding.facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    Log.d("TAG", object.toString());
                    try {
                        social_token_ = object.getString("id");
                        if (object.has("name"))
                            fullName = object.getString("name");
                        if (object.has("email"))
                            email = object.getString("email");
                        if (object.has("id"))
                            facebook_id = object.getString("id");
                        String image_url = "https://graph.facebook.com/" + social_token_ + "/picture?type=normal";

                        Log.d("MyApp", "Shared Name : " + fullName + "\nEmail : " + email + "\nProfile Pic : " + image_url);
                        if (email == null)
                            email = facebook_id + "@facebook.com";

                        social_login_type_ = AppConstant.TYPE_FACEBOOK;
                        getSocialLogin(social_token_, facebook_id, fullName, email, social_login_type_);
                    } catch (Exception e) {
                        System.out.println("error" + e);
                        e.printStackTrace();
                    }
                });
                request.executeAsync();
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                SharedPref.saveBooleanPreferences(context, "FACEBOOK_TOKEN_NULL", loggedIn);
                Log.d("API123", loggedIn + " ??");
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

}