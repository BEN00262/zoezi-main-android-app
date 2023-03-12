package com.zoeziMitzanimedia.androidapp.retrofit_network;

import androidx.core.util.TimeUtils;

import com.zoeziMitzanimedia.androidapp.BaseURLs;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.TimeUnit.*;

public class RetrofitClient {
    private static final String BASE_URL = "https://"+ BaseURLs.baseURL;
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitClient(){
        if (retrofit == null){
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, SECONDS)
                    .readTimeout(30, SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
