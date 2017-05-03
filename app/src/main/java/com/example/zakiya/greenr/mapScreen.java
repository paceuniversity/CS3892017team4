package com.example.zakiya.greenr;

/**
 * Created by shane on 4/4/17.
 */

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class mapScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    MapFragment mapFragment;
    Marker currentLocationMarker;
    private static GoogleMap mMap;
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

        //***************************************************TESTING**************************************************
        /*Toast.makeText(this, "Directions", Toast.LENGTH_LONG).show();
        getDirections("Manhattan", "Brooklyn");*/
        //***************************************************TESTING**************************************************
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
        LatLng destinationPlace = new LatLng(markerStation.getLatitude(), markerStation.getLongitude());
        if (markerStation != null) {
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    mLayout.setVisibility(View.VISIBLE);
                }
                public void onFinish() {
                    mLayout.setVisibility(View.GONE);
                }
            }.start();

            getDirections(currentLocation, destinationPlace);
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

    private void getDirections(String origin, String destination){
        String UrlStart = "https://maps.googleapis.com/maps/api/directions/json?";
        String key = "AIzaSyDmaV_3l8Spg16qVgEPuNCtqIqLPtDISBs";
        String url = UrlStart+"origin="+origin+"&destination="+destination+"&key="+key;

        Log.i("URL", url);

        new DirectionTask().execute(url);
    }

    private void getDirections(LatLng origin, LatLng destination){
        String UrlStart = "https://maps.googleapis.com/maps/api/directions/json?";
        String key = "AIzaSyDmaV_3l8Spg16qVgEPuNCtqIqLPtDISBs";
        String oCoords = Double.toString(origin.latitude)+","+Double.toString(origin.longitude);
        String dCoords = Double.toString(destination.latitude)+","+Double.toString(destination.longitude);
        String url = UrlStart+"origin="+oCoords+"&destination="+dCoords+"&key="+key;
        new DirectionTask().execute(url);
    }

    public static void drawLine(List<LatLng> points){
        PolylineOptions opt = new PolylineOptions();
        for(int i = 0; i<points.size(); i++)
            opt.add(points.get(i));
        mMap.addPolyline(opt);
    }

    private class DirectionTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String link = params[0];

            try {
                URL url = new URL(link);

                InputStream stream = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){

            Log.i("POSTEXEC", "");

            if(result==null)
                return;

            Log.i("POSTEXEC NOTNULL", "");

            try{
                JSONObject response = new JSONObject(result);
                Log.i("DIRECTIONSRESPONSE", response.toString());

                //JSONObject poly = response.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");

                JSONArray jsonRoutes = response.getJSONArray("routes");
                Log.i("JSONPARSE", jsonRoutes.toString());
                JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
                JSONObject poly = jsonRoute.getJSONObject("overview_polyline");


                String encodedPoints = poly.getString("points");
                Log.i("ENCODEDPOINTS", encodedPoints);

                List<LatLng> points = PolyUtil.decode(encodedPoints);

                mapScreen.drawLine(points);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}