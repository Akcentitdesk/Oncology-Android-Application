package com.indiaoncology.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.Window;

import androidx.core.content.ContextCompat;

import com.indiaoncology.R;


public class ProgressDialogUtils {

    private static ProgressDialog progressDialog;

    public ProgressDialogUtils() {
        throw new Error("U will not able to instantiate it");
    }

    public static void show(Context context) {

        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            int style;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                style = android.R.style.Theme_Material_Light_Dialog;
            } else {
                style = ProgressDialog.THEME_HOLO_LIGHT;
            }
            progressDialog = new ProgressDialog(context, style);
            // progressDialog = new ProgressDialog(context, R.style.MyTheme);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            // progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_rotation));
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismiss() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
