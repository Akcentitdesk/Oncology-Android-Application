package com.indiaoncology.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.indiaoncology.utils.AppConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RequestController {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static RequestController requestController;
    private static final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(3, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES);
    private static Interceptor headerInterceptor;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static final Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));


    public static RequestController getInstance() {
        if (requestController == null) {
            requestController = new RequestController();
        }
        return requestController;
    }

    public static <S> S createService(Context context, Class<S> service) {
        return createService(context, service, true);
    }

    private static <S> S createService(Context context, Class<S> service, boolean isAuth) {
        if (/*headerInterceptor == null && */!okHttpClient.interceptors().contains(headerInterceptor)) {
            if (isAuth) {
                addNetworkInterceptor(context);
            }
        }
        Retrofit retrofit = builder.client(okHttpClient.build()).build();
        return retrofit.create(service);
    }


    private static void addNetworkInterceptor(Context context) {
        headerInterceptor = chain -> {
            Request original = chain.request();
            Response response;
            Request.Builder builder;
            builder = original.newBuilder()
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .method(original.method(), original.body());
            response = chain.proceed(builder.build());
            return response;
        };
        okHttpClient.addInterceptor(headerInterceptor);
    }
}
