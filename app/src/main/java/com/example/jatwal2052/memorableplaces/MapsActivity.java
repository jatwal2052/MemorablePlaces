package com.example.jatwal2052.memorableplaces;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(location,"Your Location");
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

    public void updateLocation(Location location,String title){
        mMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        intent.getIntExtra("name",0);
        Toast.makeText(this, "" + intent.getIntExtra("name",0), Toast.LENGTH_SHORT).show();
        if(intent.getIntExtra("name",0) == 0){
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location,"Your Location");
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
            if (Build.VERSION.SDK_INT <23){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }else{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateLocation(location,"Your Location");
                }
            }

        }else{
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(MainActivity.locationss.get(intent.getIntExtra("name",0))).title(MainActivity.places.get(intent.getIntExtra("name",0))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.locationss.get(intent.getIntExtra("name",0)),15));
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "jatin", Toast.LENGTH_SHORT).show();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";


       try {
            List<Address> locations = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1) ;

           Log.i("try","message");
            if (locations !=  null && locations.size() > 0){
                Log.i("if","message");

               if(locations.get(0).getAddressLine(0)!=null){
                    address = locations.get(0).getAddressLine(0);
                }

                if(locations.get(0).getAddressLine(1)!=null){
                    address += locations.get(0).getAddressLine(1)+ "\n";
                }
                if(locations.get(0).getSubAdminArea()!=null){
                    address += locations.get(0).getSubAdminArea()+ "\n";
                }
                if(locations.get(0).getPostalCode()!=null){
                    address += locations.get(0).getPostalCode()+ ",";
                }
                if(locations.get(0).getAdminArea()!=null){
                    address += locations.get(0).getAdminArea()+ "\n";
                }
                if(locations.get(0).getCountryName()!=null){
                    address += locations.get(0).getCountryName()+ "\n";
                }



              // address = locations.get(0).toString();
                Log.i("Address",address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        MainActivity.locationss.add(latLng);
        MainActivity.places.add(address);
        MainActivity.arrayAdapter.notifyDataSetChanged();
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.jatwal2052.memorableplaces",MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();

        try {
            ArrayList<Double> latitudes = new ArrayList<>();
            ArrayList<Double> longitudes = new ArrayList<>();

            for (LatLng latLng1 : MainActivity.locationss){
                latitudes.add(latLng1.latitude);
                longitudes.add(latLng1.longitude);
            }

            sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.places)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
