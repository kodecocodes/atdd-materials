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

@RunWith(AndroidJUnit4::class)
class ViewCompanionTest {

  @Before
  fun beforeTestsRun() {
// 1
    val animal = Animal(
      22,
      Contact(
        phone = "(706) 236-4537",
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
    onView(withText("(706) 236-4537")).check(matches(isDisplayed()))
    onView(withText("coding.companion@razware.com")).check(matches(isDisplayed()))
  }

}
