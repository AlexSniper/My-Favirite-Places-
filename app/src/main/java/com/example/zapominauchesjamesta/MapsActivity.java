package com.example.zapominauchesjamesta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;


    LocationManager locationManager;
    LocationListener locationListener;

    public void centerMapOnLocation(Location location, String title) {
        if (location != null) // this if  statement Will check that GPS is Turned on
        {


            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Go to Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {// This line of code is
        // used in order to get any permission in our case it's GeoLocation
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {// This line of code checking if permission was granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {// Checking that permission in manifest was granted
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);// If permissions are fine GPS will show and update location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Will show user Last known  location
                centerMapOnLocation(lastKnownLocation, "Your Location");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);


        Intent intent = getIntent();
        if (intent.getIntExtra("placeNumber", 0) == 0) {
            // If user picked very first Item  it will zoom in on user location

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLocation(location, "Your Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {// Checking that permission in manifest was not granted

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);// If permissions are fine GPS will show and update location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Will show user Last known  location
                centerMapOnLocation(lastKnownLocation, "Your Location");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeNumber", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeNumber", 0)).longitude);
            centerMapOnLocation(placeLocation, MainActivity.places.get(intent.getIntExtra("placeNumber", 0)));
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String adress = "";

        try {
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude,  1);
            if (listAddress != null && listAddress.size() > 0) {
                if (listAddress.get(0).getThoroughfare() !=null) {
                    if (listAddress.get(0).getSubThoroughfare() !=null) {
                    adress+= listAddress.get(0).getSubThoroughfare() + "";
                    }
                    adress+= listAddress.get(0).getThoroughfare() + "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
if (adress.equals("") ) {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd", Locale.getDefault());
    adress+=sdf.format(new Date());

}
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));



MainActivity.places.add(adress);// Adding addresses to ArrayList<String> places = new ArrayList<String>(); arrayList
        MainActivity.locations.add(latLng);// Adding lattitude and longitude to  ArrayList<LatLng> locations = new ArrayList<LatLng>(); arrayList
      MainActivity.arrayAdapter.notifyDataSetChanged();// it's updating array

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.zapominauchesjamesta", Context.MODE_PRIVATE);// It gives us access to shared preferences

        try {

            ArrayList<String>latitudes = new ArrayList<>();
            ArrayList<String>longitude = new ArrayList<>();

            for (LatLng coord : MainActivity.locations ) {
                latitudes.add(Double.toString(coord.latitude));
                longitude.add(Double.toString(coord.longitude));
            }
            sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("lats", ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("lons", ObjectSerializer.serialize(longitude)).apply();

        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Location saved ", Toast.LENGTH_SHORT).show();
    }
}


