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

import com.raywenderlich.codingcompanionfinder.models.Address
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.raywenderlich.codingcompanionfinder.models.Breeds
import com.raywenderlich.codingcompanionfinder.models.Contact
import com.raywenderlich.codingcompanionfinder.searchforcompanion.ViewCompanionViewModel
import org.junit.Test

import org.junit.Assert.*

class ViewCompanionViewModelTest {
  val animal = Animal(
    22,
    Contact(
      phone = "404-867-5309",
      email = "coding.companion@razware.com",
      address = Address(
        "",
        "",
        "Atlanta",
        "GA",
        "30303",
        "USA"
      ) ),
    "5",
    "small",
    arrayListOf(),
    Breeds("shih tzu", "", false, false),
    "Spike",
    "male",
    "A sweet little guy with spikey teeth!"
  )
  @Test
  fun populateFromAnimal_sets_the_animals_name_to_the_view_model(){
    val viewCompanionViewModel = ViewCompanionViewModel()
    viewCompanionViewModel.populateFromAnimal(animal)
    assert(viewCompanionViewModel.name.equals("Spike"))
  }
}
