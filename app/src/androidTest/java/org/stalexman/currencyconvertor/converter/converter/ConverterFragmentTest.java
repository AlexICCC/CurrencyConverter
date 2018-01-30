package org.stalexman.currencyconvertor.converter.converter;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stalexman.currencyconvertor.R;
import org.stalexman.currencyconvertor.converter.ConverterActivity;

@RunWith(AndroidJUnit4.class)
public class ConverterFragmentTest {

    @Rule
    public ActivityTestRule<ConverterActivity> mConverterActivity =
            new ActivityTestRule<>(ConverterActivity.class);

    @Test
    public void emptyTest() {
        Espresso.onView(withId(R.id.et_input)).perform(typeText(""));
        Espresso.onView(withId(R.id.tv_result)).check(matches(withText("")));
    }

    @Test
    public void firstTest() {
        Espresso.onView(withId(R.id.et_input)).perform(typeText("100"));
        Espresso.onView(withId(R.id.tv_result)).check(matches(withText("1.78")));
    }

    @Test
    public void testFromAUDtoAUD() {
        Espresso.onView(withId(R.id.et_input)).perform(typeText("100"));

        Espresso.onView(withId(R.id.tv_input_curr)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.search_dialog_listView)).atPosition(0).perform(click());

        Espresso.onView(withId(R.id.tv_output_curr)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.search_dialog_listView)).atPosition(0).perform(click());

        Espresso.onView(withId(R.id.tv_result)).check(matches(withText("100.00")));
    }
}
