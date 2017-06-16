package com.sofientouati.mtnapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LoginActivity extends Activity {
    private ImageView imageView;
    private RelativeLayout relativeLayout;
    private TextInputLayout phonelay;
    private TextInputEditText phonetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageView = (ImageView) findViewById(R.id.applogo);
        relativeLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        phonelay = (TextInputLayout) findViewById(R.id.phoneLay);


        translationAnimation(imageView);


    }


    private void translationAnimation(View v) {
//        int screenHeight=relativeLayout.getHeight();


        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "y", 0, -700)
                .setDuration(1500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                translationAnimation(phonelay);
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
