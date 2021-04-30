package com.indiaoncology.adaptar.doctor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowTopDoctorsBinding;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.ui.doctor.DoctorProfile;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;
import java.util.Random;

public class DoctorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<DoctorData> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowTopDoctorsBinding binding;

    public DoctorAdapter(Context context, List<DoctorData> list) {
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
                View viewItem = inflater.inflate(R.layout.row_top_doctors, parent, false);
                viewHolder = new DoctorAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new DoctorAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final DoctorData model = dataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {
                    GradientDrawable bgShape = (GradientDrawable) binding.llData.getBackground();
                    int[] androidColors = context.getResources().getIntArray(R.array.androidPastelcolors);
                    int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    bgShape.setColor(randomAndroidColor);
                    if (model.getImage() != null && !model.getImage().isEmpty())
                        CommonUtils.setRoundImage(context, binding.ivdoctor, AppConstant.DOCTOR_URL + model.getImage());

                    binding.tvcategory.setText(model.getDoctorCategoryName());
                    binding.tvTitle.setText(model.getDoctorName());
                    binding.tvexperience.setText("Exp : " + model.getExperience());

                    binding.llData.setOnClickListener(view -> {
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

    public void add(DoctorData mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(List<DoctorData> newList) {
        for (DoctorData result : newList) {
            add(result);
        }
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    private DoctorData getItem(int position) {
        return dataList.get(position);
    }

    public void setList(List<DoctorData> list) {
        dataList = list;
        notifyDataSetChanged();
    }
}

