package com.indiaoncology.adaptar.medicine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowMedicineBinding;
import com.indiaoncology.model.medicine.MedicineDataList;
import com.indiaoncology.ui.medicine.MedicineDetail;
import com.indiaoncology.ui.medicine.Order;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<MedicineDataList> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowMedicineBinding binding;

    public ListingAdapter(Activity context, List<MedicineDataList> list) {
        this.context = context;
        this.dataList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_medicine, parent, false);
                viewHolder = new MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final MedicineDataList model = dataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {
                    if (model.getImage_() != null && !model.getImage_().isEmpty())
                        CommonUtils.setGlideImage(context, binding.ivdata, AppConstant.MEDICINE_IMAGE
                                + model.getImage_(), R.drawable.image_placeholder);
                    binding.tvAmount.setText(context.getResources().getString(R.string.Rs) + " " + model.getAmount());
                    binding.tvOff.setText(model.getDiscount_percentage() + "% off");
                    binding.tvMRP.setText(model.getMrp());
                    binding.tvMRP.setPaintFlags(binding.tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.tvName.setText(model.getName());
                    binding.btnGetQuote.setOnClickListener(v -> {
                        Intent intent = new Intent(context, Order.class);
                        intent.putExtra("quantity", "1");
                        intent.putExtra("MEDICINE_ID", model.getId());
                        context.startActivity(intent);
                    });

                    binding.llData.setOnClickListener(v -> {
                        Intent intent = new Intent(context, MedicineDetail.class);
                        intent.putExtra("MEDICINE_ID", model.getId());
                        context.startActivity(intent);
                    });
                }
                break;
            case LOADING:
                progressBinding.loadmoreProgress.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public void add(MedicineDataList mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void remove(MedicineDataList membersData) {
        int position = dataList.indexOf(membersData);
        if (position > -1) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void setList(List<MedicineDataList> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<MedicineDataList> newList) {
        int lastIndex = dataList.size() - 1;
        dataList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
    }

    public MedicineDataList getItem(int position) {
        return dataList.get(position);
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBinding = DataBindingUtil.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MedicineDataList());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = dataList.size() - 1;
        MedicineDataList item = getItem(position);
        if (item != null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }
}

