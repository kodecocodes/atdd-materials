/*
 * Copyright (c) 2021 Razeware LLC
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
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.whenever
import org.mockito.stubbing.Answer

@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

  @get:Rule
  val mockProvider = MockProviderRule.create { clazz ->
    Mockito.mock(clazz.java)
  }

  private val mockRepository: Repository by inject()
  private var faker = Faker()

  @Test
  fun onLaunchButtonIsDisplayed() {
    declareMock<Repository> {
      whenever(getJoke())
          .thenReturn(Single.just(Joke(
              faker.idNumber().valid(),
              faker.lorem().sentence())))
    }

    ActivityScenario.launch(MainActivity::class.java)
    onView(withId(R.id.buttonNewJoke))
        .check(matches(isDisplayed()))
  }

  @Test
  fun onLaunchJokeIsDisplayed() {
    val joke = Joke(
        faker.idNumber().valid(),
        faker.lorem().sentence())
    declareMock<Repository> {
      whenever(getJoke())
          .thenReturn(Single.just(joke))
    }

    ActivityScenario.launch(MainActivity::class.java)
    onView(withId(R.id.textJoke))
        .check(matches(withText(joke.joke)))
  }

  @Test
  fun onButtonClickNewJokeIsDisplayed() {
    val joke = Joke(
        faker.idNumber().valid(),
        faker.lorem().sentence())
    val jokeQueueAnswer = object: Answer<Single<Joke>> {
      val jokes = listOf(
          Joke(
              faker.idNumber().valid(),
              faker.lorem().sentence()),
          joke
      )
      var currentJoke = -1
      override fun answer(invocation: InvocationOnMock?): Single<Joke> {
        currentJoke++
        return Single.just(jokes[currentJoke])
      }
    }
    declareMock<Repository> {
      whenever(getJoke())
          .thenAnswer(jokeQueueAnswer)
    }
    ActivityScenario.launch(MainActivity::class.java)
    
    onView(withId(R.id.buttonNewJoke))
        .perform(click())
    onView(withId(R.id.textJoke))
        .check(matches(withText(joke.joke)))
  }
}