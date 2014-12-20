package com.vsdevelopment.accelerationmeasure;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.Handler;

public class LocationUpdate implements Runnable {


    LocationManager locationManager;
    int speed;
    Criteria criteria;
    boolean validAcceleration = false;
    long startTime;
    long endToHundredTime;
    long endToHundredSixtyTime;
    boolean accelerationStarted = false;
    float previousSpeed;
    float currentSpeed;
    Handler handler;

    @Override
    public void run() {

    }

    private void requestUpdates() {
        currentSpeed = (int) ((locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).
                getSpeed() * 3600) / 1000);

        if (currentSpeed == 0) { //reset values
            validAcceleration = true;
            accelerationStarted = false;
        } else if (currentSpeed < previousSpeed) {
            validAcceleration = false;
            accelerationStarted = false;
        }

        if (validAcceleration && !accelerationStarted) {
            accelerationStarted = true;
            startTime = System.currentTimeMillis();
        } else if (validAcceleration && currentSpeed == 100) {
            endToHundredTime = System.currentTimeMillis();
        } else if (validAcceleration && currentSpeed == 160) {
            endToHundredSixtyTime = System.currentTimeMillis();
        }
        previousSpeed = currentSpeed;
    }
}
