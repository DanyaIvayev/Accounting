package com.example.accounting;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by Дамир on 24.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoSearchDescription {
    @Rule
    public final ActivityTestRule<MainActivity> addBill = new ActivityTestRule<MainActivity>(MainActivity.class);

    //@Test
    public void searchDescriptionTest(){

        onView(withId(R.id.search)).perform(click());
        onView(withText(R.string.search_title)).check(matches(isDisplayed()));
        onView(withId(R.id.searchdescET)).perform(replaceText("Прем"), ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.operMessage))
                .check(matches(isDisplayed()));

    }
}
