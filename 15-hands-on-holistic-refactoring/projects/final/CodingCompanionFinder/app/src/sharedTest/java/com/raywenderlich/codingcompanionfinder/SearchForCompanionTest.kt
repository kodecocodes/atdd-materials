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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.raywenderlich.codingcompanionfinder.CommonTestDataUtil.readFile
import com.raywenderlich.codingcompanionfinder.models.AnimalResult
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.searchforcompanion.SearchForCompanionFragment
import com.raywenderlich.codingcompanionfinder.searchforcompanion.SearchForCompanionViewModel
import com.raywenderlich.codingcompanionfinder.searchforcompanion.ViewCompanionViewModel
import com.raywenderlich.codingcompanionfinder.testhooks.IdlingEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.annotation.LooperMode
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class SearchForCompanionTest : KoinTest {
  private val idlingResource = SimpleIdlingResource()

  companion object {
    val server = MockWebServer()
    val dispatcher: Dispatcher = object : Dispatcher() {
      @Throws(InterruptedException::class)
      override fun dispatch(request: RecordedRequest): MockResponse {

        return CommonTestDataUtil.dispatch(request) ?: MockResponse().setResponseCode(404)
      }
    }

    @BeforeClass
    @JvmStatic
    fun setup() {
      server.setDispatcher(dispatcher)
      server.start()
    }
  }

  private fun getMockResponseWithResults(): Response<AnimalResult> {
    val gson = Gson()
    val animalResult =
      gson.fromJson<AnimalResult>(readFile("search_30318.json"),
        AnimalResult::class.java)
    return Response.success(animalResult)
  }

  private fun getMockResponseWithNoResults(): Response<AnimalResult> {
    val gson = Gson()
    val animalResult =
      gson.fromJson<AnimalResult>("{\"animals\": []}",
        AnimalResult::class.java)
    return Response.success(animalResult)
  }
  private fun getMockResponseFailed(): Response<AnimalResult> {
    val gson = Gson()
    return Response.error(401, Mockito.mock(ResponseBody::class.java))
  }

  private fun loadKoinTestModules(serverUrl: String) {
    loadKoinModules(module(override = true) {
      single<PetFinderService> {
        val petFinderService = Mockito.mock(PetFinderService::class.java)
        Mockito.`when`(
          petFinderService.getAnimals(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.contains("30318")
          )
        ).thenReturn(GlobalScope.async { getMockResponseWithResults() })
// 1
        Mockito.`when`(
          petFinderService.getAnimals(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.contains("90210")
          )
        ).thenReturn(GlobalScope.async { getMockResponseWithNoResults() })
// 2
        Mockito.`when`(
          petFinderService.getAnimals(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.contains("dddd")
          )
        ).thenReturn(GlobalScope.async { getMockResponseFailed() })
        petFinderService
      }
      viewModel { ViewCompanionViewModel() }
      viewModel { SearchForCompanionViewModel(get()) }
    })
  }

  @Subscribe
  fun onEvent(idlingEntity: IdlingEntity) {
    idlingResource.incrementBy(idlingEntity.incrementValue)
  }

  @Before
  fun beforeTestsRun() {
    launchFragmentInContainer<SearchForCompanionFragment>(themeResId =
    R.style.AppTheme, factory = object : FragmentFactory() {
      override fun instantiate(
        classLoader: ClassLoader, className:
        String
      ): Fragment {
        stopKoin()
        GlobalScope.async {
          val serveerUrl = server.url("").toString()
          loadKoinTestModules(serveerUrl)
        }.start()
        return super.instantiate(classLoader, className)
      }
    })
    EventBus.getDefault().register(this)
    IdlingRegistry.getInstance().register(idlingResource)
  }

  @After
  fun afterTestsRun() {
    // eventbus and idling resources unregister.
    IdlingRegistry.getInstance().unregister(idlingResource)
    EventBus.getDefault().unregister(this)
    stopKoin()
  }

  @Test
  fun pressing_the_find_bottom_menu_item_takes_the_user_to_the_find_page() {
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withId(R.id.searchFieldText)).check(matches(isDisplayed()))
    onView(withId(R.id.searchFieldText)).perform(typeText("30318"))
    onView(withId(R.id.searchButton)).perform(click())
  }

  @Test
  fun searching_for_a_companion_in_90210_returns_no_results() {
    onView(withId(R.id.searchFieldText)).perform(typeText("90210"))
    onView(withId(R.id.searchButton)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withId(R.id.noResults)).check(
      matches(
        withEffectiveVisibility(
          Visibility.VISIBLE
        )
      )
    )
  }

  @Test
  fun
      searching_for_a_companion_in_a_call_returns_an_error_displays_no_results() {
    onView(withId(R.id.searchFieldText)).perform(typeText("dddd"))
    onView(withId(R.id.searchButton)).perform(click())
    onView(withId(R.id.searchButton)).check(ViewAssertions.matches(isDisplayed()))
    onView(withId(R.id.noResults))
      .check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
  }

  @Test
  fun searching_for_a_companion_in_30318_returns_two_results() {
    onView(withId(R.id.searchFieldText)).perform(typeText("30318"))
    onView(withId(R.id.searchButton)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withText("Joy")).check(matches(isDisplayed()))
    onView(withText("Male")).check(matches(isDisplayed()))
    onView(withText("Shih Tzu")).check(matches(isDisplayed()))
    onView(withText("KEVIN")).check(matches(isDisplayed()))
    onView(withText("Female")).check(matches(isDisplayed()))
    onView(withText("Domestic Short Hair")).check(matches(isDisplayed()))
  }

}
