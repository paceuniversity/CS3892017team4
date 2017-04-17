package com.example.zakiya.greenr;

/**
 * Created by shane on 4/4/17.
 */

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zakiya.greenr.content.ChargingStation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mapScreen extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapFragment mapFragment = null;

    TextView mLatitudeText;
    TextView mLongitudeText;
    GoogleApiClient mGoogleApiClient;
    String mLatitudeLabel, mLongitudeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.map_screen);
            initMap();
        }
    }



    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(mapScreen.this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "):", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "(:", Toast.LENGTH_LONG).show();
        mGoogleMap = googleMap;
        goToLocationZoom(40.7131212,-74.0006327,15);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> results = new ArrayList<>();
        ArrayList<ChargingStation> favoritesList = new ArrayList<>();
        favoritesList.add(new ChargingStation("Test1", "1 Pace Plaza, NYC", 1, "Yes"));
        favoritesList.add(new ChargingStation("Test2", "Columbus Park, NYC", 1, "Yes"));
        favoritesList.add(new ChargingStation("Test3", "Canal Street Station, NYC", 1, "Yes"));

        for (int i = 0; i < favoritesList.size(); i++) {
            String location = favoritesList.get(i).getLocation();

            try{
                results = geocoder.getFromLocationName(location, 1);
            }catch(IOException ioException){}

            double stationLat = results.get(0).getLatitude();
            double stationLong = results.get(0).getLongitude();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(stationLat,stationLong)).title(favoritesList.get(i).getStationName()));
        }

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(40.710574, -74.005767)).title("Test Marker"));
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    public void geoLocate (View view) throws IOException{
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location,1);
        android.location.Address address =list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        goToLocationZoom(lat, lng, 15);
        Toast.makeText(this, lat +" "+lng, Toast.LENGTH_LONG).show();

    }
}