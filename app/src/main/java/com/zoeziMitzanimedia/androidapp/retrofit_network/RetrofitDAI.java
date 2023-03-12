package com.zoeziMitzanimedia.androidapp.retrofit_network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitDAI {
    @Headers(
            "x-requested-with: com.zoeziMitzanimedia.androidapp"
    )
    @POST("/app/otp-verification")
    Call<ServerResponse> verifyOTP(@Body OTP otp);
}
