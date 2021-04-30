package com.indiaoncology.adaptar.doctor;

import android.app.Activity;
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
import com.indiaoncology.databinding.RowMyAppointmentBinding;
import com.indiaoncology.listener.OnCheckSelectedListener;
import com.indiaoncology.model.myAppointment.AppointmentData;
import com.indiaoncology.ui.doctor.AppointmentDetails;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;


public class MyAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<AppointmentData> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowMyAppointmentBinding binding;
    private OnCheckSelectedListener listener;

    public MyAppointmentAdapter(Activity context, List<AppointmentData> list, OnCheckSelectedListener listener) {
        this.context = context;
        this.dataList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_my_appointment, parent, false);
                viewHolder = new MyAppointmentAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new MyAppointmentAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final AppointmentData model = dataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {
                    if (model.getDoctorProfile() != null && !model.getDoctorProfile().isEmpty())
                        CommonUtils.setGlideImage(context, binding.ivProfile, AppConstant.DOCTOR_URL + model.getDoctorProfile(), R.color.transparent);

                    binding.tvStatus.setText(model.getAppointment_status());
                    if (model.getIs_cancelled() != null && !model.getIs_cancelled().isEmpty() && model.getIs_cancelled().equals("1")) {
                        binding.llCancelAppointment.setVisibility(View.GONE);
                        if (model.getCancelled_date() != null & !model.getCancelled_date().isEmpty()) {
                            binding.tvStatusDate.setVisibility(View.VISIBLE);
                            String date = CommonUtils.formatDate(model.getCancelled_date(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
                            binding.tvStatusDate.setText("On : " +date);
                        }
                    } else {
                        binding.llCancelAppointment.setVisibility(View.VISIBLE);
                        binding.tvStatusDate.setVisibility(View.GONE);
                    }


                    if (model.getAppointment_status().equalsIgnoreCase("Cancelled")) {
                        binding.tvStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
                        binding.llCancelAppointment.setVisibility(View.GONE);
                        if (model.getCancelled_date() != null & !model.getCancelled_date().isEmpty()) {
                            binding.tvStatusDate.setVisibility(View.VISIBLE);
                            String date = CommonUtils.formatDate(model.getCancelled_date(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
                            binding.tvStatusDate.setText(date);
                        }

                    } else if (model.getAppointment_status().equalsIgnoreCase("Rejected")) {
                        binding.tvStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
                        binding.llCancelAppointment.setVisibility(View.GONE);
                        if (model.getComment() != null && !model.getComment().isEmpty()) {
                            binding.tvStatusReason.setVisibility(View.VISIBLE);
                            binding.tvStatusReason.setText(model.getComment());
                        }

                        if (model.getCancelled_date() != null && !model.getCancelled_date().isEmpty()) {
                            binding.tvStatusDate.setVisibility(View.VISIBLE);
                            String date = CommonUtils.formatDate(model.getCancelled_date(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
                            binding.tvStatusDate.setText("On : " + date);
                        }
                    } else if (model.getAppointment_status().equalsIgnoreCase("Attended")) {
                        binding.llCancelAppointment.setVisibility(View.GONE);
                        binding.vieww.setVisibility(View.GONE);
                        binding.tvStatusReason.setVisibility(View.GONE);
                        binding.tvStatusDate.setVisibility(View.GONE);
                        binding.tvStatus.setTextColor(context.getResources().getColor(R.color.dark_green));
                    } else if (model.getAppointment_status().equalsIgnoreCase("Confirmed")) {
                        binding.llCancelAppointment.setVisibility(View.VISIBLE);
                        binding.tvStatusReason.setVisibility(View.GONE);
                        binding.tvStatusDate.setVisibility(View.GONE);
                        binding.tvStatus.setTextColor(context.getResources().getColor(R.color.dark_green));
                    } else {
                        binding.tvStatus.setTextColor(context.getResources().getColor(R.color.colorYellow));
                    }


                    binding.tvName.setText(model.getDoctorName());
                    binding.tvAppointmentId.setText(new StringBuilder("Appointment Id : ").append(model.getAppointment_id()));
                    String date = CommonUtils.formatDate(model.getAppointment_date(), "yyyy-MM-dd", "dd MMM yyyy");
                    binding.tvDate.setText(date);
                    binding.tvTime.setText(model.getAppointment_time());
                    binding.tvPlace.setText(model.getDoctorLocation());
                    binding.llRoot.setOnClickListener(view -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("APPOINTMENT_ID", model.getAppointment_id());
                        bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
                        ActivityController.startActivity(context, AppointmentDetails.class, bundle, false, false);
                    });

                    binding.tvCancel.setOnClickListener(view -> listener.onClick(view, position, true));
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

    public void add(AppointmentData mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void remove(AppointmentData membersData) {
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

    public void setList(List<AppointmentData> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<AppointmentData> newList) {
        int lastIndex = dataList.size() - 1;
        dataList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }

    public AppointmentData getItem(int position) {
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
        add(new AppointmentData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = dataList.size() - 1;
        AppointmentData item = getItem(position);
        if (item != null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }
}

