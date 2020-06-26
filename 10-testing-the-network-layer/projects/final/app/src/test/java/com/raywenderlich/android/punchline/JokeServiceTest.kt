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

import com.github.javafaker.Faker
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val id = "6"
private const val joke = "How does a train eat? It goes chew, chew"
private const val testJson = """{ "id": $id, "joke": "$joke" }"""

class JokeServiceTestUsingMockWebServer {

  @get:Rule
  val mockWebServer = MockWebServer()

  private val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
  }

  private val jokeService by lazy {
    retrofit.create(JokeService::class.java)
  }

  @Test
  fun getRandomJokeEmitsJoke() {
    mockWebServer.enqueue(
        MockResponse()
            .setBody(testJson)
            .setResponseCode(200))

    val testObserver = jokeService.getRandomJoke().test()
    testObserver.assertValue(Joke(id, joke))
  }

  @Test
  fun getRandomJokeGetsRandomJokeJson() {
    mockWebServer.enqueue(
        MockResponse()
            .setBody(testJson)
            .setResponseCode(200))

    val testObserver = jokeService.getRandomJoke().test()
    testObserver.assertNoErrors()
    assertEquals("/random_joke.json",
        mockWebServer.takeRequest().path)
  }
}

class JokeServiceTestMockingService {

  private val jokeService: JokeService = mock()
  private val repository = RepositoryImpl(jokeService)

  @Test
  fun getRandomJokeEmitsJoke() {
    val joke = Joke(id, joke)
    whenever(jokeService.getRandomJoke())
        .thenReturn(Single.just(joke))

    val testObserver = repository.getJoke().test()
    testObserver.assertValue(joke)
  }
}

class JokeServiceTestUsingFaker {

  var faker = Faker()
  private val jokeService: JokeService = mock()
  private val repository = RepositoryImpl(jokeService)

  @Test
  fun getRandomJokeEmitsJoke() {
    val joke = Joke(
        faker.idNumber().valid(),
        faker.lorem().sentence())
    whenever(jokeService.getRandomJoke())
        .thenReturn(Single.just(joke))

    val testObserver = repository.getJoke().test()
    testObserver.assertValue(joke)
  }
}