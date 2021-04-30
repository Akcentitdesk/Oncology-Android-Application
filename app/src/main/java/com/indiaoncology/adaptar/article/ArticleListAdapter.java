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
import com.indiaoncology.databinding.RowArticleViewBinding;
import com.indiaoncology.databinding.RowArticlesBinding;
import com.indiaoncology.listener.OnSelectedListener;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.ui.article.ArticleDescription;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Data> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowArticleViewBinding binding;
    private OnSelectedListener listener;


    public ArticleListAdapter(Context context, List<Data> list, OnSelectedListener listener) {
        this.context = context;
        this.dataList = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_article_view, parent, false);
                viewHolder = new ArticleListAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ArticleListAdapter.LoadingViewHolder(viewLoading);
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

                    if (model.getImage() != null && !model.getImage().isEmpty()) {
                        CommonUtils.setRoundImage(context, binding.ivArticle, AppConstant.BLOG_IMAGE_URL + model.getImage());
                        CommonUtils.setRoundImage(context, binding.ivShareArticle, AppConstant.BLOG_IMAGE_URL + model.getImage());
                    }

                    binding.tvDescription.setText(Html.fromHtml(model.getSubTitle()));
                    binding.tvTitle.setText(model.getTitle());
                    if (model.getDate() != null && !model.getDate().isEmpty()) {
                        String date = CommonUtils.formatDate(model.getDate(), "yyyy-MM-dd", "dd MMM yyyy");
                        binding.tvdate.setText(date);
                    }

                    if (model.getIs_favourite() != null && !model.getIs_favourite().isEmpty()) {
                        if (model.getIs_favourite().equals("1")) {
                            binding.ivFavourite.setVisibility(View.GONE);
                            binding.ivRemoveFavourite.setVisibility(View.VISIBLE);
                        } else {
                            binding.ivFavourite.setVisibility(View.VISIBLE);
                            binding.ivRemoveFavourite.setVisibility(View.GONE);
                        }
                    }
                    binding.llArticle.setOnClickListener(view -> {
                        Intent intent = new Intent(context, ArticleDescription.class);
                        intent.putExtra("BLOG_ID", model.getId());
                        context.startActivity(intent);
                    });

                    /*binding.ivShare.setOnClickListener(view ->
                            CommonUtils.shareData(context, binding.ivShareArticle, Html.fromHtml(model.getTitle()) + " \n" + model.getUrl()));
*/
                    binding.ivFavourite.setOnClickListener(view -> {
                        listener.onClick(view, position);
                    });
                    binding.ivRemoveFavourite.setOnClickListener(view -> {
                        listener.onClick(view, position);
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

