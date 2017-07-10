package com.sofientouati.olympio.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.R;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

/**
 * Created by sofirntouati on 17/06/17.
 */

public class SendFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    private static final int PICK_CONTACT = 1;
    Realm realm;
    private ViewGroup viewGroup;
    private EditText number, amount;
    private TextView max;
    private Button button;
    private int red = Color.parseColor("#C62828"),
            blue = Color.parseColor("#0072ff");

    private AlertDialog progress;
    private RelativeLayout view;
    private TextView empty;
    private AlertDialog d;
    private boolean isVisible;
    private boolean isStarted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_send, container, false);
        button = (Button) viewGroup.findViewById(R.id.buttons);
        realm = Realm.getDefaultInstance();
        view = (RelativeLayout) viewGroup.findViewById(R.id.view);
        empty = (TextView) viewGroup.findViewById(R.id.emptyView);
        max = (TextView) viewGroup.findViewById(R.id.max);
        max.setText(String.format("%.2f", Methods.getSolde()));

        amount = (EditText) viewGroup.findViewById(R.id.editText);
        number = (EditText) viewGroup.findViewById(R.id.number);


        if (Methods.getSolde() <= 0) {
            view.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
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
                progress = Methods.showProgressBar(getContext(), "Chargement...", true);
                if (!submitForm()) {
                    Methods.dismissProgressBar(progress);
                    return;
                }
                performAction();

            }
        });
        setColors();

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
        if (Float.valueOf(numberT) > Methods.getSolde()) {
            amount.setError("solde insuffisant");
            return false;
        }
        return true;
    }

    private boolean isValidNum() {
        String numberT = number.getText().toString().trim();
        Pattern pattern = Pattern.compile("97(\\d{6})");
        Matcher matcher = pattern.matcher(numberT);
        if (numberT.isEmpty() || !matcher.matches()) {
            number.setError("format de numéro de téléphone invalide");
            return false;
        }
        if (numberT.equals(Methods.getPhone())) {
            number.setError("on ne peut pas envoyer au même numéro");
            return false;
        }
        return true;
    }

    private void performAction() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ActivityObject activityObject = realm.createObject(ActivityObject.class, UUID.randomUUID().toString());
                activityObject.setSourceNumber(Methods.getPhone());
                activityObject.setDestinationNumber(number.getText().toString());
                activityObject.setAmount(Float.parseFloat(amount.getText().toString()));
                activityObject.setAction("envoi");
                activityObject.setStatus("pending");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Methods.dismissProgressBar(progress);
                d = new AlertDialog.Builder(getContext())
                        .setTitle("succés")
                        .setMessage("montant a été envoyé")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

                number.setText("");
                amount.setText("");


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                d = new AlertDialog.Builder(getContext())
                        .setTitle("erreur")
                        .setMessage("erreur de serveur")
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
                            assert phones != null;
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

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
        if (isVisible) {
            if (Methods.checkSolde()) {
                changeColor(red);
            } else {
                changeColor(blue);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible && isStarted)

            setColors();
    }

    private void setColors() {
        if (Methods.checkSolde()) {
            changeColor(red);
        } else {
            changeColor(blue);
        }
    }

    private void changeColor(int color) {

        button.setBackgroundColor(color);
        Methods.setCursorDrawableColor(number, color);
        Methods.setCursorDrawableColor(amount, color);

    }
}
