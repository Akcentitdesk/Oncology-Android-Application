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
import com.indiaoncology.databinding.RowRecentSearchDoctorsBinding;
import com.indiaoncology.model.search.SearchingResultData;
import com.indiaoncology.ui.MedicineDetail;
import com.indiaoncology.ui.doctor.DoctorProfile;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.MyViewHolder> {
    private Context context;
    private List<SearchingResultData> dataList;
    private String type;


    public RecentSearchAdapter(Context context, List<SearchingResultData> list, String type) {
        this.context = context;
        this.dataList = list;
        this.type = type;
    }


    @NonNull
    @Override
    public RecentSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recent_search_doctors, parent, false);
        return new RecentSearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentSearchAdapter.MyViewHolder holder, final int position) {
        final SearchingResultData list = dataList.get(position);
        holder.binding.tvName.setText(list.getTitle());
        if (type.equalsIgnoreCase("medicine")) {
            holder.binding.imageProduct.setVisibility(View.VISIBLE);
            holder.binding.ivDoc.setVisibility(View.GONE);
            if (list.getImage() != null && !list.getImage().isEmpty())
                CommonUtils.setGlideImage(context, holder.binding.imageProduct, AppConstant.MEDICINE_IMAGE + list.getImage(), R.drawable.image_placeholder);

        } else {
            holder.binding.ivDoc.setVisibility(View.VISIBLE);
            holder.binding.imageProduct.setVisibility(View.GONE);
            if (list.getImage() != null && !list.getImage().isEmpty())
                CommonUtils.setGlideImage(context, holder.binding.ivDoc, AppConstant.DOCTOR_URL + list.getImage(), R.color.transparent);

        }

        holder.binding.llData.setOnClickListener(v -> {
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
        private RowRecentSearchDoctorsBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
