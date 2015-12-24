package com.example.accounting;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTabHost;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.accounting.BillFragment.BillItem;
import com.example.accounting.BillFragment.BillAdapter;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Created by Дамир on 24.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoDeletePBillTest {
    @Rule
    public final ActivityTestRule<MainActivity> addBill = new ActivityTestRule<MainActivity>(MainActivity.class);

//    @Test
    public void deleteBillEspresso(){
        onView(withText(R.string.tab_title_1)).check(matches(isDisplayed()));
        Espresso.onData(is(instanceOf(BillAdapter.class))) .inAdapterView(withId(R.id.billListView));
        onData(allOf(is(instanceOf(BillItem.class)), withListItemCheck("Райффайзен"))).perform(click());
        onView(withText(R.string.menu_delete)).check(matches(isDisplayed()));
        onView(withText(R.string.menu_delete))
                .perform(click());
        onView(withText(R.string.bill_delete_message)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }
    public static Matcher<Object> withListItemCheck(final String value) {

        return new BoundedMatcher<Object,BillItem>(BillItem.class) {
            @Override
            public boolean matchesSafely(BillItem myObj) {
                return myObj.getBillName().equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + value + "'");
            }
        };
    }

}
