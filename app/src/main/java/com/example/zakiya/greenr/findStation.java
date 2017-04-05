package com.example.zakiya.greenr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class findStation extends AppCompatActivity  {

    private Button findLocation;
    private ArrayList<String> listOfStations = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_station);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference chargeSRef = database.getReference("Charging Stations");

        ChargingStation chargingStation = new ChargingStation("AA Mall Supercharger", "5016, Shennan East Road, Shenzhen, China", 3, true);
        String key = chargeSRef.push().getKey();
        chargeSRef.child(key).setValue(chargingStation);

        chargeSRef.orderByKey().limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Read each child of the user
                Log.d("Retrieve Data", "Charging Stations Being Retrieved.");

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ChargingStation cS = child.getValue(ChargingStation.class);
                    listOfStations.add(cS.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Database could not retrieve list of stations");
            }
        });

        if (listOfStations != null) {
            final ArrayAdapter adapter = new ArrayAdapter<>(this,
                    R.layout.listview, R.id.label_list, listOfStations);

            ListView listView = (ListView) findViewById(R.id.charging_stations_list);
            listView.setAdapter(adapter);
        } else {
            Log.e("Error", "The list was empty");
        }
    }
}
