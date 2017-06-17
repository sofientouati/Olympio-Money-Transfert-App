package com.sofientouati.mtnapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sofientouati.mtnapp.R;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class ActivityFragment extends Fragment {

    private ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_activity, container, false);


        return viewGroup;
    }
}
