package com.sofientouati.mtnapp.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sofientouati.mtnapp.Adapters.MainPagerAdapter;
import com.sofientouati.mtnapp.Objects.SharedStrings;
import com.sofientouati.mtnapp.R;
import com.sofientouati.mtnapp.fragments.ActivityFragment;
import com.sofientouati.mtnapp.fragments.DeposeFragment;
import com.sofientouati.mtnapp.fragments.RetireFragment;
import com.sofientouati.mtnapp.fragments.SendFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private SharedPreferences sharedPreferences;
    private float seuil = 1000f, solde = 0420.55f;
    private View view, f1, f2, f3, f4;
    private LinearLayout linearLayout;
    private ValueAnimator coloAnimator;
    private AppBarLayout appBarLayout;
    private ImageView imageView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager mainviewPager;
    private MainPagerAdapter mainPagerAdapter;
    private TextView balance, phone;
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
    private View home;
    private ImageView iconsf1;
    private SwipeRefreshLayout refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //getting views

        appBarLayout = (AppBarLayout) findViewById(R.id.mainAppBar);
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.home_nav_view);
        imageView = (ImageView) findViewById(R.id.togglebtn);
        mainviewPager = (ViewPager) findViewById(R.id.mainViewPager);
        tabLayout = (TabLayout) findViewById(R.id.mainTabLay);
        balance = (TextView) findViewById(R.id.balanceTxt);
        view = navigationView.getHeaderView(0);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);
        phone = (TextView) view.findViewById(R.id.textView);

        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        phone.setText(sharedPreferences.getString(SharedStrings.SHARED_PHONE, ""));


        //listeners
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //actions

        animateTextView(2000.00f, solde, balance);

        setupViewPager(mainviewPager);


        tabLayout.setupWithViewPager(mainviewPager);
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
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.bourse:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.proximity:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.convertisseur:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.simulateurs:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.apropos:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout: {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //animations
    private void animateTextView(float initVal, float finalVal, final TextView textView) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(initVal, finalVal);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.format("%.2f", animation.getAnimatedValue()) + " XOF");
                Log.e("onAnimationUpdate: ", String.valueOf(animation.getAnimatedValue()));
                if (Float.valueOf(animation.getAnimatedValue().toString()) <= seuil)
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(red));
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


                linearLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
//                Menu menu = null
//                getMenuInflater().inflate(R.menu.activity_home_drawer,menu);
                MenuItem view = (MenuItem) navigationView.findViewById(R.id.home);

//                navigationView.setItemIconTintList(getResources().getColorStateList(R.color.red_item_drawer));
//                navigationView.setItemTextColor(getResources().getColorStateList(R.color.red_item_drawer));
                int[][] states = new int[][]{
                        new int[]{android.R.attr.state_checked}, //  checked
                        new int[]{-android.R.attr.state_checked}, // unchecked

                };


                int[] colors = new int[]{
                        (int) animation.getAnimatedValue(),
                        Color.DKGRAY
                };
//                iconsf1.setColorFilter((int) animation.getAnimatedValue());
//                refresh.setColorSchemeColors(Color.parseColor(blue));
                navigationView.setItemIconTintList(new ColorStateList(states, colors));
                navigationView.setItemTextColor(new ColorStateList(states, colors));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor((Integer) animation.getAnimatedValue());
                }

            }
        });
        coloAnimator.setDuration(200);

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
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mainPagerAdapter.addFragment(new ActivityFragment(), "Activité");
        mainPagerAdapter.addFragment(new DeposeFragment(), "Déposer");
        mainPagerAdapter.addFragment(new SendFragment(), "Envoyer");
        mainPagerAdapter.addFragment(new RetireFragment(), "Retirer");
        viewPager.setAdapter(mainPagerAdapter);
    }


}
