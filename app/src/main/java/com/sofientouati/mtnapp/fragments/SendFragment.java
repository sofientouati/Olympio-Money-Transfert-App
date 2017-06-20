package com.sofientouati.mtnapp.fragments;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sofientouati.mtnapp.Methods;
import com.sofientouati.mtnapp.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sofientouati.mtnapp.R.id.editText;
import static com.sofientouati.mtnapp.R.id.linearLayout;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class SendFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    private ViewGroup viewGroup;
    private EditText number, amount;
    private TextView max;
    private static final int PICK_CONTACT = 1;
    private Button button;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private float seuil = 1000f, solde = 0420.55f;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_send, container, false);
        button = (Button) viewGroup.findViewById(R.id.button);

        max = (TextView) viewGroup.findViewById(R.id.max);
        max.setText(String.valueOf(solde));
        amount = (EditText) viewGroup.findViewById(R.id.editText);
        number = (EditText) viewGroup.findViewById(R.id.number);
        if (seuil > solde) {
            button.setBackgroundColor(Color.parseColor(red));
            Methods.setCursorDrawableColor(number, Color.parseColor(red));
            Methods.setCursorDrawableColor(amount, Color.parseColor(red));
        }


        number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getRawX() <= (number.getLeft() + number.getCompoundDrawables()[0].getBounds().width()))
                            // Declare
                            checkPermission();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:


                }
                return v.onTouchEvent(event);


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

        return viewGroup;
    }

    private boolean submitForm() {
        if (!isValidNum() || !isValidAmount()) {

            if (!isValidAmount()) {
                amount.requestFocus();
                return false;
            }
            if (!isValidNum()) {
                number.requestFocus();
                return false;
            }
        }

        return true;
    }

    private boolean isValidAmount() {
        String numberT = amount.getText().toString().trim();

        if (numberT.isEmpty() || numberT.equals("0")) {
            amount.setError("entrer un montant");
            return false;
        }
        return true;
    }

    private boolean isValidNum() {
        String numberT = number.getText().toString().trim();
        Pattern pattern = Pattern.compile("(97.*\\d{6,}$)");
        Matcher matcher = pattern.matcher(numberT);
        if (numberT.isEmpty() || !matcher.matches()) {
            number.setError("format de numéro de téléphone invalide");
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String x = phones.getString(phones.getColumnIndex("data1"));
                            x = x.replace(" ", "");
                            number.setText(x.replace("-", ""));

                        }
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("onRequestPermissionsResult: ", String.valueOf(requestCode));
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Methods.showSnackBar(viewGroup, "permis");

                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT);


//                    captureImage();
//                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Methods.showSnackBar(viewGroup, "non permis");


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT);
            }
        } else {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT);

        }
    }


}
