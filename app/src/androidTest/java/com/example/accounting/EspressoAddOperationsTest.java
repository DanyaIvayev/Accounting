package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.widget.Button;
import android.widget.EditText;

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
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.core.StringContains.containsString;
import static android.support.test.espresso.action.ViewActions.*;
/**
 * Created by Дамир on 23.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoAddOperationsTest  {
    @Rule public final ActivityTestRule<AddOperationActivity> addOperation = new ActivityTestRule<AddOperationActivity>(AddOperationActivity.class){
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
    public void fill() {
        onView(withId(R.id.typeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Перевод"))).perform(click());
        onView(withId(R.id.typeSpinner)).check(matches(withSpinnerText(containsString("Перевод"))));
        onView(withId(R.id.operValueET)).perform(typeText("1350.54"));
        onView(withId(R.id.billToSpinner)).check(matches(isEnabled()));
        onView(withId(R.id.operDescriptionET)).perform(replaceText("Описание"));
        onView(withId(R.id.okAddButton)).perform(
                scrollTo(),
                click());
    }

}
