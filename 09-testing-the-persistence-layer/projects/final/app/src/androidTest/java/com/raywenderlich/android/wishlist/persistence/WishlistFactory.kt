package com.raywenderlich.android.wishlist.persistence

import com.raywenderlich.android.wishlist.Wishlist
import java.util.*
import java.util.concurrent.ThreadLocalRandom

object WishlistFactory {

  private fun makeRandomString() = UUID.randomUUID().toString()

  private fun makeRandomInt() =
      ThreadLocalRandom.current().nextInt(0, 1000 + 1)

  fun makeWishlist(): Wishlist {
    return Wishlist(
        makeRandomString(),
        listOf(makeRandomString(), makeRandomString()),
        makeRandomInt())
  }
}