package com.sofientouati.olympio.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.Objects.PhonesObject;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

import static com.sofientouati.olympio.Methods.blue;
import static com.sofientouati.olympio.Methods.red;

public class QRActivity extends AppCompatActivity {
    private QRCodeReaderView qrCodeReaderView;
    private ImageButton flash, close;
    private boolean flashs;
    private String message;
    private AlertDialog d;
    private ArrayList<String> phone;
    private Realm realm;
    private AlertDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qr);
        realm = Realm.getDefaultInstance();
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        flash = (ImageButton) findViewById(R.id.flash);
        close = (ImageButton) findViewById(R.id.close);
        flashs = false;
        loadNumbers();
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String s, PointF[] pointFs) {
                Log.i("onQRCodeRead: ", s);
                qrCodeReaderView.stopCamera();
                Gson g = new Gson();
                try {

                    final JsonObject o = g.fromJson(s, JsonObject.class);
                    Log.i("onQRCodeRead: ", o.toString());
                    if (o.get("amount") == null || !isValidAmount(o.get("amount").toString())) {
                        if (message == null)
                            message = "format de montant invalide";

                        d = new AlertDialog.Builder(QRActivity.this)
                                .setTitle("erreur")
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                        if (Methods.checkSolde()) {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

                        } else {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

                        }
                        message = null;
                        return;
                    }
                    if (o.get("phone") == null || !isValidNum(o.get("phone").getAsString())) {

                        if (message == null)
                            message = "format de montant invalide";
                        d = new AlertDialog.Builder(QRActivity.this)
                                .setTitle("erreur")
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();

                                    }
                                })
                                .show();
                        if (Methods.checkSolde()) {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

                        } else {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

                        }
                        message = null;
                        return;
                    }

                    Log.i("onQRCodeRead: ", o.toString());

                    d = new AlertDialog.Builder(QRActivity.this)
                            .setTitle("confirmation")
                            .setMessage("Confirmez-vous pour effectuer cette transaction :\n" +
                                    "pour :" + o.get("phone") + " \n" +
                                    "montant : " + o.get("amount"))
                            .setCancelable(false)
                            .setPositiveButton("confirmer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progress = Methods.showProgressBar(QRActivity.this, "en cours d'envoi de transaction", true);
                                    performAction(o);
                                }
                            })
                            .setNegativeButton("annuler", null)
                            .show();
                    if (Methods.checkSolde()) {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                        d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(red);
                    } else {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);
                        d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(blue);
                    }
                } catch (JsonSyntaxException ex) {
                    ex.printStackTrace();
                    d = new AlertDialog.Builder(QRActivity.this)
                            .setTitle("erreur")
                            .setMessage("qr code invalide")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();

                                }
                            })
                            .show();
                    if (Methods.checkSolde()) {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);

                    } else {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

                    }

                }
            }
        });

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qrCodeReaderView.setTorchEnabled(flashs);

                if (flashs == true) {

                    flash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_white_36dp));
                } else
                    flash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_white_36dp));
                flashs = !flashs;
            }


        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }


    private void performAction(final JsonObject o) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ActivityObject activityObject = realm.createObject(ActivityObject.class, UUID.randomUUID().toString());
                activityObject.setSourceNumber(Methods.getPhone());
                activityObject.setDestinationNumber(o.get("phone").toString());
                activityObject.setAmount(Float.parseFloat(o.get("amount").toString()));
                activityObject.setAction("envoi");
                activityObject.setStatus("pending");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Methods.dismissProgressBar(progress);
                d = new AlertDialog.Builder(QRActivity.this)
                        .setTitle("succés")
                        .setMessage("montant a été envoyé")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                d = new AlertDialog.Builder(QRActivity.this)
                        .setTitle("erreur")
                        .setMessage("erreur de serveur")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(blue);

            }
        });
    }


    private boolean isValidAmount(String numberT) {

        try {
            Float.valueOf(numberT);
        } catch (NumberFormatException ex) {
            message = "format de montant invalide";
            return false;
        }

        if (numberT.isEmpty()) {
            message = "format de montant invalide";
            return false;
        }


        if (numberT.equals("0")) {
            message = "montant invalide";
            return false;

        }
        if (Float.valueOf(numberT) > Methods.getSolde()) {
            message = "solde Insuffisant";
            return false;
        }
        return true;
    }

    private boolean isValidNum(String numberT) {

        Pattern pattern = Pattern.compile("97(\\d{6})");
        Matcher matcher = pattern.matcher(numberT);
        if (numberT.isEmpty() || !matcher.matches()) {
            message = "format de numero invalide";
            return false;
        }
        if (numberT.equals(Methods.getPhone())) {
            message = "on ne peut pas envoyer au même numéro";
            return false;
        }
        if (!phone.contains(numberT)) {
            message = "numéro doit être ajouté";
            return false;
        }
        return true;
    }


    private void loadNumbers() {
        UserObject c = realm.where(UserObject.class)
                .equalTo("phone", Methods.getPhone())
                .findFirst();
        UserObject u = realm.copyFromRealm(c);

        phone = new ArrayList<>();
        if (!u.getMatched().isEmpty())
            for (PhonesObject z : u.getMatched()
                    ) {
                phone.add(z.getPhone());
            }
        Log.i("loadNumbers: ", phone.toString());


    }
}
