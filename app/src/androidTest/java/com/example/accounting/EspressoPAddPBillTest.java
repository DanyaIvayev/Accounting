package com.example.accounting;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.inputmethod.InputMethodManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static org.hamcrest.CoreMatchers.allOf;
/**
 * Created by Дамир on 24.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoPAddPBillTest {
    @Rule
    public final ActivityTestRule<MainActivity> addBill = new ActivityTestRule<MainActivity>(MainActivity.class);

    //@Test
    public void addBillTest(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.action_add_bill))
                .perform(click());
        //onView(withText(R.string.bill_delete_message)).check(matches(isDisplayed()));
        onView(withId(R.id.billnameT)).check(matches(allOf(withText(R.string.dialog_name), isDisplayed())));
        onView(withId(R.id.billnameET)).perform(replaceText("Райффайзен"));
        onView(withId(R.id.balanceET)).perform(typeText("25630.48"));
        //onView(withId(R.id.descriptionET)).perform(replaceText("Зарплатная карта"));
        onView(withId(R.id.descriptionET)).perform(replaceText("Зарплатная карта"), ViewActions.closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
    }
}
