package com.example.zapominauchesjamesta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



  static   ArrayList<String> places = new ArrayList<String>();

  static   ArrayList<LatLng> locations = new ArrayList<LatLng>(); // In order to access location from MapsActivity class  need to declare above OnCreate method
  static  ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.zapominauchesjamesta", Context.MODE_PRIVATE);// It gives an access to shared preferences



        ArrayList<String>latitudes = new ArrayList<>();
        ArrayList<String>longitude = new ArrayList<>();


        places.clear();
        latitudes.clear();
        longitude.clear();
        locations.clear();
        try {
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",  ObjectSerializer.serialize(new ArrayList<String>()) ));
           latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",  ObjectSerializer.serialize(new ArrayList<String>()) ));
         longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lons",  ObjectSerializer.serialize(new ArrayList<String>()) ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(places.size() > 0 && latitudes.size() > 0 && longitude.size() > 0) {
            if (latitudes.size() == places.size() && longitude.size() == places.size()){// Checking that latitude and longitude  size
                for ( int i =0 ; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }else {// if one of any conditions is not satisfied else statement will work , or at was opened for first time

            places.add("Add a new place ...");
            locations.add(new LatLng(0, 0));
        }


        ListView listView =findViewById(R.id.listView);


        places.add("Add a new place ...");
locations.add(new LatLng(0, 0));

arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter); // Connecting ListView and arrayAdapter
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // This line og code display information and take to another activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
             intent.putExtra("placeNumber", position); //Position is representing which item in menu is selected
                startActivity(intent);


            }
        });


    }
}
