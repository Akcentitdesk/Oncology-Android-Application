package com.indiaoncology.adaptar.schedule;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.RowSelectLocationBinding;
import com.indiaoncology.listener.OnSelectedListener;
import com.indiaoncology.model.Document;

import java.util.List;

public class SelectLocationAdapter extends RecyclerView.Adapter<SelectLocationAdapter.MyViewHolder> {
    private Activity context;
    private List<Document> dataList;
    private OnSelectedListener listener;

    public SelectLocationAdapter(Activity context, List<Document> list, OnSelectedListener listener) {
        this.context = context;
        this.dataList = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_select_location, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Document list = dataList.get(position);
        holder.binding.tvAddress.setText(list.getText2());
        holder.binding.tvClinicName.setText(list.getText3());
        holder.binding.tvClinicFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs))
                .append(" ").append(list.getText4()));
        holder.binding.llRoot.setOnClickListener(view -> listener.onClick(view, position));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowSelectLocationBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
