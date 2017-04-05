package com.example.zakiya.greenr;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton favButton = (ImageButton) findViewById(R.id.favoriteButton);
        favButton.setOnClickListener(this);

        ImageButton mapButton = (ImageButton) findViewById(R.id.mapsbutton);
        mapButton.setOnClickListener(this);
    }

    public void goToFavorites(View v) {
        Log.i("clicks","You Clicked The Favorites Button");
        Intent i=new Intent(MainActivity.this, findStation.class);
        startActivity(i);
    }

    public void loadMapScreen(View v){
        Log.i("clicks","You Clicked The Map Button");
        Intent i=new Intent(MainActivity.this, mapScreen.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.favoriteButton: goToFavorites(v);
                break;
            case R.id.mapsbutton: loadMapScreen(v);
                break;
        }
    }
    public void onClick2(View v) {

        try {

            s = null;
            s.trim();
        }
        catch (Exception e)
        {
            FirebaseCrash.logcat(Log.ERROR,"TAG",s+"");
        }
    }
}
