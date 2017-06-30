package com.sofientouati.olympio.Activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
    private TextView phonetext, emailtext, moneytext, rangetext, credittext, fullname;
    private int red = Color.parseColor("#C62828");
    private int blue = Color.parseColor("#0072ff");


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
        fullname = (TextView) findViewById(R.id.fullname);

        //others
        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        fullname.setText(Methods.getName() + ' ' + Methods.getLastname());
        phonetext.setText(Methods.getPhone());
//        credittext.setText(Methods.get));
        moneytext.setText(Methods.getSolde() + " XOF");
        rangetext.setText(Methods.getSeuil() + " XOF");

        credit.setVisibility(View.GONE);
        if (Methods.getPhone().isEmpty())
            phone.setVisibility(View.GONE);


        if (Methods.getMail().isEmpty()) {
            mail.setVisibility(View.GONE);
        } else emailtext.setText(Methods.getMail());


        if (Methods.checkSolde()) {
            changeColor(red);
        } else changeColor(blue);

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
        fullname.setText(Methods.getName() + ' ' + Methods.getLastname());
        phonetext.setText(Methods.getPhone());
//        credittext.setText(Methods.get));
        moneytext.setText(Methods.getSolde() + " XOF");
        rangetext.setText(Methods.getSeuil() + " XOF");

        credit.setVisibility(View.GONE);
        if (Methods.getPhone().isEmpty())
            phone.setVisibility(View.GONE);


        if (Methods.getMail().isEmpty()) {
            mail.setVisibility(View.GONE);
        } else {
            mail.setVisibility(View.VISIBLE);
            emailtext.setText(Methods.getMail());
        }


        if (Methods.checkSolde()) {
            changeColor(red);
        } else {
            changeColor(blue);
            emailtext.setText(Methods.getMail());

        }


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
                else
                    homerel.setVisibility(View.GONE);
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


    private void changeColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
        profileAppBar.setBackgroundColor(color);
        phoneicon.setColorFilter(color);
        crediticon.setColorFilter(color);
        emailicon.setColorFilter(color);
        moneyicon.setColorFilter(color);
        rangeicon.setColorFilter(color);
    }

}
