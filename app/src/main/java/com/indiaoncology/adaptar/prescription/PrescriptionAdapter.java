package com.indiaoncology.adaptar.prescription;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.RowMyPrescriptionBinding;
import com.indiaoncology.listener.OnCheckSelectedListener;
import com.indiaoncology.listener.OnSelectedListener;
import com.indiaoncology.model.PrescriptionData;
import com.indiaoncology.ui.FullScreenViewActivity;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.MyViewHolder> {
    private Context context;
    private boolean value = false;
    private String type;
    private String title;
    public static List<PrescriptionData> dataList;
    private OnSelectedListener listener;

    public PrescriptionAdapter(Context context, List<PrescriptionData> list, OnSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        dataList = list;
    }

    public void show(boolean value) {
        this.value = value;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_prescription, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final PrescriptionData model = dataList.get(position);
        if (model != null) {
            if (model.getImage() != null && !model.getImage().isEmpty())
                CommonUtils.setRoundImage(context, holder.binding.ivPrescription, AppConstant.PRESCRIPTIONS_URL_ + model.getImage());

            holder.binding.llRoot.setOnClickListener(v -> {
                ArrayList<String> imagePaths = new ArrayList<>();
                for (int j = 0; j < dataList.size(); j++) {
                    imagePaths.add(dataList.get(j).getImage());
                }
                Intent i = new Intent(context, FullScreenViewActivity.class);
                i.putExtra("position", position);
                i.putExtra("title", "Uploaded Reports");
                i.putExtra("from", "report");
                i.putStringArrayListExtra("image_paths", imagePaths);
                System.out.println("image paths : " + imagePaths.toString());
                context.startActivity(i);
            });

            holder.binding.ivCross.setOnClickListener(v -> listener.onClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private RowMyPrescriptionBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
