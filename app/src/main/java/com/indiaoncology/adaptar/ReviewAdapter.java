package com.indiaoncology.adaptar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.ItemProgressBinding;
import com.indiaoncology.databinding.RowReviewBinding;
import com.indiaoncology.model.review.ReviewData;
import com.indiaoncology.utils.CommonUtils;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String type;
    private List<ReviewData> dataList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ItemProgressBinding progressBinding;
    private RowReviewBinding binding;

    public ReviewAdapter(Context context, List<ReviewData> list, String type) {
        this.context = context;
        this.dataList = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_review, parent, false);
                viewHolder = new ReviewAdapter.MyViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ReviewAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ReviewData model = dataList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                if (model != null) {

                    if (!model.getUserRating().equals("0") && !model.getUserRating().equals("0.0") && model.getUserRating() != null && !model.getUserRating().isEmpty()) {
                        binding.llrating.setVisibility(View.VISIBLE);
                        binding.tvRating.setText(model.getUserRating());
                    }

                    if (model.getUserName() != null && !model.getUserName().isEmpty()) {
                        binding.tvUser.setText(CommonUtils.capitailizeWord(model.getUserName().toLowerCase()).substring(0, 1));
                        binding.tvTitle.setText(CommonUtils.capitailizeWord(model.getUserName().toLowerCase()));
                    }
                    CommonUtils.getRandomColors(context, binding.tvUser);
                    String date = CommonUtils.formatDate(model.getDate(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
                    binding.tvDate.setText(date);
                    binding.tvComment.setText(CommonUtils.capitailizeWord(model.getUserReview().toLowerCase()));

                }

                break;
            case LOADING:
                progressBinding.loadmoreProgress.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (type.equalsIgnoreCase("full")) {
            return dataList.size();
        } else {
            return Math.min(dataList.size(), 5);
        }
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
        //return position;
        return (position == dataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ReviewData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = dataList.size() - 1;
        ReviewData item = getItem(position);
        if (item != null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(ReviewData mc) {
        dataList.add(mc);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(List<ReviewData> newList) {
        for (ReviewData result : newList) {
            add(result);
        }
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    private ReviewData getItem(int position) {
        return dataList.get(position);
    }

    public void setList(List<ReviewData> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

