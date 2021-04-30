package com.indiaoncology.ui.article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.article.ArticleAdapter;
import com.indiaoncology.adaptar.article.ArticleListAdapter;
import com.indiaoncology.databinding.ActivityArticleListBinding;
import com.indiaoncology.listener.PaginationScrollListener;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.model.type.TypeResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class ArticleList extends AppCompatActivity {
    private ActivityArticleListBinding binding;
    public ArticleListAdapter adapter;
    private Context context;
    private Activity activity;
    private Api api;
    private List<Data> blogDataList = new ArrayList<>();
    private String blogCategoryId = "", blogCategoryName = "", c_id = "", c_title = "", from = "";

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int TOTAL_PAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_list);
        context = ArticleList.this;
        activity = ArticleList.this;
        api = RequestController.createService(context, Api.class);
        getData();

        if (blogCategoryName != null && !blogCategoryName.isEmpty() && !blogCategoryName.equals("")) {
            CommonUtils.setToolBar(binding.menubar, blogCategoryName, activity);
        } else {
            CommonUtils.setToolBar(binding.menubar, "Articles", activity);
        }
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);

        binding.swipeRefreshingLayout.setOnRefreshListener(() -> {
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();
            setAdapter();
            getCategoryWiseBlogs();

        });
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("BLOG_CATEGORY_ID") != null && !intent.getStringExtra("BLOG_CATEGORY_ID").isEmpty()) {
                blogCategoryId = intent.getStringExtra("BLOG_CATEGORY_ID");
            }
            if (intent.getStringExtra("BLOG_CATEGORY_NAME") != null && !intent.getStringExtra("BLOG_CATEGORY_NAME").isEmpty()) {
                blogCategoryName = intent.getStringExtra("BLOG_CATEGORY_NAME");
            }
        }

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvArticles.setLayoutManager(linearLayoutManager);
        binding.rvArticles.hasFixedSize();
        binding.rvArticles.setItemAnimator(new DefaultItemAnimator());
        adapter = new ArticleListAdapter(context, blogDataList, (view, position) -> {
            switch (view.getId()) {
                case R.id.ivFavourite:
                    addBlogToFavourite(position);
                    break;
                case R.id.ivRemoveFavourite:
                    removeFavourite(position);
                    break;
            }
        });
        adapter.notifyDataSetChanged();
        binding.rvArticles.setAdapter(adapter);
        binding.rvArticles.addOnScrollListener(new PaginationScrollListener(linearLayoutManager, "LINEAR") {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    protected void onResume() {
        setAdapter();
        getCategoryWiseBlogs();
        super.onResume();
    }

    private void addBlogToFavourite(int position) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.addFavouriteBlog(SharedPref.getStringPreferences(context, AppConstant.USER_ID),
                        blogDataList.get(position).getId())
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        adapter.notifyDataSetChanged();
                                        currentPage = PAGE_START;
                                        isLastPage = false;
                                        adapter.clear();
                                        setAdapter();
                                        getCategoryWiseBlogs();
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
            CommonUtils.showToastShort(context, context.getResources().getString(R.string.no_internet));
        }
    }

    private void removeFavourite(int position) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.removeFavouriteBlog(SharedPref.getStringPreferences(context, AppConstant.USER_ID),
                        blogDataList.get(position).getId())
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        adapter.notifyDataSetChanged();
                                        currentPage = PAGE_START;
                                        isLastPage = false;
                                        adapter.clear();
                                        setAdapter();
                                        getCategoryWiseBlogs();
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
            CommonUtils.showAlertPopup(context, context.getResources().getString(R.string.internet_title), context.getResources().getString(R.string.no_internet));

        }
    }


    private void loadNextPage() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", currentPage);
        map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
        if (blogCategoryId != null && !blogCategoryId.isEmpty() && !blogCategoryId.equals("")) {
            map.put("blog_category_id", blogCategoryId);
        }
        api.getBlogList(map).enqueue(new BaseCallback<TypeResponse>(context) {
            @Override
            public void onSuccess(TypeResponse response) {
                if (response != null) {
                    if (response.getPagination() != null) {
                        currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                        TOTAL_PAGES = response.getPagination().getMaxPage();
                    }

                    adapter.removeLoadingFooter();
                    isLoading = false;

                    List<Data> result = response.getDataList();
                    if (result == null)
                        return;
                    else
                        adapter.addAll(result);
                    binding.swipeRefreshingLayout.setRefreshing(false);

                    if (currentPage < TOTAL_PAGES)
                        adapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }

            @Override
            public void onFail(Call<TypeResponse> call, BaseResponse baseResponse) {
                binding.swipeRefreshingLayout.setRefreshing(false);
            }
        });
    }


    private void getCategoryWiseBlogs() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", currentPage);
        map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
        if (blogCategoryId != null && !blogCategoryId.isEmpty() && !blogCategoryId.equals("")) {
            map.put("blog_category_id", blogCategoryId);
        }
        if (CommonUtils.isOnline(context)) {
            try {
                api.getBlogList(map).enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                        binding.rvArticles.setVisibility(View.VISIBLE);
                        binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                        binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getPagination() != null) {
                                    TOTAL_PAGES = response.getPagination().getMaxPage();
                                    currentPage = Integer.parseInt(response.getPagination().getCurrentPage());
                                }
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    setEmptyLayout(false);
                                    blogDataList.clear();
                                    blogDataList.addAll(response.getDataList());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    setEmptyLayout(true);
                                    blogDataList.clear();
                                    adapter.notifyDataSetChanged();
                                }

                                if (currentPage < TOTAL_PAGES)
                                    adapter.addLoadingFooter();
                                else isLastPage = true;

                            } else {
                                setEmptyLayout(true);
                                blogDataList.clear();
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<TypeResponse> call, BaseResponse baseResponse) {
                        binding.swipeRefreshingLayout.setRefreshing(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
            noInternetPopup();
        }
    }

    public void noInternetPopup() {
        binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
        binding.swipeRefreshingLayout.setVisibility(View.GONE);
        binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
        binding.emptyLayout.tvHeading.setText(getResources().getString(R.string.no_internet_connection));
        binding.emptyLayout.ivImage.setImageResource(R.drawable.no_internet);
        binding.emptyLayout.tvSubHeading.setText(R.string.no_internet);
        binding.emptyLayout.btnSubmit.setText(getResources().getString(R.string.retry));
        binding.emptyLayout.btnSubmit.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
    }

    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.swipeRefreshingLayout.setVisibility(View.GONE);
            binding.emptyLayout.ivImage.setImageResource(R.drawable.nofavourite);
            binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
            binding.emptyLayout.tvSubHeading.setText(getResources().getString(R.string.no_data_found));
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }
}
