package com.sofientouati.mtnapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by sofirntouati on 16/06/17.
 */
public class Methods {


    //show snack bar
    public static void showSnackBar(View view, String message) {

        Snackbar s = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        s.show();
    }

    //progress bar
    public static ProgressDialog showProgressBar(Context context, String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        return progressDialog;


    }

    public static void dismissProgressBar(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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

