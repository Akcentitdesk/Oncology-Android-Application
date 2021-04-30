package com.indiaoncology.adaptar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowTypeListBinding;
import com.indiaoncology.databinding.RowTypesBinding;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.ui.Redirect;
import com.indiaoncology.ui.review.AllReviews;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<Data> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int ITEM_LIST = 2;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowTypesBinding binding;
    private RowTypeListBinding bindingList;
    private String type;

    public TypeAdapter(Activity context, List<Data> list, String type) {
        this.context = context;
        this.dataList = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = null;
        if (viewType == ITEM) {
            view = LayoutInflater.from(context).inflate(R.layout.row_types, parent, false);
            return new TypeAdapter.MyViewHolder(view);
        } else if (viewType == ITEM_LIST) {
            view = LayoutInflater.from(context).inflate(R.layout.row_type_list, parent, false);
            return new TypeAdapter.ListViewHolder(view);
        } else if (viewType == LOADING) {
            view = LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false);
            return new TypeAdapter.LoadingViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Data model = dataList.get(position);
        final int itemType = getItemViewType(position);
        Bundle bundle = new Bundle();

        if (itemType == ITEM) {
            if (model != null) {

                if (model.getImage() != null && !model.getImage().isEmpty())
                    CommonUtils.setGlideImage(context, binding.ivtype, AppConstant.TYPE_URL + model.getImage(), R.drawable.type1);
                binding.tvTitle.setText(model.getTitle());

                binding.llData.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Redirect.class);
                    intent.putExtra("URL", model.getSubTitle());
                    intent.putExtra("TITLE", model.getTitle());
                    context.startActivity(intent);
                });
            }
        } else if (itemType == ITEM_LIST) {
            if (model != null) {

                if (model.getImage() != null && !model.getImage().isEmpty())
                    CommonUtils.setGlideImage(context, bindingList.ivtype, AppConstant.TYPE_URL + model.getImage(), R.drawable.type1);
                bindingList.tvTitle.setText(model.getTitle());

                bindingList.llData.setOnClickListener(view -> {
                    Intent intent = new Intent(context, Redirect.class);
                    intent.putExtra("URL", model.getSubTitle());
                    intent.putExtra("TITLE", model.getTitle());
                    context.startActivity(intent);
                });
            }
        } else if (itemType == LOADING) {

            progressBinding.loadmoreProgress.setVisibility(View.VISIBLE);
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

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingList = DataBindingUtil.bind(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBinding = DataBindingUtil.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equalsIgnoreCase("grid"))
            return (position == dataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM_LIST;
        else
            return (position == dataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Data());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = dataList.size() - 1;
        Data item = getItem(position);
        if (item != null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(Data mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(List<Data> newList) {
        for (Data result : newList) {
            add(result);
        }
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    private Data getItem(int position) {
        return dataList.get(position);
    }

    public void setList(List<Data> list) {
        dataList = list;
        notifyDataSetChanged();
    }
}

