package com.indiaoncology.adaptar.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.adaptar.doctor.DoctorAdapter;

import com.indiaoncology.databinding.RowDashboardBinding;
import com.indiaoncology.databinding.RowShiftBinding;
import com.indiaoncology.model.dashboard.IndexData;
import com.indiaoncology.model.doctor.DoctorData;
import com.indiaoncology.ui.doctor.DoctorListing;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {
    private Activity context;
    private List<IndexData> dataList;
    private List<DoctorData> timeDataList = new ArrayList<>();

    public DashboardAdapter(Activity context, List<IndexData> list) {
        this.context = context;
        this.dataList = list;
    }


    @NonNull
    @Override
    public DashboardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_dashboard, parent, false);
        return new DashboardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DashboardAdapter.MyViewHolder holder, final int position) {
        final IndexData list = dataList.get(position);
        holder.binding.tvHeading.setText(list.getHeading());
        holder.binding.tvDescription.setText(list.getDescription());
        if (list.getSubdataList() != null && !list.getSubdataList().isEmpty()) {
            timeDataList.clear();
            timeDataList.addAll(list.getSubdataList());
        } else {
           /* holder.binding.tvHeading.setVisibility(View.GONE);
            holder.binding.tvDescription.setVisibility(View.GONE);
            timeDataList.clear();*/
        }

        holder.binding.tvViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorListing.class);
            intent.putExtra("TAG_ID", list.getId());
            intent.putExtra("TITLE", list.getHeading());
            context.startActivity(intent);
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.binding.rvRoot.setLayoutManager(layoutManager);
        holder.binding.rvRoot.hasFixedSize();
        holder.binding.rvRoot.setItemAnimator(new DefaultItemAnimator());
        DoctorAdapter adapter = new DoctorAdapter(context, timeDataList);
        holder.binding.rvRoot.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowDashboardBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
