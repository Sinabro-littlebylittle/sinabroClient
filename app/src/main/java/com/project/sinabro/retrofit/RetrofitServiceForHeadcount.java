package com.project.sinabro.retrofit;

import com.google.gson.Gson;
import com.project.sinabro.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitServiceForHeadcount {
    private Retrofit retrofit;

    public RetrofitServiceForHeadcount() {
        initializeRetrofit();
    }


    private void initializeRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.MINUTES)
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.RETROFIT_FLASK_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}