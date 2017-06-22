package com.sofientouati.olympio.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private AppBarLayout appbar;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private ImageView imageView;
    private Button coordbtn, seuilbtn, passbtn;
    private EditText nameTxt, lnameTxt, mailTxt, seuilTxt, opassTxt, npassTxt, cnpassTxt;
    private ExpandableRelativeLayout coord, seuil, pass;
    private TextView coordTxt, seuilT, passTxt;
    private int red = Color.parseColor("#C62828");
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
//layouts
        appbar = (AppBarLayout) findViewById(R.id.settingsAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.settingsDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.settings_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.settings);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);
        TextView phone = (TextView) view.findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.togglebtns);

        //buttons
        coordbtn = (Button) findViewById(R.id.coordbtn);
        seuilbtn = (Button) findViewById(R.id.suilebtn);
        passbtn = (Button) findViewById(R.id.passbtn);

        //ExpandableRelativeLayout
        coord = (ExpandableRelativeLayout) findViewById(R.id.coordrels);
        seuil = (ExpandableRelativeLayout) findViewById(R.id.seurels);
        pass = (ExpandableRelativeLayout) findViewById(R.id.passrels);

        //EditText
        nameTxt = (EditText) findViewById(R.id.nom);
        lnameTxt = (EditText) findViewById(R.id.prenom);
        mailTxt = (EditText) findViewById(R.id.email);
        seuilTxt = (EditText) findViewById(R.id.suile);
        opassTxt = (EditText) findViewById(R.id.opass);
        npassTxt = (EditText) findViewById(R.id.npass);
        cnpassTxt = (EditText) findViewById(R.id.cnpass);

        //textView
        coordTxt = (TextView) findViewById(R.id.txt);
        seuilT = (TextView) findViewById(R.id.text2);
        passTxt = (TextView) findViewById(R.id.text3);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        coordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seuil.isExpanded() || pass.isExpanded()) {
                    seuil.collapse();
                    pass.collapse();
                }
                coord.toggle();

            }
        });

        seuilT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coord.isExpanded() || pass.isExpanded()) {
                    coord.collapse();
                    pass.collapse();
                }
                seuil.toggle();
            }
        });
        passTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass.toggle();
                if (coord.isExpanded() || seuil.isExpanded()) {
                    coord.collapse();
                    seuil.collapse();
                }
                pass.toggle();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        coordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "sauvegarde");
                if (!submitCoordForm()) {
                    Methods.dismissProgressBar(progressDialog);
                }

            }
        });


        //actions
        setcolors();


        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        phone.setText(sharedPreferences.getString(SharedStrings.SHARED_PHONE, ""));


    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (coord.isExpanded() || seuil.isExpanded() || pass.isExpanded()) {

            coord.collapse();
            seuil.collapse();
            pass.collapse();
            return;
        }


        super.onBackPressed();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.i("onNavigationItemSelected: ", String.valueOf(id));
        switch (id) {
            case R.id.home:
                ActivityCompat.startActivity(SettingsActivity.this, new Intent(SettingsActivity.this, HomeActivity.class), null);
                break;
            case R.id.bourse:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.proximity:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.convertisseur:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.simulateurs:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.apropos:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout: {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                ActivityCompat.startActivity(SettingsActivity.this, new Intent(SettingsActivity.this, LoginActivity.class), null);
                finish();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.settings);

    }


    private void setcolors() {
        if (Methods.checkSolde()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(red);
            }

            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked}, //  checked
                    new int[]{-android.R.attr.state_checked}, // unchecked

            };
            int[] colors = new int[]{
                    red,
                    Color.DKGRAY
            };
            appbar.setBackgroundColor(red);
            navigationView.setItemIconTintList(new ColorStateList(states, colors));
            navigationView.setItemTextColor(new ColorStateList(states, colors));
            linearLayout.setBackgroundColor(red);
            Methods.setCursorDrawableColor(nameTxt, red);
            Methods.setCursorDrawableColor(lnameTxt, red);
            Methods.setCursorDrawableColor(mailTxt, red);
            Methods.setCursorDrawableColor(seuilTxt, red);
            Methods.setCursorDrawableColor(opassTxt, red);
            Methods.setCursorDrawableColor(npassTxt, red);
            Methods.setCursorDrawableColor(cnpassTxt, red);
            coordbtn.setBackgroundColor(red);
            seuilbtn.setBackgroundColor(red);
            passbtn.setBackgroundColor(red);
        }
    }


    //validating
    private boolean submitCoordForm() {
        if (!validateEmail(mailTxt) || !validateName(lnameTxt) || !validateName(nameTxt)) {

            /*if (!validatePassword(opassTxt)) {
                opassTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(npassTxt)) {
                npassTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(cnpassTxt)) {
                cnpassTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }*/
            if (!validateName(nameTxt)) {
                nameTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(lnameTxt)) {
                lnameTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateEmail(mailTxt)) {
                mailTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }


        }
        return true;
    }


    private boolean validatePassword(EditText pass) {
        String password = pass.getText().toString().trim();
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$");
        Matcher match = pattern.matcher(password);


        if (!match.matches()) {
            pass.requestFocus();
//            if (password.isEmpty()) {
//                pass.setError("champs obligatoire");
//
//                return false;
//            }
            pass.setError("mot de passe doit au moins six character contenant une lettre majuscule,miniscule et un chiffre");
            return false;
        }


        return true;

    }

    private boolean validateEmail(EditText email) {
        String emails = email.getText().toString().trim();

        if (!TextUtils.isEmpty(emails) && android.util.Patterns.EMAIL_ADDRESS.matcher(emails).matches()) {
            email.requestFocus();
//            if (emails.isEmpty()) {
//                email.setError("champs obligatoire");
//
//                return false;
//            }
            email.setError("mot de passe doit au moins six character contenant une lettre majuscule,miniscule et un chiffre");
            return false;
        }


        return true;

    }

    private boolean validateConfirmPassword(EditText editText) {
        String password = editText.getText().toString().trim();
        if (password.isEmpty() || !password.equals(editText.getText().toString().trim())) {
            editText.requestFocus();
//            if (password.isEmpty())
//                editText.setError("champs obligatoire");

            if (!password.equals(editText.getText().toString().trim()))
                editText.setError("les 2 mots de passes ne sont pas les mêmes ");

            return false;
        }
        return true;
    }

    private boolean validateName(EditText text) {
        String lastnameS = text.getText().toString();
        Pattern pattern = Pattern.compile("(\\w){2,}?");
        Matcher matcher = pattern.matcher(lastnameS);
        if (!matcher.matches() && !lastnameS.isEmpty()) {
            text.requestFocus();
//            if (lastnameS.isEmpty()) {
//                text.setError("champs obligatoire");
//                return false;
//            }
            text.setError("format de numéro de téléphone invalide");

            return false;
        }
        return true;
    }
}
