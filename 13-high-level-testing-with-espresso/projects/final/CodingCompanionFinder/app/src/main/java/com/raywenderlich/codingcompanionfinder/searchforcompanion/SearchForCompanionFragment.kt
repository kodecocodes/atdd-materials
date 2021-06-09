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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.raywenderlich.codingcompanionfinder.MainActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.raywenderlich.codingcompanionfinder.R
import com.raywenderlich.codingcompanionfinder.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus

class SearchForCompanionFragment : Fragment() {

  private lateinit var accessToken: String

  private var petRecyclerView: RecyclerView? = null

  private lateinit var companionAdapter: CompanionAdapter

  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_search_for_companion, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    view?.findViewById<MaterialButton>(R.id.searchButton)?.setOnClickListener {
      try {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.hideSoftInputFromWindow(activity?.getCurrentFocus()?.getWindowToken(), 0)
      } catch (e: Exception) {
        // only happens when the keyboard is already closed
      }
      searchForCompanions()
    }

    super.onActivityCreated(savedInstanceState)
  }



  private fun searchForCompanions() {
    val companionLocation = view?.findViewById<TextInputEditText>(R.id.searchFieldText)?.text.toString()
    val noResultsTextView = view?.findViewById<TextView>(R.id.noResults)
    val searchForCompanionFragment = this

    GlobalScope.launch {
      accessToken = (activity as MainActivity).accessToken
      (activity as MainActivity).petFinderService?.let { petFinderService ->
        // increment the IdlingResources
        EventBus.getDefault().post(IdlingEntity(1))
        val getAnimalsRequest = petFinderService.getAnimals(accessToken, location = companionLocation)

        val searchForPetResponse = getAnimalsRequest.await()

        if (searchForPetResponse.isSuccessful) {
          searchForPetResponse.body()?.let {
            GlobalScope.launch(Dispatchers.Main) {
              if (it.animals.size > 0) {
                noResultsTextView?.visibility = INVISIBLE
                viewManager = LinearLayoutManager(context)
                companionAdapter = CompanionAdapter(it.animals, searchForCompanionFragment)
                petRecyclerView = view?.let {
                  it.findViewById<RecyclerView>(R.id.petRecyclerView).apply {
                    layoutManager = viewManager
                    adapter = companionAdapter
                  }
                }
              } else {
                noResultsTextView?.visibility = VISIBLE
              }
            }
          }
        } else {
          noResultsTextView?.visibility = VISIBLE
        }
        // Decrement the idling resources.
        EventBus.getDefault().post(IdlingEntity(-1))
      }
    }
  }

}
