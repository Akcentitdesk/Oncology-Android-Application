package com.indiaoncology.adaptar.images;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.bumptech.glide.Glide;
import com.indiaoncology.R;
import com.indiaoncology.ui.others.FullScreenViewActivity;
import com.indiaoncology.utils.AppConstant;

import java.util.ArrayList;

public class ProductImageAdapter extends PagerAdapter {

    private final ArrayList<String> list;
    private final LayoutInflater inflater;
    private final Context context;
    private boolean value = false;
    private String productName;

    public ProductImageAdapter(Context context, ArrayList<String> list, String productName) {
        this.context = context;
        this.list = list;
        this.productName = productName;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        final ImageView imageViewProduct = imageLayout.findViewById(R.id.imageProduct);
        final ProgressBar progressBar = imageLayout.findViewById(R.id.progress);

        imageViewProduct.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        setImage(imageViewProduct, position, progressBar);
        imageViewProduct.setOnClickListener(v -> {
            Intent i = new Intent(context, FullScreenViewActivity.class);
            i.putExtra("position", position);
            i.putExtra("from", "medicine");
            i.putExtra("title", productName);
            i.putStringArrayListExtra("image_paths", list);
            context.startActivity(i);
        });

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    private void setImage(ImageView imageView, int position, ProgressBar progressBar) {
        Glide.with(context).load(AppConstant.MEDICINE_IMAGE + list.get(position))
                .error(R.drawable.image_placeholder)
                .placeholder(R.drawable.image_placeholder)
                .into(imageView);

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
