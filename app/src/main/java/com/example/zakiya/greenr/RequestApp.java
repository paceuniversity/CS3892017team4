package com.example.zakiya.greenr;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Arize on 4/17/2017.
 */

public class RequestApp extends Application {

    private static RequestApp mInstance;
    private static RequestQueue opQueue;
    private static String TAG = "DEFAULT";

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized RequestApp getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(opQueue == null){
            Volley.newRequestQueue(this.getApplicationContext());
        }
        return opQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? "DEFAULT" : tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public <T> void cancelPendingRequests(Object tag){
        if(opQueue != null){
            opQueue.cancelAll(tag);
        }
    }
}
