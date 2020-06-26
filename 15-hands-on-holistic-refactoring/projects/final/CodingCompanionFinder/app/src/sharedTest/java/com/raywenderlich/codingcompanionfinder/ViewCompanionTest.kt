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

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.raywenderlich.codingcompanionfinder.models.Address
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.raywenderlich.codingcompanionfinder.models.Breeds
import com.raywenderlich.codingcompanionfinder.models.Contact
import com.raywenderlich.codingcompanionfinder.searchforcompanion.ViewCompanionFragment
import com.raywenderlich.codingcompanionfinder.searchforcompanion.ViewCompanionFragmentArgs
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ViewCompanionTest {
  @Before
  fun beforeTestsRun() {
// 1
    val animal = Animal(
      22,
      Contact(
        phone = "404-867-5309",
        email = "coding.companion@razware.com",
        address = Address(
          "",
          "",
          "Atlanta",
          "GA",
          "30303",
          "USA"
        )
      ),
      "5",
      "small",
      arrayListOf(),
      Breeds("shih tzu", "", false, false),
      "Spike",
      "male",
      "A sweet little guy with spikey teeth!"
    )
// 2
    val bundle = ViewCompanionFragmentArgs(animal).toBundle()
// 3
    launchFragmentInContainer<ViewCompanionFragment>(
      bundle,
      R.style.AppTheme
    )
  }

  @Test
  fun check_that_all_values_display_correctly() {
    onView(withText("Spike")).check(matches(isDisplayed()))
    onView(withText("Atlanta, GA")).check(matches(isDisplayed()))
    onView(withText("shih tzu")).check(matches(isDisplayed()))
    onView(withText("5")).check(matches(isDisplayed()))
    onView(withText("male")).check(matches(isDisplayed()))
    onView(withText("small")).check(matches(isDisplayed()))
    onView(withText("A sweet little guy with spikey teeth!")).check(matches(isDisplayed()))
        onView(withText("404-867-5309")).check(matches(isDisplayed()))
        onView(withText("coding.companion@razware.com")).check(matches(isDisplayed()))
  }
}
