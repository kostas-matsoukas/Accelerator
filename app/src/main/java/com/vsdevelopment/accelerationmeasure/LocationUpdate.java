package com.vsdevelopment.accelerationmeasure;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationUpdate implements Runnable, LocationListener, GoogleApiClient.OnConnectionFailedListener,  GoogleApiClient.ConnectionCallbacks {


    private LocationManager locationManager;
    private int speed;
    private Criteria criteria;
    private boolean validAcceleration = false;
    private long startTime;
    private long endToSixtyTime;
    private long endToHundredTime;
    private long endToHundredSixtyTime;
    private boolean accelerationStarted = false;
    private int previousSpeed;
    private int currentSpeed;
    private Handler handler;
    private Context context;
    private Handler mainHandler;
    private GoogleApiClient googleApiClient;
    private Location location;
    private boolean isConnected = false;

    public final static String DEFAULT_PROVIDER = LocationManager.GPS_PROVIDER;
    public final static int MAX_INTERVAL_MSEC = 5000;
    public final static int INTERVAL_MSEC = 1000;
    public final static int HUNDRED_COMPLETED = 1;
    public final static int HUNDRED_SIXTY_COMPLETED = 2;
    public final static int CURRENT_SPEED = 3;
    public final static int ACCELERATION_STARTED = 4;
    public final static int ACCELERATION_STOPPED = 5;
    public final static int MISSED_GEAR_SPEED_OFFSET_KMH = 1;
    public final static int SIXTY_COMPLETED = 7;
    public final static int PROVIDER_UNAVAILABLE = 8;
    public final static int SATELLITES_INFO = 9;
    public final static int CONNECTION_OK = 10;
    public final static int CONNECTION_FAILED = 11;

    public LocationUpdate(Context context, Handler handler) {
        this.context = context;
        mainHandler = handler;
        previousSpeed = currentSpeed = 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("app", "locationChanged");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("app", "connected");
        mainHandler.sendEmptyMessage(CONNECTION_OK);
        isConnected = true;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(MAX_INTERVAL_MSEC);
        mLocationRequest.setFastestInterval(INTERVAL_MSEC);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        handler.post(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mainHandler.sendEmptyMessage(CONNECTION_FAILED);
        Log.i("app", "connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("app", "connection suspended");
    }

    @Override
    public void run() {
        if (googleApiClient == null) {
            Log.i("starting requesting", "s");
            Looper.prepare();
            handler = new Handler();
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            Looper.loop();
        }
        if (isConnected) {
            requestUpdates();
        }
    }

    public void requestUpdates() {
        if (Thread.interrupted()) {
            if (googleApiClient != null) {
                googleApiClient.disconnect();
            }
            handler.getLooper().quit();
            return;
        }
        logic();
    }

    public synchronized void logic() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            float msec = location.getSpeed();
            Log.i("m/sec", String.valueOf(msec));
            double kmh = msec * 3.6;
            int possibleSpeed = (int) Math.floor(kmh);
            currentSpeed = possibleSpeed <= 1 ? 0 : possibleSpeed;
            Message m = new Message();
            m.what = CURRENT_SPEED;
            m.obj = currentSpeed;
            Log.i("current_speed", String.valueOf(currentSpeed));
            mainHandler.sendMessage(m);

            if (currentSpeed == 0) { //reset values
                validAcceleration = true;
                accelerationStarted = false;
            } else if (currentSpeed < (previousSpeed - MISSED_GEAR_SPEED_OFFSET_KMH)) { // set offset maybe missed gear lol
                validAcceleration = false;
                accelerationStarted = false;
                mainHandler.sendEmptyMessage(ACCELERATION_STOPPED);
            }

            if (validAcceleration) {
                if (!accelerationStarted) {
                    accelerationStarted = true;
                    startTime = System.currentTimeMillis();
                    mainHandler.sendEmptyMessage(ACCELERATION_STARTED);
                } else if (currentSpeed == 60) {
                    endToSixtyTime = (System.currentTimeMillis() - startTime) / 1000;
                    m = new Message();
                    m.what = SIXTY_COMPLETED;
                    m.obj = endToSixtyTime;
                    mainHandler.sendMessage(m);
                } else if (currentSpeed == 100) {
                    endToHundredTime = (System.currentTimeMillis() - startTime) / 1000;
                    m = new Message();
                    m.what = HUNDRED_COMPLETED;
                    m.obj = endToHundredTime;
                    mainHandler.sendMessage(m);
                } else if (currentSpeed == 160) {
                    endToHundredSixtyTime = (System.currentTimeMillis() - startTime) / 1000;
                    m = new Message();
                    m.what = HUNDRED_SIXTY_COMPLETED;
                    m.obj = endToHundredSixtyTime;
                    mainHandler.sendMessage(m);
                }
            }
            previousSpeed = currentSpeed;
        }
        handler.postDelayed(this, INTERVAL_MSEC);
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public boolean isValidAcceleration() {
        return validAcceleration;
    }

    public void setValidAcceleration(boolean validAcceleration) {
        this.validAcceleration = validAcceleration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndToHundredTime() {
        return endToHundredTime;
    }

    public void setEndToHundredTime(long endToHundredTime) {
        this.endToHundredTime = endToHundredTime;
    }

    public long getEndToHundredSixtyTime() {
        return endToHundredSixtyTime;
    }

    public void setEndToHundredSixtyTime(long endToHundredSixtyTime) {
        this.endToHundredSixtyTime = endToHundredSixtyTime;
    }

    public long getEndToSixtyTime() {
        return endToSixtyTime;
    }

    public void setEndToSixtyTime(long endToSixtyTime) {
        this.endToSixtyTime = endToSixtyTime;
    }

    public boolean isAccelerationStarted() {
        return accelerationStarted;
    }

    public void setAccelerationStarted(boolean accelerationStarted) {
        this.accelerationStarted = accelerationStarted;
    }

    public int getPreviousSpeed() {
        return previousSpeed;
    }

    public void setPreviousSpeed(int previousSpeed) {
        this.previousSpeed = previousSpeed;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
