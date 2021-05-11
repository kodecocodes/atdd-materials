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

package com.raywenderlich.android.cocktails.game.factory

import com.raywenderlich.android.cocktails.common.network.Cocktail
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.common.repository.RepositoryCallback
import com.raywenderlich.android.cocktails.game.factory.CocktailsGameFactory.Callback
import com.raywenderlich.android.cocktails.game.model.Game
import com.raywenderlich.android.cocktails.game.model.Question
import com.raywenderlich.android.cocktails.game.model.Score

class CocktailsGameFactoryImpl(
    private val repository: CocktailsRepository)
  : CocktailsGameFactory {

  override fun buildGame(callback: Callback) {
    repository.getAlcoholic(
        object : RepositoryCallback<List<Cocktail>, String> {
          override fun onSuccess(cocktailList: List<Cocktail>) {
            val questions = buildQuestions(cocktailList)
            val score = Score(repository.getHighScore())
            val game = Game(questions, score)
            callback.onSuccess(game)
          }

          override fun onError(e: String) {
            callback.onError()
          }
        })
  }

  private fun buildQuestions(cocktailList: List<Cocktail>) = cocktailList.map { cocktail ->
    val otherCocktail = cocktailList.shuffled().first { it != cocktail }
    Question(cocktail.strDrink, otherCocktail.strDrink, cocktail.strDrinkThumb)
  }
}