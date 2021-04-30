package com.indiaoncology.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityDashboardBinding;
import com.indiaoncology.fragment.FragmentBlog;
import com.indiaoncology.fragment.FragmentDoctor;
import com.indiaoncology.fragment.FragmentHome;
import com.indiaoncology.fragment.FragmentProfile;
import com.indiaoncology.listener.onFragmentChange;
import com.indiaoncology.model.user.LoginData;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, onFragmentChange {
    private Context context;
    private Activity activity;
    private ActivityDashboardBinding binding;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        context = Dashboard.this;
        activity = Dashboard.this;
        setUpGClient();
        getNextScreenSelection(new FragmentHome(), true);
        setFragments();
        getDataApi();
        getPermission();
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

                                SharedPref.saveStringPreference(context, AppConstant.PRIVACY_POLICY_URL, response.body().getPrivacy_policy_url());
                                SharedPref.saveStringPreference(context, AppConstant.ABOUT_US_URL, response.body().getAbout_us_url());
                                SharedPref.saveStringPreference(context, AppConstant.TERMS_CONDITION_URL, response.body().getTerm_condition_url());
                                SharedPref.saveStringPreference(context, AppConstant.COMPANY_ADDRESS, response.body().getCompany_address());
                                SharedPref.saveStringPreference(context, AppConstant.COMPANY_MOBILE_NUMBER, response.body().getCompany_mobile());
                                SharedPref.saveStringPreference(context, AppConstant.COMPANY_EMAIL, response.body().getCompany_email());
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

    private synchronized void setUpGClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();

    }

    // next screen selection on basis of user login or not
    public void getNextScreenSelection(Fragment fragment, boolean isSkip) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setFragments() {
        binding.navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getNextScreenSelection(new FragmentHome(), true);
                    return true;
                case R.id.medicine:
                    getNextScreenSelection(new FragmentBlog(), true);
                    return true;
                case R.id.doctor:
                    getNextScreenSelection(new FragmentDoctor(), true);
                    return true;
                case R.id.account:
                    getNextScreenSelection(new FragmentProfile(googleApiClient), true);
                    return true;
            }
            return false;
        });
    }

    private void getPermission() {
        if (CommonUtils.checkIsMarshMallowVersion()) {
            CommonUtils.requestPermissionStorage(activity);
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void sendData(String data) {
        if (data.equalsIgnoreCase("Doctor"))
            binding.navigation.setSelectedItemId(R.id.doctor);
    }
}