package com.indiaoncology.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ActivityMedicineDetailBinding;
import com.indiaoncology.databinding.ActivityOrderBinding;
import com.indiaoncology.databinding.PopupAddReminderBinding;
import com.indiaoncology.model.address.CheckPincodeBaseResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.RegexUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;

public class Order extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ActivityOrderBinding binding;
    private String med_id, quantity, pincode, address, landmark, city, state, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        context = Order.this;
        activity = Order.this;
        getData();
        setToolbar();
        setViews();
    }

    private void setViews() {
        binding.etCity.setEnabled(false);
        binding.etstate.setEnabled(false);
        binding.etPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvPincodeError.setVisibility(View.INVISIBLE);
                if (s.length() == 6) {
                    checkPincode();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvMobileError.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvAddress1Error.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etAddress2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvAddress2Error.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.tvTitle.setText(getResources().getString(R.string.place_order));
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("MEDICINE_ID") != null && !intent.getStringExtra("MEDICINE_ID").isEmpty()) {
                med_id = intent.getStringExtra("MEDICINE_ID");
            }
            if (intent.getStringExtra("quantity") != null && !intent.getStringExtra("quantity").isEmpty()) {
                quantity = intent.getStringExtra("quantity");
            }
        }
    }

    private void checkPincode() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.checkPincode(Objects.requireNonNull(binding.etPincode.getText()).toString()).enqueue(new BaseCallback<CheckPincodeBaseResponse>(context) {
                    @Override
                    public void onSuccess(CheckPincodeBaseResponse response) {
                        ProgressDialogUtils.dismiss();
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                binding.etstate.setText(response.getData().getState());
                                binding.etCity.setText(response.getData().getCity());
                            } else {
                                Objects.requireNonNull(binding.etstate.getText()).clear();
                                Objects.requireNonNull(binding.etCity.getText()).clear();
                                CommonUtils.setErrorMessage(binding.etPincode, binding.tvPincodeError, response.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<CheckPincodeBaseResponse> call, BaseResponse baseResponse) {
                        ProgressDialogUtils.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtils.showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnSubmit:
                if (isValid()) {
                    submitData();
                }
                break;
            default:
                break;
        }
    }

    private void submitData() {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("address_line1", binding.etAddress1.getText().toString());
                map.put("address_line2", binding.etAddress2.getText().toString());
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("product_id", med_id);
                map.put("quantity", quantity);
                map.put("pincode", binding.etPincode.getText().toString());
                map.put("state", binding.etstate.getText().toString());
                map.put("city", binding.etCity.getText().toString());
                map.put("mobile", binding.etMobile.getText().toString());
                api.order(map).enqueue(new BaseCallback<BaseResponse>(context) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ProgressDialogUtils.dismiss();
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                openPopup("Order Placed", "Thankyou for placing your order with us. One of our care mangaer will call you shortly to process your order further.");
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
            CommonUtils.showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));
        }
    }

    private void openPopup(String heading, String sub_heading) {
        final PopupAddReminderBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_add_reminder, null, false);
        Dialog dialogSubmit = DialogUtils.createDialog(context, dataBinding.getRoot(), 0);
        dialogSubmit.setCancelable(false);
        dataBinding.subHeading.setText(sub_heading);
        dataBinding.heading.setText(heading);
        dataBinding.btnOk.setOnClickListener(v -> {
            dialogSubmit.dismiss();
            ActivityController.startActivity(activity, Dashboard.class, true);
        });
    }

    private boolean isValid() {
        pincode = Objects.requireNonNull(binding.etPincode.getText()).toString().trim();
        city = Objects.requireNonNull(binding.etCity.getText()).toString().trim();
        state = Objects.requireNonNull(binding.etstate.getText()).toString().trim();
        mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
        address = Objects.requireNonNull(binding.etAddress1.getText()).toString().trim();
        landmark = Objects.requireNonNull(binding.etAddress2.getText()).toString().trim();
        if (pincode.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etPincode, binding.tvPincodeError, getResources().getString(R.string.empty_pincode));
        } else if (pincode.length() < 6) {
            CommonUtils.setErrorMessage(binding.etPincode, binding.tvPincodeError, getResources().getString(R.string.invalid_pincode));
        } else if (address.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etAddress1, binding.tvAddress1Error, getResources().getString(R.string.empty_address));
        } else if (address.length() < 2) {
            CommonUtils.setErrorMessage(binding.etAddress1, binding.tvAddress1Error, getResources().getString(R.string.invalid_address));
        } else if (landmark.isEmpty()) {
            CommonUtils.setErrorMessage(binding.etAddress2, binding.tvAddress2Error, getResources().getString(R.string.empty_landmark));
        } else if (landmark.length() < 2) {
            CommonUtils.setErrorMessage(binding.etAddress2, binding.tvAddress2Error, getResources().getString(R.string.invalid_landmark));
        } else if (!RegexUtils.isValidMobileNumber(mobile)) {
            CommonUtils.setErrorMessage(binding.etMobile, binding.tvMobileError, getResources().getString(R.string.invalid_mobile));
        } else {
            return true;
        }
        return false;
    }
}