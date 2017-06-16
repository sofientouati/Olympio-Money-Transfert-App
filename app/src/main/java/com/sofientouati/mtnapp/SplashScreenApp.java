package com.sofientouati.mtnapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashScreenApp extends Activity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        imageView = (ImageView) findViewById(R.id.applogo);


        startAnimation();


    }


    public void startAnimation() {
        imageView.setVisibility(View.VISIBLE);
        final Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(2000);
//        animation1.setStartOffset(500);
        final Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(2000);
//        animation2.setStartOffset(500);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animation1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenApp.this, LoginActivity.class));
                overridePendingTransition(0, 0);
            }
        }, 3000);

    }
}
