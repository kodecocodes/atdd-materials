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

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.raywenderlich.codingcompanionfinder.retrofit.AuthorizationInterceptor
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.searchforcompanion.SearchForCompanionViewModel
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
import java.util.concurrent.TimeUnit
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import java.util.concurrent.CountDownLatch

class SearchForCompanionViewModelTest {

  private val mainThreadSurrogate = newSingleThreadContext("Mocked UI thread")

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  // 1
  val server = MockWebServer()

  lateinit var petFinderService: PetFinderService
  // 2
  val dispatcher: Dispatcher = object : Dispatcher() {
    @Throws(InterruptedException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
      return CommonTestDataUtil.dispatch(request) ?: MockResponse().setResponseCode(
        404
      )
    }
  }

  // 3
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
      .addInterceptor(AuthorizationInterceptor())
      .build()
    petFinderService = Retrofit.Builder()
      .baseUrl(server.url("").toString())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .client(client)
      .build().create(PetFinderService::class.java)
  }

  // 4
  @Test
  fun `call to searchForCompanions gets results`() {
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

  fun
      callSearchForCompanionWithALocationAndWaitForVisibilityResult(location:
                                                                    String): SearchForCompanionViewModel{
    val searchForCompanionViewModel =
      SearchForCompanionViewModel(petFinderService)
    searchForCompanionViewModel.companionLocation.value = location
    val countDownLatch = CountDownLatch(1)
    searchForCompanionViewModel.searchForCompanions()
    searchForCompanionViewModel.noResultsViewVisiblity.observeForever {
      countDownLatch.countDown()
    }
    countDownLatch.await(2, TimeUnit.SECONDS)
    return searchForCompanionViewModel
  }
  @Test
  fun `call to searchForCompanions with results sets the visibility of no results to INVISIBLE`() {
    val searchForCompanionViewModel =
      callSearchForCompanionWithALocationAndWaitForVisibilityResult("30318")
    Assert.assertEquals(INVISIBLE,
      searchForCompanionViewModel.noResultsViewVisiblity.value)
  }
  @Test
  fun `call to searchForCompanions with no results sets the visibility of no results to VISIBLE`() {
    val searchForCompanionViewModel =
      callSearchForCompanionWithALocationAndWaitForVisibilityResult("90210")
    Assert.assertEquals(VISIBLE,
      searchForCompanionViewModel.noResultsViewVisiblity.value)
  }

}
