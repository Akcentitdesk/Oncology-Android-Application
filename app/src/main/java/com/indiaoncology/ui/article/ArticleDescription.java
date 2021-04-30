package com.indiaoncology.ui.article;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.adaptar.ReviewAdapter;
import com.indiaoncology.adaptar.article.ArticleAdapter;
import com.indiaoncology.databinding.ActivityArticleDescriptionBinding;
import com.indiaoncology.databinding.DialogRatingBinding;
import com.indiaoncology.model.blogDetail.BlogDetail;
import com.indiaoncology.model.blogDetail.BlogDetailResponse;
import com.indiaoncology.model.review.ReviewData;
import com.indiaoncology.model.review.ReviewResponse;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.Dashboard;
import com.indiaoncology.ui.review.AddReview;
import com.indiaoncology.ui.review.AllReviews;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class ArticleDescription extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityArticleDescriptionBinding binding;
    private boolean like = false;
    DialogRatingBinding ratingBinding;
    private boolean expandable, expand, isDeepLink = false;
    private Dialog dialog;
    private String blogId = "", blog_image = "", blog_URL = "", blogHeading = "", blogCategoryId = "",
            blogCategoryName = "", blogTitle = "";
    private List<Data> blogDataList = new ArrayList<>();
    private List<ReviewData> commentsDataList = new ArrayList<>();
    private ReviewAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_description);
        context = ArticleDescription.this;
        activity = ArticleDescription.this;
        getData();
        CommonUtils.setToolBar(binding.menuBar, getResources().getString(R.string.article_detail), activity);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
        binding.menuBar.ivRight.setVisibility(View.GONE);
        setListener();
        getArticleDescriptionApi();
        getArticleCommentsApi();
    }

    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }

    private void getData() {
        Uri uri = getIntent().getData();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("BLOG_ID") != null && !intent.getStringExtra("BLOG_ID").isEmpty()) {
                blogId = intent.getStringExtra("BLOG_ID");
            } else {
                if (uri != null) {
                    List<String> params = uri.getQueryParameters("id");
                    blogId = params.get(0);
                    isDeepLink = true;
                    System.out.println("Deep link data : " + blogId);
                }
            }
        }
    }


    private void setListener() {
        binding.ivShare.setOnClickListener(this);
        binding.ivFavourite.setOnClickListener(this);
        binding.expand.setOnClickListener(this);
        binding.tvLeaveComment.setOnClickListener(this);
        binding.tvViewAllRelatedArticles.setOnClickListener(this);
        binding.llViewAll.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.ivBack:
                if (isDeepLink)
                    ActivityController.startActivity(activity, Dashboard.class, true, true);
                else
                    finish();
                break;
            case R.id.tvLeaveComment:
                bundle.putString("ITEM_NAME", blogHeading);
                bundle.putString("IMAGE", blog_image);
                bundle.putString("ITEM_ID", blogId);
                bundle.putString(AppConstant.FROM, AppConstant.TYPE_BLOG);
                ActivityController.startActivity(context, AddReview.class, bundle);
                break;
            case R.id.ivShare:
                //   CommonUtils.shareData(context, binding.ivShareArticle, Html.fromHtml(blogHeading) + " \n" + blog_URL);
                break;
            case R.id.ivFavourite:
                if (!like) {
                    binding.ivFavourite.setImageResource(R.drawable.ic_fav);
                    addBlogToFavourite();
                } else {
                    binding.ivFavourite.setImageResource(R.drawable.favourite);
                    removeFavourite();
                }
                break;
            case R.id.tvViewAllRelatedArticles:
                intent = new Intent(context, ArticleList.class);
                intent.putExtra("BLOG_CATEGORY_ID", blogCategoryId);
                intent.putExtra("BLOG_CATEGORY_NAME", "Related Articles");
                context.startActivity(intent);
                break;
            case R.id.llViewAll:
                bundle.putString("BLOG_ID", blogId);
                bundle.putString("TITLE", "Comments & Rating");
                bundle.putString(AppConstant.FROM, AppConstant.TYPE_BLOG);
                ActivityController.startActivity(activity, AllReviews.class, bundle, false, false);
                break;
            default:
                break;
        }
    }

    private void removeFavourite() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.removeFavouriteBlog(SharedPref.getStringPreferences(context, AppConstant.USER_ID), blogId)
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        like = false;
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<BaseResponse> call, BaseResponse baseResponse) {

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showAlertPopup(context, context.getResources().getString(R.string.internet_title), context.getResources().getString(R.string.no_internet));

        }
    }

    // article comment section

    private void getArticleCommentsApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getBlogComments(1, blogId)
                        .enqueue(new BaseCallback<ReviewResponse>(context) {
                            @Override
                            public void onSuccess(ReviewResponse response) {
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {

                                        if (response.getReviewDataList() != null && !response.getReviewDataList().isEmpty()) {
                                            binding.llComments.setVisibility(View.VISIBLE);
                                            commentsDataList.clear();
                                            commentsDataList.addAll(response.getReviewDataList());
                                            if (response.getReviewDataList().size() > 5) {
                                                binding.llViewAll.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            binding.llComments.setVisibility(View.GONE);
                                            commentsDataList.clear();
                                        }
                                        setCommentsAdapter();
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<ReviewResponse> call, BaseResponse baseResponse) {
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showToastShort(context, getResources().getString(R.string.no_internet));
        }
    }

    private void setCommentsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvComments.setLayoutManager(layoutManager);
        binding.rvComments.hasFixedSize();
        binding.rvComments.setItemAnimator(new DefaultItemAnimator());
        commentAdapter = new ReviewAdapter(context, commentsDataList, "half");
        binding.rvComments.setAdapter(commentAdapter);
    }


    // article description section
    private void getArticleDescriptionApi() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();

        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getBlogDetail(blogId, SharedPref.getStringPreferences(context, AppConstant.USER_ID))
                        .enqueue(new BaseCallback<BlogDetailResponse>(context) {
                            @Override
                            public void onSuccess(BlogDetailResponse response) {
                                binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                binding.llData.setVisibility(View.VISIBLE);
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        BlogDetail blogDetail = response.getBlogDetail();
                                        blog_URL = blogDetail.getUrl();
                                        blogCategoryId = blogDetail.getCategoryId();
                                        setArticleDetail(blogDetail);
                                        if (blogDetail.getRelatedBlogDataList() != null && !blogDetail.getRelatedBlogDataList().isEmpty()) {
                                            binding.llHealthArticles.setVisibility(View.VISIBLE);
                                            blogDataList.clear();
                                            blogDataList.addAll(blogDetail.getRelatedBlogDataList());
                                        } else {
                                            binding.llHealthArticles.setVisibility(View.GONE);
                                            blogDataList.clear();
                                        }

                                        if (blogDetail.getIs_view_all() != null && !blogDetail.getIs_view_all().isEmpty()) {
                                            if (blogDetail.getIs_view_all().equals("1"))
                                                binding.tvViewAllRelatedArticles.setVisibility(View.VISIBLE);
                                        }

                                        setRelatedArticleAdapter();
                                    } else {
                                        blogDataList.clear();
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<BlogDetailResponse> call, BaseResponse baseResponse) {
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
        binding.llData.setVisibility(View.GONE);
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

    private void setRelatedArticleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvArticles.setLayoutManager(layoutManager);
        binding.rvArticles.hasFixedSize();
        binding.rvArticles.setItemAnimator(new DefaultItemAnimator());
        ArticleAdapter multipleViewAdapter = new ArticleAdapter(context, blogDataList);
        binding.rvArticles.setAdapter(multipleViewAdapter);
    }

    private void setArticleDetail(BlogDetail blogDetail) {
        if (blogDetail.getIsFavourite() != null && blogDetail.getIsFavourite().equals("1")) {
            like = true;
            binding.ivFavourite.setImageResource(R.drawable.ic_fav);
        } else {
            like = false;
            binding.ivFavourite.setImageResource(R.drawable.favourite);
        }
        if (blogDetail.getDate() != null && !blogDetail.getDate().isEmpty()) {
            String date = CommonUtils.formatDate(blogDetail.getDate(), "yyyy-MM-dd", "dd MMMM yyyy");
            binding.tvdate.setText(date);
        }
        if (blogDetail.getImage() != null && !blogDetail.getImage().isEmpty()) {
            blog_image = blogDetail.getImage();
            CommonUtils.setGlideImage(context, binding.ivShareArticle, AppConstant.BLOG_IMAGE_URL + blog_image, R.color.transparent);
            CommonUtils.setRoundImage(context, binding.ivArticle, AppConstant.BLOG_IMAGE_URL + blog_image);
        }
        blogHeading = blogDetail.getTitle();
        // binding.menuBar.tvTitle.setText(Html.fromHtml(blogHeading));
        binding.tvTitle.setText(Html.fromHtml(blogHeading));
        if (blogDetail.getDescription() != null && !blogDetail.getDescription().isEmpty()) {
            binding.tvDescription.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + blogDetail.getDescription(), "text/html", "UTF-8", null);
        }

        binding.tvPostedBy.setText(new StringBuffer("Posted By ").append(blogDetail.getBlogBy()));
        if (blogDetail.getRating() != null && !blogDetail.getRating().isEmpty()) {
            binding.ArticleRating.setVisibility(View.VISIBLE);
            binding.ArticleRating.setRating(Float.parseFloat(blogDetail.getRating()));
        }
    }

    private void addBlogToFavourite() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.addFavouriteBlog(SharedPref.getStringPreferences(context, AppConstant.USER_ID), blogId)
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        like = true;
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<BaseResponse> call, BaseResponse baseResponse) {

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showToastShort(context, getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isDeepLink)
            ActivityController.startActivity(activity, Dashboard.class, true, true);
        else
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getArticleCommentsApi();
    }
}

