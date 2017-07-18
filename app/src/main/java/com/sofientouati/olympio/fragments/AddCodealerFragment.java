package com.sofientouati.olympio.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sofientouati.olympio.Adapters.AddCodealersRecyclerAdapter;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.CodealerObject;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import io.realm.Realm;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 11/07/17.
 */

public class AddCodealerFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    private static final int PICK_CONTACT = 1;
    private ViewGroup viewGroup;

    private RecyclerView recyclerView;
    private TextView empty;
    private ImageButton imageButton;
    private SearchView searchView;
    private AddCodealersRecyclerAdapter addCodealersRecyclerAdapter;
    private Realm realm;
    private boolean isStarted;
    private boolean isVisible;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //getting views
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_add_codealers, container, false);

        searchView = (SearchView) viewGroup.findViewById(R.id.search);
        imageButton = (ImageButton) viewGroup.findViewById(R.id.contacts);

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.dealerActivityRecyclerView);
        empty = (TextView) viewGroup.findViewById(R.id.dealerEmpty_view);


        realm = Realm.getDefaultInstance();
        load();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.i("onQueryTextChange: ", newText);
                addCodealersRecyclerAdapter.filterResults(newText);


                return false;
            }
        });

        //listeners
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission();
            }
        });
        return viewGroup;
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
                            searchView.setQuery(x.replace("-", ""), true);

                        }
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }
                }
        }
    }

    private void load() {
        addCodealersRecyclerAdapter = new AddCodealersRecyclerAdapter(getContext(),
                realm.where(UserObject.class)
                        .findAllAsync(),
                recyclerView,
                empty,
                searchView
        );
        addCodealersRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(addCodealersRecyclerAdapter);
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


    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
        if (!isVisible) {

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
        isVisible = isVisibleToUser;
        if (!isVisible && isStarted) {
//            activityRecyclerViewAdapter.notifyDataSetChanged();

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);


        }
    }
}

