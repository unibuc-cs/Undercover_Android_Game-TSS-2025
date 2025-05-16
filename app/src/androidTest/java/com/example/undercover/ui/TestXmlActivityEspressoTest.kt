package com.example.undercover.ui

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestXmlActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TestXmlActivity::class.java)

    @Test
    fun testTypeAndClick() {
        onView(withHint("Număr de jucători"))
            .perform(typeText("2"), closeSoftKeyboard())

        onView(withText("Începe Jocul"))
            .perform(click())

        onView(withText("Introdu un număr între 3 și 20!"))
            .check(matches(isDisplayed()))
    }

} 