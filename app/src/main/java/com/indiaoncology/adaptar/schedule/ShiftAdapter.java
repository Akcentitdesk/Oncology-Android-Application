package com.indiaoncology.adaptar.schedule;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.RowShiftBinding;
import com.indiaoncology.model.doctor.location.Slot;
import com.indiaoncology.model.doctor.location.TimeArray;

import java.util.ArrayList;
import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.MyViewHolder> {
    private Activity context;
    private List<Slot> dataList;
    private String doc_id, day, date, loc_id, type,fees;
    private List<TimeArray> timeDataList = new ArrayList<>();

    public ShiftAdapter(Activity context, List<Slot> list) {
        this.context = context;
        this.dataList = list;
    }

    public void fetchData(String doc_id, String day, String date, String loc_id, String fees) {
        this.doc_id = doc_id;
        this.day = day;
        this.date = date;
        this.loc_id = loc_id;
        this.fees = fees;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_shift, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Slot list = dataList.get(position);
        if (list.getTimeArray() != null && !list.getTimeArray().isEmpty()) {
            holder.binding.tvShiftName.setText(list.getShift());
            timeDataList.clear();
            timeDataList.addAll(list.getTimeArray());
        } else {
            holder.binding.tvShiftName.setVisibility(View.GONE);
            timeDataList.clear();
        }
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        holder.binding.rvTimings.setLayoutManager(layoutManager);
        holder.binding.rvTimings.hasFixedSize();
        holder.binding.rvTimings.setItemAnimator(new DefaultItemAnimator());
        TimeSlotAdapter adapter = new TimeSlotAdapter(context, timeDataList);
        holder.binding.rvTimings.setAdapter(adapter);
        adapter.fetchData(doc_id, day, date, loc_id,fees);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowShiftBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
