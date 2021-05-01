package com.indiaoncology.adaptar.images;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.indiaoncology.model.type.Data;
import com.indiaoncology.utils.AppConstant;

import java.util.List;



public class AndroidImageAdapter extends PagerAdapter {

    private final List<Data> imageModelArrayList;
    private final LayoutInflater inflater;
    private final Context context;
    private boolean value = false;

    public AndroidImageAdapter(Context context, List<Data> imageModelArrayList) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        inflater = LayoutInflater.from(context);
    }

    public boolean show(boolean value) {
        this.value = value;
        return value;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        final ProgressBar progressBar = imageLayout.findViewById(R.id.progress);

        setImage(imageView, position, progressBar);

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    private void setImage(ImageView imageView, int position, ProgressBar progressBar) {
        Glide.with(context).load(AppConstant.BANNER_URL_ + imageModelArrayList.get(position).getImage())
                .error(R.drawable.banner1)
                .placeholder(R.drawable.banner1)
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
