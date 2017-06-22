package com.sofientouati.olympio.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;
//import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class GetPicActivity extends AppCompatActivity {

    private static final String TAG = "sdfs";
    private RelativeLayout emptyView;
    private ImageView loaded;
    private LinearLayout btnBar;
    ImageButton cancel, done;
    private CharSequence[] items = {"Camera", "Gallery"};
    private Uri fileUri; // file url to store image/video
    EasyImage.ImageSource imageSource;
    private Bitmap profilePic;
    private String imageb64;
    private File imageFile = null;
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_PHOTO = "photoprofile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pic);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //getting views
        emptyView = (RelativeLayout) findViewById(R.id.emptyView);
        loaded = (ImageView) findViewById(R.id.loadedImage);
        btnBar = (LinearLayout) findViewById(R.id.buttonBar);
        cancel = (ImageButton) findViewById(R.id.cancel_action);
        done = (ImageButton) findViewById(R.id.confirm);

        //listeners
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyView.setVisibility(View.VISIBLE);
                btnBar.setVisibility(View.GONE);
                loaded.setVisibility(View.GONE);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String filePath = imageFile.getPath();
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                profilePic = BitmapFactory.decodeStream(stream);
                imageb64 = encodePicture(profilePic);
                Log.d("IMAGE B64:", imageb64);
                addProfilePicture(imageb64);
                Intent intent = new Intent(GetPicActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityCompat.startActivity(GetPicActivity.this, intent, null);
                finish();
            }
        });

        //actions
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GetPicActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {

                                //camera permission
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(GetPicActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    } else {
                                        EasyImage.openCamera(GetPicActivity.this, 0);
                                    }

                                } else {
                                    EasyImage.openCamera(GetPicActivity.this, 0);

                                }


                            }
                            break;


                            case 1:
                                //storage Permission
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //Storage Permission
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(GetPicActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                    } else {
                                        EasyImage.openGallery(GetPicActivity.this, 0);

                                    }
                                } else {
                                    EasyImage.openGallery(GetPicActivity.this, 0);

                                }
                                break;
                        }
                    }
                });
                builder.show();

            }
        });

        EasyImage.configuration(this)
                .setImagesFolderName("Olympio")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true);


    }

    private void addProfilePicture(final String imageb64) {

        final SharedPreferences sharedpreferences = GetPicActivity.this.getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        Log.i(TAG, "onResponseImage: " + imageb64);
        final String userid = sharedpreferences.getString(SharedStrings.SHARED_USERID, "");
        Log.i(TAG, "addProfilePictureId: " + userid);

        editor.putString(SharedStrings.SHARED_CINP, imageb64);
        editor.commit();

        // Log.d("RESULT VOLLEY",result[0]+"");
        // return (result[0]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Methods.showSnackBar(emptyView, "granted");
                    EasyImage.openCamera(GetPicActivity.this, 0);

//                    captureImage();
//                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Methods.showSnackBar(emptyView, "Camera & ReadStorage permission not granted");


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Methods.showSnackBar(emptyView, "granted");
                    EasyImage.openGallery(GetPicActivity.this, 0);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Methods.showSnackBar(emptyView, "Read Storage permission not granted");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            return;
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image

        Log.i(TAG, "onActivityResult: " + requestCode);


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Methods.showSnackBar(emptyView, "sorry couldn't load image please try again");
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                imageFile = imageFiles.get(0);
                Log.i(TAG, "onImagesPicked: " + imageFile);
                emptyView.setVisibility(View.GONE);
                btnBar.setVisibility(View.VISIBLE);
                loaded.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(imageFiles.get(0))
                        .thumbnail(0.5f)
                        .into(loaded);

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(GetPicActivity.this);
                    if (photoFile != null) photoFile.delete();

                }
            }
        });


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    public String encodePicture(Bitmap bm) {
        //Bitmap bm = BitmapFactory.decodeFile("/path/to/image.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
        return (encodedImage);

    }

    public Bitmap decodePicture(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.NO_WRAP);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
