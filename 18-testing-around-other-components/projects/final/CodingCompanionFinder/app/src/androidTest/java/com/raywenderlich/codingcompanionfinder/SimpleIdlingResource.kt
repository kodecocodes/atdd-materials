package com.raywenderlich.codingcompanionfinder

import androidx.annotation.Nullable
import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

class SimpleIdlingResource : IdlingResource {

  // 1
  @Nullable
  @Volatile
  private var callback: IdlingResource.ResourceCallback? = null

  // 2
  // Idleness is controlled with this boolean.
  var activeResources = AtomicInteger(0)

  override fun getName(): String {
    return this.javaClass.name
  }

  // 3
  override fun isIdleNow(): Boolean {
    return activeResources.toInt() < 1
  }

  override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
    this.callback = callback
  }

  // 4
  fun incrementBy(incrementValue: Int) {
    if (activeResources.addAndGet(incrementValue) < 1 && callback != null) {
      callback!!.onTransitionToIdle()
    }
  }
}