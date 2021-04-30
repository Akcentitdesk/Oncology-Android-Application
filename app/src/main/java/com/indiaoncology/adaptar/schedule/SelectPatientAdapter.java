package com.indiaoncology.adaptar.schedule;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.DialogBinding;
import com.indiaoncology.databinding.RowPatientBinding;
import com.indiaoncology.listener.OnSelectedListener;
import com.indiaoncology.model.patient.PatientData;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.List;

public class SelectPatientAdapter extends RecyclerView.Adapter<SelectPatientAdapter.MyViewHolder> {
    private Context context;
    private boolean hide_value = false;
    private List<PatientData> dataList;
    private final OnSelectedListener selectedListener;
    private Dialog dialog;
    private int selectedItem;


    public SelectPatientAdapter(Context context, List<PatientData> list, OnSelectedListener selectedListener) {
        this.context = context;
        this.dataList = list;
        this.selectedListener = selectedListener;
        selectedItem = 0;
    }

    public void hide(boolean hide_value) {
        this.hide_value = hide_value;
    }


    @NonNull
    @Override
    public SelectPatientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_patient, parent, false);
        return new SelectPatientAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectPatientAdapter.MyViewHolder holder, final int position) {
        final PatientData model = dataList.get(position);
        holder.binding.tvPatientName.setText(model.getName());
        holder.binding.tvAge.setText(new StringBuffer(model.getAge()).append(", ").append(model.getGender()));
        holder.binding.tvEmail.setText(model.getEmail());
        holder.binding.tvNumber.setText(model.getMobile());
        if (hide_value) {
            holder.binding.ivSelected.setVisibility(View.GONE);
            holder.binding.llData.setVisibility(View.VISIBLE);
        } else {
            if (selectedItem == position) {
                holder.binding.ivSelected.setImageResource(R.drawable.ic_checked);
                holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.border_main));
                holder.binding.llData.setVisibility(View.VISIBLE);

            } else {
                holder.binding.ivSelected.setImageResource(R.drawable.ic_bullet_point);
                holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.border_gray));
                holder.binding.llData.setVisibility(View.GONE);
            }
        }
        holder.binding.llRoot.setOnClickListener(v -> {
            int previousItem = selectedItem;
            selectedItem = position;
            notifyItemChanged(previousItem);
            notifyItemChanged(position);

        });
        SharedPref.saveStringPreference(context, AppConstant.Selected_Patient_ID, dataList.get(selectedItem).getPatientId());
        holder.binding.tvEdit.setOnClickListener(view -> selectedListener.onClick(view, position));
        holder.binding.ivDelete.setOnClickListener(view -> openDialog("Delete Patient Details", "Are you sure you want to delete the patient's detail.", position));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RowPatientBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    private void openDialog(String heading, String desc, final int position) {
        final DialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog, null, false);
        dialog = DialogUtils.createDialog(context, dialogBinding.getRoot(), 0);
        dialog.setCancelable(false);
        dialogBinding.tvHeading.setText(heading);
        dialogBinding.tvDescription.setText(desc);
        dialogBinding.tvNo.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.tvYes.setOnClickListener(v -> {
            selectedListener.onClick(v, position);
            notifyDataSetChanged();
            dialog.dismiss();
        });
    }
}
