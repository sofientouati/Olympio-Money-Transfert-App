package com.sofientouati.mtnapp.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.mtnapp.Methods;
import com.sofientouati.mtnapp.Objects.TelephonyInfo;
import com.sofientouati.mtnapp.R;

import java.util.ArrayList;

public class LoginActivity extends Activity {
    private ImageView imageView;
    private RelativeLayout mainlay, form;
    private LinearLayout btns;
    private Button signupbtn, loginbtn;
    private TextView bienvenu;
    private AutoCompleteTextView phone;
    private ArrayList<String> numbers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//getting views
        imageView = (ImageView) findViewById(R.id.logoapp);
        mainlay = (RelativeLayout) findViewById(R.id.mainlay);
        form = (RelativeLayout) findViewById(R.id.form);
        signupbtn = (Button) findViewById(R.id.signupbtn);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        btns = (LinearLayout) findViewById(R.id.btns);
        bienvenu = (TextView) findViewById(R.id.bienvenu);
        phone = (AutoCompleteTextView) findViewById(R.id.phoneTxt);
        signupbtn.setBackgroundColor(Color.WHITE);
        loginbtn.setBackgroundColor(Color.WHITE);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CheckPatternActivity.class));
            }
        });


        if ((getIntent().getStringExtra("class") != null) && (getIntent().getStringExtra("class").equals("SplashScreen"))) {

            startYTranslation(imageView);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

            imageView.setLayoutParams(layoutParams);
            form.setVisibility(View.VISIBLE);
            btns.setVisibility(View.VISIBLE);

        }



    }


    private ArrayList<String> getPermissions() {
        ArrayList<String> x = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.READ_PHONE_STATE;
            Log.i("checkPermission: ", permission);
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            } else {

                getPhoneNumber();
            }
        } else {
            getPhoneNumber();
        }
        return x;
    }

    private void getPhoneNumber() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        String imsiSIM1 = telephonyInfo.getImsiSIM1();
        String imsiSIM2 = telephonyInfo.getImsiSIM2();
        String strMobileNumber;
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (isSIM1Ready) {

            strMobileNumber = tm.getLine1Number();
            numbers.add(strMobileNumber);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, numbers);
            phone.setAdapter(arrayAdapter);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Methods.showSnackBar(form, "permis");
                    getPhoneNumber();


//                    captureImage();
//                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Methods.showSnackBar(form, "non permis");


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }
        }
    }

    private int x = 0;

    //animations
    private void fadeIn(View v) {
        v.setVisibility(View.VISIBLE);
        final Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(1000);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btns.setVisibility(View.VISIBLE);
                startXTranslation(bienvenu, 300, 1500, true, 1000);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation1);
    }

    private void startYTranslation(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "y", 500, 0)
                .setDuration(1000);
        animator.setStartDelay(200);
//        animator.setInterpolator();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (x == 0) {
                    fadeIn(bienvenu);
                    x = 1;
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }

    private void startXTranslation(final View v, int x, int y, final Boolean first, int delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "x", x, y)
                .setDuration(1000);
        animator.setStartDelay(delay);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (first == true) {
                    startXTranslation(form, -1000, 50, false, 0);
                    return;
                }
                getPermissions();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}
