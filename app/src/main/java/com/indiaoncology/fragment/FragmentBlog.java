package com.indiaoncology.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comm100.livechat.ChatActivity;
import com.comm100.livechat.VisitorClientInterface;
import com.indiaoncology.R;
import com.indiaoncology.databinding.FragmentBlogBinding;
import com.indiaoncology.ui.SearchActivity;
import com.indiaoncology.ui.SubmitQuery;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;


public class FragmentBlog extends Fragment implements View.OnClickListener {
    private static Context context;
    private FragmentBlogBinding binding;

    public FragmentBlog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog, container, false);
        View view = binding.getRoot();
        context = getActivity();
        setToolbar();
        return view;
    }

    private void setToolbar() {
        binding.menubar.ivBack.setVisibility(View.GONE);
        binding.menubar.ivLogo.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setText("Medicine");
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.llSearchBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSearchBar:
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.FROM, AppConstant.MEDICINE);
                ActivityController.startActivity(getActivity(), SearchActivity.class, bundle, false, false);
                break;
            default:
                break;
        }
    }
}