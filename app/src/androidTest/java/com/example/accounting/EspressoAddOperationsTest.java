package com.example.accounting;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.action.ViewActions.click;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by Дамир on 23.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoAddOperationsTest  {
    @Rule public final ActivityTestRule<AddOperationActivity> addOperation = new ActivityTestRule<AddOperationActivity>(AddOperationActivity.class);

    @Test
    public void fill(){
        onView(withId(R.id.typeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Перевод"))).perform(click());
        onView(withId(R.id.typeSpinner)).check(matches(withSpinnerText(containsString("Перевод"))));
        onView(withId(R.id.operValueET)).perform(typeText("1350.54"));
        
    }

}
