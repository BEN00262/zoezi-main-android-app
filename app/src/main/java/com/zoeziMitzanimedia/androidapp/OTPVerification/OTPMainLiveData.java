package com.zoeziMitzanimedia.androidapp.OTPVerification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OTPMainLiveData extends ViewModel {
    MutableLiveData<String> otpText = new MutableLiveData<>();

    public void setOtpText(String _otpText){
        otpText.postValue(_otpText);
    }

    public LiveData<String> getPrefilledString(){
        return otpText;
    }
}
