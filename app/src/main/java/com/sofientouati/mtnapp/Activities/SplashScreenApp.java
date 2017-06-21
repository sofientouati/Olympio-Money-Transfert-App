package com.sofientouati.mtnapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.sofientouati.mtnapp.Objects.SharedStrings;
import com.sofientouati.mtnapp.R;

import java.util.ArrayList;

public class SplashScreenApp extends Activity {
    private ImageView imageView;
    private ArrayList<String> permissions;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);

        imageView = (ImageView) findViewById(R.id.logoapp);
        startAnimation();


    }


    public void startAnimation() {
        final Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(1000);
//        animation1.setStartOffset(1000);
        final Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(1000);
//        animation2.setStartOffset(1000);
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
        checkPermissions();
        imageView.startAnimation(animation2);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                checkPermissions();
                Intent intent;
                boolean logged = sharedPreferences.getBoolean(SharedStrings.SHARED_ISLOGGED, false);
                Log.i("run: logged", String.valueOf(logged));
                if (!logged) {

                    intent = new Intent(SplashScreenApp.this, LoginActivity.class);
                    intent.putExtra("class", "SplashScreen");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();


                } else {

                    intent = new Intent(SplashScreenApp.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);


    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> x = new ArrayList<String>();
            x.add(Manifest.permission.READ_PHONE_STATE);
            x.add(Manifest.permission.READ_CONTACTS);
            x.add(Manifest.permission.CAMERA);
            x.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            x.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] permissions = new String[10];
            for (int i = 0; i < x.size(); i++) {
                if (ContextCompat.checkSelfPermission(this, x.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    permissions[i] = x.get(i);
                }
            }
            ActivityCompat.requestPermissions(this, new String[]{x.get(0), x.get(1), x.get(2), x.get(3)}, 1);


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                checkPermissions();
                    Intent intent;
                    boolean logged = sharedPreferences.getBoolean(SharedStrings.SHARED_ISLOGGED, false);
                    Log.i("run: logged", String.valueOf(logged));
                    if (!logged) {

                        intent = new Intent(SplashScreenApp.this, LoginActivity.class);
                        intent.putExtra("class", "SplashScreen");
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();


                    } else {

                        intent = new Intent(SplashScreenApp.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, 2000);
        }

    }
}
