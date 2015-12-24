package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import com.example.accounting.OperationActivity.OperItem;
import static org.hamcrest.Matchers.*;
/**
 * Created by Дамир on 23.12.2015.
 */

@RunWith(AndroidJUnit4.class)
public class EspressoDeleteOperations {
    @Rule
    public final ActivityTestRule<OperationActivity> operation = new ActivityTestRule<OperationActivity>(OperationActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, MainActivity.class);
            result.putExtra("index", 0);
            return result;
        }
    };

    //@Test
    public void deleteOper() {
//          onData(is(instanceOf(OperationActivity.OperAdapter.class)))
//                  .inAdapterView(allOf(withId(R.id.operListView), isDisplayed()))
//                  .perform(scrollTo());
    //    onData(anything()).inAdapterView(withId(R.id.operListView)).atPosition(5).perform(click());

        Espresso.onData(is(instanceOf(OperationActivity.OperAdapter.class))) .inAdapterView(withId(R.id.operListView));
        onData(allOf(is(instanceOf(OperItem.class)), withListItemCheck("1350.54"))).perform(click());
        onView(withText(R.string.menu_delete)).check(matches(isDisplayed()));
        onView(withText(R.string.menu_delete))
                .perform(click());
    }
    public static Matcher<Object> withListItemCheck(final String value) {

        return new BoundedMatcher<Object,OperItem>(OperItem.class) {
            @Override
            public boolean matchesSafely(OperItem myObj) {
                return (myObj.getValue()==Double.valueOf(value).doubleValue());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + value + "'");
            }
        };
    }
}
