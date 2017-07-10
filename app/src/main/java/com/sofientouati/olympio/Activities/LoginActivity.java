package com.sofientouati.olympio.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.Objects.TelephonyInfo;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private RelativeLayout parent, mainlay, login, signup;
    private LinearLayout btns;
    private Button signupbtn, loginbtn;
    private TextView bienvenu;
    private AutoCompleteTextView phonelog, phonesign, chose;
    private EditText logpass, signpass, name, lastname;
    private ArrayList<String> numbers = new ArrayList<>();
    private String status;
    private AlertDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        /*getting views*/
        //layouts


        imageView = (ImageView) findViewById(R.id.logoapp);
        parent = (RelativeLayout) findViewById(R.id.parent);
        mainlay = (RelativeLayout) findViewById(R.id.mainlay);
        login = (RelativeLayout) findViewById(R.id.form);
        signup = (RelativeLayout) findViewById(R.id.signupLay);
        //btns
        signupbtn = (Button) findViewById(R.id.signupbtn);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        btns = (LinearLayout) findViewById(R.id.btns);
        //txtviews
        bienvenu = (TextView) findViewById(R.id.bienvenu);
        //edit texts
        phonelog = (AutoCompleteTextView) findViewById(R.id.phoneTxt);
        phonesign = (AutoCompleteTextView) findViewById(R.id.signPhoneTxt);
        logpass = (EditText) findViewById(R.id.logpassTxt);
        signpass = (EditText) findViewById(R.id.passTxt);
        name = (EditText) findViewById(R.id.name);
        lastname = (EditText) findViewById(R.id.lastname);
        //others
        realm = Realm.getDefaultInstance();


        //get Shared
        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /*listeners*/
        logpass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                performAction();
                return true;
            }
        });
        lastname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                performAction();
                return true;
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == null) {
                    getPermissions(phonelog);
                    int bienvenuPo = (parent.getWidth() - bienvenu.getWidth()) / 2;
                    int loginPo = (parent.getWidth() - login.getWidth()) / 2;
                    startXTranslation(bienvenu, bienvenuPo, parent.getWidth(), true, 0);
                    startXTranslation(login, -parent.getWidth(), 25, false, 500);
                    signupbtn.setText("Annuler");
                    status = "login";
                    return;
                }
                performAction();
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == null) {

                    getPermissions(phonesign);
                    int bienvenuPo = (mainlay.getWidth() - bienvenu.getWidth()) / 2;
                    int loginPo = (parent.getWidth()) / 22;
                    startXTranslation(bienvenu, bienvenuPo, parent.getWidth(), true, 0);
                    startXTranslation(signup, -parent.getWidth(), 25, false, 500);
                    signupbtn.setText("Annuler");
                    loginbtn.setText("creer un compte");
                    status = "signup";
                    return;
                }
                cancelAction();
            }
        });

        if ((getIntent().getStringExtra("class") != null) && (getIntent().getStringExtra("class").equals("SplashScreen"))) {

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Log.i("run: ", String.valueOf(parent.getHeight()));
                    Log.i("run: ", String.valueOf(imageView.getHeight()));
                    int height = (parent.getHeight() - imageView.getHeight()) / 2;
                    startYTranslation(imageView, height, -100);
                }
            };
            new Handler(Looper.getMainLooper()).post(r);


        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

            imageView.setLayoutParams(layoutParams);
            bienvenu.setVisibility(View.VISIBLE);
            btns.setVisibility(View.VISIBLE);

        }


    }


    //actions
    private void cancelAction() {
        if (status.equals("login")) {
            status = null;
            int bienvenuPo = (mainlay.getWidth() - bienvenu.getWidth()) / 2;
            startXTranslation(login, 25, -parent.getWidth(), true, 0);
            startXTranslation(bienvenu, parent.getWidth(), bienvenuPo, false, 0);
            signupbtn.setText("créer un compte");
            loginbtn.setText("Connexion");
            logpass.setText("");
            logpass.setError(null);
            phonelog.setText("");
            phonelog.setError(null);

            return;

        }
        if (status.equals("signup")) {
            status = null;
            int bienvenuPo = (mainlay.getWidth() - bienvenu.getWidth()) / 2;

            startXTranslation(signup, 25, -parent.getWidth(), true, 0);
            startXTranslation(bienvenu, parent.getWidth(), bienvenuPo, false, 0);
            signupbtn.setText("créer un compte");
            loginbtn.setText("connexion");
            name.setText("");
            name.setError(null);
            signpass.setText("");
            signpass.setError(null);
            phonesign.setText("");
            phonesign.setError(null);
            return;

        }
    }

    private void performAction() {
        if (status.equals("login")) {
            progressDialog = Methods.showProgressBar(LoginActivity.this, "Chargement", false);
            if (submitLoginForm()) {


                UserObject query = realm.where(UserObject.class)
                        .equalTo("phone", phonelog.getText().toString())
                        .findFirst();
                Log.i("performAction: query", String.valueOf(query));
                if (query == null) {
                    phonelog.setError("aucun compte avec ce télephone");
                    phonelog.requestFocus();
                    Methods.dismissProgressBar(progressDialog);
                } else {
                    if (query.getPassword().equals(logpass.getText().toString())) {
                        setSharedPreferences(query);
                        Methods.dismissProgressBar(progressDialog);
                        ActivityCompat.startActivity(LoginActivity.this, new Intent(LoginActivity.this, HomeActivity.class), null);
                        finish();
                    } else {
                        Methods.dismissProgressBar(progressDialog);
                        logpass.setError("mot de passe incorrect");
                        logpass.requestFocus();
                    }
                }

            } else
                Methods.dismissProgressBar(progressDialog);
            return;
        }
        if (status.equals("signup")) {
            progressDialog = Methods.showProgressBar(LoginActivity.this, "Création de compte", false);
            if (submitSingupForm()) {
                signup();
                return;
            }
        }
    }

    //databases requests

    private void signup() {

        final UserObject user = new UserObject();
        user.setPhone(phonesign.getText().toString());
        user.setPassword(signpass.getText().toString());
        user.setName(name.getText().toString());
        user.setLastname(lastname.getText().toString());
        final Random random = new Random();
        user.setSolde(random.nextFloat() * 2000f);
        user.setSeuil(1000f);


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserObject user = realm.createObject(UserObject.class, UUID.randomUUID().toString());
                user.setPhone(phonesign.getText().toString());
                user.setPassword(signpass.getText().toString());
                user.setName(name.getText().toString());
                user.setLastname(lastname.getText().toString());
                user.setSolde(random.nextFloat() * 2000f);
                user.setSeuil(1000.0f);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                setSharedPreferences(user);
                Methods.dismissProgressBar(progressDialog);
                ActivityCompat.startActivity(LoginActivity.this, new Intent(LoginActivity.this, GetPicActivity.class), null);
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Methods.dismissProgressBar(progressDialog);
                error.printStackTrace();
                if (error.getMessage().contains("Primary key value already exists:")) {
                    phonesign.setError("compte déja existant avec ce numéro");
                    phonesign.requestFocus();
                } else Methods.showSnackBar(parent, "erreur de serveur");
            }
        });
    }


    //getting phone number
    private ArrayList<String> getPermissions(AutoCompleteTextView phone) {
        ArrayList<String> x = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.READ_PHONE_STATE;
            Log.i("checkPermission: ", permission);
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                chose = phone;
                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            } else {

                getPhoneNumber(phone);
            }
        } else {
            getPhoneNumber(phone);
        }
        return x;
    }

    private void getPhoneNumber(AutoCompleteTextView phone) {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        String imsiSIM1 = telephonyInfo.getImsiSIM1();
        String imsiSIM2 = telephonyInfo.getImsiSIM2();
        String strMobileNumber;
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (isSIM1Ready) {

            strMobileNumber = tm.getLine1Number();
            numbers = new ArrayList<String>();
            numbers.add(strMobileNumber);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, numbers);
            phone.setAdapter(arrayAdapter);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Methods.showSnackBar(login, "permis");
                    getPhoneNumber(chose);


//                    captureImage();
//                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Methods.showSnackBar(login, "non permis");


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (status != null && status.equals("login")) {
            status = null;
            int bienvenuPo = (mainlay.getWidth() - bienvenu.getWidth()) / 2;
            startXTranslation(login, 25, -1500, true, 0);
            startXTranslation(bienvenu, 1500, bienvenuPo, false, 0);
            signupbtn.setText("créer un compte");
            loginbtn.setText("Connexion");
            logpass.setText("");
            logpass.setError(null);
            phonelog.setText("");
            phonelog.setError(null);

            return;

        }
        if (status != null && status.equals("signup")) {
            status = null;
            int bienvenuPo = (mainlay.getWidth() - bienvenu.getWidth()) / 2;
            startXTranslation(signup, 25, -1500, true, 0);
            startXTranslation(bienvenu, 1500, bienvenuPo, false, 0);
            signupbtn.setText("créer un compte");
            loginbtn.setText("connexion");
            name.setText("");
            name.setError(null);
            signpass.setText("");
            signpass.setError(null);
            phonesign.setText("");
            phonesign.setError(null);
            return;


        }

        super.onBackPressed();
    }


    //animations


    private void fadeIn(View v) {
        v.setVisibility(View.VISIBLE);
        final Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(1000);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btns.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation1);
    }

    private void startYTranslation(View v, int height, int height1) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "y", height, height1)
                .setDuration(1000);
        animator.setStartDelay(200);
//        animator.setInterpolator();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                fadeIn(bienvenu);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }

    private void startXTranslation(final View v, int x, int y, final boolean first, int delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "x", x, y)
                .setDuration(400);
//        animator.setStartDelay(delay);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                if (first)
//                    v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }


    //validation process
    private boolean submitLoginForm() {
        if (!validatePhone(phonelog) || !validatePassword(logpass)) {
            if (!validatePhone(phonelog)) {
                phonelog.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }

            if (!validatePassword(logpass)) {
                logpass.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }


        }
        return true;
    }

    private boolean submitSingupForm() {
        if (!validatePhone(phonesign) || !validatePassword(signpass) || !validateName(name) || !validateName(lastname)) {
            if (!validatePhone(phonesign)) {
                phonesign.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validatePassword(signpass)) {
                signpass.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(name)) {
                name.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }
            if (!validateName(lastname)) {
                lastname.requestFocus();
                Methods.dismissProgressBar(progressDialog);
                return false;
            }


        }
        return true;
    }

    private boolean validatePhone(AutoCompleteTextView phone) {
        String phoneNum = phone.getText().toString().trim();
        Pattern pattern = Pattern.compile("97(\\d{6})");
        Matcher matcher = pattern.matcher(phoneNum);
//        if (!android.util.Patterns.PHONE.matcher(phoneNum).matches()) {
        if (!matcher.matches()) {

            phone.requestFocus();
            if (phoneNum.isEmpty()) {
                phone.setError("champs obligatoire");
                return false;
            }
            phone.setError("numero de telephone doit être un numero MTN ");

            return false;
        }
        return true;
    }

    private boolean validatePassword(EditText pass) {
        String password = pass.getText().toString().trim();
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$");
        Matcher match = pattern.matcher(password);


        if (!match.matches()) {
            pass.requestFocus();
            if (password.isEmpty()) {
                pass.setError("champs obligatoire");

                return false;
            }
            pass.setError("mot de passe doit au moins six character contenant une lettre majuscule,miniscule et un chiffre");
            return false;
        }


        return true;

    }

    private boolean validateConfirmPassword() {
        String password = name.getText().toString().trim();
        if (password.isEmpty() || !password.equals(signpass.getText().toString().trim())) {
            name.requestFocus();
            if (password.isEmpty())
                name.setError("champs obligatoire");

            if (!password.equals(signpass.getText().toString().trim()))
                name.setError("les 2 mots de passes ne sont pas les mêmes ");

            return false;
        }
        return true;
    }

    private boolean validateName(EditText text) {
        String lastnameS = text.getText().toString();
        Pattern pattern = Pattern.compile("(\\w){2,}?");
        Matcher matcher = pattern.matcher(lastnameS);
        if (!matcher.matches()) {
            text.requestFocus();
            if (lastnameS.isEmpty()) {
                text.setError("champs obligatoire");
                return false;
            }
            text.setError("format de numéro de téléphone invalide");

            return false;
        }
        return true;
    }

    private void setSharedPreferences(UserObject query) {
        Log.i("setSharedPreferences: ", String.valueOf(query));
        editor.putString(SharedStrings.SHARED_ID, query.getId());
        editor.putBoolean(SharedStrings.SHARED_ISLOGGED, true);
        Methods.setLogged(true);
        editor.putString(SharedStrings.SHARED_PHONE, query.getPhone());
        Methods.setPhone(query.getPhone());
        editor.putString(SharedStrings.SHARED_NAME, query.getName());
        Methods.setName(query.getName());
        editor.putString(SharedStrings.SHARED_LASTNAME, query.getLastname());
        Methods.setLastname(query.getLastname());
        editor.putFloat(SharedStrings.SHARED_SEUIL, query.getSeuil());
        Methods.setSeuil(query.getSeuil());
        editor.putFloat(SharedStrings.SHARED_SOLDE, query.getSolde());
        Methods.setSolde(query.getSolde());
        if (!(query.getEmail() == null)) {
            if (!query.getEmail().isEmpty())
                editor.putString(SharedStrings.SHARED_MAIL, query.getEmail());
            Methods.setMail(query.getEmail());
        }
        editor.commit();
    }
}

