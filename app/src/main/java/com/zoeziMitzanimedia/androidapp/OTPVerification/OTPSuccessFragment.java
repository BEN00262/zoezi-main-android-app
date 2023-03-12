package com.zoeziMitzanimedia.androidapp.OTPVerification;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zoeziMitzanimedia.androidapp.MainActivity;
import com.zoeziMitzanimedia.androidapp.R;
//import com.zoeziMitzanimedia.androidapp.databinding.FragmentOTPMainBinding;
import com.zoeziMitzanimedia.androidapp.databinding.FragmentOTPSuccessBinding;

import java.util.Objects;

public class OTPSuccessFragment extends Fragment {
    FragmentOTPSuccessBinding binding;

    public OTPSuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_o_t_p_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentOTPSuccessBinding.bind(view);

        ((Animatable)binding.imageView.getDrawable()).start();

        int DISPLAY_TIME = 1000;
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getActivity(), MainActivity.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.right,R.anim.left);
            Objects.requireNonNull(getActivity()).finish();
        }, DISPLAY_TIME);
    }
}