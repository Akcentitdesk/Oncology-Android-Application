package com.indiaoncology.adaptar.doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.RowCategoryBinding;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.ui.doctor.DoctorListing;
import com.indiaoncology.ui.doctor.DoctorProfile;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Data> dataList;


    public CategoryAdapter(Context context, List<Data> list) {
        this.context = context;
        this.dataList = list;
    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, final int position) {
        final Data list = dataList.get(position);
        holder.binding.tvTitle.setText(list.getTitle());
        if (list.getImage() != null && !list.getImage().isEmpty())
            CommonUtils.setGlideImage(context, holder.binding.ivImage, AppConstant.DOCTOR_CATEGORY_URL+list.getImage(), R.drawable.type1);
        holder.binding.llData.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorListing.class);
            intent.putExtra("Doctor_Category_ID", list.getId());
            intent.putExtra("TITLE", list.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowCategoryBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
