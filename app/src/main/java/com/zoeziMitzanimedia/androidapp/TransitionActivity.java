package com.zoeziMitzanimedia.androidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zoeziMitzanimedia.androidapp.OTPVerification.OTPMainActivity;

import java.util.Calendar;

public class TransitionActivity extends AppCompatActivity implements View.OnClickListener {
    ZoeziPreferenceManager zoeziPreferenceManager;
    Button btn;
    TextView footer,copyRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        zoeziPreferenceManager = new ZoeziPreferenceManager(this);

        btn = findViewById(R.id.get_started);
        btn.setOnClickListener(this);

        footer = findViewById(R.id.footer);
        footer.setText(Html.fromHtml(getResources().getString(R.string.footer)));
        footer.setMovementMethod(LinkMovementMethod.getInstance());

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String copyRightText = "Copyright © "+ year + " Zoezi";

        copyRight = findViewById(R.id.copyright);
        copyRight.setText(copyRightText);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.get_started){
            boolean isNotVerifiedOrNew = zoeziPreferenceManager.isVerified() == ZoeziStatus.NOT_VERIFIED.ordinal();

            startActivity(new Intent(getApplicationContext(),isNotVerifiedOrNew ? OTPMainActivity.class : MainActivity.class));
            overridePendingTransition(R.anim.right,R.anim.left);
            finish();
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
