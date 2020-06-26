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

package com.raywenderlich.codingcompanionfinder.searchforcompanion


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.raywenderlich.codingcompanionfinder.GlideApp

import com.raywenderlich.codingcompanionfinder.R
import com.raywenderlich.codingcompanionfinder.databinding.FragmentViewCompanionBinding
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest


class ViewCompanionFragment : Fragment() {
  companion object {
    val ANIMAL: String = "ANIMAL"
  }

  private lateinit var animal: Animal
  private lateinit var petPhotos: ArrayList<String>
  private lateinit var petCaroselView: CarouselView
  private lateinit var viewCompanionFragment: ViewCompanionFragment
  private lateinit var fragmentViewCompanionBinding: FragmentViewCompanionBinding
  private lateinit var viewCompanionViewModel: ViewCompanionViewModel

  val args: ViewCompanionFragmentArgs by navArgs()
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    animal = args.animal
    viewCompanionFragment = this
    // 1
    fragmentViewCompanionBinding =
      FragmentViewCompanionBinding.inflate(inflater, container, false)
    // 2
    viewCompanionViewModel = ViewModelProviders.of(this).get(ViewCompanionViewModel::class.java)
// 3
    viewCompanionViewModel.populateFromAnimal(animal)
// 4
    fragmentViewCompanionBinding.viewCompanionViewModel =
      viewCompanionViewModel
// 5

    setupPhonNumberOnClick()
    return fragmentViewCompanionBinding.root
  }

  override fun onResume() {
    populatePhotos()
    super.onResume()
  }

  private fun populatePhotos() {
    petPhotos = ArrayList()
    animal.photos.forEach { photo ->
      petPhotos.add(photo.full)
    }

    view?.let {
      petCaroselView = it.findViewById(R.id.petCarouselView)
      petCaroselView.setViewListener(object : ViewListener {

        override fun setViewForPosition(position: Int): View {
          val carouselItemView = layoutInflater.inflate(R.layout.companion_photo_layout, null)
          val imageView = carouselItemView.findViewById<ImageView>(R.id.petImage)
          GlideApp.with(viewCompanionFragment).load(petPhotos[position])
            .fitCenter()
            .into(imageView)
          return carouselItemView
        }
      })
      petCaroselView.pageCount = petPhotos.size
    }
  }

  private fun setupPhonNumberOnClick(){
    fragmentViewCompanionBinding.telephone.setOnClickListener {
      Dexter.withActivity(activity)
        .withPermission(Manifest.permission.CALL_PHONE)
        .withListener(
          object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {/* ... */
              val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + viewCompanionViewModel.telephone))
              startActivity(intent)
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {/* ... */
            }

            override fun onPermissionRationaleShouldBeShown(
              permission: PermissionRequest,
              token: PermissionToken
            ) {/* ... */
              token.continuePermissionRequest()
            }

          })
        .onSameThread()
        .check()

    }
  }
}