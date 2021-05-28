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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.wishlist.databinding.ItemWishlistBinding


class WishlistAdapter(
    lifecycleOwner: LifecycleOwner,
    private val wishlist: LiveData<List<Wishlist>>,
    private val onItemSelected: (Wishlist) -> Unit
) : RecyclerView.Adapter<WishListViewHolder>() {

  init {
    wishlist.observe(lifecycleOwner, { notifyDataSetChanged() })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
    return WishListViewHolder(ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemSelected)
  }

  override fun getItemCount(): Int {
    return wishlist.value?.size ?: 0
  }

  override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
    wishlist.value?.get(position)?.let { holder.bind(it) }
  }
}

class WishListViewHolder(private val binding: ItemWishlistBinding, val onItemSelected: (Wishlist) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

  private val wishItemAdapter = WishItemAdapter()

  init {
    binding.recyclerWishes.layoutManager = LinearLayoutManager(binding.root.context)
    binding.recyclerWishes.adapter = wishItemAdapter
  }

  fun bind(wishlist: Wishlist) {
    binding.title.text = wishlist.receiver
    binding.root.setOnClickListener {
      onItemSelected(wishlist)
    }
    wishItemAdapter.items.clear()
    wishItemAdapter.items.addAll(wishlist.wishes)
    wishItemAdapter.notifyDataSetChanged()
  }
}

class WishItemAdapter : RecyclerView.Adapter<WishViewHolder>() {

  val items: MutableList<String> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishViewHolder {
    return WishViewHolder(TextView(parent.context))
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
    holder.bind(items[position])
  }

}

class WishViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
  fun bind(wish: String) {
    view.text = wish
  }
}

