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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.raywenderlich.codingcompanionfinder.retrofit.AuthorizationInterceptor
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.testhooks.IdlingEntity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

  var petFinderService: PetFinderService? = null

  var accessToken = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (petFinderService == null) {
      val logger = HttpLoggingInterceptor()
      logger.level = HttpLoggingInterceptor.Level.BODY
      val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(AuthorizationInterceptor())
        .build()

      val baseUrl = intent.getStringExtra(PETFINDER_URI) ?: "https://api.petfinder.com/v2/"

      petFinderService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build().create(PetFinderService::class.java)
    }

  }

  // 1
  @Subscribe
  fun onOnEvent(idlingEntity: IdlingEntity) {
    // noop
  }

  // 2
  override fun onStart() {
    super.onStart()
    EventBus.getDefault().register(this)
  }

  // 3
  override fun onStop() {
    super.onStop()
    EventBus.getDefault().unregister(this)
  }

  override fun onResume() {
    super.onResume()

    val navHostController = Navigation.findNavController(this, R.id.mainPetfinderFragment)

    val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

    NavigationUI.setupWithNavController(bottomNavigation, navHostController)
  }

  companion object {
    val PETFINDER_URI = "petfinder_uri"
    val PETFINDER_KEY = "petfinder_key"
    val API_KEY = "your client id"
    val API_SECRET = "your client secret"
    val DEFAULT_PETFINDER_URL = "https://api.petfinder.com/v2/"
  }
}
