package com.sofientouati.olympio.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.R;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private AppBarLayout appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private TextView names;
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int red = Color.parseColor("#C62828");
    private int blue = Color.parseColor("#0072ff");
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private AlertDialog d;
//    private FloatingActionButton fab;

//    private LocationRequest mLocationRequestHighAccuracy;
//    private LocationRequest mLocationRequestBalancedPowerAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                //.addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        fab= (FloatingActionButton) findViewById(R.id.fab);
        appbar = (AppBarLayout) findViewById(R.id.mapsAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.mapDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.map_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.proximity);
        linearLayout = (LinearLayout) view.findViewById(R.id.navheaderlayout);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        names = (TextView) view.findViewById(R.id.name);
        imageView = (ImageView) findViewById(R.id.togglebtns);


        phone.setText(Methods.getPhone());
        names.setText(Methods.getName() + " " + Methods.getLastname());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        sharedPreferences = getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
        if (Methods.checkSolde())
            setcolors(red);
        else
            setcolors(blue);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
            }
        });*/


    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
                ActivityCompat.startActivity(MapsActivity.this, new Intent(MapsActivity.this, HomeActivity.class), null);
                break;
            case R.id.bourse:
                ActivityCompat.startActivity(MapsActivity.this, new Intent(MapsActivity.this, BourseActivity.class), null);
                break;
            case R.id.proximity:
                startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                break;
            case R.id.convertisseur:
                startActivity(new Intent(MapsActivity.this, ConvertisseurActivity.class));
                break;
            case R.id.simulateurs:
                startActivity(new Intent(MapsActivity.this, SimulateurActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
                break;
            case R.id.apropos:
                startActivity(new Intent(MapsActivity.this, AboutActivity.class));
                break;
            case R.id.logout: {
                editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                ActivityCompat.startActivity(MapsActivity.this, new Intent(MapsActivity.this, LoginActivity.class), null);
                finish();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
//        fab.setBackgroundTintList(ColorStateList.valueOf(color));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Methods.checkSolde())
            setcolors(red);
        else
            setcolors(blue);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.00, 2.45), 7));

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.i("onMyLocationButtonClick: ", "mylocation");
                showCurrentLocation();
                return true;
            }
        });

        googleApiClient.connect();


        addMarkers();


//
    }


    private void showCurrentLocation() {
        if (
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            return;
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i("showCurrentLocation ", "not enabled");
                d = new AlertDialog.Builder(this)
                        .setTitle("Activer localisation")
                        .setMessage("s'il vous plais activer la localistion pour le bien fonctionnement de l'applicarion")
                        .setPositiveButton("activer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("annuler", null)
                        .show();
                if (Methods.checkSolde()) {
                    d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(red);
                    d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(red);
                } else {

                    d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(blue);
                    d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(blue);
                }
            }

            getLocation();

//            Bitmap b = ((BitmapDrawable) getResources().getDrawable(R.drawable.point)).getBitmap();
//            Bitmap bf = Bitmap.createScaledBitmap(b, 64, 64, false);
//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromBitmap(bf))
//                    .title("ma position")
//
//            );
        }


    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            Log.i("showCurrentLocation: ", "null");
            Methods.showSnackBar(appbar, "erreur");
            return;
        }
        Log.i("showCurrentLocation onConnected: ", "location : " + location);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            Methods.showSnackBar(appbar, latLng.toString());
        Log.i("showCurrentLocation: ", latLng.toString());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.i("showCurrentLocation ", "not enabled");
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    showCurrentLocation();

//                    Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
////                    if (location == null) {
////                        Log.i("showCurrentLocation: ", "null");
////                        Methods.showSnackBar(appbar, "erreur");
////                        return;
////                    }
////                    Log.i("showCurrentLocation onConnected: ", "location : " + location);
////                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
////                    Methods.showSnackBar(appbar, latLng.toString());
////                    Log.i("showCurrentLocation: ", latLng.toString());
////                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
////                    mMap.animateCamera(cameraUpdate);
////                    Bitmap b = ((BitmapDrawable) getResources().getDrawable(R.drawable.point)).getBitmap();
////                    Bitmap bf = Bitmap.createScaledBitmap(b, 64, 64, false);
////                    mMap.addMarker(new MarkerOptions()
////                            .position(latLng)
////                            .icon(BitmapDescriptorFactory.fromBitmap(bf))
////                            .title("ma position")
////
////                    );
                } else {

                    Methods.showSnackBar(appbar, "non permis");
                    setLocationButton(true);
                }
            }
        }
    }

    private void setLocationButton(Boolean b) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(b);
    }

    private void addMarkers() {
        HashMap<String, LatLng> markers = new HashMap<>();
//        markers.put("Échangeur de Steinmetz, Cotonou, Littoral Department",new LatLng(6.358079, 2.4353048));
        markers.put("Cotonou", new LatLng(6.358079, 2.437499));
        markers.put("Avakpka, Porto Novo", new LatLng(6.4819854, 2.6086521));
        markers.put("RNIE2, Parakou", new LatLng(9.3641815, 2.6223102));
//        markers.put("",new LatLng(6.3576431, 2.3664993));

        for (Map.Entry<String, LatLng> marker : markers.entrySet()
                ) {

            Bitmap b = ((BitmapDrawable) getResources().getDrawable(R.drawable.bank_building)).getBitmap();
            Bitmap bf = Bitmap.createScaledBitmap(b, 64, 64, false);
            mMap.addMarker(new MarkerOptions()

                    .position(marker.getValue())
                    .icon(BitmapDescriptorFactory.fromBitmap(bf))
                    .anchor(0.0f, 1.0f)
                    .title(marker.getKey()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Methods.showSnackBar(appbar, "connected");
        Log.i("showCurrentLocation onConnected: ", "connected");


        setLocationButton(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Methods.showSnackBar(appbar, "suspended" + i);
        Log.i("showCurrentLocation onConnectionSuspended: ", "suspended" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Methods.showSnackBar(appbar, "failed" + connectionResult.toString());
        Log.i("showCurrentLocation onConnectionFailed: ", "failed" + connectionResult.toString());
    }


}
