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

public class DeposeFragment extends Fragment {

    private ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_depose, container, false);


        return viewGroup;
    }
}
