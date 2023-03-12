package com.zoeziMitzanimedia.androidapp.retrofit_network;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitRepo {
    RetrofitDAI retrofitDAI;
    private static final String TAG = "RetrofitRepo";

    public RetrofitRepo() {
        retrofitDAI = RetrofitClient.getRetrofitClient().create(RetrofitDAI.class);
    }

    public void sendOTP(OTP otp, MutableLiveData<Boolean> mutableResponse, MutableLiveData<String> mutableError) {
        Call<ServerResponse> call = retrofitDAI.verifyOTP(otp);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()){
                    ServerResponse serverResponse = response.body();
                    assert serverResponse != null;

                    if (serverResponse.getStatus()){
                        mutableResponse.postValue(true);
                    }else{
                        mutableResponse.postValue(false);
                        mutableError.postValue(serverResponse.getErrors());
                    }
                }else{
                    mutableResponse.postValue(false);
                    mutableError.postValue("404. Request not found");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                mutableError.postValue(t.getLocalizedMessage());
            }
        });
    }
}
