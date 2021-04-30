package com.indiaoncology.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.indiaoncology.R;
import com.indiaoncology.databinding.DialogBinding;
import com.indiaoncology.databinding.FragmentProfileBinding;
import com.indiaoncology.databinding.PopupAddReminderBinding;
import com.indiaoncology.model.user.LoginData;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.Contact;
import com.indiaoncology.ui.OnBoarding;
import com.indiaoncology.ui.Redirect;
import com.indiaoncology.ui.SubmitQuery;
import com.indiaoncology.ui.article.ArticleDescription;
import com.indiaoncology.ui.doctor.AppointmentDetails;
import com.indiaoncology.ui.doctor.MyOrder;
import com.indiaoncology.ui.patient.SelectPatient;
import com.indiaoncology.ui.user.EditProfile;
import com.indiaoncology.ui.user.Login;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.indiaoncology.utils.CommonUtils.*;

public class FragmentProfile extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private FragmentProfileBinding binding;
    private Context context;
    private String from = "", device_token, LOGIN_TYPE;
    private Dialog dialog;

    public FragmentProfile() {

    }

    public FragmentProfile(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        context = getActivity();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // fetching device token from prefrence manager
        PrefManager prefManager = PrefManager.getInstance(getActivity());
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        LOGIN_TYPE = SharedPref.getStringPreferences(context, AppConstant.LOGIN_TYPE);
        setToolbar(getActivity().getResources().getString(R.string.my_account));
        return view;
    }

    private void setToolbar(String title) {
        binding.menuBar.ivBack.setVisibility(View.GONE);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
        binding.menuBar.ivRight.setVisibility(View.GONE);
        binding.menuBar.ivLogo.setVisibility(View.VISIBLE);
        binding.menuBar.tvTitle.setVisibility(View.VISIBLE);
        binding.menuBar.tvTitle.setText("My Account");
        binding.llLogout.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
        binding.llPatient.setOnClickListener(this);
        binding.llAppointments.setOnClickListener(this);
        binding.llConatctUs.setOnClickListener(this);
        binding.tvPrivacyPolicy.setOnClickListener(this);
        binding.tvTermsConditions.setOnClickListener(this);
        binding.tvAboutUs.setOnClickListener(this);
        binding.llOnlineConsultation.setOnClickListener(this);
        binding.llRequestCallBack.setOnClickListener(this);
    }

    private void getDataApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getuserdata(SharedPref.getStringPreferences(context, AppConstant.USER_ID)).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1")) {

                                binding.tvEmail.setText(response.body().getEmail());
                                binding.tvMobile.setText(response.body().getMobile());
                                if (response.body().getName() != null && !response.body().getName().isEmpty())
                                    binding.tvName.setText(response.body().getName());
                                else
                                    binding.tvName.setText("User");

                                SharedPref.saveStringPreference(context, AppConstant.USER_MOBILE, response.body().getMobile());
                                SharedPref.saveStringPreference(context, AppConstant.USER_NAME, response.body().getName());
                                SharedPref.saveStringPreference(context, AppConstant.USER_EMAIL, response.body().getEmail());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLogout:
                openLogoutDialog();
                break;
            case R.id.tvEdit:
                ActivityController.startActivity(context, EditProfile.class);
                break;
            case R.id.llPatient:
                ActivityController.startActivity(context, SelectPatient.class, AppConstant.PROFILE);
                break;
            case R.id.llConatctUs:
                ActivityController.startActivity(context, Contact.class);
                break;
            case R.id.llOnlineConsultation:
                ActivityController.startActivity(context, SubmitQuery.class);
                break;
            case R.id.llAppointments:
                ActivityController.startActivity(context, MyOrder.class);
                break;
            case R.id.tvPrivacyPolicy:
                serUrl(SharedPref.getStringPreferences(context, AppConstant.PRIVACY_POLICY_URL), "Privacy Policy");
                break;
            case R.id.tvTermsConditions:
                serUrl(SharedPref.getStringPreferences(context, AppConstant.TERMS_CONDITION_URL), "Term & Conditions");
                break;
            case R.id.tvAboutUs:
                serUrl(SharedPref.getStringPreferences(context, AppConstant.ABOUT_US_URL), "About Us");
                break;
            case R.id.llRequestCallBack:
                requestCallBack();
                break;
        }
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

    private void serUrl(String url, String title) {
        Intent intent = new Intent(context, Redirect.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        context.startActivity(intent);
    }

    private void openLogoutDialog() {
        final DialogBinding logoutDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog, null, false);
        dialog = DialogUtils.createDialog(context, logoutDialogBinding.getRoot(), 0);
        dialog.setCancelable(false);
        logoutDialogBinding.tvHeading.setText("Logout");
        logoutDialogBinding.tvDescription.setText("Are you sure you want to logout?");
        logoutDialogBinding.tvNo.setOnClickListener(v -> dialog.dismiss());
        logoutDialogBinding.tvYes.setOnClickListener(v -> {
            if (LOGIN_TYPE.equalsIgnoreCase(AppConstant.TYPE_GMAIL)) {
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(status -> {
                    if (status.isSuccess()) {
                        dialog.dismiss();
                        userLogout(AppConstant.TYPE_GMAIL);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Session not close", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (LOGIN_TYPE.equalsIgnoreCase(AppConstant.TYPE_FACEBOOK)) {
                if (!SharedPref.getBooleanPreferences(context, "FACEBOOK_TOKEN_NULL")) {
                    AccessToken.setCurrentAccessToken(null);
                    LoginManager.getInstance().logOut();
                }
                dialog.dismiss();
                userLogout(AppConstant.TYPE_FACEBOOK);
            } else {

                dialog.dismiss();
                userLogout(AppConstant.TYPE_APPLICATION);
            }

        });
    }

    private void userLogout(String type) {
        if (isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("device_token", device_token);
                if (!type.equalsIgnoreCase(AppConstant.TYPE_APPLICATION)) {
                    map.put("social_type", type);
                    map.put("social_token", SharedPref.getStringPreferences(context, AppConstant.SOCIAL_TOKEN));
                }
                api.logout(map).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("1"))
                                goToLogin();
                            else
                                showToastShort(context, response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        ProgressDialogUtils.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));
        }
    }

    private void goToLogin() {
        SharedPref.clearPrefs(getActivity());
        ActivityController.startActivity(getActivity(), Login.class, true, true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getDataApi();
    }
}