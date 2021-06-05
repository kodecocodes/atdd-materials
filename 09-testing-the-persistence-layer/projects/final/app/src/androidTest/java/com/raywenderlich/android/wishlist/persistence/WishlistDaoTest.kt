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

package com.raywenderlich.android.wishlist.persistence

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.raywenderlich.android.wishlist.Wishlist
import com.raywenderlich.android.wishlist.persistance.WishlistDao
import com.raywenderlich.android.wishlist.persistance.WishlistDatabase
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class WishlistDaoTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var wishlistDatabase: WishlistDatabase
  private lateinit var wishlistDao: WishlistDao

  @Before
  fun initDb() {
    wishlistDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        WishlistDatabase::class.java).build()

    wishlistDao = wishlistDatabase.wishlistDao()
  }

  @After
  fun closeDb() {
    wishlistDatabase.close()
  }

  @Test
  fun getAllReturnsEmptyList() {
    val testObserver: Observer<List<Wishlist>> = mock()
    wishlistDao.getAll().observeForever(testObserver)
    verify(testObserver).onChanged(emptyList())
  }

  @Test
  fun saveWishlistsSavesData() {
    val wishlist1 = WishlistFactory.makeWishlist()
    val wishlist2 = WishlistFactory.makeWishlist()
    wishlistDao.save(wishlist1, wishlist2)

    val testObserver: Observer<List<Wishlist>> = mock()
    wishlistDao.getAll().observeForever(testObserver)

    val listClass =
        ArrayList::class.java as Class<ArrayList<Wishlist>>
    val argumentCaptor = ArgumentCaptor.forClass(listClass)
    verify(testObserver).onChanged(argumentCaptor.capture())
    assertTrue(argumentCaptor.value.size > 0)
  }

  @Test
  fun getAllRetrievesData() {
    val wishlist1 = WishlistFactory.makeWishlist()
    val wishlist2 = WishlistFactory.makeWishlist()
    wishlistDao.save(wishlist1, wishlist2)

    val testObserver: Observer<List<Wishlist>> = mock()
    wishlistDao.getAll().observeForever(testObserver)

    val listClass =
        ArrayList::class.java as Class<ArrayList<Wishlist>>
    val argumentCaptor = ArgumentCaptor.forClass(listClass)
    verify(testObserver).onChanged(argumentCaptor.capture())
    val capturedArgument = argumentCaptor.value
    assertTrue(capturedArgument
        .containsAll(listOf(wishlist1, wishlist2)))
  }

  @Test
  fun findByIdRetrievesCorrectData() {
    val wishlist1 = WishlistFactory.makeWishlist()
    val wishlist2 = WishlistFactory.makeWishlist()
    wishlistDao.save(wishlist1, wishlist2)

    val testObserver: Observer<Wishlist> = mock()
    wishlistDao.findById(wishlist2.id).observeForever(testObserver)
    verify(testObserver).onChanged(wishlist2)
  }
}
