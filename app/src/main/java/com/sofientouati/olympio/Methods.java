package com.sofientouati.olympio;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sofientouati.olympio.Objects.SharedStrings;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * Created by SOFIEN TOUATI on 16/06/17.
 */
public class Methods {


    private static float seuil = 1000f;
    private static float solde = 1420.55f;
    private static String phone;

    private static String id;

    private static String name;
    private static String lastname;
    private static String mail;
    private static boolean logged;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Methods.id = id;
    }

    public static boolean isLogged() {
        return logged;
    }

    public static void setLogged(boolean logged) {
        Methods.logged = logged;
    }

    public static float getSeuil() {
        return seuil;
    }

    public static void setSeuil(float seuil) {
        Methods.seuil = seuil;
    }

    public static float getSolde() {
        return solde;
    }

    public static void setSolde(float solde) {
        Methods.solde = solde;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        Methods.phone = phone;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Methods.name = name;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        Methods.lastname = lastname;
    }

    public static String getMail() {
        return mail;
    }

    public static void setMail(String mail) {
        Methods.mail = mail;
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable drawable = editText.getBackground();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            editText.setBackground(drawable);

            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

    //show snack bar
    public static void showSnackBar(View view, String message) {

        Snackbar s = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        s.show();
    }

    //progress bar
    public static AlertDialog showProgressBar(Context context, String message, Boolean x) {
        AlertDialog progressDialog = new SpotsDialog(context);
        if (checkSolde() && x)
            progressDialog = new SpotsDialog(context, R.style.ProgressBar);

        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(message);
        return progressDialog;


    }

    public static void dismissProgressBar(AlertDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public static boolean checkSolde() {

        if (seuil > solde)
            return true;
        return false;
    }

    public static void getShared(SharedPreferences sharedPreferences) {
        Methods.setId(sharedPreferences.getString(SharedStrings.SHARED_PHONE, ""));
        Methods.setPhone(sharedPreferences.getString(SharedStrings.SHARED_PHONE, ""));
        Methods.setName(sharedPreferences.getString(SharedStrings.SHARED_NAME, ""));
        Methods.setLastname(sharedPreferences.getString(SharedStrings.SHARED_LASTNAME, ""));
        Methods.setMail(sharedPreferences.getString(SharedStrings.SHARED_MAIL, ""));
        Methods.setLogged(sharedPreferences.getBoolean(SharedStrings.SHARED_ISLOGGED, false));
        Methods.setSolde(sharedPreferences.getFloat(SharedStrings.SHARED_SOLDE, 0f));
        Methods.setSeuil(sharedPreferences.getFloat(SharedStrings.SHARED_SEUIL, 1000f));
    }

    public static String setDate() {

        SimpleDateFormat da = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return da.format(new Date());
    }

}

