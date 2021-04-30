package com.indiaoncology.adaptar.doctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.RowDocLocationBinding;
import com.indiaoncology.model.doctor.location.LocationDatum;

import java.util.ArrayList;
import java.util.List;

public class DoctorLocationAdapter extends RecyclerView.Adapter<DoctorLocationAdapter.MyViewHolder> {
    private Context context;
    private List<LocationDatum> dataList;


    public DoctorLocationAdapter(Context context, List<LocationDatum> list) {
        this.context = context;
        this.dataList = list;
    }


    @NonNull
    @Override
    public DoctorLocationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_doc_location, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DoctorLocationAdapter.MyViewHolder holder, final int position) {
        final LocationDatum model = dataList.get(position);
        holder.binding.tvClinicName.setText(model.getClinicName());
        holder.binding.tvAddress.setText(model.getAddress());

        holder.binding.timings.setVisibility(View.GONE);
        holder.binding.tvTimings.setVisibility(View.GONE);
        // holder.binding.tvTimings.setText(Html.fromHtml(model.getTimings()).toString());
        holder.binding.tvGetDirections.setOnClickListener(v -> {
            String uri = "http://maps.google.co.in/maps?q=" + model.getAddress();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });
        holder.binding.tvConsultationFees.setText(new StringBuffer(context.getResources().getString(R.string.Rs)).append(" ").append(model.getFees()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.binding.rvClinicPhotos.setLayoutManager(layoutManager);
        holder.binding.rvClinicPhotos.hasFixedSize();
        holder.binding.rvClinicPhotos.setItemAnimator(new DefaultItemAnimator());
        ArrayList<String> data = new ArrayList<>();
        data.addAll(model.getBanner());
        int View_type;
        if (data.size() > 1) {
            View_type = 2;
        } else {
            View_type = 1;
        }
        ClinicImagesAdapter adapter = new ClinicImagesAdapter(context, data, View_type);
        holder.binding.rvClinicPhotos.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private RowDocLocationBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
