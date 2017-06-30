package com.sofientouati.olympio.Activities;

import android.animation.Animator;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Adapters.MainPagerAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;
import com.sofientouati.olympio.fragments.ActivityFragment;
import com.sofientouati.olympio.fragments.DeposeFragment;
import com.sofientouati.olympio.fragments.RetirerFragment;
import com.sofientouati.olympio.fragments.SendFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private SharedPreferences sharedPreferences;

    private LinearLayout linearLayout;
    private AppBarLayout appBarLayout;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager mainviewPager;
    private int[] tabicons = {
            R.drawable.ic_dashboard,
            R.drawable.ic_file_upload_white_36dp,
            R.drawable.ic_near_me_white_36dp,
            R.drawable.ic_file_download_white_36dp,
    };
    private String
            red = "#C62828",
            blue = "#0072ff";
    private RelativeLayout homerel, homerel1, parent;
    private final Runnable revealRunnable = new Runnable() {
        @Override
        public void run() {
            TransitionsStart();
        }
    };

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
        homerel = (RelativeLayout) findViewById(R.id.homerel);
        homerel.setVisibility(View.INVISIBLE);

        homerel1 = (RelativeLayout) findViewById(R.id.homerel1);
        parent = (RelativeLayout) findViewById(R.id.parent);
        appBarLayout = (AppBarLayout) findViewById(R.id.mainAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.home_nav_view);
        ImageButton imageView = (ImageButton) findViewById(R.id.togglebtn);
        ImageButton profile = (ImageButton) findViewById(R.id.profile);
        mainviewPager = (ViewPager) findViewById(R.id.mainViewPager);
        tabLayout = (TabLayout) findViewById(R.id.mainTabLay);
        TextView balance = (TextView) findViewById(R.id.balanceTxt);
        View view = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.home);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView name = (TextView) view.findViewById(R.id.name);
        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, MODE_PRIVATE);

        Methods.getShared(sharedPreferences);
        name.setText(Methods.getName() + " " + Methods.getLastname());
        phone.setText(Methods.getPhone());

        //listeners

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    TransitionsStart();
                    homerel.post(revealRunnable);
                    return;
                }
                ActivityCompat.startActivity(HomeActivity.this, new Intent(HomeActivity.this, ProfileActivity.class), null);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //actions
        animateTextView(2000.00f, Methods.getSolde(), balance);
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
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.bourse:
                startActivity(new Intent(HomeActivity.this, BourseActivity.class));

                break;
            case R.id.proximity:
                startActivity(new Intent(HomeActivity.this, MapsActivity.class));
                break;
            case R.id.convertisseur:
                startActivity(new Intent(HomeActivity.this, ConvertisseurActivity.class));
                break;
            case R.id.simulateurs:
                startActivity(new Intent(HomeActivity.this, SimulateurActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));

                break;
            case R.id.apropos:
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                break;
            case R.id.logout: {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                ActivityCompat.startActivity(HomeActivity.this, new Intent(HomeActivity.this, LoginActivity.class), null);
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
        valueAnimator.setDuration(800);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.format("%.2f", animation.getAnimatedValue()) + " XOF");
                Log.e("onAnimationUpdate: ", String.valueOf(animation.getAnimatedValue()));

                if (Float.valueOf(animation.getAnimatedValue().toString()) < Methods.getSeuil())
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(red));

            }

        });
        valueAnimator.start();
    }

    private void animateAppAndStatusBar(Integer fromColor, final Integer toColor) {
        ValueAnimator coloAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);

        coloAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
               /* appBarLayout.setBackgroundColor((Integer) animation.getAnimatedValue());


                linearLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
                int[][] states = new int[][]{
                        new int[]{android.R.attr.state_checked}, //  checked
                        new int[]{-android.R.attr.state_checked}, // unchecked

                };
                int[] colors = new int[]{
                        (int) animation.getAnimatedValue(),
                        Color.DKGRAY
                };
                navigationView.setItemIconTintList(new ColorStateList(states, colors));
                navigationView.setItemTextColor(new ColorStateList(states, colors));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor((Integer) animation.getAnimatedValue());
                }*/
                setColors((int) animation.getAnimatedValue());

            }
        });
        coloAnimator.setDuration(200);
        coloAnimator.start();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void TransitionsStart() {
        homerel.setVisibility(View.VISIBLE);
        int cx = parent.getWidth();
        int cy = parent.getTop();


        float finalRadius = (float) Math.hypot(homerel.getWidth(), homerel.getHeight());


        final Animator animator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            if (Methods.checkSolde()) {
                homerel.setBackgroundColor(Color.parseColor(red));
            } else
                homerel.setBackgroundColor(Color.parseColor(blue));

            animator = ViewAnimationUtils.createCircularReveal(homerel, cx, cy, 0, finalRadius);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    homerel1.setVisibility(View.GONE);
                    ActivityCompat.startActivity(HomeActivity.this, new Intent(HomeActivity.this, ProfileActivity.class), null);
                    overridePendingTransition(0, 0);
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


    @Override
    protected void onResume() {
        super.onResume();
        homerel.setVisibility(View.GONE);
        homerel1.setVisibility(View.VISIBLE);
        navigationView.setCheckedItem(R.id.home);
        if (Methods.checkSolde()) {
            setColors(Color.parseColor(red));
        } else setColors(Color.parseColor(blue));
        int x = mainviewPager.getCurrentItem();
        mainviewPager.setCurrentItem(3);
        mainviewPager.setCurrentItem(2);
        mainviewPager.setCurrentItem(1);
        mainviewPager.setCurrentItem(0);
        mainviewPager.setCurrentItem(x);
    }

    //setting viewpager
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabOne.setText("Activités");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_dashboard_white_36dp), null, null);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabTwo.setText("Déposer");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_file_upload_white_36dp), null, null);

        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabThree.setText("Envoyer");

        tabThree.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_near_me_white_36dp), null, null);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_main_tab, null);
        tabFour.setText("Retirer");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_file_download_white_36dp), null, null);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mainPagerAdapter.addFragment(new ActivityFragment(), "Activité");
        mainPagerAdapter.addFragment(new DeposeFragment(), "Déposer");
        mainPagerAdapter.addFragment(new SendFragment(), "Envoyer");
        mainPagerAdapter.addFragment(new RetirerFragment(), "Retirer");
        viewPager.setAdapter(mainPagerAdapter);
    }


    private void setColors(int color) {
        appBarLayout.setBackgroundColor(color);
        linearLayout.setBackgroundColor(color);


        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, //  checked
                new int[]{-android.R.attr.state_checked}, // unchecked

        };
        int[] colors = new int[]{
                color,
                Color.DKGRAY
        };
        navigationView.setItemIconTintList(new ColorStateList(states, colors));
        navigationView.setItemTextColor(new ColorStateList(states, colors));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }


}
