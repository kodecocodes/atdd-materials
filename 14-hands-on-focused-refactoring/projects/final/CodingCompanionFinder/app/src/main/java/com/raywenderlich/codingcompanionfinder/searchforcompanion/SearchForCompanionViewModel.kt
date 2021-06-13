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
package com.raywenderlich.codingcompanionfinder.searchforcompanion

import android.view.View
import android.view.View.INVISIBLE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.raywenderlich.codingcompanionfinder.retrofit.PetFinderService
import com.raywenderlich.codingcompanionfinder.testhooks.IdlingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class SearchForCompanionViewModel(val petFinderService: PetFinderService): ViewModel() {
  val noResultsViewVisiblity : MutableLiveData<Int> = MutableLiveData<Int>()
  val companionLocation : MutableLiveData<String> = MutableLiveData()

  // 1
  val animals: MutableLiveData<ArrayList<Animal>> = MutableLiveData<ArrayList<Animal>>()
  lateinit var accessToken: String

  fun searchForCompanions() {

    GlobalScope.launch {

      EventBus.getDefault().post(IdlingEntity(1))
// 2
      val getAnimalsRequest = petFinderService.getAnimals(
          accessToken,
          location = companionLocation.value
      )

      val searchForPetResponse = getAnimalsRequest.await()

      GlobalScope.launch(Dispatchers.Main) {
        if (searchForPetResponse.isSuccessful) {
          searchForPetResponse.body()?.let {
            // 3
            animals.postValue(it.animals)
            if (it.animals.size > 0) {
// 3
              noResultsViewVisiblity.postValue(INVISIBLE)
            } else {
// 3
              noResultsViewVisiblity.postValue(View.VISIBLE)
            }
          }
        } else {
// 3
          noResultsViewVisiblity.postValue(View.VISIBLE)
        }
      }
      EventBus.getDefault().post(IdlingEntity(-1))
    }
  }

}
