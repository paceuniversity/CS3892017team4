package com.example.zakiya.greenr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button favButton = (Button) findViewById(R.id.favoriteButton);
        favButton.setOnClickListener(this);

        Button mapButton = (Button) findViewById(R.id.mapsbutton);
        mapButton.setOnClickListener(this);
    }

    public void goToFavorites(View v) {
        Log.i("clicks", "You Clicked The Favorites Button");
        Intent i = new Intent(MainActivity.this, findStation.class);
        startActivity(i);
    }

    public void loadMapScreen(View v) {
        Log.i("clicks", "You Clicked The Map Button");
        Intent i = new Intent(MainActivity.this, mapScreen.class);
        startActivity(i);
    }

    /*  public void loadOpenChargeScreen(View v) {
          Log.i("clicks", "You Clicked The OpenCharge Button");
          Intent i = new Intent(MainActivity.this, OpenCharge.class);
          startActivity(i);
      }*/
    public void loadFavoriteStations(View v) {
        Log.i("clicks", "You Clicked The Favorites Button");
        Intent i = new Intent(MainActivity.this, findStation.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mapsbutton:
                loadMapScreen(v);
                break;
            case R.id.favoriteButton:
                loadFavoriteStations(v);
        }
    }
}