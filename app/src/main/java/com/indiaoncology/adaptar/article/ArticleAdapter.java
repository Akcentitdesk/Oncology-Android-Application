package com.indiaoncology.adaptar.article;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowArticlesBinding;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.ui.article.ArticleDescription;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Data> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowArticlesBinding binding;

    public ArticleAdapter(Context context, List<Data> list) {
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
                View viewItem = inflater.inflate(R.layout.row_articles, parent, false);
                viewHolder = new ArticleAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ArticleAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Data model = dataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {

                    if (model.getImage() != null && !model.getImage().isEmpty())
                        CommonUtils.setRoundImage(context, binding.ivArticle, AppConstant.BLOG_IMAGE_URL + model.getImage());

                    binding.tvDescription.setText(Html.fromHtml(model.getSubTitle()));
                    binding.tvTitle.setText(model.getTitle());

                    binding.llData.setOnClickListener(view -> {
                        Intent intent = new Intent(context, ArticleDescription.class);
                        intent.putExtra("BLOG_ID", model.getId());
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

