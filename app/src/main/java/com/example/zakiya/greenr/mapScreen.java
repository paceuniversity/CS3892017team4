package com.example.zakiya.greenr;

/**
 * Created by shane on 4/4/17.
 */

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.zakiya.greenr.content.ChargingStation;
import com.example.zakiya.greenr.content.OpenChargeStation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mapScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;
    protected static final String TAG = "MainActivity";
    GoogleMap mGoogleMap;
    MapFragment mapFragment = null;
    private Double myLat;
    private Double myLong;
    RequestQueue requestQueue;
    public ArrayList<OpenChargeStation> arrayOfStations;

    TextView mLatitudeText;
    TextView mLongitudeText;
    String mLatitudeLabel, mLongitudeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.map_screen);
            requestQueue = Volley.newRequestQueue(this);

            buildGoogleApiClient();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(50000);
            locationRequest.setFastestInterval(20000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            initMap();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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
        mGoogleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        goToLocationZoom(40.7131212, -74.0006327, 15);
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
        /*favoritesList.add(new ChargingStation("Test1", "1 Pace Plaza, NYC", 1, "Yes"));
        favoritesList.add(new ChargingStation("Test2", "Columbus Park, NYC", 1, "Yes"));*/
        favoritesList.add(new ChargingStation("Test3", "Canal Street Station, NYC", 1, "Yes"));

        for (int i = 0; i < favoritesList.size(); i++) {
            String location = favoritesList.get(i).getLocation();

            try {
                results = geocoder.getFromLocationName(location, 1);
            } catch (IOException ioException) {
            }

            double stationLat = results.get(0).getLatitude();
            double stationLong = results.get(0).getLongitude();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(stationLat, stationLong))
                    .title(favoritesList.get(i).getStationName())
                    .icon(BitmapDescriptorFactory.defaultMarker(130)))
                    .setTag(i);
        }

        //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLong)).title("Your Location"));

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(40.710574, -74.005767)).title("Test Marker"));

        //For testing the navigation method:
        //navigate(40.710574, -74.005767, 40.758903, -73.985120);
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

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        android.location.Address address = list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        goToLocationZoom(lat, lng, 15);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {requestLocationUpdates();}

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        myLat = location.getLatitude();
        myLong = location.getLongitude();

        try {
            String lati = String.valueOf(myLat);
            String longi = String.valueOf(myLong);

            /*This function : getNearbyStation(...,...) takes three parameters, the latitutde and
             longitude and distance, then returns an ArrayList of OpenChargeStations
             within the specified distance from the specified latitude and longitude.
             Currently, it runs with the lat and long of your current location with a distance of 30.
            You can iterate through  the list "arrayofStations" after this executs and
             extract lat and long of each station to show on the screen.
              The OpenChargeStation class can be found in the content folder.
            */
            arrayOfStations = getNearbyStations(lati, longi, "30");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem converting coordinates to String.");
        }

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLong)).title("Your Location"));

        if (myLat == null || myLong == null) {
            Toast.makeText(this, "no location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      /*       TO: Consider calling
                ActivityCompat#requestPermissions
             here to request the missing permissions, and then overriding
              public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    int[] grantResults)
             to handle the case where the user grants the permission. See the documentation
             for ActivityCompat#requestPermissions for more details.*/

            Toast.makeText(this, "Please Enable Location Permissions To Execute This Function.",
                    Toast.LENGTH_LONG).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    //Launches the google maps app with the given coordinates and starts navigation
    public void navigate(double sourceLatitude, double sourceLongitude, double destLatitude, double destLongitude) {
        String sLat = Double.toString(sourceLatitude);
        String sLng = Double.toString(sourceLongitude);
        String dLat = Double.toString(destLatitude);
        String dLng = Double.toString(destLongitude);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + sLat + "," + sLng + "&daddr=" + dLat + "," + dLng));
        startActivity(intent);
    }

    //Input the latitude coordinates and longitude coordinates "AS STRINGS"

    private ArrayList<OpenChargeStation> getNearbyStations(String latCoor, String longCoor, String distance) {
        String url = "https://api.openchargemap.io/v2/poi/?output=json&countrycode=US&latitude=" + latCoor + "&longitude=" +
                longCoor + "&distance=" + distance + "&maxresults=5&compact=true&verbose=false&camelcase=true";
        arrayOfStations = new ArrayList<>();

        JsonArrayRequest arrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObj = response.getJSONObject(i).getJSONObject("addressInfo");
                                OpenChargeStation openChargeStation = new OpenChargeStation(
                                        jsonObj.getInt("id"), jsonObj.getString("title"), jsonObj.getString("addressLine1"),
                                        jsonObj.getString("town"), jsonObj.getString("stateOrProvince"), jsonObj.getString("postcode"),
                                        jsonObj.getDouble("latitude"), jsonObj.getDouble("longitude"), jsonObj.getString("contactTelephone1")
                                );
                                arrayOfStations.add(openChargeStation);
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                for (int k = 0; k < arrayOfStations.size(); k++) {
                                    //String location = arrayOfStations.get(i).getLocation();

                                    double stationLat = arrayOfStations.get(i).getLatitude();
                                    double stationLong = arrayOfStations.get(i).getLongitude();
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(stationLat, stationLong))
                                            .title(arrayOfStations.get(i).getTitle())
                                            .icon(BitmapDescriptorFactory.defaultMarker(130)));

                                }

                                Log.i(TAG, "OpenCharge parsed correctly: \n" + openChargeStation.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Problem parsing JSON");
                            }
                        }
                        //  Toast.makeText(getApplicationContext(), arrayOfStations.get(0).toString(), Toast.LENGTH_LONG).show();
                        // Put code to use data here
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Error");
            }
        });
        requestQueue.add(arrayRequest);
        return arrayOfStations;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //how to get unique station data on marker click?
        // When the marker is created... Do marker.setTag(i) --done
        //Then when it is clicked, do arrayOfStations.get(marker.getTag())... This returns the appropriate OpenChargeStation class
        //Then use the coordinates in there to execute the Intent (May have to use CASE function)
        //https://developers.google.com/maps/documentation/android-api/marker

        OpenChargeStation openChargeStationThis = new OpenChargeStation();

        if (true) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com?saddr=40.710968, -74.004730+&daddr=40.718303, -73.999195"));
            startActivity(intent);
        }
        return false;
    }


    private String getMarkersCoordinates(int i) {
        return null;
    }

}