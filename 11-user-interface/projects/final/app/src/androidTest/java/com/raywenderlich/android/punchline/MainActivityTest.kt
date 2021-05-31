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