package com.indiaoncology.ui.startAndDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indiaoncology.R;
import com.indiaoncology.adaptar.OnboardingAdapter;
import com.indiaoncology.model.OnboardingItem;
import com.indiaoncology.ui.user.Login;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class OnBoarding extends AppCompatActivity {
    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicator;
    private TextView tv_skip;
    private LinearLayout layout_button;
    private TextView btn_login;
    private Context context;
    private Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = OnBoarding.this;
        SharedPref preference = SharedPref.getInstance(this);
        if (!preference.isLogin()) {
            setupOnboardingItem();

        } else {
           /* Intent intent = new Intent(this, HomeActivity.class);
            finishAffinity();
            startActivity(intent);*/
        }
        layoutOnboardingIndicator = findViewById(R.id.layoutOnboardingIndicators);
        tv_skip = findViewById(R.id.buttonOnBoarding);
        layout_button = findViewById(R.id.layout_button);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        ViewPager2 viewPager2 = findViewById(R.id.onBoardingViewpage);
        viewPager2.setAdapter(onboardingAdapter);
        if (onboardingAdapter == null) {
            return;
        }
        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });


        tv_skip.setOnClickListener(v -> {
            viewPager2.postDelayed(() -> viewPager2.setCurrentItem(2), 150);
            /*Intent intent = new Intent(this, Dashboard.class);
            intent.putExtra(AppConstant.LOGIN_TYPE, AppConstant.WITHOUT_LOGIN);
            startActivity(intent);*/

        });
        btn_login.setOnClickListener(v -> {
            Intent intent = new Intent(context, Login.class);
            intent.putExtra(AppConstant.LOGIN_TYPE, AppConstant.TYPE_LOGIN);
            startActivity(intent);
        });

       /* btn_signup.setOnClickListener(v -> {
            Intent intent = new Intent(context, Login.class);
            intent.putExtra(AppConstant.LOGIN_TYPE, AppConstant.TYPE_SIGNUP);
            startActivity(intent);
        });

*/
    }

    private void setupOnboardingItem() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem item1 = new OnboardingItem();
        item1.setTitle("Consult with top oncologists");
        item1.setDescription("Lorem ipsum dolor sit amet, ipsum dolor adipiscing elit.");
        item1.setImage(R.drawable.onboarding_2);

        OnboardingItem item2 = new OnboardingItem();
        item2.setTitle("Track your booking details");
        item2.setDescription("Lorem ipsum dolor sit amet, ipsum dolor adipiscing elit.");
        item2.setImage(R.drawable.coontact_us2);

        OnboardingItem item3 = new OnboardingItem();
        item3.setTitle("Book appointments near you");
        item3.setDescription("Lorem ipsum dolor sit amet, ipsum dolor adipiscing elit.");
        item3.setImage(R.drawable.contact_us);

        onboardingItems.add(item1);
        onboardingItems.add(item2);
        onboardingItems.add(item3);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);

    }

    private void setupOnboardingIndicators() {

        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(6, 0, 6, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicator.addView(indicators[i]);
        }

    }

    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            tv_skip.setVisibility(View.GONE);
            layoutOnboardingIndicator.setVisibility(View.GONE);
            layout_button.setVisibility(View.VISIBLE);
        } else {
            tv_skip.setVisibility(View.VISIBLE);
            layoutOnboardingIndicator.setVisibility(View.VISIBLE);
            layout_button.setVisibility(View.GONE);
        }

    }

}