package com.github.factotum_sdp.factotum

import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.core.AllOf.allOf


@RunWith(AndroidJUnit4::class)
class MainActivityTest {


    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )


}