package com.indiaoncology.adaptar.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowDoctorsBinding;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.ui.doctor.DoctorProfile;
import com.indiaoncology.ui.doctor.ScheduleDetail;
import com.indiaoncology.ui.patient.SelectPatient;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class FindDoctorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private int mainPosition = 0;
    private boolean like = false;
    private List<DoctorData> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowDoctorsBinding binding;
    ArrayList<String> location_id_list = new ArrayList<>();
    ArrayList<String> location_name_list = new ArrayList<>();
    ArrayList<String> clinic_name_list = new ArrayList<>();
    ArrayList<String> clinic_fees_list = new ArrayList<>();

    public FindDoctorAdapter(Activity context, List<DoctorData> list) {
        this.context = context;
        this.dataList = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_doctors, parent, false);
                viewHolder = new FindDoctorAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new FindDoctorAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final DoctorData model = dataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {
                    if (model.getImage() != null && !model.getImage().isEmpty()) {
                        CommonUtils.setGlideImage(context, binding.ivDoc, AppConstant.DOCTOR_URL + model.getImage(), R.color.transparent);

                    }
                    binding.tvCategory.setText(model.getDoctorCategoryName());
                    binding.tvDoctorName.setText(model.getDoctorName());
                    binding.tvExperience.setText(model.getExperience() + " Experience");
                    if (model.getRating() != null && !model.getRating().isEmpty() && !model.getRating().equals("0.00")) {
                        binding.llRating.setVisibility(View.VISIBLE);
                        binding.ratingBar.setRating(Float.parseFloat(model.getRating()));
                        if (model.getDoctorReviews() != null && !model.getDoctorReviews().isEmpty())
                            binding.tvRatingReviewCount.setText(new StringBuilder(model.getDoctorReviews()).append(" review(s) found."));

                    }

                   /* if (model.getLocation_data().get(0).getAddress() != null && !model.getLocation_data().get(0).getAddress().isEmpty())
                        binding.tvAddress.setText(model.getLocation_data().get(0).getAddress());
                    else {
                        binding.tvAddress.setText(" ");
                        binding.ivaddress.setVisibility(View.GONE);
                        binding.tvAddress.setVisibility(View.GONE);
                    }*/


                    binding.btnBookAppointment.setOnClickListener(view -> {

                      /*  mainPosition = position;
                        for (int i = 0; i < dataList.get(mainPosition).getLocation_data().size(); i++) {
                            location_id_list.add(dataList.get(mainPosition).getLocation_data().get(i).getLocationId());
                            location_name_list.add(dataList.get(mainPosition).getLocation_data().get(i).getAddress());
                            clinic_name_list.add(dataList.get(mainPosition).getLocation_data().get(i).getClinicName());
                            clinic_fees_list.add(dataList.get(mainPosition).getLocation_data().get(i).getFees());
                        }

                        Intent intent = new Intent(context, ScheduleDetail.class);
                        intent.putExtra("location-id-array", location_id_list);
                        intent.putExtra("location-name-array", location_name_list);
                        intent.putExtra("clinic-name-array", clinic_name_list);
                        intent.putExtra("DOCTOR_ID", model.getDoctorId());
                        intent.putExtra("Doctor_Name", model.getDoctorName());
                        intent.putExtra("clinic-fees-array", clinic_fees_list);
                        intent.putExtra("Doctor_Profile", model.getImage());
                        intent.putExtra("Doctor_Category", model.getDoctorCategoryName());
                        intent.putExtra("Doctor_Experience", model.getExperience());
                        context.startActivity(intent);*/

                        Bundle bundle = new Bundle();
                        bundle.putString("Selected_Doctor_Id", model.getDoctorId());
                        bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                        ActivityController.startActivity(context, SelectPatient.class, bundle, false, false);


                    });

                    binding.btnViewProfile.setOnClickListener(view -> {
                        Intent intent = new Intent(context, DoctorProfile.class);
                        intent.putExtra("DOCTOR_ID", model.getDoctorId());
                        context.startActivity(intent);
                    });
                }
                break;
            case LOADING:
                progressBinding.loadmoreProgress.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public void add(DoctorData mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void remove(DoctorData membersData) {
        int position = dataList.indexOf(membersData);
        if (position > -1) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void setList(List<DoctorData> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<DoctorData> newList) {
        int lastIndex = dataList.size() - 1;
        dataList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }

    public DoctorData getItem(int position) {
        return dataList.get(position);
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBinding = DataBindingUtil.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new DoctorData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = dataList.size() - 1;
        DoctorData item = getItem(position);
        if (item != null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

}
