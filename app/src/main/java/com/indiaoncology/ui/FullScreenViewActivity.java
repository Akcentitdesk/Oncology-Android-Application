package com.indiaoncology.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.FullScreenImageAdapter;
import com.indiaoncology.databinding.ActivityFullScreenViewBinding;

import java.util.ArrayList;

public class FullScreenViewActivity extends AppCompatActivity implements View.OnClickListener {
    private FullScreenImageAdapter adapter;
    private Activity context;
    private ArrayList<String> imagepaths;
    private int position;
    private String title,from;
    private ActivityFullScreenViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen_view);
        context = FullScreenViewActivity.this;
        getData();
        setToolBar();
        setViewPager();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBack) {
            finish();
        }

    }

    private void setToolBar() {
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.tvTitle.setText(title);
    }

    private void setViewPager() {
        adapter = new FullScreenImageAdapter(context, imagepaths,from);
        binding.pager.setAdapter(adapter);
        binding.pager.setCurrentItem(position);
        binding.tabLayout.setupWithViewPager(binding.pager);
    }

    private void getData() {
        Intent i = getIntent();
        if (i != null) {
            position = i.getIntExtra("position", 0);
            if (i.getStringArrayListExtra("image_paths") != null)
                imagepaths = i.getStringArrayListExtra("image_paths");
            if (i.getStringExtra("title") != null && !i.getStringExtra("title").isEmpty())
                title = i.getStringExtra("title");

            if (i.getStringExtra("from") != null && !i.getStringExtra("from").isEmpty())
                from = i.getStringExtra("from");
        }
    }
}
