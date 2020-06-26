/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.codingcompanionfinder

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.raywenderlich.codingcompanionfinder.testhooks.IdlingEntity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.BeforeClass

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FindCompanionInstrumentedTest {

  lateinit var testScenario: ActivityScenario<MainActivity>

  private val idlingResource = SimpleIdlingResource()

  companion object {
    // 1
    val server = MockWebServer()
    // 2
    val dispatcher: Dispatcher = object : Dispatcher() {
      @Throws(InterruptedException::class)
      override fun dispatch(request: RecordedRequest): MockResponse {
        return CommonTestDataUtil.dispatch(request) ?: MockResponse().setResponseCode(
            404
        )
      }
    }

    lateinit var startIntent: Intent

    @BeforeClass
    @JvmStatic
    fun setup() {
// 3
      server.setDispatcher(dispatcher)
      server.start()
// 3
      startIntent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
      startIntent.putExtra(MainActivity.PETFINDER_URI,
          server.url("").toString())
    }
  }

  @Subscribe
  fun onEvent(idlingEntity: IdlingEntity) {
    idlingResource.incrementBy(idlingEntity.incrementValue)
  }

  @Before
  fun beforeTestsRun() {
    testScenario = ActivityScenario.launch(startIntent)
    EventBus.getDefault().register(this)
    IdlingRegistry.getInstance().register(idlingResource)
  }

  @After
  fun afterTestsRun() {
    // eventbus and idling resources unregister.
    IdlingRegistry.getInstance().unregister(idlingResource)
    EventBus.getDefault().unregister(this)
    testScenario.close()
  }

  @Test
  fun pressing_the_find_bottom_menu_item_takes_the_user_to_the_find_page() {
    onView(withId(R.id.searchForCompanionFragment)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withId(R.id.searchFieldText)).check(matches(isDisplayed()))
  }

  @Test
  fun searching_for_a_companion_and_tapping_on_it_takes_the_user_to_the_companion_details() {
    find_and_select_kevin_in_30318()
    onView(withText("Rome, GA")).check(matches(isDisplayed()))
  }

  @Test
  fun verify_that_companion_details_shows_a_valid_phone_number_and_email() {
    find_and_select_kevin_in_30318()
    onView(withText("(706) 236-4537")).check(matches(isDisplayed()))
    onView(withText("adoptions@gahomelesspets.com")).check(matches(isDisplayed()))
  }

  private fun find_and_select_kevin_in_30318(){
    onView(withId(R.id.searchForCompanionFragment)).perform(click())
    onView(withId(R.id.searchFieldText)).perform(typeText("30318"))
    onView(withId(R.id.searchButton)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withText("KEVIN")).perform(click())
  }
}
