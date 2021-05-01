package com.indiaoncology.ui.review;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.indiaoncology.R;
import com.indiaoncology.databinding.DialogRatingBinding;
import com.indiaoncology.model.feedback.FeedbackResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.startAndDashboard.Dashboard;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReview extends AppCompatActivity implements View.OnClickListener {
    private DialogRatingBinding binding;
    private Context context;
    private Activity activity;
    private boolean isDeepLink = false;
    private String from = "", item_id, item_name, image, type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_rating);
        context = AddReview.this;
        activity = AddReview.this;
        getData();
        setData();
        setListeners();
    }

    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }
            if (bundle.getString("ITEM_ID") != null && !Objects.requireNonNull(bundle.getString("ITEM_ID")).isEmpty()) {
                item_id = bundle.getString("ITEM_ID");
            }
            if (bundle.getString("ITEM_NAME") != null && !Objects.requireNonNull(bundle.getString("ITEM_NAME")).isEmpty()) {
                item_name = bundle.getString("ITEM_NAME");
            }

            if (bundle.getString("IMAGE") != null && !Objects.requireNonNull(bundle.getString("IMAGE")).isEmpty()) {
                image = bundle.getString("IMAGE");
            }

            System.out.println("" + from + " " + item_id + " " + item_name + " " + image);

        }

        Uri uri = getIntent().getData();
        if (uri != null) {
            binding.llRootDialog.setVisibility(View.GONE);
            binding.btnShareExperience.setVisibility(View.GONE);
            binding.shimmerEffect.shimmerlayout.setVisibility(View.VISIBLE);
            binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
            String path_name = uri.getPath();
            String query_key_id_ = "";
            if (path_name != null) {
                isDeepLink = true;
                if (path_name.equalsIgnoreCase("/doctor-profile.php")) {
                    from = AppConstant.FROM_APPOINTMENT;
                    type = "DOCTOR";
                    query_key_id_ = "id";
                } else if (path_name.equalsIgnoreCase("/blog-details.php")) {
                    from = AppConstant.TYPE_BLOG;
                    type = "BLOG";
                    query_key_id_ = "id";
                }
            }

            List<String> params = uri.getQueryParameters(query_key_id_);
            if (params != null)
                item_id = params.get(0);
            getDetailsApi(item_id, type);
            System.out.println("Deep link data : " + type + " " /*+ item_name + " " + image + " "*/ + path_name + " " + query_key_id_);
        }
    }

    private void getDetailsApi(String id, String type) {
        if (CommonUtils.isOnline(Objects.requireNonNull(context))) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getProfileDetails(id, type)
                        .enqueue(new Callback<FeedbackResponse>() {
                            @Override
                            public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("1")) {
                                        binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                                        binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                                        binding.llRootDialog.setVisibility(View.VISIBLE);
                                        binding.btnShareExperience.setVisibility(View.VISIBLE);
                                        if (response.body().getFeedbackData() != null) {
                                            item_id = response.body().getFeedbackData().getId();
                                            item_name = response.body().getFeedbackData().getName();
                                            image = response.body().getFeedbackData().getImage();
                                            //  setData();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<FeedbackResponse> call, Throwable t) {

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
        binding.llRootDialog.setVisibility(View.GONE);
        binding.btnShareExperience.setVisibility(View.GONE);
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

    private void setData() {
        binding.menuBar.tvTitle.setVisibility(View.VISIBLE);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
        binding.menuBar.ivBack.setVisibility(View.VISIBLE);
        binding.menuBar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menuBar.ivBack.setOnClickListener(this);
        if (from.equalsIgnoreCase(AppConstant.FROM_APPOINTMENT)) {
            binding.menuBar.tvTitle.setText("Add Doctor Review");
            binding.tvTitle.setText("How was your experience with\n" + item_name + " ?");
            binding.etmessage.setHint("Add your experience...");
            binding.ivArticle.setVisibility(View.GONE);

            if (image != null && !image.isEmpty())
                CommonUtils.setGlideImage(context, binding.ivDoc, AppConstant.DOCTOR_URL + image, R.drawable.doc1);

        } else if (from.equalsIgnoreCase(AppConstant.TYPE_BLOG)) {
            binding.menuBar.ivRight.setVisibility(View.GONE);
            binding.menuBar.tvTitle.setText("Add Comment & Rating");
            binding.etmessage.setHint("Please write your comment here...");
            binding.tvTitle.setText(item_name);
            binding.ivDoc.setVisibility(View.GONE);
            binding.ivArticle.setVisibility(View.VISIBLE);
            if (image != null && !image.isEmpty())
                CommonUtils.setRoundImage(context, binding.ivArticle, AppConstant.BLOG_IMAGE_URL + image);
        }

    }


    private void setListeners() {
        binding.btnShareExperience.setOnClickListener(this);
        binding.etmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = binding.etmessage.length();
                String convert = String.valueOf(length);
                binding.maxWordLimit.setText(convert + "/450");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            binding.tvReviewError.setVisibility(View.GONE);
            if (rating == 1) {
                binding.tvRatingStatus.setText("Poor!");
            } else if (rating == 2) {
                binding.tvRatingStatus.setText("Fair!");
            } else if (rating == 3) {
                binding.tvRatingStatus.setText("Average!");
            } else if (rating == 4) {
                binding.tvRatingStatus.setText("Good!");
            } else if (rating == 5) {
                binding.tvRatingStatus.setText("Great!");
            } else {
                binding.tvRatingStatus.setText("");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShareExperience:
                if (from.equalsIgnoreCase(AppConstant.TYPE_BLOG)) {
                    if (binding.etmessage.getText().toString().isEmpty()) {
                        CommonUtils.setErrorMessage(binding.etmessage, Objects.requireNonNull(binding.tvReviewError), getResources().getString(R.string.empty_comment));

                    } else if (binding.etmessage.getText().toString().length() < 3) {
                        CommonUtils.setErrorMessage(binding.etmessage, Objects.requireNonNull(binding.tvReviewError), getResources().getString(R.string.invalid_comment));

                    } else {
                        addCommentApi();
                    }
                } else {
                    if (binding.ratingBar.getRating() == 0)
                        CommonUtils.setErrorMessage(binding.etmessage, Objects.requireNonNull(binding.tvReviewError), getResources().getString(R.string.empty_rating));
                    else
                        addExperience();
                }
                break;
            case R.id.ivBack:
                gotoNextScreen();
                break;
            default:
                break;
        }
    }

    private void addCommentApi() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.addComment(SharedPref.getStringPreferences(context, AppConstant.USER_ID),
                        item_id, String.valueOf(binding.ratingBar.getRating()), binding.etmessage.getText().toString())
                        .enqueue(new BaseCallback<BaseResponse>(context) {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        gotoNextScreen();
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

    private void addExperience() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.addDoctorReview(SharedPref.getStringPreferences(context, AppConstant.USER_ID), item_id,
                        binding.ratingBar.getRating(), binding.etmessage.getText().toString())
                        .enqueue(new Callback<BaseResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                                ProgressDialogUtils.dismiss();
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("1"))
                                        gotoNextScreen();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoNextScreen();
    }

    private void gotoNextScreen() {
        if (isDeepLink)
            ActivityController.startActivity(activity, Dashboard.class, true, true);
        else
            finish();
    }
}
