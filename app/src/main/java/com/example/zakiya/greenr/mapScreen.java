package com.example.zakiya.greenr;

/**
 * Created by shane on 4/4/17.
 */

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class mapScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    MapFragment mapFragment;
    Marker currentLocationMarker;
    private GoogleMap mMap;
    LatLng currentLocation;
    protected static final String TAG = "MainActivity";
    RequestQueue requestQueue;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private ArrayList<OpenChargeStation> arrayOfStations;
    private Button favouritesAdd;
    private LinearLayout mLayout;
    private  OpenChargeStation markerStation;
    FirebaseDatabase database;
    DatabaseReference updateStation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (googleServicesAvailable()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_screen);
            requestQueue = Volley.newRequestQueue(this);
            locationRequest = new LocationRequest();
            locationRequest.setInterval(50000);
            locationRequest.setFastestInterval(20000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            buildGoogleApiClient();
            initMap();

            mLayout= (LinearLayout) findViewById(R.id.button_holder);

            favouritesAdd = (Button) findViewById(R.id.favouritesAdd);
            favouritesAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStationToDatabase(markerStation);
                }
            });
            database = FirebaseDatabase.getInstance();
            updateStation = database.getReference("OpenCharge Station");

        } else {
            Toast.makeText(this.getApplicationContext(), "Please Install Google Play Services", Toast.LENGTH_LONG).show();
        }
    }

    public void initMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
            Log.e(TAG, "Google Services not available");
        } else {
            Toast.makeText(this, "):", Toast.LENGTH_LONG).show();
        }
        return false;
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
        locationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void goToLocationZoom(LatLng latAndLong, float zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latAndLong, zoom);
        mMap.moveCamera(update);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation).title("Your Location"));
        goToLocationZoom(currentLocation, 10);

        String lat = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        getNearbyStations(lat, longitude, "30");
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Enable Location Permissions To Execute This Function.",
                    Toast.LENGTH_LONG).show();
        }
        locationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    //Input the latitude coordinates and longitude coordinates "AS STRINGS"

    private ArrayList<OpenChargeStation> getNearbyStations(String latCoor, String longCoor, String distance) {
        String url = "https://api.openchargemap.io/v2/poi/?output=json&latitude=" + latCoor + "&longitude=" +
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Problem parsing JSON");
                            }
                        }
                        Log.i(TAG, "OpenCharge parsed correctly: \n" + arrayOfStations.get(0).toString());
                        populateMapWithStations();
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

    private void populateMapWithStations() {
        Marker mapMarker;
        for (int i = 0; i < arrayOfStations.size(); i++) {
            double stationLat = arrayOfStations.get(i).getLatitude();
            double stationLong = arrayOfStations.get(i).getLongitude();
            mapMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stationLat, stationLong))
                    .title(arrayOfStations.get(i).getTitle())
                    .icon(BitmapDescriptorFactory.defaultMarker(130)));
            mapMarker.setTag(i);
        }
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        markerStation = arrayOfStations.get((int) marker.getTag());
        if (markerStation != null) {
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    mLayout.setVisibility(View.VISIBLE);
                }
                public void onFinish() {
                    mLayout.setVisibility(View.GONE);
                }
            }.start();
        }else {
            Log.e(TAG, "ERROR WHEN CLICKING MARKER TO RETRIEVE STATION");
        }
        return false;
    }

    public void addStationToDatabase(OpenChargeStation openChargeStation){
        String key = updateStation.push().getKey();
        updateStation.child(key).setValue(openChargeStation);
        Log.i(TAG,"MARKER STATION Just Sent");
    }
}