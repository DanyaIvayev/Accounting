package com.example.accounting;

/**
 * Created by Дамир on 24.12.2015.
 */
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import com.jayway.android.robotium.solo.Solo;
public class RobotiumTestRotateSecondScreen extends ActivityInstrumentationTestCase2<AddOperationActivity> {
    private Solo solo;
    public RobotiumTestRotateSecondScreen() {
        super(AddOperationActivity.class);
    }
    @Override
    public void setUp() throws Exception{
        Intent i = new Intent();
        i.putExtra("myExtra", "anyValue");
        setActivityIntent(i);
        solo = new Solo(getInstrumentation(), getActivity());
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void rotateSecondTest() throws Exception{
        solo.setActivityOrientation(Solo.LANDSCAPE);
        Thread.sleep(3000);
        solo.setActivityOrientation(Solo.PORTRAIT);
        Thread.sleep(3000);

    }
}
