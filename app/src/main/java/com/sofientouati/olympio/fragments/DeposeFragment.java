package com.sofientouati.olympio.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.R;

import java.util.ArrayList;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class DeposeFragment extends Fragment {

    private ViewGroup viewGroup;
    private Spinner spinner;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    private EditText number;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";

    private Button button;
    private AlertDialog progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_depose, container, false);
        list = new ArrayList<>();
        list.add("choix de destination");
        list.add("Telephone");
        list.add("Carte de Credit");
        spinner = (Spinner) viewGroup.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        number = (EditText) viewGroup.findViewById(R.id.editText);
        button = (Button) viewGroup.findViewById(R.id.button);

        if (Methods.checkSolde()) {

            Methods.setCursorDrawableColor(number, Color.parseColor(red));
            Drawable drawable = number.getBackground();
            drawable.setColorFilter(Color.parseColor(red), PorterDuff.Mode.SRC_ATOP);
            number.setBackground(drawable);
            button.setBackgroundColor(Color.parseColor(red));
        }


        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    number.clearFocus();
                    spinner.requestFocus();
                    spinner.performClick();

                }
                return true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = Methods.showProgressBar(getContext(), "Chargement...");
                if (!submitForm()) {
                    Methods.dismissProgressBar(progress);
                }
            }
        });
        spinner.setAdapter(adapter);

        return viewGroup;
    }

    private boolean submitForm() {


        if (!isValidSpinner() || !isValidAmount()) {
            if (!isValidAmount()) {
                number.requestFocus();
                return false;
            }
            if (!isValidSpinner()) {
                spinner.requestFocus();
                return false;
            }

        }


        return true;
    }

    private boolean isValidAmount() {
        String numberT = number.getText().toString().trim();

        if (numberT.isEmpty() || numberT.equals("0")) {
            number.setError("entrer un montant");
            return false;
        }
        return true;
    }

    private boolean isValidSpinner() {
        int i = spinner.getSelectedItemPosition();

        if (i == 0) {
            ((TextView) spinner.getSelectedView()).setError("champs obligatoire");
            return false;
        }
        return true;
    }


}
