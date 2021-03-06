package com.example.zakiya.greenr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.example.zakiya.greenr.content.OpenChargeStation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class findStation extends AppCompatActivity {

    private Button findLocation;
    private ArrayList<String> listOfStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_station);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.charging_stations_list);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simplelayout, listOfStations);

        listView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference chargeSRef = database.getReference("OpenCharge Station");

        chargeSRef.orderByKey().limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Read each child of the user
                Log.d("Retrieve Data", "Charging Stations Being Retrieved.");


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    OpenChargeStation cS = child.getValue(OpenChargeStation.class);
                    listOfStations.add(cS.toString());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Database could not retrieve list of stations");
            }
        });
    }
}