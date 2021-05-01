package com.indiaoncology.adaptar.images;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.indiaoncology.R;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;

import java.util.ArrayList;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private String type;


    public FullScreenImageAdapter(Activity activity, ArrayList<String> imagePaths, String from) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.type = from;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        if (type.equalsIgnoreCase("clinic")) {

            CommonUtils.setGlideImage(_activity, imgDisplay, AppConstant.CLINIC_IMAGE_URL +
                    _imagePaths.get(position), R.drawable.image_placeholder);
        } else if (type.equalsIgnoreCase("medicine")) {
            CommonUtils.setGlideImage(_activity, imgDisplay, AppConstant.MEDICINE_IMAGE +
                    _imagePaths.get(position), R.drawable.image_placeholder);
        } else {

            CommonUtils.setGlideImage(_activity, imgDisplay, AppConstant.PRESCRIPTIONS_URL_ +
                    _imagePaths.get(position), R.drawable.image_placeholder);
        }


        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
