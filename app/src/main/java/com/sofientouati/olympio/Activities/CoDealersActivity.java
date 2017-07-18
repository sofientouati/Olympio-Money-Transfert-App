package com.sofientouati.olympio.Activities;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Adapters.MainPagerAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;
import com.sofientouati.olympio.fragments.AddCodealerFragment;
import com.sofientouati.olympio.fragments.MatchedCodealerFragment;
import com.sofientouati.olympio.fragments.RequestCodealerFragment;

public class CoDealersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarLayout appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int red = Color.parseColor("#C62828");
    private int blue = Color.parseColor("#0072ff");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co_dealers);


        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


        appbar = (AppBarLayout) findViewById(R.id.dealerAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.dealerDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.dealer_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.dealer);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView names = (TextView) view.findViewById(R.id.name);
        ImageView imageView = (ImageView) findViewById(R.id.togglebtns);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.dealerTabLay);
        ViewPager viewPager = (ViewPager) findViewById(R.id.dealerViewPager);


        //actions
        //actions
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);


        phone.setText(Methods.getPhone());
        names.setText(Methods.getName() + " " + Methods.getLastname());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        if (Methods.checkSolde())
            setcolors(red);
        else
            setcolors(blue);

    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.i("onNavigationItemSelected: ", String.valueOf(id));
        switch (id) {
            case R.id.home:
                ActivityCompat.startActivity(CoDealersActivity.this, new Intent(CoDealersActivity.this, HomeActivity.class), null);
                break;
            case R.id.bourse:
                startActivity(new Intent(CoDealersActivity.this, BourseActivity.class));
                break;
            case R.id.proximity:
                startActivity(new Intent(CoDealersActivity.this, MapsActivity.class));
                break;
            case R.id.convertisseur:
                startActivity(new Intent(CoDealersActivity.this, ConvertisseurActivity.class));
                break;
            case R.id.simulateurs:
                startActivity(new Intent(CoDealersActivity.this, SimulateurActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(CoDealersActivity.this, SettingsActivity.class));
                break;
            case R.id.apropos:
                startActivity(new Intent(CoDealersActivity.this, AboutActivity.class));
                break;
            case R.id.logout: {
                editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                ActivityCompat.startActivity(CoDealersActivity.this, new Intent(CoDealersActivity.this, LoginActivity.class), null);
                finish();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setcolors(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, //  checked
                new int[]{-android.R.attr.state_checked}, // unchecked

        };
        int[] colors = new int[]{
                color,
                Color.DKGRAY
        };
        appbar.setBackgroundColor(color);
        navigationView.setItemIconTintList(new ColorStateList(states, colors));
        navigationView.setItemTextColor(new ColorStateList(states, colors));
        linearLayout.setBackgroundColor(color);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Methods.checkSolde())
            setcolors(red);
        else
            setcolors(blue);
    }


    private void setupViewPager(ViewPager viewPager) {
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mainPagerAdapter.addFragment(new RequestCodealerFragment(), "Demandes");
        mainPagerAdapter.addFragment(new AddCodealerFragment(), "Nouveaux");
        mainPagerAdapter.addFragment(new MatchedCodealerFragment(), "Ajoutés");
        viewPager.setAdapter(mainPagerAdapter);
    }


}
