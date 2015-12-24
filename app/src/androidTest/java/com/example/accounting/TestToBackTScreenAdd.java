package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Дамир on 25.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class TestToBackTScreenAdd extends UiAutomatorTestCase {
    @Rule
    public final ActivityTestRule<AddOperationActivity> addOperation = new ActivityTestRule<AddOperationActivity>(AddOperationActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, MainActivity.class);
            result.putExtra("index", 0);
            return result;
        }
    };

    @Test
    public void test() throws UiObjectNotFoundException, RemoteException {
        findAndRunApp();
    }

    private void findAndRunApp() throws UiObjectNotFoundException, RemoteException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();
        device.pressRecentApps();
        UiObject app = new UiObject(new UiSelector().description("AddOperationActivity"));
        app.click();
    }

}
