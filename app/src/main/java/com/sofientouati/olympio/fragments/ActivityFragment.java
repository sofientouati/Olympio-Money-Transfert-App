package com.sofientouati.olympio.fragments;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofientouati.olympio.Adapters.ActivityRecyclerViewAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class ActivityFragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private ActivityRecyclerViewAdapter activityRecyclerViewAdapter;
    private RealmResults<ActivityObject> list;
    private SwipeRefreshLayout refresh;
    private TextView empty;
    private Realm realm;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private ValueAnimator coloAnimator;
//    private SharedPreferences sharedPreferences= getActivity().getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
//        return super.onCreateView(inflater, container, savedInstanceState);
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.activityRecyclerView);
        refresh = (SwipeRefreshLayout) viewGroup.findViewById(R.id.refresh);

        if (Methods.checkSolde())
            refresh.setColorSchemeColors(Color.parseColor(red));
        else
            refresh.setColorSchemeColors(Color.parseColor(blue));
        empty = (TextView) viewGroup.findViewById(R.id.empty_view);

        if (loadData()) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            String color = blue;
            if (Methods.checkSolde())
                color = red;
            activityRecyclerViewAdapter = new ActivityRecyclerViewAdapter(getContext(), realm.where(ActivityObject.class)
                    .equalTo("sourceNumber", Methods.getPhone())
                    .or()
                    .equalTo("destinationNumber", Methods.getPhone())
                    .findAllAsync());
            activityRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(activityRecyclerViewAdapter);
            LinearLayoutManager k = new LinearLayoutManager(getContext());
            k.setStackFromEnd(true);
            k.setReverseLayout(true);
            recyclerView.setLayoutManager(k);

        }
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
                    activityRecyclerViewAdapter = new ActivityRecyclerViewAdapter(getContext(), realm.where(ActivityObject.class)
                            .equalTo("sourceNumber", Methods.getPhone())
                            .or()
                            .equalTo("destinationNumber", Methods.getPhone())
                            .findAllAsync().sort("date"));
                    activityRecyclerViewAdapter.notifyDataSetChanged();
                    LinearLayoutManager k = new LinearLayoutManager(getContext());
                    k.setStackFromEnd(true);
                    k.setReverseLayout(true);
                    recyclerView.setLayoutManager(k);

                }
            }
        });
        animateTextView(2000f, Methods.getSolde());


        return viewGroup;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    private boolean loadData() {
//        ArrayList<ActivityObject> list = new ArrayList<ActivityObject>();
//        ActivityObject activityObject = new ActivityObject("1", "97532813", "97532814", "done", "21/10/14 21:20", "retiré", 40.7f);
//        list.add(activityObject);
//        activityObject = new ActivityObject("2", "97532814", "97532813", "pending", "21/11/14 21:20", "déposé", 1000.7f);
//        list.add(activityObject);
//        activityObject = new ActivityObject("3", "97532814", "97888888", "cancel", "88/88/88 88:88", "déposé", 40.7f);
//        list.add(activityObject);
//        activityObject = new ActivityObject("4", "97534814", "97884888", "cancel", "88/28/88 18:88", "retiré", 500.17f);
//        list.add(activityObject);
        Log.i("loadData: ", String.valueOf(realm.where(ActivityObject.class)
                .equalTo("sourceNumber", Methods.getPhone())
                .or()
                .equalTo("destinationNumber", Methods.getPhone())
                .count()));
        return realm.where(ActivityObject.class)
                .equalTo("sourceNumber", Methods.getPhone())
                .or()
                .equalTo("destinationNumber", Methods.getPhone())
                .count() == 0;
    }

    //animations
    private void animateTextView(float initVal, float finalVal) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(initVal, finalVal);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("onAnimationUpdate: ", String.valueOf(animation.getAnimatedValue()));
                if (Float.valueOf(animation.getAnimatedValue().toString()) <= Methods.getSeuil())
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(red));
            }

        });
        valueAnimator.start();
    }

    private void setColor() {
        if (Methods.checkSolde())
            refresh.setColorSchemeColors(Color.parseColor(red));
        else
            refresh.setColorSchemeColors(Color.parseColor(blue));
    }

    private void animateAppAndStatusBar(Integer fromColor, final Integer toColor) {
        coloAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);

        coloAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                refresh.setColorSchemeColors(Color.parseColor(red));

            }
        });
        coloAnimator.setDuration(200);

        coloAnimator.start();

    }
}
