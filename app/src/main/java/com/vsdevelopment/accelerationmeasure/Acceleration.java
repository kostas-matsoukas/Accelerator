package com.vsdevelopment.accelerationmeasure;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Acceleration extends Activity {

    private LocationUpdate locationUpdate;
    private Thread thread;
    private Handler accelerationHandler;
    private TextView sixtyText;
    private TextView hundredText;
    private TextView hundredSixtyText;
    private TextView currentSpeedText;
    private ProgressBar sixtyProgressBar;
    private ProgressBar hundredProgressBar;
    private ProgressBar hundredSixtyProgressBar;
    private TextView usedSatellitesText;
    private TextView totalSatellitesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);

        sixtyText = (TextView) findViewById(R.id.accelerationSixty);
        hundredText = (TextView) findViewById(R.id.accelerationHundred);
        hundredSixtyText = (TextView) findViewById(R.id.accelerationHundredSixty);
        currentSpeedText = (TextView) findViewById(R.id.accelerationCurrentSpeed);
        sixtyProgressBar = (ProgressBar) findViewById(R.id.accelerationSixtyProgress);
        usedSatellitesText = (TextView) findViewById(R.id.accelerationUsedSatellites);
        totalSatellitesText = (TextView) findViewById(R.id.accelerationTotalSatellites);
        hundredProgressBar = (ProgressBar) findViewById(R.id.accelerationHundredProgress);
        hundredSixtyProgressBar = (ProgressBar) findViewById(R.id.accelerationHundredSixtyProgress);

        accelerationHandler = new AccelerationHandler(getMainLooper());
        locationUpdate = new LocationUpdate(this, accelerationHandler);
        thread = new Thread(locationUpdate);
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_acceleration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            thread.interrupt();
        }
        super.onDestroy();
    }

    private class AccelerationHandler extends Handler {

        public AccelerationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationUpdate.SIXTY_COMPLETED:
                    sixtyText.setText(" " + String.valueOf(msg.obj) + " s");
                    sixtyProgressBar.setVisibility(View.GONE);
                    break;
                case LocationUpdate.HUNDRED_COMPLETED:
                    hundredText.setText(" " + String.valueOf(msg.obj) + " s");
                    hundredProgressBar.setVisibility(View.GONE);
                    break;
                case LocationUpdate.HUNDRED_SIXTY_COMPLETED:
                    hundredSixtyText.setText(" " + String.valueOf(msg.obj) + " s");
                    hundredSixtyProgressBar.setVisibility(View.GONE);
                    break;
                case LocationUpdate.CURRENT_SPEED:
                    currentSpeedText.setText(" " + String.valueOf(msg.obj) + " km/h");
                    break;
                case LocationUpdate.ACCELERATION_STARTED:
                    sixtyProgressBar.setVisibility(View.VISIBLE);
                    hundredProgressBar.setVisibility(View.VISIBLE);
                    hundredSixtyProgressBar.setVisibility(View.VISIBLE);
                    hundredText.setText(null);
                    hundredSixtyText.setText(null);
                    break;
                case LocationUpdate.ACCELERATION_STOPPED:
                    hundredProgressBar.setVisibility(View.GONE);
                    hundredSixtyProgressBar.setVisibility(View.GONE);
                    break;
                case LocationUpdate.SATELLITES_INFO:
                    usedSatellitesText.setText(msg.arg1);
                    totalSatellitesText.setText(msg.arg2);
                    break;
                case LocationUpdate.CONNECTION_OK:
                    Toast.makeText(Acceleration.this, "Connected", Toast.LENGTH_SHORT);
                    break;
                case LocationUpdate.CONNECTION_FAILED:
                    Toast.makeText(Acceleration.this, "Connection failed", Toast.LENGTH_SHORT);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public Handler getAccelerationHandler() {
        return accelerationHandler;
    }

    public TextView getHundredText() {
        return hundredText;
    }

    public TextView getHundredSixtyText() {
        return hundredSixtyText;
    }

    public TextView getCurrentSpeedText() {
        return currentSpeedText;
    }

    public ProgressBar getHundredProgressBar() {
        return hundredProgressBar;
    }

    public ProgressBar getHundredSixtyProgressBar() {
        return hundredSixtyProgressBar;
    }

    public TextView getSixtyText() {
        return sixtyText;
    }

    public void setSixtyText(TextView sixtyText) {
        this.sixtyText = sixtyText;
    }

    public ProgressBar getSixtyProgressBar() {
        return sixtyProgressBar;
    }

    public void setSixtyProgressBar(ProgressBar sixtyProgressBar) {
        this.sixtyProgressBar = sixtyProgressBar;
    }
}
