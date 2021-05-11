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

package com.raywenderlich.android.cocktails.common.repository

import com.raywenderlich.android.cocktails.common.network.Cocktail
import com.raywenderlich.android.cocktails.common.network.CocktailsApi
import com.raywenderlich.android.cocktails.common.network.CocktailsContainer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CocktailsRepositoryImpl(private val api: CocktailsApi) : CocktailsRepository {

  private var getAlcoholicCall: Call<CocktailsContainer>? = null

  override fun getAlcoholic(callback: RepositoryCallback<List<Cocktail>, String>) {
    getAlcoholicCall?.cancel()
    getAlcoholicCall = api.getAlcoholic()
    getAlcoholicCall?.enqueue(wrapCallback(callback))
  }

  private fun wrapCallback(callback: RepositoryCallback<List<Cocktail>, String>) =
      object : Callback<CocktailsContainer> {
        override fun onResponse(call: Call<CocktailsContainer>?,
                                response: Response<CocktailsContainer>?) {
          if (response != null && response.isSuccessful) {
            val cocktailsContainer = response.body()
            if (cocktailsContainer != null) {
              callback.onSuccess(cocktailsContainer.drinks ?: emptyList())
              return
            }
          }
          callback.onError("Couldn't get cocktails")
        }

        override fun onFailure(call: Call<CocktailsContainer>?, t: Throwable?) {
          callback.onError("Couldn't get cocktails")
        }
      }
}