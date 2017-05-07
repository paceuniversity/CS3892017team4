package com.example.zakiya.greenr;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MenuInflater;
import android.view.Menu;
import android.os.Bundle;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.*;
import java.util.*;
/**
 * Created by Zakiya on 4/16/17.
 */

public class favoritesScreen extends AppCompatActivity implements OnItemSelectedListener {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_station);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
