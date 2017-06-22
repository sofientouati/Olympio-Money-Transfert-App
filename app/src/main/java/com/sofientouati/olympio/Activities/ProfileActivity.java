package com.sofientouati.olympio.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private RelativeLayout phone, mail, credit, money, range;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout homerel;
    private AppBarLayout profileAppBar;
    private LinearLayout parent;
    private ImageButton back, setting;
    private ImageView crediticon, phoneicon, emailicon, moneyicon, rangeicon;
    private TextView phonetext, emailtext, moneytext, rangetext, credittext;
    private String
            blue = "#0072ff";
    private int red = Color.parseColor("#C62828");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //getting views
        //layouts
        parent = (LinearLayout) findViewById(R.id.parent);
        profileAppBar = (AppBarLayout) findViewById(R.id.profileAppBar);
        homerel = (RelativeLayout) findViewById(R.id.homerel);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parentcord);
        //relativeLayout
        phone = (RelativeLayout) findViewById(R.id.phone);
        credit = (RelativeLayout) findViewById(R.id.credit);
        mail = (RelativeLayout) findViewById(R.id.email);
        money = (RelativeLayout) findViewById(R.id.money);
        range = (RelativeLayout) findViewById(R.id.range);


        //btns
        setting = (ImageButton) findViewById(R.id.settings);
        back = (ImageButton) findViewById(R.id.backbtn);
        //image
        phoneicon = (ImageView) findViewById(R.id.phoneicon);
        crediticon = (ImageView) findViewById(R.id.crediticon);
        emailicon = (ImageView) findViewById(R.id.emailicon);
        moneyicon = (ImageView) findViewById(R.id.moneyicon);
        rangeicon = (ImageView) findViewById(R.id.rangeicon);
        //textviews
        phonetext = (TextView) findViewById(R.id.phoneTxt);
        credittext = (TextView) findViewById(R.id.creditTxt);
        emailtext = (TextView) findViewById(R.id.mailTxt);
        moneytext = (TextView) findViewById(R.id.moneyTxt);
        rangetext = (TextView) findViewById(R.id.rangeTxt);

        //others
        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);

        phonetext.setText(sharedPreferences.getString(SharedStrings.SHARED_PHONE, ""));
        credittext.setText(sharedPreferences.getString(SharedStrings.SHARED_CREDIT, "non disponible"));
        emailtext.setText(sharedPreferences.getString(SharedStrings.SHARED_MAIL, "non disponible"));
        moneytext.setText(sharedPreferences.getString(SharedStrings.SHARED_SOLDE, ""));
        rangetext.setText(sharedPreferences.getString(SharedStrings.SHARED_SEUIL, "1000.00") + " XOF");

        if (sharedPreferences.getString(SharedStrings.SHARED_PHONE, "").isEmpty())
            phone.setVisibility(View.GONE);


//        if (sharedPreferences.getString(SharedStrings.SHARED_MAIL, "non disponible").isEmpty())
//            mail.setVisibility(View.GONE);

//        if (sharedPreferences.getString(SharedStrings.SHARED_SOLDE, "").isEmpty())
        money.setVisibility(View.GONE);

        if (sharedPreferences.getString(SharedStrings.SHARED_SEUIL, "1000.00").isEmpty())
            range.setVisibility(View.GONE);

        if (Methods.checkSolde()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(red);
            }
            profileAppBar.setBackgroundColor(red);
            phoneicon.setColorFilter(red);
            crediticon.setColorFilter(red);
            emailicon.setColorFilter(red);
            moneyicon.setColorFilter(red);
            rangeicon.setColorFilter(red);
        }

        //listeners
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                ProfileActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.startActivity(ProfileActivity.this, new Intent(ProfileActivity.this, SettingsActivity.class), null);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //actions
        coordinatorLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)

                    TransitionsStart();
            }
        });

    }

    private void TransitionsStart() {
        int cx = coordinatorLayout.getWidth();
        int cy = coordinatorLayout.getTop();


        float finalRadius = (float) Math.hypot(coordinatorLayout.getWidth(), coordinatorLayout.getHeight());


        final Animator animator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            if (Methods.checkSolde()) {
                homerel.setBackgroundColor(red);
            }

            animator = ViewAnimationUtils.createCircularReveal(coordinatorLayout, cx, cy, finalRadius, 0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    homerel.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
//            animator.setDuration(1000);


            animator.start();
        }


    }

}
