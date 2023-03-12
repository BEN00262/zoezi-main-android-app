package com.zoeziMitzanimedia.androidapp.OTPVerification;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
//import com.zoeziMitzanimedia.androidapp.R;
import com.zoeziMitzanimedia.androidapp.databinding.ActivityOTPMainBinding;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPMainActivity extends AppCompatActivity implements OTPCommInterface {

    ActivityOTPMainBinding binding;
    SmsRetrieverClient client;
    OTPMainLiveData otpMainLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = SmsRetriever.getClient(this);
        otpMainLiveData = new ViewModelProvider(this).get(OTPMainLiveData.class);

        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentHolder.getId(),new OTPMainFragment(this, otpMainLiveData))
                .commit();
    }

    @Override
    protected void onStart() {
        scheduleOTPListener();
        super.onStart();
    }

    private void scheduleOTPListener(){
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            // intentFilter.addAction("com.google.android.gms.auth.api.phone.SMS_RETRIEVED");

            registerReceiver(SmsBroadcastReceiver, intentFilter);

            Snackbar.make(binding.getRoot(),"Waiting for OTP", Snackbar.LENGTH_SHORT).show();
        });

        task.addOnFailureListener(e -> Snackbar.make(
                binding.getRoot(),
                "Failed to auto verify ... please paste the OTP to proceed",
                Snackbar.LENGTH_LONG
        ).show());
    }

    private final BroadcastReceiver SmsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())){
                Bundle extras = intent.getExtras();
                Status status = (Status)extras.get(SmsRetriever.EXTRA_STATUS);

                switch(status.getStatusCode()){
                    case CommonStatusCodes.SUCCESS:
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        Pattern pattern = Pattern.compile(":\\s*(.){6}\n");

                        Matcher matched = pattern.matcher(message);
                        if (matched.find()){
                            setOTP(Objects.requireNonNull(matched.group(0)).replace(":","").trim());
                        }

                        break;
                    case CommonStatusCodes.TIMEOUT:
                        Toast.makeText(context,"OTP Timeout", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };

    @Override
    protected void onStop() {
        unregisterReceiver(SmsBroadcastReceiver);
        super.onStop();
    }

    private void setOTP(String otp){
        otpMainLiveData.setOtpText(otp);
    }

    @Override
    public void moveTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentHolder.getId(),fragment)
                .commit();
    }
}