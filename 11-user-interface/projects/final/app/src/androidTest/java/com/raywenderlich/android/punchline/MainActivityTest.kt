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

package com.raywenderlich.android.punchline

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

  private val mockRepository: Repository by inject()
  private var faker = Faker()

  @Before
  fun setUp() {
    declareMock<Repository>()
  }

  @Test
  fun onLaunchButtonIsDisplayed() {
    whenever(mockRepository.getJoke())
        .thenReturn(Single.just(Joke(
            faker.idNumber().valid(),
            faker.lorem().sentence())))

    ActivityScenario.launch(MainActivity::class.java)
    onView(withId(R.id.buttonNewJoke))
        .check(matches(isDisplayed()))
  }

  @Test
  fun onLaunchJokeIsDisplayed() {
    val joke = Joke(
        faker.idNumber().valid(),
        faker.lorem().sentence())
    whenever(mockRepository.getJoke())
        .thenReturn(Single.just(joke))

    ActivityScenario.launch(MainActivity::class.java)
    onView(withId(R.id.textJoke))
        .check(matches(withText(joke.joke)))
  }

  @Test
  fun onButtonClickNewJokeIsDisplayed() {
    whenever(mockRepository.getJoke())
        .thenReturn(Single.just(Joke(
            faker.idNumber().valid(),
            faker.lorem().sentence())))
    ActivityScenario.launch(MainActivity::class.java)

    val joke = Joke(
        faker.idNumber().valid(),
        faker.lorem().sentence())
    whenever(mockRepository.getJoke())
        .thenReturn(Single.just(joke))

    onView(withId(R.id.buttonNewJoke))
        .perform(click())
    onView(withId(R.id.textJoke))
        .check(matches(withText(joke.joke)))
  }
}