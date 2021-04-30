package com.indiaoncology.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ActivityController {

    public static void startActivity(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public static void startActivity(Activity activity, Class clazz, boolean isClearPrevious) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        if (isClearPrevious)
            activity.finish();
    }

    public static void startActivity(Context activity, Class clazz, String type) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(activity, clazz);
        bundle.putString(AppConstant.FROM, type);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, Class clazz, Bundle bundle) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, Class clazz, Bundle bundle, boolean isClearPrevious) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        if (isClearPrevious)
            activity.finish();
    }

    public static void startActivity(Activity activity, Class clazz, String type, boolean isClearPrevious) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(activity, clazz);
        bundle.putString(AppConstant.FROM, type);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        if (isClearPrevious)
            activity.finish();
    }

    public static void startActivity(Activity activity, Class clazz, boolean isClearPrevious, boolean isClearStack) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        if (isClearStack && isClearPrevious || isClearStack)
            ActivityCompat.finishAffinity(activity);
        if (isClearPrevious)
            activity.finish();
    }

    public static void startActivityForResult(Activity activity, Class clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Activity activity, Class clazz, Bundle bundle, boolean isClearPrevious, boolean isClearStack) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        if (isClearStack && isClearPrevious || isClearStack)
            ActivityCompat.finishAffinity(activity);
        if (isClearPrevious)
            activity.finish();
    }

    public static void startActivity(Context context, Class clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * Start activity for result.
     *
     * @param activity          the activity
     * @param clazz             the clazz
     * @param bundle            the bundle
     * @param requestCode       the request code
     * @param isEnableAnimation the is enable animation
     */
    public static void startActivityForResult(AppCompatActivity activity, Class clazz, Bundle bundle, int requestCode, boolean isEnableAnimation) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        if (!isEnableAnimation) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        activity.startActivityForResult(intent, requestCode);
    }

}
