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

package com.raywenderlich.codingcompanionfinder.retrofit

import com.raywenderlich.codingcompanionfinder.MainActivity
import com.raywenderlich.codingcompanionfinder.models.Token
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.io.IOException

class AuthorizationInterceptor : Interceptor, KoinComponent {
  private val petFinderService: PetFinderService by inject()
  private var token = Token()
  @Throws(IOException::class)
  // 1
  override fun intercept(chain: Interceptor.Chain): Response {
    var mainResponse = chain.proceed(chain.request())
    val mainRequest = chain.request()
    // 2
    if ((mainResponse.code() == 401 || mainResponse.code() == 403) && !
      mainResponse.request().url().url().toString().contains("oauth2/token")) {
      // 3
      val tokenRequest = petFinderService.getToken(clientId =
      MainActivity.API_KEY, clientSecret = MainActivity.API_SECRET)
      val tokenResponse = tokenRequest.execute()
      if (tokenResponse.isSuccessful) {
        // 4
        tokenResponse.body()?.let {
          token = it
          // 5
          val builder =
            mainRequest.newBuilder().header("Authorization", "Bearer " +
                it.accessToken)
              .method(mainRequest.method(), mainRequest.body())
          mainResponse = chain.proceed(builder.build())
        } }
    }
    // 6
    return mainResponse
  }
}
