package com.sofientouati.olympio.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.R;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class DeposeFragment extends Fragment {

    private ViewGroup viewGroup;
    private Realm realm;
    private Spinner spinner;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    private EditText number;
    private int
            red = Color.parseColor("#C62828"),
    //            yellow = "#F9A825",
    blue = Color.parseColor("#0072ff");

    private Button button;
    private AlertDialog progress;
    private AlertDialog d;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_depose, container, false);

        realm = Realm.getDefaultInstance();
        list = new ArrayList<>();

        spinner = (Spinner) viewGroup.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        number = (EditText) viewGroup.findViewById(R.id.editText);
        button = (Button) viewGroup.findViewById(R.id.buttond);
        list.add("choix de destination");
        list.add("Telephone");
//        list.add("Carte de Credit");

        if (Methods.checkSolde()) {
            changeColor(red);
        } else changeColor(blue);
        spinner.setAdapter(adapter);

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
                    return;
                }
                performACtion();
            }
        });


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

    private void performACtion() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ActivityObject activityObject = realm.createObject(ActivityObject.class, UUID.randomUUID().toString());
                activityObject.setSourceNumber(Methods.getPhone());
                activityObject.setDestinationNumber(Methods.getPhone());
                activityObject.setAmount(Float.parseFloat(number.getText().toString()));
                activityObject.setAction("depose");
                activityObject.setStatus("pending");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Methods.dismissProgressBar(progress);
                d = new AlertDialog.Builder(getContext())
                        .setTitle("succés")
                        .setMessage("depose d'argent a été envoyé")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

                number.setText("");
                spinner.setSelection(0);

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                d = new AlertDialog.Builder(getContext())
                        .setTitle("erreur")
                        .setMessage("erreur serveur")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (Methods.checkSolde()) {
                changeColor(red);
            } else
                changeColor(blue);
        }

    }

    private void changeColor(int color) {

        Methods.setCursorDrawableColor(number, color);
        Log.i("changeColor: ", String.valueOf(button));
        button.setBackgroundColor(color);
    }
}
