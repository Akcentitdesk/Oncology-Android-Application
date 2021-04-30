package com.indiaoncology.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.iid.FirebaseInstanceId;
import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivitySplashBinding;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.SharedPref;


public class Splash extends AppCompatActivity {
    private Context context;
    private String refreshedToken;
    private PrefManager prefManager;
    private Activity activity;
    private int SPLASH_TIME_OUT = 1500;
    private ActivitySplashBinding binding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        context = Splash.this;
        activity = Splash.this;
        // fixing portrait mode pr oblem for SDK 26 if using windowIsTranslucent = true
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        prefManager = PrefManager.getInstance(activity);
        getDeviceToken();
    }

    private void getDeviceToken() {
        if (prefManager.getPreference(AppConstant.DEVICE_TOKEN_) != null && prefManager.getPreference(AppConstant.DEVICE_TOKEN_).toString().trim().length() > 0) {
            decideNextActivity();
        } else {
            try {
                refreshedToken = FirebaseInstanceId.getInstance().getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(() -> {
                if (prefManager.getPreference(AppConstant.DEVICE_TOKEN_) != null && !prefManager.getPreference(AppConstant.DEVICE_TOKEN_).toString().trim().isEmpty()) {
                    decideNextActivity();
                } else {
                    if (refreshedToken != null) {
                        prefManager.savePreference(AppConstant.DEVICE_TOKEN_, refreshedToken);
                        if (refreshedToken.trim().length() > 0) {
                            decideNextActivity();

                        } else {
                            getDeviceToken();
                        }
                    } else {
                        getDeviceToken();
                    }
                }
            }, SPLASH_TIME_OUT);
        }
        Log.e("token", "" + prefManager.getPreference(AppConstant.DEVICE_TOKEN_));
    }

    private void decideNextActivity() {
        new Handler().postDelayed(() -> {
            if (SharedPref.getBooleanPreferences(context, AppConstant.IS_LOGIN)) {
                ActivityController.startActivity(activity, Dashboard.class, true);
            } else {
                ActivityController.startActivity(activity, OnBoarding.class, true);
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onStop() {
        finish();
        super.onStop();
    }
}
