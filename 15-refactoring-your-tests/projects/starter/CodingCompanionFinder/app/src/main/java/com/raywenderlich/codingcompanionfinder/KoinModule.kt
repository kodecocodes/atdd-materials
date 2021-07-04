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

import com.raywenderlich.codingcompanionfinder.retrofit.AuthorizationInterceptor
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.searchforcompanion.SearchForCompanionViewModel
import com.raywenderlich.codingcompanionfinder.searchforcompanion.ViewCompanionViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val PETFINDER_URL = "PETFINDER_URL"
val urlsModule = module {
  single(named(PETFINDER_URL)){MainActivity.DEFAULT_PETFINDER_URL}
}
val appModule = module {
  single<PetFinderService> {
    val logger = HttpLoggingInterceptor()
    val client = OkHttpClient.Builder()
      .addInterceptor(logger)
      .connectTimeout(60L, TimeUnit.SECONDS)
      .readTimeout(60L, TimeUnit.SECONDS)
      .addInterceptor(AuthorizationInterceptor())
      .build()
    Retrofit.Builder()
      .baseUrl(get<String>(named(PETFINDER_URL)))
      .addConverterFactory(GsonConverterFactory.create())
      .client(client)
      .build().create(PetFinderService::class.java)
  }
  viewModel { ViewCompanionViewModel() }
  viewModel { SearchForCompanionViewModel(get()) }
}