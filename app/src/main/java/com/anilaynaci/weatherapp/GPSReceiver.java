package com.anilaynaci.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by anila on 2.11.2017.
 */

public class GPSReceiver {

    Context context;
    MainActivity mA;

    public GPSReceiver(Context context) {
        this.context = context;
        mA = (MainActivity) context;
    }

    public class GpsListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String latitude = Double.toString(location.getLatitude());
                String longitude = Double.toString(location.getLongitude());
                String status = latitude + " - " + longitude;
                mA.showStatusValue(status);
                new RetrieveRestClient(context).execute(latitude, longitude);
            } else {
                Toast.makeText(context, "Konum bilgisi alınamıyor.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(context, "Uygulamaya yönlendiriliyorsunuz...", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent myIntent = new Intent(context, MainActivity.class);
                    context.startActivity(myIntent);
                }
            }, 3000);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    public void getGeoCoord() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsListener gpsListener = new GpsListener();
        Long minTime = (60 * 60) * 1000L; //1 saat
        float minDistance = 1000.f; //1 km

        /*Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = lm.getBestProvider(criteria, true);*/

        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isNetworkEnabled) {
            mA.showStatusValue("GPS Kapalı! Ayarlara yönlendiriliyorsunuz...");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            }, 3000);
        } else {
            mA.showStatusValue("Konum verisi alınıyor...");
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
    }
}