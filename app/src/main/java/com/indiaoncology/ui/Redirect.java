package com.indiaoncology.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityTypeDetailScreenBinding;
import com.indiaoncology.databinding.ShimmerDummyLayoutBinding;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.Objects;

public class Redirect extends AppCompatActivity {
    private Context context;
    private Activity activity;
    private ActivityTypeDetailScreenBinding binding;
    private String url = " ", title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_type_detail_screen);
        context = Redirect.this;
        activity = Redirect.this;
        getData();
        setData();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("URL") != null && !Objects.requireNonNull(intent.getStringExtra("URL")).isEmpty()) {
                url = intent.getStringExtra("URL");
            }
            if (intent.getStringExtra("TITLE") != null && !Objects.requireNonNull(intent.getStringExtra("TITLE")).isEmpty()) {
                title = intent.getStringExtra("TITLE");
            }
        }
    }

    public class AppWebViewClients extends WebViewClient {
        private ShimmerDummyLayoutBinding placeholderBinding;

        public AppWebViewClients(ShimmerDummyLayoutBinding placeholderBinding) {
            this.placeholderBinding = placeholderBinding;
            placeholderBinding.shimmerlayout.setVisibility(View.VISIBLE);
            placeholderBinding.shimmerViewContainer.startShimmerAnimation();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            placeholderBinding.shimmerlayout.setVisibility(View.GONE);
            binding.webview.setVisibility(View.VISIBLE);
        }
    }

    private void setData() {
        CommonUtils.setToolBar(binding.menubar, title, activity);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);

        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new AppWebViewClients(binding.shimmerEffect));
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (url != null && !url.isEmpty()) {
            binding.webview.loadUrl(url);
        }
    }
}