package com.indiaoncology.ui.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.indiaoncology.R;
import com.indiaoncology.adaptar.schedule.DateAdapter;
import com.indiaoncology.adaptar.schedule.SelectLocationAdapter;
import com.indiaoncology.adaptar.schedule.ShiftAdapter;
import com.indiaoncology.databinding.ActivityScheduleDetailBinding;
import com.indiaoncology.databinding.BottomSheetChooseLocationBinding;
import com.indiaoncology.model.Document;
import com.indiaoncology.model.doctor.location.LocationResponse;
import com.indiaoncology.model.doctor.location.LocationResponseData;
import com.indiaoncology.model.doctor.location.Slot;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class ScheduleDetail extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private BottomSheetDialog dialog;
    private int mainPosition = 0;
    private ActivityScheduleDetailBinding binding;
    private List<Document> locationList = new ArrayList<>();
    private ShiftAdapter timeSlotAdapter;
    private DateAdapter dateAdapter;
    private String Doctor_Profile, Doctor_Category, Doctor_Experience, Doctor_Name, Doctor_Id, Location_Id, doc_fees, from = "", selectedDate, selectedDay, Address_ID, orderId, SELECTED_DATE;
    private List<Slot> timeSlotDataList = new ArrayList<>();
    private List<LocationResponseData> dateDataList = new ArrayList<>();
    private List<LocationResponseData> dateListLab = new ArrayList<>();
    ArrayList<String> location_id_list = new ArrayList<>();
    ArrayList<String> location_name_list = new ArrayList<>();
    ArrayList<String> clinic_name_list = new ArrayList<>();
    ArrayList<String> clinic_fees_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_detail);
        context = ScheduleDetail.this;
        activity = ScheduleDetail.this;
        getData();
        setdata();
        setToolbar();
    }

    private void setdata() {

        if (Doctor_Profile != null && !Doctor_Profile.isEmpty())
            CommonUtils.setGlideImage(context, binding.ivDoctor, AppConstant.DOCTOR_URL + Doctor_Profile, R.drawable.doc1);
        binding.tvDoctorName.setText(Doctor_Name);
        binding.tvDocData.setText(Doctor_Experience + " experience | " + Doctor_Category);
        if (location_id_list.size() > 0)
            Location_Id = location_id_list.get(0);
        if (clinic_fees_list.size() > 0)
            doc_fees = clinic_fees_list.get(0);
        if (location_name_list.size() > 0)
            binding.tvLocationName.setText(location_name_list.get(0));
        if (doc_fees != null && !doc_fees.isEmpty() && !doc_fees.equals(""))
            binding.tvClinicFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs)).append(" ").append(doc_fees));

        if (location_id_list.size() > 1) {
            binding.dropdown.setVisibility(View.VISIBLE);
            binding.dropdown.setOnClickListener(this);
        }
        getDateTimeSlot(Location_Id);

    }

    private void setDateAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvDate.setLayoutManager(layoutManager);
        binding.rvDate.hasFixedSize();
        if (dateDataList.size() > 0) {
            dateDataList.get(mainPosition).setSelected(true);
            timeSlotDataList.clear();
            timeSlotDataList.addAll(dateDataList.get(mainPosition).getSlots());
            setTimeAdapter(mainPosition, timeSlotDataList);
        }
        dateAdapter = new DateAdapter(activity, dateDataList, (view, position, selected) -> {
            mainPosition = position;
            for (int i = 0; i < dateDataList.size(); i++) {
                dateDataList.get(i).setSelected(false);
            }
            dateDataList.get(position).setSelected(true);
            dateAdapter.notifyDataSetChanged();
            timeSlotDataList.clear();
            timeSlotDataList.addAll(dateDataList.get(position).getSlots());
            setTimeAdapter(position, timeSlotDataList);

        }, "dynamic");
        binding.rvDate.setAdapter(dateAdapter);
        dateAdapter.notifyDataSetChanged();
    }


    private void getData() {
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppConstant.FROM) != null && !Objects.requireNonNull(bundle.getString(AppConstant.FROM)).isEmpty())
                from = bundle.getString(AppConstant.FROM);

            if (bundle.getString("SELECTED_DATE") != null && !Objects.requireNonNull(bundle.getString("SELECTED_DATE")).isEmpty())
                SELECTED_DATE = bundle.getString("SELECTED_DATE");
        }
        if (intent.getStringExtra("Doctor_Profile") != null && !intent.getStringExtra("Doctor_Profile").isEmpty())
            Doctor_Profile = intent.getStringExtra("Doctor_Profile");

        if (intent.getStringExtra("Doctor_Category") != null && !intent.getStringExtra("Doctor_Category").isEmpty())
            Doctor_Category = intent.getStringExtra("Doctor_Category");
        if (intent.getStringExtra("Doctor_Experience") != null && !intent.getStringExtra("Doctor_Experience").isEmpty())
            Doctor_Experience = intent.getStringExtra("Doctor_Experience");

        if (intent.getStringExtra("Doctor_Name") != null && !intent.getStringExtra("Doctor_Name").isEmpty())
            Doctor_Name = intent.getStringExtra("Doctor_Name");

        if (intent.getStringExtra("DOCTOR_ID") != null && !intent.getStringExtra("DOCTOR_ID").isEmpty())
            Doctor_Id = intent.getStringExtra("DOCTOR_ID");
        if (intent.getStringExtra("Doctor_Profile") != null && !intent.getStringExtra("Doctor_Profile").isEmpty())
            Doctor_Profile = intent.getStringExtra("Doctor_Profile");

        if (intent.getSerializableExtra("location-id-array") != null)
            location_id_list = (ArrayList<String>) getIntent().getSerializableExtra("location-id-array");

        if (intent.getSerializableExtra("location-name-array") != null)
            location_name_list = (ArrayList<String>) getIntent().getSerializableExtra("location-name-array");

        if (intent.getSerializableExtra("clinic-name-array") != null)
            clinic_name_list = (ArrayList<String>) getIntent().getSerializableExtra("clinic-name-array");

        if (intent.getSerializableExtra("clinic-fees-array") != null)
            clinic_fees_list = (ArrayList<String>) getIntent().getSerializableExtra("clinic-fees-array");

        System.out.println(" id :" + location_id_list.toString());
        System.out.println(" address :" + location_name_list.toString());
        System.out.println(" clinic name :" + clinic_name_list.toString());
        System.out.println(" clinic fees :" + clinic_fees_list.toString());
    }

    private void setToolbar() {
        binding.menubar.tvTitle.setVisibility(View.VISIBLE);
        binding.menubar.ivBack.setVisibility(View.VISIBLE);
        binding.menubar.ivRight.setVisibility(View.GONE);
        binding.menubar.ivSecond.setVisibility(View.GONE);
        binding.menubar.tvTitle.setText(R.string.select_slot);
        binding.menubar.ivBack.setImageResource(R.drawable.ic_back);
        binding.menubar.ivBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
    }

    private void setTimeAdapter(int position, List<Slot> timeSlotDataList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        binding.rvTimeSlot.setLayoutManager(linearLayoutManager);
        binding.rvTimeSlot.hasFixedSize();
        binding.rvTimeSlot.setItemAnimator(new DefaultItemAnimator());
        timeSlotAdapter = new ShiftAdapter(activity, timeSlotDataList);
        binding.rvTimeSlot.setAdapter(timeSlotAdapter);
        selectedDate = dateDataList.get(position).getDate();
        selectedDay = dateDataList.get(position).getDay();
        timeSlotAdapter.fetchData(Doctor_Id, selectedDay, selectedDate, Location_Id, doc_fees);
        System.out.println("date :  " + selectedDate + "  day : " + selectedDay + " location id :" + Location_Id + " Doctor fees :" + doc_fees);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.dropdown:
                showBottomSheetDialog();
                break;

            default:
                break;
        }
    }


    public void showBottomSheetDialog() {
        final BottomSheetChooseLocationBinding locationBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.bottom_sheet_choose_location, null, false);
        dialog = DialogUtils.createBottomDialog(context, locationBinding.getRoot());
        dialog.setCancelable(true);
        setLocationAdapter(locationBinding.rvLocations);
    }

    private void setLocationAdapter(RecyclerView rvLocations) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rvLocations.setLayoutManager(linearLayoutManager);
        rvLocations.hasFixedSize();
        rvLocations.setItemAnimator(new DefaultItemAnimator());
        locationList.clear();
        locationList = PrepareDataMessage2(location_id_list,
                location_name_list, clinic_name_list, clinic_fees_list);
        SelectLocationAdapter adapter = new SelectLocationAdapter(activity, locationList, (view, position) -> {
            Location_Id = locationList.get(position).getText();
            doc_fees = locationList.get(position).getText4();
            binding.tvLocationName.setText(locationList.get(position).getText2());
            binding.tvClinicFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs))
                    .append(" ").append(doc_fees));
            dialog.dismiss();
            getDateTimeSlot(Location_Id);
        });
        rvLocations.setAdapter(adapter);
    }

    private List<Document> PrepareDataMessage2(ArrayList<String> location_id_list, ArrayList<String> location_name_list, ArrayList<String> clinic_name_list, ArrayList<String> clinic_fees_list) {
        List<Document> data = new ArrayList<>();
        for (int i = 0; i < location_id_list.size(); i++) {
            Document document = new Document();
            document.setText(location_id_list.get(i));
            document.setText2(location_name_list.get(i));
            document.setText3(clinic_name_list.get(i));
            document.setText4(clinic_fees_list.get(i));
            data.add(document);
        }
        locationList.addAll(data);
        return data;
    }

    private void getDateTimeSlot(String LOCATION_ID) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                Api api = RequestController.createService(context, Api.class);
                api.getTimeSlot(SharedPref.getStringPreferences(context, AppConstant.USER_ID), Doctor_Id, LOCATION_ID)
                        .enqueue(new BaseCallback<LocationResponse>(context) {
                            @Override
                            public void onSuccess(LocationResponse response) {
                                ProgressDialogUtils.dismiss();
                                if (response != null) {
                                    if (response.getStatus().equals("1")) {
                                        if (response.getData() != null && !response.getData().isEmpty()) {
                                            setEmptyLayout(false);
                                            dateDataList.clear();
                                            dateDataList.addAll(response.getData());
                                            setDateAdapter();
                                        } else {
                                            setEmptyLayout(true);
                                            dateDataList.clear();
                                        }
                                    } else {
                                        setEmptyLayout(true);
                                        dateDataList.clear();
                                    }
                                }
                            }

                            @Override
                            public void onFail(Call<LocationResponse> call, BaseResponse baseResponse) {
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

    // visible when user have no reminders set
    private void setEmptyLayout(boolean value) {
        if (value) {
            binding.rvDate.setVisibility(View.GONE);
            binding.emptyLayout.llRoot.setVisibility(View.VISIBLE);
            binding.emptyLayout.ivImage.setVisibility(View.GONE);
            binding.emptyLayout.btnSubmit.setVisibility(View.GONE);
            binding.emptyLayout.tvHeading.setText("No Slot Available!");
            binding.emptyLayout.tvSubHeading.setVisibility(View.VISIBLE);
            binding.emptyLayout.tvSubHeading.setText("Doctor not currently available.");
        } else {
            binding.emptyLayout.llRoot.setVisibility(View.GONE);
            binding.rvDate.setVisibility(View.VISIBLE);
            binding.llTimeSlot.setVisibility(View.VISIBLE);
        }
    }

    private static List<LocalDate> getDates(Date dateString1, Date dateString2) {
        ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
        // DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate start = null;
        LocalDate end = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start = toLocalDate(dateString1);
            end = toLocalDate(dateString2);
        }
        while (!start.equals(end)) {
            dates.add(start);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                start = start.plusDays(1);
            }
        }
        return dates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate toLocalDate(Date date) {
        Date lDate = new Date(date.getTime());
        return lDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


}

