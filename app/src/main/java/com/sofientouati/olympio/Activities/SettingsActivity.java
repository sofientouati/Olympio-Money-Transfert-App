package com.sofientouati.olympio.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView names;
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
    private int blue = Color.parseColor("#0072ff");
    private AlertDialog progressDialog;
    private Realm realm;
    private AlertDialog d;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, MODE_PRIVATE);
//layouts
        realm = Realm.getDefaultInstance();
        appbar = (AppBarLayout) findViewById(R.id.settingsAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.settingsDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.settings_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.settings);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);

        TextView phone = (TextView) view.findViewById(R.id.phone);
        names = (TextView) view.findViewById(R.id.name);
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

        SharedPreferences sharedPreferences = this.getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        coordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
                if (seuil.isExpanded() || pass.isExpanded()) {
                    seuil.collapse();
                    pass.collapse();
                }
                if (!coord.isExpanded())
                    nameTxt.requestFocus();
                coord.toggle();

            }
        });

        seuilT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
                if (coord.isExpanded() || pass.isExpanded()) {
                    coord.collapse();
                    pass.collapse();
                }
                if (!seuil.isExpanded())
                    seuilTxt.requestFocus();
                seuil.toggle();
            }
        });
        passTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
                pass.toggle();
                if (coord.isExpanded() || seuil.isExpanded()) {
                    coord.collapse();
                    seuil.collapse();
                }
                if (!pass.isExpanded())
                    opassTxt.requestFocus();
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
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "sauvegarder", true);
                if (!submitCoordForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return;

                }
                PerformCoordAction();

            }
        });

        mailTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "sauvegarder", true);
                if (!submitCoordForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return false;

                }
                PerformCoordAction();
                return true;
            }
        });


        seuilbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "sauvegarder", true);
                if (!submitSeuilForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return;
                }
                PerformSeuilAction();
            }
        });


        seuilTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "sauvegarder", true);
                if (!submitSeuilForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return false;
                }
                PerformSeuilAction();
                return true;
            }
        });
        passbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "mettre à jours", true);
                if (!submitPassForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return;
                }
                PerformPassAction();

            }
        });

        cnpassTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                progressDialog = Methods.showProgressBar(SettingsActivity.this, "mettre à jours", true);
                if (!submitPassForm()) {
                    Methods.dismissProgressBar(progressDialog);
                    return false;
                }
                PerformPassAction();
                return true;
            }
        });


        //actions
        if (Methods.checkSolde())
            setcolors(red);
        else setcolors(blue);


        phone.setText(Methods.getPhone());
        names.setText(Methods.getName() + " " + Methods.getLastname());


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
                startActivity(new Intent(SettingsActivity.this, BourseActivity.class));

                break;
            case R.id.dealer:
                startActivity(new Intent(SettingsActivity.this, CoDealersActivity.class));

                break;
            case R.id.proximity:
                startActivity(new Intent(SettingsActivity.this, MapsActivity.class));
                break;
            case R.id.convertisseur:
                startActivity(new Intent(SettingsActivity.this, ConvertisseurActivity.class));
                break;
            case R.id.simulateurs:
                startActivity(new Intent(SettingsActivity.this, SimulateurActivity.class));
                break;
            case R.id.settings:
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.apropos:
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
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
        coord.collapse();
        seuil.collapse();
        pass.collapse();
    }


    private void PerformCoordAction() {
        final String name = nameTxt.getText().toString(),
                email = mailTxt.getText().toString(),
                lastName = lnameTxt.getText().toString();
        realm.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {


                UserObject userObject = realm.where(UserObject.class)
                        .equalTo("phone", Methods.getPhone())
                        .findFirst();
                if (!name.isEmpty()) {
                    userObject.setName(name);
                }
                if (!lastName.isEmpty()) {
                    userObject.setLastname(lastName);
                }
                if (!email.isEmpty()) {
                    userObject.setEmail(email);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("succées")
                        .setMessage("coordonnées mis à jours")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                coord.collapse();
                                clearText();
                            }
                        })
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                if (!name.isEmpty()) {
                    editor.putString(SharedStrings.SHARED_NAME, name);
                    Methods.setName(name);
                }
                if (!lastName.isEmpty()) {
                    editor.putString(SharedStrings.SHARED_LASTNAME, lastName);
                    Methods.setLastname(lastName);
                }
                if (!email.isEmpty()) {
                    editor.putString(SharedStrings.SHARED_MAIL, email);
                    Methods.setMail(email);
                }
                editor.commit();
                names.setText(Methods.getName() + " " + Methods.getLastname());
                if (Methods.checkSolde())
                    setcolors(red);
                else setcolors(blue);
                Methods.dismissProgressBar(progressDialog);

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Methods.dismissProgressBar(progressDialog);
                error.printStackTrace();
                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("erreur")
                        .setMessage("erreur de serveur")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

            }
        });
    }

    private void PerformSeuilAction() {
        final String seuilT = seuilTxt.getText().toString();
        realm.executeTransactionAsync(new Realm.Transaction() {


            @Override
            public void execute(Realm realm) {


                UserObject userObject = realm.where(UserObject.class)
                        .equalTo("phone", Methods.getPhone())
                        .findFirst();

                userObject.setSeuil(Float.parseFloat(seuilT));


            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {


                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("succées")
                        .setMessage("seuil changé")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                seuil.collapse();
                                clearText();
                            }
                        })
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

                editor.putFloat(SharedStrings.SHARED_SEUIL, Float.parseFloat(seuilT));
                editor.commit();
                Methods.setSeuil(Float.parseFloat(seuilT));
                if (Methods.checkSolde())
                    setcolors(red);
                else setcolors(blue);
                Methods.dismissProgressBar(progressDialog);


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Methods.dismissProgressBar(progressDialog);
                error.printStackTrace();
                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("erreur")
                        .setMessage("erreur de serveur")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
            }
        });
    }

    private void PerformPassAction() {
        realm.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                String opass = opassTxt.getText().toString();
                String npass = npassTxt.getText().toString();
                UserObject userObject = realm.where(UserObject.class)
                        .equalTo("phone", Methods.getPhone())
                        .findFirst();
                if (opass.equals(userObject.getPassword()))
                    userObject.setPassword(npass);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("succées")
                        .setMessage("mot de passe mis à jours")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pass.collapse();
                                clearText();
                            }
                        })
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                Methods.dismissProgressBar(progressDialog);
                if (Methods.checkSolde())
                    setcolors(red);
                else setcolors(blue);


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Methods.dismissProgressBar(progressDialog);
                error.printStackTrace();
                d = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("erreur")
                        .setMessage("erreur de serveur")
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
            }
        });
    }

    private void setcolors(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, //  checked
                new int[]{-android.R.attr.state_checked}, // unchecked

        };
        int[] colors = new int[]{
                color,
                Color.DKGRAY
        };
        appbar.setBackgroundColor(color);
        navigationView.setItemIconTintList(new ColorStateList(states, colors));
        navigationView.setItemTextColor(new ColorStateList(states, colors));
        linearLayout.setBackgroundColor(color);
        Methods.setCursorDrawableColor(nameTxt, color);
        Methods.setCursorDrawableColor(lnameTxt, color);
        Methods.setCursorDrawableColor(mailTxt, color);
        Methods.setCursorDrawableColor(seuilTxt, color);
        Methods.setCursorDrawableColor(opassTxt, color);
        Methods.setCursorDrawableColor(npassTxt, color);
        Methods.setCursorDrawableColor(cnpassTxt, color);
        coordbtn.setBackgroundColor(color);
        seuilbtn.setBackgroundColor(color);
        passbtn.setBackgroundColor(color);

    }

    private void clearText() {
        nameTxt.setText("");
        nameTxt.setError(null);
        lnameTxt.setText("");
        lnameTxt.setError(null);
        mailTxt.setText("");
        mailTxt.setError(null);
        seuilTxt.setText("");
        seuilTxt.setError(null);
        opassTxt.setText("");
        opassTxt.setError(null);
        npassTxt.setText("");
        npassTxt.setError(null);
        cnpassTxt.setText("");
        cnpassTxt.setError(null);
    }

    //validating
    private boolean submitCoordForm() {
        if (!validateEmail(mailTxt) || !validateName(lnameTxt) || !validateName(nameTxt) || !validateEmptyCoord()) {

            /*if (!validatePassword(opassTxt)) {
                opassTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(npassTxt)) {
                npassTxt.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }*/
            if (!validateEmptyCoord()) {

                Methods.dismissProgressBar(progressDialog);
                AlertDialog d = new AlertDialog.Builder(this)
                        .setTitle("erreur")
                        .setMessage("entrer au moin un champs pour modifier")
                        .setPositiveButton("ok", null)
                        .show();

                d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

                return false;
            }
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

    private boolean submitSeuilForm() {


        if (!isValidAmount()) {

            seuilTxt.requestFocus();
            return false;
        }


        return true;
    }

    private boolean submitPassForm() {

        if (!validatePassword(cnpassTxt) ||
                !validatePassword(npassTxt) ||
                !validatePassword(opassTxt)

                ) {
            if (!validatePassword(opassTxt)) {
                opassTxt.requestFocus();
                return false;
            }
            if (!validatePassword(npassTxt)) {
                npassTxt.requestFocus();
                return false;
            }
            if (!validatePassword(cnpassTxt)) {
                cnpassTxt.requestFocus();
                return false;
            }

        }
        if (!validateIdPassword(cnpassTxt, npassTxt, true)) {
            Log.i("submitPassForm: ", "not valid id pass");
            cnpassTxt.requestFocus();
            return false;
        }
        if (!validateIdPassword(npassTxt, opassTxt, false)) {
            Log.i("submitPassForm: ", "not valid id pass ideb");
            npassTxt.requestFocus();
            return false;
        }


        Log.i("submitPassForm: ", "succeess");

        return true;
    }


    private boolean validateEmptyCoord() {
        return !(mailTxt.getText().toString().isEmpty() && nameTxt.getText().toString().isEmpty() && lnameTxt.getText().toString().isEmpty());
    }

    private boolean isValidAmount() {
        String numberT = seuilTxt.getText().toString().trim();

        if (numberT.isEmpty()) {
            seuilTxt.setError("entrer un montant");
            return false;
        }
        return true;
    }

    private boolean validatePassword(EditText pass) {
        String password = pass.getText().toString().trim();
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$");
        Matcher match = pattern.matcher(password);


        if (!match.matches()) {
            if (password.isEmpty()) {
                pass.setError("champs obligatoire");
            } else
                pass.setError("mot de passe doit au moins six character contenant une lettre majuscule,miniscule et un chiffre");

            return false;
        }


        return true;

    }

    private boolean validateIdPassword(EditText editText, EditText editText2, boolean ch) {
        String password = editText.getText().toString().trim();
        String password2 = editText2.getText().toString().trim();

        if (password.isEmpty() || !password.equals(password2)) {


            if (!password.equals(password2) && ch) {

                editText.setError("les deux mots de passes ne sont pas les mêmes ");
                return false;
            }


        }
        if (password.equals(password2) && !ch) {
            Log.i("validateIdPassword: ", String.valueOf(password.equals(password2)));
            editText.setError("cette mot de passe déja utilisé entrer un nouveau");
            return false;
        }
        return true;
    }

    private boolean validateEmail(EditText email) {
        String emails = email.getText().toString().trim();

        if (!TextUtils.isEmpty(emails) && !android.util.Patterns.EMAIL_ADDRESS.matcher(emails).matches()) {
            email.requestFocus();
            email.setError("format d'email invalide");
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


    private void updatetask() {

    }
}
