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
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class AuthorizationInterceptor(private val mainActivity: MainActivity) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    var mainResponse = chain.proceed(chain.request())
    val mainRequest = chain.request()

    // if response code is 401 or 403, 'mainRequest' has encountered authentication error
    if ((mainResponse.code() == 401 || mainResponse.code() == 403) && !mainResponse.request().url().url().toString().contains("oauth2/token")) {
      // request to login API to get fresh token
      // synchronously calling login API
      mainActivity.petFinderService?.let { petFinderService ->
        val tokenRequest = petFinderService.getToken(clientId = mainActivity.apiKey, clientSecret = mainActivity.apiSecret)
        val tokenResponse = tokenRequest.execute()

        if (tokenResponse.isSuccessful()) {
          // login request succeeded, new token generated
          // save the new token
          // retry the 'mainRequest' which encountered an authentication error
          // add new token into 'mainRequest' header and request again
          tokenResponse.body()?.let {
            mainActivity.token = it
            val builder = mainRequest.newBuilder().header("Authorization", "Bearer " + it.accessToken)
                .method(mainRequest.method(), mainRequest.body())
            mainResponse.close()
            mainResponse = chain.proceed(builder.build())
          }
        }
      }
    }

    return mainResponse
  }

}
