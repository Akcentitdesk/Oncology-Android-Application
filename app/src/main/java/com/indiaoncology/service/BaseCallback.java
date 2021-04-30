package com.indiaoncology.service;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Callback wrapper written to handle some generic response sent by server like 401, 4.2, etc.
 */
public abstract class BaseCallback<T extends BaseResponse> implements Callback<T> {

    public static final String TAG = BaseCallback.class.getSimpleName();
    private static final int INVALID_SESSION = 604;
    private WeakReference<Context> ref;
    private Context mContext;

    public BaseCallback(Context activity) {
        ref = new WeakReference<>(mContext);
    }

    public BaseCallback(AppCompatActivity activity) {
    }

    public BaseCallback(Fragment activity) {
    }

    /**
     * Invoked when Successful response sent from server.
     *
     * @param response Response from server
     */
    public abstract void onSuccess(T response);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call Failed Call instance
     */
    public abstract void onFail(Call<T> call, BaseResponse baseResponse);

    public void onResponse(Call<T> call, Response<T> response) {
        //Generic error response code are handled at base level
        Context act = null;
        if (ref != null) {
            act = ref.get();
        }
        //act.hideProgressBar();
        if (response.isSuccessful()) {
            BaseResponse responseBase = response.body();
            if (responseBase == null) {
                return;
            }
            if (responseBase.getStatus().equals(INVALID_SESSION)) {
                //act.showToast(act.getString(R.string.authentication_error));
                //act.localLogOut();
                //act.showSessionExpiredDialog();
                return;
            }
            onSuccess(response.body());
        } else {
            if (response.raw().code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                ResponseBody baseResponse = response.errorBody();
                try {
                    JSONObject jObjError = new JSONObject(baseResponse.string());
                    String msg = jObjError.getString("message");
                    onFail(call, null);
                    //act.showSnackbarFromTop(act,msg);
                } catch (JSONException e) {
                    //LogUtils.LOGE(TAG,e.getMessage());
                } catch (IOException e) {
                    //LogUtils.LOGE(TAG,e.getMessage());
                }
            } else if (response.raw().code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                //ResponseBody responseBody=response.errorBody();
                //  onSuccess(responseBody.get);
                ResponseBody responseBody = response.errorBody();
                BaseResponse baseResponse = new BaseResponse();
                try {
                    JSONObject jObjError = new JSONObject(responseBody.string());
                    baseResponse.setMessage(jObjError.getString("message"));
                    String msg = jObjError.getString("message");
                    //act.showSnackbarFromTop(act,msg);
                    //act.localLogOut();
                    //  onFail(call, null);
                    if (!TextUtils.isEmpty(msg)) {
                        if (msg.contains("session")) {
                        } else {
                        }
                    }
                    onFail(call, baseResponse);
                } catch (JSONException e) {
                    onFail(call, null);
                    //LogUtils.LOGE(TAG,e.getMessage());
                } catch (IOException e) {
                    onFail(call, null);
                    //LogUtils.LOGE(TAG,e.getMessage());
                }

                // onFail(call,null);
            } else {
                ResponseBody responseBody = response.errorBody();
                BaseResponse baseResponse = new BaseResponse();
                try {
                    JSONObject jObjError = new JSONObject(responseBody.string());
                    baseResponse.setMessage(jObjError.getString("message"));
                    // baseResponse.setError(jObjError.getString("message"));
                    //  baseResponse.setStatus(jObjError.getInt("status"));
                    baseResponse.setStatus(jObjError.getString("status"));
                    onFail(call, baseResponse);
                } catch (JSONException e) {
                    //LogUtils.LOGE(TAG,e.getMessage());
                } catch (IOException e) {
                    //LogUtils.LOGE(TAG,e.getMessage());
                }
            }
        }
    }

    public void onFailure(Call<T> call, Throwable t) {
        try {
            //LogUtils.printStackTrace(t);
            Context act = null;
            if (ref != null) {
                act = ref.get();
            }
            //act.hideProgressBar();
            Log.d("mylog", "Error in Callback : " + t.getMessage());

            if (t instanceof ConnectException || t instanceof UnknownHostException) {
                //act.showSnackBar(act.getResources().getString(R.string.no_internet));
            } else if (t instanceof SocketTimeoutException) {
                // act.showSnackBar(act.getResources().getString(R.string.error_timeout));
            } else {
                //act.showSnackBar(act.getResources().getString(R.string.error_something_wrong));
            }
            onFail(call, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fail(Call<T> call) {
        onFail(call, null);
    }

}
