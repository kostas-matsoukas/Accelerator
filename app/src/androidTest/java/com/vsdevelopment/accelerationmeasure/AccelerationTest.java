package com.vsdevelopment.accelerationmeasure;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class AccelerationTest extends ActivityUnitTestCase<Acceleration> {

    private Activity acceleration;
    private Instrumentation i;

    public AccelerationTest() {
        super(Acceleration.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent intent = new Intent(getInstrumentation().getTargetContext(), Acceleration.class);
        startActivity(intent, null, null);
        acceleration = getActivity();
    }

    public void testLayout() {
        assertNotNull(acceleration.findViewById(R.id.accelerationCurrentSpeed));
    }
}
