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

package com.raywenderlich.android.wishlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.raywenderlich.android.wishlist.persistance.RepositoryImpl
import com.raywenderlich.android.wishlist.persistance.WishlistDao
import com.raywenderlich.android.wishlist.persistance.WishlistDaoImpl
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DetailViewModelTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  private val wishlistDao: WishlistDao =
      Mockito.spy(WishlistDaoImpl())
  private val viewModel =
      DetailViewModel(RepositoryImpl(wishlistDao))

  @Test
  fun saveNewItemCallsDatabase() {
    viewModel.saveNewItem(Wishlist("Victoria",
        listOf("RW Android Apprentice Book", "Android phone"), 1),
        "Smart watch")

    verify(wishlistDao).save(any())
  }

  @Test
  fun saveNewItemSavesData() {
    val wishlist = Wishlist("Victoria",
        listOf("RW Android Apprentice Book", "Android phone"), 1)
    val name = "Smart watch"
    viewModel.saveNewItem(wishlist, name)

    val mockObserver = mock<Observer<Wishlist>>()
    wishlistDao.findById(wishlist.id)
        .observeForever(mockObserver)
    verify(mockObserver).onChanged(
        wishlist.copy(wishes = wishlist.wishes + name))
  }

  @Test
  fun getWishListCallsDatabase() {
    viewModel.getWishlist(1)

    verify(wishlistDao).findById(any())
  }

  @Test
  fun getWishListReturnsCorrectData() {
    val wishlist = Wishlist("Victoria",
        listOf("RW Android Apprentice Book", "Android phone"), 1)

    wishlistDao.save(wishlist)

    val mockObserver = mock<Observer<Wishlist>>()
    viewModel.getWishlist(1).observeForever(mockObserver)
    verify(mockObserver).onChanged(wishlist)
  }
}
