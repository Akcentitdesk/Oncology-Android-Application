package com.indiaoncology.adaptar.schedule;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.RowDateBinding;
import com.indiaoncology.listener.OnCheckSelectedListener;
import com.indiaoncology.model.doctor.location.LocationResponseData;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {
    private Activity context;
    private List<LocationResponseData> dataList;
    private OnCheckSelectedListener listener;
    private String type;

    public DateAdapter(Activity context, List<LocationResponseData> list, OnCheckSelectedListener listener, String type) {
        this.context = context;
        this.dataList = list;
        this.listener = listener;
        this.type = type;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_date, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final LocationResponseData list = dataList.get(position);
        if (list.isSelected()) {
            holder.binding.check.setChecked(true);
        } else {
            holder.binding.check.setChecked(false);
        }

        if (holder.binding.check.isChecked()) {
            holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.button_main));
            holder.binding.tvDay.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.binding.tvDate.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.border_gray));
            holder.binding.tvDay.setTextColor(context.getResources().getColor(R.color.colorBlack));
            holder.binding.tvDate.setTextColor(context.getResources().getColor(R.color.lightBlack));
        }
        if (type.equalsIgnoreCase("Static")) {
            holder.binding.tvDate.setText(list.getDate());
        } else {
            String date = CommonUtils.formatDate(list.getDate(), "dd-MM-yyyy", "dd MMM yyyy");
            holder.binding.tvDate.setText(date);
        }

        holder.binding.tvDay.setText(list.getDay());
        holder.binding.llRoot.setOnClickListener(view -> listener.onClick(view, position,
                holder.binding.check.isChecked()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowDateBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
