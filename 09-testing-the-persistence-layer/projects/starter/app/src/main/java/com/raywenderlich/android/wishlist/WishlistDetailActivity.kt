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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_wishlist_detail.*
import kotlinx.android.synthetic.main.view_input_bottom_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WishlistDetailActivity : AppCompatActivity() {

  companion object {
    private const val EXTRA_WISHLIST = "EXTRA_WISHLIST"

    fun newIntent(wishlist: Wishlist, context: Context): Intent {
      return Intent(context, WishlistDetailActivity::class.java).apply {
        putExtra(EXTRA_WISHLIST, wishlist.id)
      }
    }
  }

  private val viewModel: DetailViewModel by viewModel()
  private val wishlistAdapter: WishItemAdapter = WishItemAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_wishlist_detail)
    recyclerWishes.layoutManager = LinearLayoutManager(this)
    recyclerWishes.adapter = wishlistAdapter
    viewModel.getWishlist(intent.getIntExtra(EXTRA_WISHLIST, 0)).observe(this, {
      render(it)
    })
  }

  private fun render(wishlist: Wishlist) {
    textViewTitle.text = wishlist.receiver
    wishlistAdapter.items.clear()
    wishlistAdapter.items.addAll(wishlist.wishes)
    wishlistAdapter.notifyDataSetChanged()

    buttonAddList.setOnClickListener { showAddListInput(wishlist) }
  }

  private fun showAddListInput(wishlist: Wishlist) {
    BottomSheetDialog(this).apply {
      val view = layoutInflater.inflate(R.layout.view_input_bottom_sheet, null)
      view.title.text = getString(R.string.title_add_wish)
      view.buttonSave.setOnClickListener {
        viewModel.saveNewItem(wishlist, view.editTextInput.text.toString())
        this.dismiss()
      }
      setContentView(view)
      show()
    }
  }
}
