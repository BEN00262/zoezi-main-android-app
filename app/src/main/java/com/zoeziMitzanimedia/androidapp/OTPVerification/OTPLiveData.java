package com.zoeziMitzanimedia.androidapp.OTPVerification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zoeziMitzanimedia.androidapp.retrofit_network.OTP;
import com.zoeziMitzanimedia.androidapp.retrofit_network.RetrofitRepo;

public class OTPLiveData extends ViewModel {
    private final RetrofitRepo retrofitRepo = new RetrofitRepo();
    private final MutableLiveData<String> errors = new MutableLiveData<>();
    private final MutableLiveData<Boolean> otpStatus = new MutableLiveData<>();

    public void sendOTP(OTP otp){
        retrofitRepo.sendOTP(otp,otpStatus, errors);
    }

    public LiveData<String> getErrors(){
        return errors;
    }

    public LiveData<Boolean> getOTPStatus(){
        return otpStatus;
    }
}