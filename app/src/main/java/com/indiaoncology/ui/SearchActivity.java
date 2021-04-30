package com.indiaoncology.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.indiaoncology.R;
import com.indiaoncology.adaptar.search.RecentSearchAdapter;
import com.indiaoncology.adaptar.search.SearchAdapter;
import com.indiaoncology.databinding.ActivitySearchBinding;
import com.indiaoncology.model.search.SearchingResponse;
import com.indiaoncology.model.search.SearchingResultData;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

import static com.indiaoncology.utils.CommonUtils.*;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private String from = "", device_token;
    private Activity activity;
    private ActivitySearchBinding binding;
    private List<SearchingResultData> data = new ArrayList<>();
    private List<SearchingResultData> searchDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        context = SearchActivity.this;
        activity = SearchActivity.this;
        getData();
        setData();
        requestFocus();
        if (from.equalsIgnoreCase(AppConstant.MEDICINE)) {
            binding.tvFrequentlySearchHeading.setText("Recently Searched Medicines");
            getRecentDocSearchApi("medicine");
        } else {
            binding.tvFrequentlySearchHeading.setText("Recently Searched Doctors");
            getRecentDocSearchApi("doctor");
        }

    }

    private void getData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty()) {
                from = bundle.getString(AppConstant.FROM);
            }
        }
    }


    private void requestFocus() {
        binding.etSearchBar.requestFocus();
        binding.etSearchBar.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(binding.etSearchBar, 0);
        }, 200);
    }

    private void setData() {
        PrefManager prefManager = PrefManager.getInstance(activity);
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        binding.ivCross.setOnClickListener(this);
        binding.ivClearSearchHistory.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.ivCross.setVisibility(View.VISIBLE);
                binding.ivSearch.setVisibility(View.GONE);
                if (charSequence.length() >= 1) {
                    setEmptyLayout(false, false);
                } else {
                    searchDataList.clear();
                    binding.ivCross.setVisibility(View.GONE);
                    binding.ivSearch.setVisibility(View.VISIBLE);
                    setEmptyLayout(true, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setEmptyLayout(boolean value, boolean no_search_history) {
        if (value) {
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
            binding.emptyLayout.ivImage.setVisibility(View.GONE);
            binding.emptyLayout.tvSubHeading.setVisibility(View.GONE);

            if (!no_search_history) {
                binding.emptyLayout.tvHeading.setVisibility(View.VISIBLE);
                binding.emptyLayout.tvHeading.setText("No Search Result Found!");
            } else {
                binding.emptyLayout.tvHeading.setVisibility(View.GONE);
            }
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.rvSearchItems.setVisibility(View.VISIBLE);
            //binding.llSearchHistoryData.setVisibility(View.GONE);

            if (from.equalsIgnoreCase(AppConstant.MEDICINE)) {
                getDoctorSearchApi("medicine");
            } else {
                getDoctorSearchApi("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivClearSearchHistory:
                binding.llSearchHistoryData.setVisibility(View.GONE);
                break;
            case R.id.ivCross:
                requestFocus();
                binding.etSearchBar.getText().clear();
                binding.ivSearch.setVisibility(View.VISIBLE);
                searchDataList.clear();
                if (from.equalsIgnoreCase(AppConstant.MEDICINE)) {
                    getRecentDocSearchApi("medicine");
                } else {
                    getRecentDocSearchApi("");
                }
                break;
            default:
                break;
        }
    }

    private void setSearchAdapter(String type) {
        LinearLayoutManager lm = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvSearchItems.setLayoutManager(lm);
        binding.rvSearchItems.setAdapter(new SearchAdapter(context, searchDataList, type));
    }

    private void setRecentSearchAdapter(String type) {
        LinearLayoutManager lm = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvSearchHistory.setLayoutManager(lm);
        binding.rvSearchHistory.setAdapter(new RecentSearchAdapter(context, data, type));
    }

    private void getDoctorSearchApi(String type) {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getSearchData(binding.etSearchBar.getText().toString(), type)
                        .enqueue(new BaseCallback<SearchingResponse>(context) {
                            @Override
                            public void onSuccess(SearchingResponse response) {
                                if (response != null) {
                                    if (response.getStatus().equalsIgnoreCase("1")) {
                                        if (response.getSearchingResultDataList() != null && !response.getSearchingResultDataList().isEmpty()) {
                                            searchDataList.clear();
                                            searchDataList.addAll(response.getSearchingResultDataList());
                                        } else {
                                            searchDataList.clear();
                                            setEmptyLayout(true, false);
                                        }
                                        setSearchAdapter(type);
                                    } else {
                                        searchDataList.clear();
                                        setEmptyLayout(true, false);
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<SearchingResponse> call, BaseResponse baseResponse) {
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showToastShort(context, getResources().getString(R.string.no_internet));
        }
    }


    private void getRecentDocSearchApi(String type) {
        if (isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getRecentSearchData(device_token, SharedPref.getStringPreferences(context, AppConstant.USER_ID),
                        "app", type).enqueue(new BaseCallback<SearchingResponse>(context) {
                    @Override
                    public void onSuccess(SearchingResponse response) {
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getSearchingResultDataList() != null && !response.getSearchingResultDataList().isEmpty()) {
                                    binding.llSearchHistoryData.setVisibility(View.VISIBLE);
                                    binding.emptyLayout.llRoot.setVisibility(View.GONE);
                                    data.clear();
                                    data.addAll(response.getSearchingResultDataList());
                                } else {
                                    data.clear();
                                    setEmptyLayout(true, true);
                                }
                                setRecentSearchAdapter(type);
                            } else {
                                setEmptyLayout(true, true);
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<SearchingResponse> call, BaseResponse baseResponse) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}