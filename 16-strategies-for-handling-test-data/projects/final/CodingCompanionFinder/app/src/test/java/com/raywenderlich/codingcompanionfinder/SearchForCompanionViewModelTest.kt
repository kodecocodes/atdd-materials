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
package com.raywenderlich.codingcompanionfinder

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raywenderlich.codingcompanionfinder.retrofit.AuthorizationInterceptor
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.searchforcompanion.SearchForCompanionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SearchForCompanionViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val mainThreadSurrogate =
    newSingleThreadContext("Mocked UI thread")

  val server = MockWebServer()

  lateinit var petFinderService: PetFinderService

  val dispatcher: Dispatcher = object : Dispatcher() {
    @Throws(InterruptedException::class)
    override fun dispatch(
      request: RecordedRequest
    ): MockResponse {
      return CommonTestDataUtil.nonInterceptedDispatch(request) ?:
      MockResponse().setResponseCode(404)
    }
  }

  @Before
  fun setup() {
    Dispatchers.setMain(mainThreadSurrogate)
    server.setDispatcher(dispatcher)
    server.start()
    val logger = HttpLoggingInterceptor()
    val client = OkHttpClient.Builder()
      .addInterceptor(logger)
      .connectTimeout(60L, TimeUnit.SECONDS)
      .readTimeout(60L, TimeUnit.SECONDS)
      .build()
    petFinderService = Retrofit.Builder()
      .baseUrl(server.url("").toString())
      .addConverterFactory(GsonConverterFactory.create())
      .client(client)
      .build().create(PetFinderService::class.java)
  }

  // 4
  @Test
  fun call_to_searchForCompanions_gets_results() {
    val searchForCompanionViewModel =
      SearchForCompanionViewModel(petFinderService)
    searchForCompanionViewModel.companionLocation.value = "30318"
// 1
    val countDownLatch = CountDownLatch(1)
    searchForCompanionViewModel.searchForCompanions()
// 2
    searchForCompanionViewModel.animals.observeForever {
      countDownLatch.countDown()
    }
// 3
    countDownLatch.await(2, TimeUnit.SECONDS)
    Assert.assertEquals(2,
      searchForCompanionViewModel.animals.value!!.size)
  }

  fun callSearchForCompanionWithALocationAndWaitForVisibilityResult(location: String): SearchForCompanionViewModel{
    val searchForCompanionViewModel =
      SearchForCompanionViewModel(petFinderService)
    searchForCompanionViewModel.companionLocation.value = location
    val countDownLatch = CountDownLatch(1)
    searchForCompanionViewModel.searchForCompanions()
    searchForCompanionViewModel.noResultsViewVisiblity
      .observeForever {
        countDownLatch.countDown()
      }

    countDownLatch.await(2, TimeUnit.SECONDS)
    return searchForCompanionViewModel
  }

  @Test
  fun call_to_searchForCompanions_with_results_sets_the_visibility_of_no_results_to_INVISIBLE() {
    val searchForCompanionViewModel = callSearchForCompanionWithALocationAndWaitForVisibilityResult("30318")
    Assert.assertEquals(INVISIBLE,
      searchForCompanionViewModel.noResultsViewVisiblity.value)
  }

  @Test
  fun call_to_searchForCompanions_with_no_results_sets_the_visibility_of_no_results_to_VISIBLE() {
    val searchForCompanionViewModel = callSearchForCompanionWithALocationAndWaitForVisibilityResult("90210")
    Assert.assertEquals(VISIBLE,
      searchForCompanionViewModel.noResultsViewVisiblity.value)
  }
}