package com.example.jatwal2052.memorableplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places;
    static ArrayList<LatLng> locationss;
    static ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listView);

        places = new ArrayList<>();
        locationss = new ArrayList<>();



        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.jatwal2052.memorableplaces",MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();

        ArrayList<Double> latitude =new ArrayList<>();
        ArrayList<Double> longitude = new ArrayList<>();

        places.clear();
        locationss.clear();
        latitude.clear();
        longitude.clear();
        try {
            places = (ArrayList<String>)(ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>()))));
            latitude = (ArrayList<Double>)(ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<Double>()))));
            longitude = (ArrayList<Double>)(ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<Double>()))));

            System.out.println(places.size());
            System.out.println(latitude.size());
            System.out.println(longitude.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (places.size() > 0 && latitude.size() > 0 && longitude.size() > 0){
            if(places.size() == latitude.size() && latitude.size() == longitude.size()){
                for (int i = 0; i < latitude.size(); i++) {
                    locationss.add(new LatLng(latitude.get(i),longitude.get(i)));
                }
            }
        }
        else {
            places.add("Add a new location");
            locationss.add(new LatLng(0, 0));
        }

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("name",position);
                startActivity(intent);
            }
        });
    }
}
