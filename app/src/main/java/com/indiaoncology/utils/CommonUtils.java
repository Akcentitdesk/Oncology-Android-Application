package com.indiaoncology.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import com.comm100.livechat.ChatActivity;
import com.comm100.livechat.VisitorClientInterface;
import com.google.android.material.snackbar.Snackbar;
import com.indiaoncology.R;
import com.indiaoncology.databinding.ToolbarBinding;
import com.indiaoncology.ui.others.SearchActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;


import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CommonUtils {
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final int PERMISSION_REQUEST_CODE = 100;

    public static boolean checkIsMarshMallowVersion() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion >= Build.VERSION_CODES.M;

    }

    public static boolean isOnline(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    public static RequestBody parseString(String str) {
        return RequestBody.create(MediaType.parse("text/plain"), str != null ? str : "");
    }

    public static void setSnackbar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // displaying error messages for edittexts
    public static void setErrorMessage(EditText editText, TextView textView, String errorMessage) {
        editText.requestFocus();
        textView.setVisibility(View.VISIBLE);
        textView.setText(errorMessage);
    }


    public static void setListCount(List list, TextView textView, String string) {
        String listCount = String.format("%02d", list.size());
        textView.setText(string + "(" + listCount + ")");
    }

    // setting random colors for names
    public static void getRandomColors(Context context, TextView textView) {
        int[] androidColors = context.getResources().getIntArray(R.array.androidPastelcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        ((GradientDrawable) textView.getBackground()).setColor(randomAndroidColor);
    }

    public static void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    // open dialer
    public static void openDialer(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }


    // setting round cornors to image using url
    public static void setRoundImage(Context context, ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context).asBitmap().load(url).error(R.color.transparent_).into(getRoundedImageTarget(context, imageView, 7));
        }
    }

    public static void setGlideImage(Context context, ImageView imageView, String url, int holderImage) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context).load(url).placeholder(holderImage).error(holderImage).into(imageView);
        }
    }

    public static BitmapImageViewTarget getRoundedImageTarget(@NonNull final Context context,
                                                              @NonNull final ImageView imageView, final float radius) {
        return new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(final Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(radius);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        };
    }

    public static String formatDate(String date, String pattern, String pattern2) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date newDate = null;
            try {
                newDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat(pattern2);
            return date = format.format(newDate);
        }
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    public static void hideKeyboard(Activity activity, View view) {
        if (view != null) {
            view.setOnTouchListener((v, event) -> {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
                    return true;
                } else
                    return false;
            });
        }
    }

    public static void showAlertPopup(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    // Method to convert the string
    public static String capitailizeWord(String str) {
        StringBuffer s = new StringBuffer();
        char ch = ' ';
        for (int i = 0; i < str.length(); i++) {
            if (ch == ' ' && str.charAt(i) != ' ')
                s.append(Character.toUpperCase(str.charAt(i)));
            else
                s.append(str.charAt(i));
            ch = str.charAt(i);
        }
        return s.toString().trim();
    }

    // share data
    public static void shareData(Context context, ImageView imageView, String shareText) {
        // Share to Social Media
        if (imageView.getDrawable() != null) {
            Drawable mDrawable = imageView.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), mBitmap, "Design", null);
            Uri uri = Uri.parse(path);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(share, "Share"));
        } else {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        }
    }

    public static void setToolBar(ToolbarBinding menuBar, String title, Activity context) {
        menuBar.tvTitle.setVisibility(View.VISIBLE);
        menuBar.ivBack.setVisibility(View.VISIBLE);
        menuBar.ivSecond.setVisibility(View.VISIBLE);
        menuBar.ivRight.setVisibility(View.VISIBLE);
        menuBar.ivSecond.setOnClickListener(v -> {
            int siteId = 40100011;
            String planId = "94fcdecc-e382-49b1-98db-4ec441aa2c16";
            String chatServer = "https://entchatserver.comm100.com";
            VisitorClientInterface.setChatUrl(chatServer + "/chatWindow.aspx?planId=" + planId + "&siteId=" + siteId);
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        });
        menuBar.ivBack.setOnClickListener(v -> context.finish());
        menuBar.ivRight.setOnClickListener(v -> ActivityController.startActivity(context, SearchActivity.class));
        menuBar.tvTitle.setText(title);

    }

    public static void requestPermissionStorage(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
