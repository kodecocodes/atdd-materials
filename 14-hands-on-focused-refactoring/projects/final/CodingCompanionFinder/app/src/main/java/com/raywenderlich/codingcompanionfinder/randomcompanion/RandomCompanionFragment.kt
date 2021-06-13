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

package com.raywenderlich.codingcompanionfinder.randomcompanion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.raywenderlich.codingcompanionfinder.GlideApp
import com.raywenderlich.codingcompanionfinder.MainActivity

import com.raywenderlich.codingcompanionfinder.R
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.synnapps.carouselview.CarouselView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RandomCompanionFragment : Fragment() {

  lateinit var animal: Animal
  lateinit var key: String
  lateinit var petPhotos: ArrayList<String>
  lateinit var petCaroselView: CarouselView
  lateinit var randomCompanionFragment: RandomCompanionFragment

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    randomCompanionFragment = this
    return inflater.inflate(R.layout.fragment_random_companion, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    GlobalScope.launch {
      key = (activity as MainActivity).accessToken
      (activity as MainActivity).petFinderService?.let { petFinderService ->

        val animalsResponse = petFinderService.getAnimals((activity as MainActivity).accessToken, 1)
        if (animalsResponse.isSuccessful) {
          animalsResponse.body()?.let {animalResult ->
            if (animalResult.animals.size > 0) {
                animalResult.animals.first().let {
                  animal = it
                  GlobalScope.launch(Dispatchers.Main) {
                    populatePet()
                  }
                }

            }
          }
        }
      }
    }
    super.onActivityCreated(savedInstanceState)
  }

  private fun populatePet() {
    populateTextField(R.id.petName, animal.name)
    populateTextField(
        R.id.city,
        animal.contact.address.city + ", " + animal.contact.address.state
    )
    populateTextField(R.id.age, animal.age)
    populateTextField(R.id.sex, animal.gender)
    populateTextField(R.id.size, animal.size)
    populateTextField(R.id.meetTitlePlaceholder, "Meet " + animal.name)
    populateTextField(R.id.description, animal.description)
    populatePhotos()
    populateTextField(R.id.breed, animal.breeds.primary)
  }

  private fun populatePhotos() {
    petPhotos = ArrayList()
    animal.photos.forEach { photo ->
      if (photo.full != null)
        petPhotos.add(photo.full)
    }

    view?.let {
      petCaroselView = it.findViewById(R.id.petCarouselView)
      petCaroselView.setViewListener { position ->
        val carouselItemView = layoutInflater.inflate(R.layout.companion_photo_layout, null)
        val imageView = carouselItemView.findViewById<ImageView>(R.id.petImage)
        GlideApp.with(randomCompanionFragment).load(petPhotos[position])
          .fitCenter()
          .into(imageView)
        carouselItemView
      }
      petCaroselView.pageCount = petPhotos.size
    }
  }

  private fun populateTextField(id: Int, stringValue: String) {
    view?.let {
      (it.findViewById(id) as TextView).text = stringValue
    }
  }
}
