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

package com.raywenderlich.android.wishlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.raywenderlich.android.wishlist.persistance.Repository
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

  @get:Rule
  @Suppress("unused")
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  private val mockRepository: Repository = mock()

  private val viewModel = MainViewModel(mockRepository)

  @Test
  fun saveNewListCallsRepository() {
    viewModel.saveNewList("New list")

    verify(mockRepository).saveWishlist(any())
  }

  @Test
  fun saveNewListCallsRepositoryCorrectWithData() {
    val name = "New list"
    viewModel.saveNewList(name)

    verify(mockRepository).saveWishlist(Wishlist(name, listOf()))
  }

  @Test
  fun getWishlistsCallsRepository() {
    viewModel.getWishlists()

    verify(mockRepository).getWishlists()
  }

  @Test
  fun getWishListReturnsReturnsData() {
    val wishes = listOf(Wishlist("Victoria", listOf("RW Book")))
    whenever(mockRepository.getWishlists())
        .thenReturn(MutableLiveData<List<Wishlist>>().apply { postValue(wishes) })

    val mockObserver = mock<Observer<List<Wishlist>>>()
    viewModel.getWishlists().observeForever(mockObserver)

    verify(mockObserver).onChanged(wishes)
  }
}