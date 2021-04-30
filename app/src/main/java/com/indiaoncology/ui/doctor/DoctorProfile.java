package com.indiaoncology.ui.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.indiaoncology.R;
import com.indiaoncology.adaptar.ReviewAdapter;
import com.indiaoncology.adaptar.doctor.DoctorLocationAdapter;
import com.indiaoncology.adaptar.doctor.FAQAdapter;
import com.indiaoncology.adaptar.schedule.SelectLocationAdapter;
import com.indiaoncology.databinding.ActivityDoctorProfileBinding;
import com.indiaoncology.databinding.BottomSheetChooseLocationBinding;
import com.indiaoncology.model.Document;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.model.doctor.DoctorResponse;
import com.indiaoncology.model.doctor.location.LocationDatum;
import com.indiaoncology.model.review.ReviewData;
import com.indiaoncology.model.review.ReviewResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.patient.SelectPatient;
import com.indiaoncology.ui.review.AddReview;
import com.indiaoncology.ui.review.AllReviews;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.PrefManager;
import com.indiaoncology.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfile extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private ActivityDoctorProfileBinding binding;
    List<Document> faqdata = new ArrayList<>();

    private String doctor_Id, doc_fees, doc_duration;
    private List<ReviewData> reviewDataList = new ArrayList<>();
    private List<LocationDatum> locationDatumList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    ArrayList<String> location_id_list = new ArrayList<>();
    ArrayList<String> location_name_list = new ArrayList<>();
    ArrayList<String> clinic_name_list = new ArrayList<>();
    ArrayList<String> clinic_fees_list = new ArrayList<>();
    private DoctorLocationAdapter doctorLocationAdapter;
    private FAQAdapter faqAdapter;
    private String doc_image, doc_name, location_id, location_name, device_token = "", doc_experience, doc_category;
    private Api api;
    private Activity activity;
    private BottomSheetDialog dialog;

    private List<com.indiaoncology.model.Document> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_profile);
        context = DoctorProfile.this;
        activity = DoctorProfile.this;
        // fetching device token from prefrence manager
        PrefManager prefManager = PrefManager.getInstance(context);
        device_token = prefManager.getPreference(AppConstant.DEVICE_TOKEN_);
        api = RequestController.createService(context, Api.class);
        getData();
        setToolbar();
        saveDataForSearch();
        getDoctorProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDoctorReviews();
    }

    private void saveDataForSearch() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                HashMap<String, Object> map = new HashMap<>();
                map.put("item_id", doctor_Id);
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("device_token", device_token);
                map.put("type", "app");
                map.put("item_type", "doctor");
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

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("DOCTOR_ID") != null && !intent.getStringExtra("DOCTOR_ID").isEmpty()) {
                doctor_Id = intent.getStringExtra("DOCTOR_ID");
            }
        }

        System.out.println("doctor Id : " + doctor_Id);
    }

    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvReview.setLayoutManager(layoutManager);
        binding.rvReview.hasFixedSize();
        binding.rvReview.setItemAnimator(new DefaultItemAnimator());
        reviewAdapter = new ReviewAdapter(context, reviewDataList, "half");
        binding.rvReview.setAdapter(reviewAdapter);
    }

    private void getDoctorReviews() {
        if (CommonUtils.isOnline(context)) {
            try {
                api.getDoctorReviews(1, doctor_Id).enqueue(new BaseCallback<ReviewResponse>(context) {
                    @Override
                    public void onSuccess(ReviewResponse response) {
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getReviewDataList() != null && !response.getReviewDataList().isEmpty()) {
                                    binding.llFeedback.setVisibility(View.VISIBLE);
                                    reviewDataList.clear();
                                    reviewDataList.addAll(response.getReviewDataList());
                                    if (response.getReviewDataList().size() > 3) {
                                        binding.llFeedbackOptions.setVisibility(View.VISIBLE);
                                        binding.btnBookAppointment.setVisibility(View.VISIBLE);
                                        binding.llOptions.setVisibility(View.GONE);
                                    } else {
                                        binding.llFeedbackOptions.setVisibility(View.GONE);
                                        binding.btnBookAppointment.setVisibility(View.GONE);
                                        binding.llOptions.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    binding.llFeedback.setVisibility(View.GONE);
                                    binding.btnBookAppointment.setVisibility(View.GONE);
                                    binding.llOptions.setVisibility(View.VISIBLE);
                                    reviewDataList.clear();
                                }
                                setAdapter();
                            } else {
                                binding.llFeedback.setVisibility(View.GONE);
                                binding.btnBookAppointment.setVisibility(View.GONE);
                                binding.llOptions.setVisibility(View.VISIBLE);
                                reviewDataList.clear();
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
        }
    }


    private void setToolbar() {
        CommonUtils.setToolBar(binding.menubar, getResources().getString(R.string.doctor_profile), activity);
        binding.btnBookAppointment.setOnClickListener(this);
        binding.btnBookAppointment2.setOnClickListener(this);
        binding.btnShareExperience.setOnClickListener(this);
        binding.btnShareExperience2.setOnClickListener(this);
        binding.btnViewAllReviews.setOnClickListener(this);
        binding.menubar.ivSecond.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnBookAppointment:
            case R.id.btnBookAppointment2:
                bundle.putString("Selected_Doctor_Id", doctor_Id);
                bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                ActivityController.startActivity(activity, SelectPatient.class, bundle, false, false);

               /* bundle = new Bundle();
                bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                Intent intent = new Intent(context, ScheduleDetail.class);
                intent.putExtra("location-id-array", location_id_list);
                intent.putExtra("location-name-array", location_name_list);
                intent.putExtra("clinic-name-array", clinic_name_list);
                intent.putExtra("clinic-fees-array", clinic_fees_list);
                intent.putExtra("DOCTOR_ID", doctor_Id);
                intent.putExtra("Doctor_Name", doc_name);
                intent.putExtra("Doctor_Category", doc_category);
                intent.putExtra("Doctor_Experience", doc_experience);
                intent.putExtra("Doctor_Profile", doc_image);
                intent.putExtras(bundle);
                startActivity(intent);*/

                break;
            case R.id.btnShareExperience:
            case R.id.btnShareExperience2:
                if (locationList.size() > 1)
                    showLocationSelectionDialog();
                else {
                    bundle.putString("ITEM_NAME", doc_name);
                    bundle.putString("IMAGE", doc_image);
                    bundle.putString("ITEM_ID", doctor_Id);
                    bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                    ActivityController.startActivity(context, AddReview.class, bundle);
                }
                break;
            case R.id.btnViewAllReviews:
                bundle.putString("DOCTOR_ID", doctor_Id);
                bundle.putString("TITLE", "Patient Review's");
                bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                ActivityController.startActivity(activity, AllReviews.class, bundle, false, false);
                break;
            default:
                break;
        }
    }

    private void showLocationSelectionDialog() {
        final BottomSheetChooseLocationBinding locationBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bottom_sheet_choose_location, null, false);
        dialog = DialogUtils.createBottomDialog(context, locationBinding.getRoot());
        dialog.setCancelable(true);
        locationBinding.tvClinicName.setText("Choose Location");
        setLocationSelectionAdapter(locationBinding.rvLocations);
    }

    private void setLocationSelectionAdapter(RecyclerView rvLocations) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rvLocations.setLayoutManager(linearLayoutManager);
        rvLocations.hasFixedSize();
        rvLocations.setItemAnimator(new DefaultItemAnimator());
        locationList.clear();
        locationList = PrepareDataMessage2(location_id_list, location_name_list, clinic_name_list, clinic_fees_list);
        SelectLocationAdapter adapter = new SelectLocationAdapter(activity, locationList, (view, position) -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("ITEM_NAME", doc_name);
            bundle.putString("IMAGE", doc_image);
            bundle.putString("ITEM_ID", doctor_Id);
            bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
            ActivityController.startActivity(context, AddReview.class, bundle);
        });
        rvLocations.setAdapter(adapter);
    }

    private List<com.indiaoncology.model.Document> PrepareDataMessage2(ArrayList<String> location_id_list, ArrayList<String> location_name_list, ArrayList<String> clinic_name_list, ArrayList<String> clinic_fees_list) {
        List<com.indiaoncology.model.Document> data = new ArrayList<>();
        for (int i = 0; i < location_id_list.size(); i++) {
            com.indiaoncology.model.Document document = new Document();
            document.setText(location_id_list.get(i));
            document.setText2(location_name_list.get(i));
            document.setText3(clinic_name_list.get(i));
            document.setText4(clinic_fees_list.get(i));
            data.add(document);
        }
        locationList.addAll(data);
        return data;
    }


    @Override
    public void onPause() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        super.onPause();
    }


    private void getDoctorProfile() {
        binding.shimmerEffect.shimmerViewContainer.startShimmerAnimation();
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getDoctorProfile(doctor_Id).enqueue(new BaseCallback<DoctorResponse>(context) {
                    @Override
                    public void onSuccess(DoctorResponse response) {
                        if (response != null) {
                            binding.llData.setVisibility(View.VISIBLE);
                            binding.shimmerEffect.shimmerViewContainer.stopShimmerAnimation();
                            binding.shimmerEffect.shimmerlayout.setVisibility(View.GONE);
                            if (response.getStatus().equals("1")) {
                                DoctorData doctorData = response.getDoctorData();
                                if (doctorData != null) {
                                    setProfileData(doctorData);
                                   /* if (doctorData.getLocation_data() != null && !doctorData.getLocation_data().isEmpty()) {
                                        for (LocationDatum d : doctorData.getLocation_data()) {
                                            if (d.getLocationId() != null && !d.getLocationId().contains("6")) {
                                                binding.llDocLocations.setVisibility(View.VISIBLE);
                                                locationDatumList.clear();
                                                locationDatumList.addAll(doctorData.getLocation_data());
                                                location_id = locationDatumList.get(0).getLocationId();
                                                location_name = locationDatumList.get(0).getAddress();
                                            }
                                        }
                                    } else {
                                        locationDatumList.clear();
                                    }
                                    setLocationAdapter();*/
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<DoctorResponse> call, BaseResponse baseResponse) {
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

    private void setLocationAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvDoctorLocations.setLayoutManager(layoutManager);
        binding.rvDoctorLocations.hasFixedSize();
        binding.rvDoctorLocations.setItemAnimator(new DefaultItemAnimator());
        doctorLocationAdapter = new DoctorLocationAdapter(context, locationDatumList);
        binding.rvDoctorLocations.setAdapter(doctorLocationAdapter);

        for (int i = 0; i < locationDatumList.size(); i++) {
            location_id_list.add(locationDatumList.get(i).getLocationId());
            location_name_list.add(locationDatumList.get(i).getAddress());
            clinic_name_list.add(locationDatumList.get(i).getClinicName());
            clinic_fees_list.add(locationDatumList.get(i).getFees());
        }
    }


    private void setProfileData(DoctorData doctorData) {
        doc_category = doctorData.getDoctorCategoryName();
        binding.tvCategory.setText(doctorData.getDoctorCategoryName());
        doc_name = doctorData.getDoctorName();
        binding.tvDoctorName.setText(doc_name);
        doc_experience = doctorData.getExperience();
        binding.tvQualification.setText(doctorData.getQualification());
        binding.tvExperience.setText("Experience : " + doctorData.getExperience());
        if (doctorData.getRating() != null && !doctorData.getRating().isEmpty() && !doctorData.getRating().equals("0.00")) {
            binding.tvRating.setRating(Float.parseFloat(doctorData.getRating()));
        }
        if (doctorData.getDoctorReviews() != null && !doctorData.getDoctorReviews().isEmpty() && !doctorData.getDoctorReviews().equals("0.00")) {
            binding.tvRatingReviewCount.setText(new StringBuilder(doctorData.getDoctorReviews()).append(" review(s) found."));
        } else {
            binding.tvRatingReviewCount.setVisibility(View.GONE);
            binding.ivReview.setVisibility(View.GONE);
        }

        doc_image = doctorData.getImage();
        if (doc_image != null && !doc_image.isEmpty()) {
            CommonUtils.setGlideImage(context, binding.ivDoc, AppConstant.DOCTOR_URL + doc_image, R.color.transparent);
        }
        if (doctorData.getAwards() != null && !doctorData.getAwards().isEmpty()) {
            binding.llAwards.setVisibility(View.VISIBLE);
            binding.tvAwards.setText(Html.fromHtml(doctorData.getAwards()));
        }
        if (doctorData.getOrgan_specialization() != null && !doctorData.getOrgan_specialization().isEmpty()) {
            binding.llServices.setVisibility(View.VISIBLE);
            binding.tvService.setText(doctorData.getOrgan_specialization().toString().replace("[", " -  ").replace("]", "").replace(",", "\n - "));
        }

        if (doctorData.getFullExperience() != null && !doctorData.getFullExperience().isEmpty()) {
            binding.llExperience.setVisibility(View.VISIBLE);
            binding.tvExperienceFull.setText(Html.fromHtml(doctorData.getFullExperience()));
        }
        if (doctorData.getSpecializations() != null && !doctorData.getSpecializations().isEmpty()) {
            binding.llSpecialization.setVisibility(View.VISIBLE);
            binding.tvSpecialization.setText(doctorData.getSpecializations().toString()
                    .replace("[", " -  ").replace("]", "").replace(",", "\n - "));
        }

        if (doctorData.getRegistration() != null && !doctorData.getRegistration().isEmpty()) {
            binding.llRegistration.setVisibility(View.VISIBLE);
            binding.tvRegistration.setText(Html.fromHtml(doctorData.getRegistration()));
        }
        if (doctorData.getEducation() != null && !doctorData.getEducation().isEmpty()) {
            binding.llEducation.setVisibility(View.VISIBLE);
            binding.tvQualificationFull.setText(Html.fromHtml(doctorData.getEducation()));
        }
        if (doctorData.getAbout() != null && !doctorData.getAbout().isEmpty()) {
            binding.llAbout.setVisibility(View.VISIBLE);
            binding.about.setText("More about " + doc_name);
            binding.tvDescription.setText(Html.fromHtml(doctorData.getAbout()).toString());
        }


        setFaqAdaptar();
    }

    private void setFaqAdaptar() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvfaq.setLayoutManager(layoutManager);
        String heading[] = {"Where can I meet " + doc_name + " ?",
                "How can I get an appointment with " + doc_name + " ?",
                "How much does " + doc_name + " charge for consultation?"};
        String sub_heading[] = {"You can call " + SharedPref.getStringPreferences(context, AppConstant.COMPANY_MOBILE_NUMBER) + " for this service.",
                "You can book a appointment with" + doc_name + " through us. Request a call back or call us on " + SharedPref.getStringPreferences(context, AppConstant.COMPANY_MOBILE_NUMBER) + " .",
                "To know more accurate consultation fees, please get in touch with us."};
        faqdata = PrepareDataMessage(heading, sub_heading);
        faqAdapter = new FAQAdapter(context, faqdata);
        binding.rvfaq.setAdapter(faqAdapter);
    }

    private List<Document> PrepareDataMessage(String[] heading, String[] sub_heading) {
        List<Document> data = new ArrayList<>();
        for (int i = 0; i < heading.length; i++) {
            Document document = new Document();
            document.setText(heading[i]);
            document.setText2(sub_heading[i]);
            data.add(document);
        }
        return data;
    }
}
