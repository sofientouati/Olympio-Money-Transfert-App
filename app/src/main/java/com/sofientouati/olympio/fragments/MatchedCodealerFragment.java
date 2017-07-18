package com.sofientouati.olympio.fragments;

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

import com.sofientouati.olympio.Adapters.CoDealersRecyclerAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.CodealerObject;
import com.sofientouati.olympio.R;

import io.realm.Realm;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 11/07/17.
 */

public class MatchedCodealerFragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private TextView empty;
    private Realm realm;
    private CoDealersRecyclerAdapter coDealersRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_match_codealers, container, false);

        //get views

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.dealerActivityRecyclerView);
        refresh = (SwipeRefreshLayout) viewGroup.findViewById(R.id.dealerRefresh);
        empty = (TextView) viewGroup.findViewById(R.id.dealerEmpty_view);

        realm = Realm.getDefaultInstance();

        if (loadData()) {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);


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


        return viewGroup;
    }

    private void load() {
        coDealersRecyclerAdapter = new CoDealersRecyclerAdapter(getContext(),
                realm.where(CodealerObject.class)
                        .equalTo("receiver", Methods.getPhone())
                        .equalTo("status", "done")
                        .or()
                        .equalTo("sender", Methods.getPhone())
                        .equalTo("status", "done")
                        .findAllAsync(),
                recyclerView, empty, "matched");
        coDealersRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(coDealersRecyclerAdapter);
        LinearLayoutManager k = new LinearLayoutManager(getContext());
        k.setStackFromEnd(true);
        k.setReverseLayout(true);
        recyclerView.setLayoutManager(k);

    }


    private boolean loadData() {

        boolean x = realm.where(CodealerObject.class)
                .equalTo("receiver", Methods.getPhone())
                .equalTo("status", "done")
                .or()
                .equalTo("sender", Methods.getPhone())
                .equalTo("status", "done")
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

}
