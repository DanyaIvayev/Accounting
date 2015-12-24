package com.example.accounting;


import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import com.jayway.android.robotium.solo.Solo;



/**
 * Created by Дамир on 24.12.2015.
 */

public class RobotiumTestRotateScreen extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    public RobotiumTestRotateScreen() {
        super(MainActivity.class);
    }
    @Override
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void rotateTest() throws Exception{
        solo.setActivityOrientation(Solo.LANDSCAPE);
        Thread.sleep(3000);

        solo.setActivityOrientation(Solo.PORTRAIT);
        Thread.sleep(3000);

    }
}
