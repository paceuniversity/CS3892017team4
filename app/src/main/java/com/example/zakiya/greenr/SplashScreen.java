package com.example.zakiya.greenr;

/**
 * Splash Screen
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final VideoView videoView = (VideoView) findViewById(R.id.VideoView);
        videoView.setVideoPath(
                "User/user/AndroidStudioProjects/Greenr/app/src/main/res/drawable/splashVideo.mp4");

        videoView.start();
        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(new Intent(SplashScreen.this, MainActivity.class));

                // close this activity
                SplashScreen.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }


}