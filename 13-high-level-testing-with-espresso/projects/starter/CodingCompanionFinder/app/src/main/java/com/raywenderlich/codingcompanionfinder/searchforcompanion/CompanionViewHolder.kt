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

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.codingcompanionfinder.GlideApp
import com.raywenderlich.codingcompanionfinder.R
import com.raywenderlich.codingcompanionfinder.models.Animal

class CompanionViewHolder(val view: View, val fragment: Fragment) : RecyclerView.ViewHolder(view) {

  fun populatePet(animal: Animal) {
    populateTextField(R.id.name, animal.name)
    populateTextField(R.id.sex, animal.gender)
    populateTextField(R.id.breed, animal.breeds.primary)
    animal.photos?.let {
      if (it.size > 0) {
        val imageView = view.findViewById<ImageView>(R.id.petImage)
        GlideApp.with(view.context).load(it[0].full)
            .fitCenter()
            .into(imageView)
      }
    }

    setupClickEvent(animal)
  }

  private fun populateTextField(id: Int, stringValue: String) {
    (view.findViewById(id) as TextView).text = stringValue
  }

  private fun setupClickEvent(animal: Animal){
    view.setOnClickListener {
      val viewCompanionFragment = ViewCompanionFragment()
      val bundle = Bundle()
      bundle.putSerializable(ViewCompanionFragment.ANIMAL, animal)
      viewCompanionFragment.arguments = bundle
      val transaction = fragment.childFragmentManager.beginTransaction()
      transaction.replace(R.id.viewCompanion, viewCompanionFragment).addToBackStack("companionView").commit()
    }
  }
}
