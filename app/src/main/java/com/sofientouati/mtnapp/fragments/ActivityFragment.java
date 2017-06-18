package com.sofientouati.mtnapp.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofientouati.mtnapp.Adapters.ActivityRecyclerViewAdapter;
import com.sofientouati.mtnapp.Objects.ActivityObject;
import com.sofientouati.mtnapp.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.empty;
import static android.R.id.list;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class ActivityFragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private ActivityRecyclerViewAdapter activityRecyclerViewAdapter;
    private ArrayList<ActivityObject> list = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private TextView empty;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.activityRecyclerView);
        refresh = (SwipeRefreshLayout) viewGroup.findViewById(R.id.refresh);
        refresh.setColorSchemeColors(getResources().getColor(R.color.redtoolbar));
        empty = (TextView) viewGroup.findViewById(R.id.empty_view);
        list = loadData();
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            activityRecyclerViewAdapter = new ActivityRecyclerViewAdapter(list, getContext(), Color.parseColor(red));
            activityRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(activityRecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        }
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list = loadData();
                refresh.setRefreshing(false);
                if (list.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });


        return viewGroup;
    }

    private ArrayList<ActivityObject> loadData() {
        ArrayList<ActivityObject> list = new ArrayList<ActivityObject>();
        ActivityObject activityObject = new ActivityObject("1", "97532813", "97532814", "done", "21/10/14 21:20", "retiré", 40.7f);
        list.add(activityObject);
        activityObject = new ActivityObject("2", "97532814", "97532813", "pending", "21/11/14 21:20", "déposé", 1000.7f);
        list.add(activityObject);
        activityObject = new ActivityObject("3", "97532814", "97888888", "cancel", "88/88/88 88:88", "déposé", 40.7f);
        list.add(activityObject);
        activityObject = new ActivityObject("4", "97534814", "97884888", "cancel", "88/28/88 18:88", "retiré", 500.17f);
        list.add(activityObject);
        return list;
    }
}
