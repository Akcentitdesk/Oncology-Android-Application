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
import com.indiaoncology.databinding.LayoutViewMoreBinding;
import com.indiaoncology.databinding.RowClinicImagesBinding;
import com.indiaoncology.ui.FullScreenViewActivity;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;

public class ClinicImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int View_type;
    ArrayList<String> arrayList;
    public static final int ITEM_TYPE_ONE = 3;
    public static final int ITEM_TYPE_TWO = 4;

    public ClinicImagesAdapter(Context context, ArrayList<String> arrayList, int View_type) {
        this.context = context;
        this.arrayList = arrayList;
        this.View_type = View_type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE_ONE) {
            view = LayoutInflater.from(context).inflate(R.layout.row_clinic_images, parent, false);
            return new ViewHolder(view);
        } else if (viewType == ITEM_TYPE_TWO) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_view_more, parent, false);
            return new ButtonViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_ONE) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CommonUtils.setRoundImage(context, viewHolder.imagesBinding.ivdata, AppConstant.CLINIC_IMAGE_URL + arrayList.get(position));
            viewHolder.imagesBinding.ivdata.setOnClickListener(v -> {
                Intent i = new Intent(context, FullScreenViewActivity.class);
                i.putExtra("position", position);
                i.putExtra("title", "Clinic Images");
                i.putExtra("from", "clinic");
                i.putStringArrayListExtra("image_paths", arrayList);
                System.out.println("image paths : " + arrayList.toString());
                context.startActivity(i);
            });
        } else if (itemType == ITEM_TYPE_TWO) {
            ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
            buttonViewHolder.binding.tvViewMore.setOnClickListener(v -> {
                Intent i = new Intent(context, FullScreenViewActivity.class);
                i.putExtra("position", position);
                i.putExtra("title", "Clinic Images");
                i.putExtra("from", "clinic");
                i.putStringArrayListExtra("image_paths", arrayList);
                System.out.println("image paths : " + arrayList.toString());
                context.startActivity(i);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 1) ? ITEM_TYPE_TWO : ITEM_TYPE_ONE;
    }

    @Override
    public int getItemCount() {
        return View_type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RowClinicImagesBinding imagesBinding;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagesBinding = DataBindingUtil.bind(itemView);
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        private LayoutViewMoreBinding binding;

        ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
