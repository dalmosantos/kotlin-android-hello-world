package com.example.helloworld

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenActivityStarts_textViewShouldBeEmpty() {
        onView(withId(R.id.tvGreet))
            .check(matches(withText("")))
    }

    @Test
    fun whenButtonClicked_textViewShouldShowHelloAndroid() {
        // Given - Initial state verification
        onView(withId(R.id.tvGreet))
            .check(matches(withText("")))

        // When - Action
        onView(withId(R.id.btnGreet))
            .perform(click())

        // Then - Result verification
        onView(withId(R.id.tvGreet))
            .check(matches(withText(R.string.hello_android)))
    }

    @Test
    fun buttonShouldBeVisible_andClickable() {
        onView(withId(R.id.btnGreet))
            .check(matches(allOf(
                isDisplayed(),
                isClickable()
            )))
    }

    @Test
    fun buttonShouldHaveCorrectText() {
        onView(withId(R.id.btnGreet))
            .check(matches(withText(R.string.press_me)))
    }
}
