package com.example.zakiya.greenr;

/**
 * Splash Screen
 */

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.FileUriExposedException;
import android.net.Uri;

public class SplashScreen extends Activity {

    // Splash screen timer

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*
        final VideoView videoView = (VideoView) findViewById(R.id.splashVid);
        Uri uri = Uri.parse("android.resource://"+ getPackageName() + "/" + R.raw.splashvideo);
        videoView.setVideoURI(uri);
        videoView.start();
        */
        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                // close this activity
                SplashScreen.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }


}