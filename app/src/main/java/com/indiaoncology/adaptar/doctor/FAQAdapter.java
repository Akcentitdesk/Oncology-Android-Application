package com.indiaoncology.adaptar.doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.RowCategoryBinding;
import com.indiaoncology.databinding.RowFaqBinding;
import com.indiaoncology.model.Document;
import com.indiaoncology.model.type.Data;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder> {
    private Context context;
    private List<Document> dataList;


    public FAQAdapter(Context context, List<Document> list) {
        this.context = context;
        this.dataList = list;
    }


    @NonNull
    @Override
    public FAQAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_faq, parent, false);
        return new FAQAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FAQAdapter.MyViewHolder holder, final int position) {
        final Document list = dataList.get(position);
        holder.binding.heading.setText(list.getText());
        holder.binding.subHeading.setText(list.getText2());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RowFaqBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
