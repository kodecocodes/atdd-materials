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

package com.raywenderlich.android.cocktails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import android.view.View
//import com.bumptech.glide.Glide
//import com.raywenderlich.android.cocktails.game.model.Question
//import com.raywenderlich.android.cocktails.game.model.Score
//import com.raywenderlich.android.cocktails.game.viewmodel.CocktailsGameViewModel
//import com.raywenderlich.android.cocktails.game.viewmodel.CocktailsGameViewModelFactory
//import kotlinx.android.synthetic.main.activity_game.*
//import kotlinx.android.synthetic.main.view_error.*
//import kotlinx.android.synthetic.main.view_loading.*

class CocktailsGameActivity : AppCompatActivity() {

//  private lateinit var viewModel: CocktailsGameViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_game)
//
//    val repository = (application as CocktailsApplication).repository
//    val factory = (application as CocktailsApplication).gameFactory
//    viewModel = ViewModelProviders.of(this, CocktailsGameViewModelFactory(repository, factory))
//        .get(CocktailsGameViewModel::class.java)
//
//    viewModel.getLoading().observe(this, Observer {
//      it?.let { loading -> showLoading(loading) }
//    })
//    viewModel.getError().observe(this, Observer {
//      it?.let { error -> showError(error) }
//    })
//    viewModel.getQuestion().observe(this, Observer {
//      showQuestion(it)
//    })
//    viewModel.getScore().observe(this, Observer {
//      it?.let { score -> showScore(score) }
//    })
//
//    nextButton.setOnClickListener { viewModel.nextQuestion() }
//
//    viewModel.initGame()
  }
//
//  private fun showScore(score: Score) {
//    scoreTextView.text = getString(R.string.game_score, score.current)
//    highScoreTextView.text = getString(R.string.game_highscore, score.highest)
//  }
//
//  private fun showQuestion(question: Question?) {
//    if (question != null) {
//      mainGroup.visibility = View.VISIBLE
//
//      if (question.answeredOption != null) {
//        showAnsweredQuestion(question)
//      } else {
//        showUnansweredQuestion(question)
//      }
//    } else {
//      mainGroup.visibility = View.GONE
//      noResultsTextView.visibility = View.VISIBLE
//      questionResultImageView.visibility = View.GONE
//    }
//  }
//
//  private fun showUnansweredQuestion(question: Question) {
//    val options = question.getOptions()
//    firstOptionButton.text = options[0]
//    firstOptionButton.setOnClickListener { viewModel.answerQuestion(question, firstOptionButton.text.toString()) }
//    firstOptionButton.isEnabled = true
//    secondOptionButton.text = options[1]
//    secondOptionButton.setOnClickListener { viewModel.answerQuestion(question, secondOptionButton.text.toString()) }
//    secondOptionButton.isEnabled = true
//
//    Glide.with(cocktailImageView).load(question.imageUrl).into(cocktailImageView)
//    questionResultImageView.visibility = View.GONE
//  }
//
//  private fun showAnsweredQuestion(question: Question) {
//    firstOptionButton.setOnClickListener(null)
//    firstOptionButton.isEnabled = false
//    secondOptionButton.setOnClickListener(null)
//    secondOptionButton.isEnabled = false
//    if (question.isAnsweredCorrectly) {
//      questionResultImageView.setImageResource(R.drawable.ic_check_24dp)
//    } else {
//      questionResultImageView.setImageResource(R.drawable.ic_error_24dp)
//    }
//    questionResultImageView.visibility = View.VISIBLE
//  }
//
//  private fun showError(show: Boolean) {
//    if (show) {
//      errorContainer.visibility = View.VISIBLE
//      retryButton.setOnClickListener { viewModel.initGame() }
//    } else {
//      errorContainer.visibility = View.GONE
//    }
//  }
//
//  private fun showLoading(show: Boolean) {
//    loadingContainer.visibility = if (show) View.VISIBLE else View.GONE
//    mainGroup.visibility = View.GONE
//    noResultsTextView.visibility = View.GONE
//    questionResultImageView.visibility = View.GONE
//  }
}
