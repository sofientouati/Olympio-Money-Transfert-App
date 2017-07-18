package com.sofientouati.olympio.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sofientouati.olympio.Adapters.ConfirmActivityRecyclerViewAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sofirntouati on 07/07/17.
 */

public class ConfirmTransactionActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private ConfirmActivityRecyclerViewAdapter activityRecyclerViewAdapter;
    private RealmResults<ActivityObject> list;
    private SwipeRefreshLayout refresh;
    private TextView empty;
    private Realm realm;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm_transaction);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }

        //views
        imageButton = (ImageButton) findViewById(R.id.backbtn);
        appBarLayout = (AppBarLayout) findViewById(R.id.confirmAppBar);
        recyclerView = (RecyclerView) findViewById(R.id.confirmActivityRecyclerView);
        refresh = (SwipeRefreshLayout) findViewById(R.id.confirmRefresh);
        empty = (TextView) findViewById(R.id.confirmEmpty_view);

        //values
        realm = Realm.getDefaultInstance();

//actions
        setColor();
        if (loadData()) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            String color = blue;

            load();


        }


        //listeners
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                if (loadData()) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    load();

                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ConfirmTransactionActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                finish();
            }
        });


    }


    private void load() {
        activityRecyclerViewAdapter = new ConfirmActivityRecyclerViewAdapter(this, realm.where(ActivityObject.class)
                .equalTo("action", "envoi")
                .equalTo("destinationNumber", Methods.getPhone())
                .equalTo("status", "pending")
                .findAllAsync(), recyclerView, empty);
        activityRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(activityRecyclerViewAdapter);
        LinearLayoutManager k = new LinearLayoutManager(this);
        k.setStackFromEnd(true);
        k.setReverseLayout(true);
        recyclerView.setLayoutManager(k);
        setColor();
    }


    private boolean loadData() {

        boolean x = realm.where(ActivityObject.class)
                .equalTo("action", "envoi")
                .equalTo("destinationNumber", Methods.getPhone())
                .equalTo("status", "pending")
                .count() == 0;
        if (x) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
        return x;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        setColor();
    }

    private void setColor() {
        if (Methods.checkSolde()) {
            refresh.setColorSchemeColors(Color.parseColor(red));
            appBarLayout.setBackgroundColor(Color.parseColor(red));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(red));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(blue));
            }
            refresh.setColorSchemeColors(Color.parseColor(blue));
            appBarLayout.setBackgroundColor(Color.parseColor(blue));
        }
    }


}