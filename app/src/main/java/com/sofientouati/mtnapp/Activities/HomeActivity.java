package com.sofientouati.mtnapp.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sofientouati.mtnapp.Adapters.MainPagerAdapter;
import com.sofientouati.mtnapp.R;
import com.sofientouati.mtnapp.fragments.ActivityFragment;
import com.sofientouati.mtnapp.fragments.DeposeFragment;
import com.sofientouati.mtnapp.fragments.RetireFragment;
import com.sofientouati.mtnapp.fragments.SendFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private View view;
    private LinearLayout linearLayout;
    private ValueAnimator coloAnimator;
    private AppBarLayout appBarLayout;
    private ImageView imageView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager mainviewPager;
    private MainPagerAdapter mainPagerAdapter;
    private TextView balance;
    private TabLayout tabLayout;
    private int[] tabicons = {
            R.drawable.ic_dashboard_white_36dp,
            R.drawable.ic_file_upload_white_36dp,
            R.drawable.ic_near_me_white_36dp,
            R.drawable.ic_file_download_white_36dp,
    };

    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getting views
        view = findViewById(R.id.home);
        appBarLayout = (AppBarLayout) findViewById(R.id.mainAppBar);
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.home_nav_view);
        imageView = (ImageView) findViewById(R.id.togglebtn);
        mainviewPager = (ViewPager) findViewById(R.id.mainViewPager);
        tabLayout = (TabLayout) findViewById(R.id.mainTabLay);
        balance = (TextView) findViewById(R.id.balanceTxt);


        //listeners
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //actions

        animateTextView(3000.00f, 1000.14f, balance);

        setupViewPager(mainviewPager);

        tabLayout.setupWithViewPager(mainviewPager, true);
        setupTabIcons();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.bourse:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.proximity:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.convertisseur:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.simulateurs:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.apropos:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout:
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //animations
    private void animateTextView(float initVal, float finalVal, final TextView textView) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(initVal, finalVal);
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.format("%.2f", valueAnimator.getAnimatedValue()) + " XOF");
                if ((float) valueAnimator.getAnimatedValue() < 2000f) {
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(yellow));

                }
                if ((float) valueAnimator.getAnimatedValue() < 1000f) {
                    animateAppAndStatusBar(Color.parseColor(yellow), Color.parseColor(red));
                }

            }

        });
        valueAnimator.start();
    }

    private void animateAppAndStatusBar(Integer fromColor, final Integer toColor) {
        coloAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);

        coloAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                appBarLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
                linearLayout = (LinearLayout) findViewById(R.id.navheaderlayout);
                linearLayout.setBackgroundColor(toColor);
//                Menu menu = null
//                getMenuInflater().inflate(R.menu.activity_home_drawer,menu);

//                navigationView.setItemIconTintList(ColorStateList.valueOf(toColor));
//                navigationView.setItemTextColor(ColorStateList.valueOf(toColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor((Integer) animation.getAnimatedValue());
                }

            }
        });
        coloAnimator.setDuration(500);

        coloAnimator.start();

    }


    //setting viewpager
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabOne.setText("Activités");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabicons[0], 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabTwo.setText("Déposer");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, tabicons[1], 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabThree.setText("Envoyer");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, tabicons[2], 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabFour.setText("Retirer");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, tabicons[3], 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPagerAdapter.addFragment(new ActivityFragment(), "Activité");
        mainPagerAdapter.addFragment(new DeposeFragment(), "Déposer");
        mainPagerAdapter.addFragment(new SendFragment(), "Envoyer");
        mainPagerAdapter.addFragment(new RetireFragment(), "Retirer");
        viewPager.setAdapter(mainPagerAdapter);
    }


}
