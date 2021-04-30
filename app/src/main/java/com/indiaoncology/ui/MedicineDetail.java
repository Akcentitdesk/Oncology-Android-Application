package com.indiaoncology.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.indiaoncology.R;
import com.indiaoncology.adaptar.ProductImageAdapter;
import com.indiaoncology.databinding.ActivityDoctorProfileBinding;
import com.indiaoncology.databinding.ActivityMedicineDetailBinding;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.model.doctor.DoctorResponse;
import com.indiaoncology.model.medicine.MedicineResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.doctor.DoctorProfile;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineDetail extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private ArrayList<String> productImages = new ArrayList<>();
    private ActivityMedicineDetailBinding binding;
    boolean expand = false;
    private Activity activity;
    private String med_id, device_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medicine_detail);
        context = MedicineDetail.this;
        activity = MedicineDetail.this;
        PrefManager prefManager = PrefManager.getInstance(context);
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        getData();
        setToolbar();
        getMedicineDetail();
        saveDataForSearch();
        binding.swiperefresh.setOnRefreshListener(() -> getMedicineDetail());
    }

    private void getMedicineDetail() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getMedicine(med_id).enqueue(new BaseCallback<MedicineResponse>(context) {
                    @Override
                    public void onSuccess(MedicineResponse response) {
                        if (response != null) {
                            binding.swiperefresh.setRefreshing(false);
                            binding.llData.setVisibility(View.VISIBLE);
                            binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                            if (response.getStatus().equals("1")) {
                                binding.tvProductName.setText(response.getMedicineData().getName());
                                binding.tvProductDetails.setText(Html.fromHtml(response.getMedicineData().getDescription()));
                                binding.tvAmount.setText(response.getMedicineData().getAmount());
                                binding.tvMRP.setText(response.getMedicineData().getMrp());
                                binding.tvOff.setText(response.getMedicineData().getDiscount_percentage() + "% off");

                                if (response.getMedicineData().getImage() != null && !response.getMedicineData().getImage().isEmpty()) {
                                    productImages.clear();
                                    productImages.addAll(response.getMedicineData().getImage());
                                    setProductImages(productImages, response.getMedicineData().getName());
                                } else {
                                    binding.pager.setVisibility(View.GONE);
                                    binding.tabLayout.setVisibility(View.GONE);
                                    productImages.clear();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<MedicineResponse> call, BaseResponse baseResponse) {
                        binding.swiperefresh.setRefreshing(false);
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


    private void setProductImages(ArrayList<String> list, String productName) {
        ProductImageAdapter adapter = new ProductImageAdapter(this, list, productName);
        binding.pager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.pager);
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

    private void saveDataForSearch() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, Object> map = new HashMap<>();
                map.put("item_id", med_id);
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("device_token", device_token);
                map.put("type", "app");
                map.put("item_type", "medicine");
                api.saveSearchData(map).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        int count;
        switch (view.getId()) {
            case R.id.btnAdd:
                Intent intent = new Intent(context, Order.class);
                intent.putExtra("quantity", binding.tvCount.getText().toString());
                intent.putExtra("MEDICINE_ID", med_id);
                context.startActivity(intent);
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivRight:
                bundle.putString(AppConstant.FROM, AppConstant.MEDICINE);
                ActivityController.startActivity(activity, SearchActivity.class, bundle, false, false);
                break;
            case R.id.btnPlus:
                count = Integer.parseInt(String.valueOf(binding.tvCount.getText()));
                if (count <= 29)
                    count++;
                binding.tvCount.setText("" + count);
                break;
            case R.id.btnMinus:
                count = Integer.parseInt(String.valueOf(binding.tvCount.getText()));
                if (count == 1)
                    binding.tvCount.setText("1");
                else {
                    count -= 1;
                    binding.tvCount.setText("" + count);
                }
                break;
            case R.id.tvShowMore:
                if (!expand) {
                    expand = true;
                    binding.tvProductDetails.setMaxLines(Integer.MAX_VALUE);
                    binding.tvShowMore.setText(getResources().getString(R.string.show_less));
                } else {
                    binding.tvProductDetails.setMaxLines(3);
                    binding.tvShowMore.setText(getResources().getString(R.string.show_more));
                    expand = false;
                }
                break;
            case R.id.ivShare:

                break;
            default:
                break;
        }
    }

    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setText(getResources().getString(R.string.medicine_detail));
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.VISIBLE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivRight.setOnClickListener(this);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.tvShowMore.setOnClickListener(this);
        binding.btnPlus.setOnClickListener(this);
        binding.btnMinus.setOnClickListener(this);
        binding.ivShare.setOnClickListener(this);

    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("MEDICINE_ID") != null && !intent.getStringExtra("MEDICINE_ID").isEmpty()) {
                med_id = intent.getStringExtra("MEDICINE_ID");
            }
        }

        System.out.println("doctor Id : " + med_id);
    }
}