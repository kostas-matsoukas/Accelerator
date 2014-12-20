package com.vsdevelopment.accelerationmeasure;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Acceleration extends Activity implements LocationListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);
        handler = new Handler();
        previousSpeed = currentSpeed = 0;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentSpeed = (int) ((location.getSpeed() * 3600) / 1000);

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
        ((TextView) findViewById(R.id.currentSpeed)).setText(String.valueOf(currentSpeed));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acceleration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
