package com.example.helloworld

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testButtonClickChangesTextView() {
        // Verifica o texto inicial do TextView
        onView(withId(R.id.tvGreet)).check(matches(withText("")))

        // Clica no botão
        onView(withId(R.id.btnGreet)).perform(click())

        // Verifica se o texto do TextView foi alterado
        onView(withId(R.id.tvGreet)).check(matches(withText(R.string.hello_android)))
    }
}