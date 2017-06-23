package com.sofientouati.olympio;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import dmax.dialog.SpotsDialog;

/**
 * Created by sofirntouati on 16/06/17.
 */
public class Methods {


    private static float seuil = 1000f;
    private static float solde = 1420.55f;
    private static String phone;

    public static float getSeuil() {
        return seuil;
    }

    public void setSeuil(float seuil) {
        this.seuil = seuil;
    }

    public static float getSolde() {
        return solde;
    }

    public void setSolde(float solde) {
        this.solde = solde;
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
    public static AlertDialog showProgressBar(Context context, String message) {
        AlertDialog progressDialog = new SpotsDialog(context);
        if (checkSolde())
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

        if (getSeuil() > getSolde())
            return true;
        return false;
    }

    /*public static boolean isb64(String s){
        MediaCodec.CryptoInfo.Pattern pattern= Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
        Matcher matcher= pattern.matcher(s);
        return matcher.find();
    }

    *//*public static String imtob64(File file){

    }*//*




*/


}

