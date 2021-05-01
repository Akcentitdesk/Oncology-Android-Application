package com.indiaoncology.adaptar.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.RowSearchViewBinding;
import com.indiaoncology.model.search.SearchingResultData;
import com.indiaoncology.ui.medicine.MedicineDetail;
import com.indiaoncology.ui.doctor.DoctorProfile;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private Context context;
    private List<SearchingResultData> dataList;
    private String type;


    public SearchAdapter(Context context, List<SearchingResultData> list, String type) {
        this.context = context;
        this.dataList = list;
        this.type = type;
    }


    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_search_view, parent, false);
        return new SearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.MyViewHolder holder, final int position) {
        final SearchingResultData list = dataList.get(position);

        if (type.equalsIgnoreCase("medicine")) {
            holder.binding.ivType.setImageDrawable(context.getResources().getDrawable(R.drawable.medicine));
            holder.binding.tvCategory.setText(context.getResources().getString(R.string.Rs) + " " + list.getSubtitle());
        } else {
            holder.binding.tvCategory.setText(list.getSubtitle());
            holder.binding.ivType.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_nav_doc_red));
        }
        holder.binding.tvTitle.setText(list.getTitle());
        holder.binding.llRoot.setOnClickListener(v -> {
            if (type.equalsIgnoreCase("medicine")) {
                Intent intent = new Intent(context, MedicineDetail.class);
                intent.putExtra("MEDICINE_ID", list.getItem_id());
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, DoctorProfile.class);
                intent.putExtra("DOCTOR_ID", list.getItem_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowSearchViewBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
