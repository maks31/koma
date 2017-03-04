package com.koma.logintest;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koma.logintest.asynctasks.AddressFromLatLng;
import com.koma.logintest.interfaces.GetAddressFromLatLng;
import com.koma.logintest.utils.Constants;

import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements GetAddressFromLatLng {

    private Button currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String longitude;
    String latitude;

    SharedPreferences mPref;
    SharedPreferences.Editor mPrefEditor;
    Context mContext;
    EditText etZipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        /*view definations*****/
        currentLocation = (Button) findViewById(R.id.btnCrntLocation);
        etZipcode = (EditText) findViewById(R.id.etZipcode);
        /******************/

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mContext = this;
        mPref = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        mPrefEditor = mPref.edit();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude() + "";
                latitude = location.getLatitude() + "";

                /*convert lat lng to address through a call back*/

                new AddressFromLatLng(mContext, latitude, longitude).execute();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET

                }, 10);
                return;
            } else {
                configureLocation();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureLocation();
                return;
        }
    }

    private void configureLocation() {
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }


    @Override
    public void getAddressFromLatLngCallBack(Context mContext, String lat, String lng, String address) {
        // get address from here
//        Toast.makeText(mContext, "" + address, Toast.LENGTH_SHORT).show();
       setZipCode(address);

    }

    private void setZipCode(String address) {

        etZipcode.setText(address);
        mPrefEditor.putString(Constants.PREF_ZIPCODE,address).commit();
        startActivity(new Intent(mContext, MainActivity.class));
        finishAffinity();
    }
}
