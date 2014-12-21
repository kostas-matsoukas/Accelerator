package com.vsdevelopment.accelerationmeasure;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.TextView;

public class LocationUpdateTest extends ActivityUnitTestCase<Acceleration> {

    private Acceleration acceleration;
    private LocationUpdate locationUpdate;
    public String provider = LocationManager.GPS_PROVIDER;
    private LocationManager lm;
    private float sixtyKmhInMs = (float) (60 / 3.6);
    private float hundredKmhInMs = (float) (100 / 3.6);
    private float hundredSixtyKmhInMs = (float) (161 / 3.6);
    private final static int SLEEP_TIME = 100;

    public LocationUpdateTest() {
        super(Acceleration.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent intent = new Intent(getInstrumentation().getTargetContext(), Acceleration.class);
        startActivity(intent, null, null);
        acceleration = getActivity();
        lm = (LocationManager) acceleration.getSystemService(acceleration.LOCATION_SERVICE);
        lm.addTestProvider(provider,
                        "requiresNetwork" == "",
                        "requiresSatellite" == "",
                        "requiresCell" == "",
                        "hasMonetaryCost" == "",
                        "supportsAltitude" == "supportsAltitude",
                        "supportsSpeed" == "supportsSpeed",
                        "supportsBearing" == "",
                        Criteria.POWER_LOW,
                        android.location.Criteria.ACCURACY_FINE);
        lm.setTestProviderEnabled(provider, true);
    }

    public void testAccelerationStartedIfSpeedZero() throws InterruptedException {
        locationUpdate = new LocationUpdate(acceleration, acceleration.getAccelerationHandler());
        locationUpdate.setPreviousSpeed(0);
        _setMockLocation(0);

        Thread.sleep(SLEEP_TIME);
        locationUpdate.logic();
        Thread.sleep(SLEEP_TIME);

        assertTrue(locationUpdate.isAccelerationStarted());
        assertEquals("", acceleration.getHundredText().getText());
        assertEquals("", acceleration.getHundredSixtyText().getText());
        assertEquals(View.VISIBLE, acceleration.getHundredProgressBar().getVisibility());
        assertEquals(View.VISIBLE, acceleration.getHundredSixtyProgressBar().getVisibility());
    }

    public void testZeroToSixtyHundredIfValid() throws InterruptedException {
        acceleration.getSixtyProgressBar().setVisibility(View.VISIBLE);
        locationUpdate = new LocationUpdate(acceleration, acceleration.getAccelerationHandler());
        locationUpdate.setPreviousSpeed(50);
        locationUpdate.setAccelerationStarted(true);
        locationUpdate.setValidAcceleration(true);
        locationUpdate.setStartTime(System.currentTimeMillis() - 10000);
        _setMockLocation(sixtyKmhInMs);

        Thread.sleep(SLEEP_TIME);
        locationUpdate.logic();
        Thread.sleep(SLEEP_TIME);

        assertEquals(10, locationUpdate.getEndToSixtyTime());
        assertEquals(View.GONE, acceleration.getSixtyProgressBar().getVisibility());
    }

    public void testZeroToHundredIfValid() throws InterruptedException {
        acceleration.getHundredProgressBar().setVisibility(View.VISIBLE);
        locationUpdate = new LocationUpdate(acceleration, acceleration.getAccelerationHandler());
        locationUpdate.setPreviousSpeed(50);
        locationUpdate.setAccelerationStarted(true);
        locationUpdate.setValidAcceleration(true);
        locationUpdate.setStartTime(System.currentTimeMillis() - 10000);
        _setMockLocation(hundredKmhInMs);

        Thread.sleep(SLEEP_TIME);
        locationUpdate.logic();
        Thread.sleep(SLEEP_TIME);

        assertEquals(10, locationUpdate.getEndToHundredTime());
        assertEquals(0, locationUpdate.getEndToHundredSixtyTime());
        assertEquals(View.GONE, acceleration.getHundredProgressBar().getVisibility());
    }

    public void testZeroToHundredSixtyIfValid() throws InterruptedException {
        locationUpdate = new LocationUpdate(acceleration, acceleration.getAccelerationHandler());
        locationUpdate.setPreviousSpeed(50);
        locationUpdate.setAccelerationStarted(true);
        locationUpdate.setValidAcceleration(true);
        locationUpdate.setStartTime(System.currentTimeMillis());
        _setMockLocation(hundredSixtyKmhInMs);

        Thread.sleep(SLEEP_TIME);
        locationUpdate.logic();
        Thread.sleep(SLEEP_TIME);

        assertEquals(0, locationUpdate.getEndToHundredTime());
        assertNotNull(locationUpdate.getEndToHundredSixtyTime());
        assertEquals(String.valueOf(locationUpdate.getEndToHundredSixtyTime()),
                ((TextView)acceleration.findViewById(R.id.accelerationHundredSixty)).getText());
        assertEquals(String.valueOf(locationUpdate.getCurrentSpeed()),
                ((TextView)acceleration.findViewById(R.id.accelerationCurrentSpeed)).getText());
    }

    private void _setMockLocation(final float speed) {
        Location location = new Location(provider);
        location.setSpeed(speed);
        location.setLatitude(50);
        location.setLongitude(50);
        location.setAccuracy(100);
        location.setElapsedRealtimeNanos(10);
        location.setTime(System.currentTimeMillis());
        lm.setTestProviderLocation(provider, location);
    }
}
