package com.zoeziMitzanimedia.androidapp.OTPVerification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.zoeziMitzanimedia.androidapp.BaseURLs;
import com.zoeziMitzanimedia.androidapp.R;
import com.zoeziMitzanimedia.androidapp.ZoeziPreferenceManager;
import com.zoeziMitzanimedia.androidapp.databinding.FragmentOTPMainBinding;
import com.zoeziMitzanimedia.androidapp.retrofit_network.OTP;

public class OTPMainFragment extends Fragment {
    FragmentOTPMainBinding binding;
    OTPCommInterface otpCommInterface;
    OTPLiveData otpLiveData;
    ZoeziPreferenceManager zoeziPreferenceManager;
    OTPMainLiveData otpMainLiveData;
    String verifying = "verifying...";


    public OTPMainFragment(OTPCommInterface otpCommInterface, OTPMainLiveData otpMainLiveData) {
        this.otpCommInterface = otpCommInterface;
        this.otpMainLiveData = otpMainLiveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        otpLiveData = new ViewModelProvider(this).get(OTPLiveData.class);
        zoeziPreferenceManager = new ZoeziPreferenceManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_o_t_p_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentOTPMainBinding.bind(view);

        otpMainLiveData.getPrefilledString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.otpText.setText(s);

                binding.submitOtpCode.performClick();
                binding.submitOtpCode.setText(verifying);
            }
        });

        binding.resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resendOTPRedirect = new Intent(Intent.ACTION_VIEW, Uri.parse(BaseURLs.resendToken));
                startActivity(resendOTPRedirect);
            }
        });

        binding.submitOtpCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otpCode = binding.otpText.getText().toString();


                if (TextUtils.isEmpty(otpCode)){
                    Snackbar.make(binding.getRoot(), "Please enter the OTP code", Snackbar.LENGTH_LONG).show();
                    return;
                }

                binding.submitOtpCode.setText(verifying);
                otpLiveData.sendOTP(new OTP(otpCode));
            }
        });

        otpLiveData.getOTPStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    zoeziPreferenceManager.verify();
                    otpCommInterface.moveTo(new OTPSuccessFragment());
                    return;
                }

                binding.submitOtpCode.setText(R.string.verify_account_btn_text);
            }
        });

        otpLiveData.getErrors().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.submitOtpCode.setText(R.string.verify_account_btn_text);
                Snackbar.make(binding.getRoot(),s,Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        binding = null;
        zoeziPreferenceManager = null;
        otpLiveData = null;
    }
}