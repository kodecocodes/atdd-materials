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
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.raywenderlich.android.cocktails.databinding.ActivityGameBinding
import com.raywenderlich.android.cocktails.game.model.Question
import com.raywenderlich.android.cocktails.game.model.Score
import com.raywenderlich.android.cocktails.game.viewmodel.CocktailsGameViewModel
import com.raywenderlich.android.cocktails.game.viewmodel.CocktailsGameViewModelFactory

class CocktailsGameActivity : AppCompatActivity() {

  private lateinit var viewModel: CocktailsGameViewModel

  private lateinit var binding: ActivityGameBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityGameBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val repository = (application as CocktailsApplication).repository
    val factory = (application as CocktailsApplication).gameFactory
    viewModel = ViewModelProvider(this, CocktailsGameViewModelFactory(repository, factory))
        .get(CocktailsGameViewModel::class.java)

    viewModel.getLoading().observe(this) {
      it?.let { loading -> showLoading(loading) }
    }
    viewModel.getError().observe(this) {
      it?.let { error -> showError(error) }
    }
    viewModel.getQuestion().observe(this) {
      showQuestion(it)
    }
    viewModel.getScore().observe(this) {
      it?.let { score -> showScore(score) }
    }
    viewModel.getGameOver().observe(this) {
      it?.let { isOver -> showGameOver(isOver) }
    }

    binding.nextButton.setOnClickListener { viewModel.nextQuestion() }

    viewModel.initGame()
  }

  private fun showScore(score: Score) {
    binding.scoreTextView.text = getString(R.string.game_score, score.current)
    binding.highScoreTextView.text = getString(R.string.game_highscore, score.highest)
  }

  private fun showQuestion(question: Question?) {
    if (question != null) {
      binding.mainGroup.visibility = View.VISIBLE

      if (question.answeredOption != null) {
        showAnsweredQuestion(question)
      } else {
        showUnansweredQuestion(question)
      }
    } else {
      binding.mainGroup.visibility = View.GONE
      binding.noResultsTextView.visibility = View.VISIBLE
      binding.questionResultImageView.visibility = View.GONE
    }
  }

  private fun showUnansweredQuestion(question: Question) {
    val options = question.getOptions()
    binding.firstOptionButton.text = options[0]
    binding.firstOptionButton.setOnClickListener { viewModel.answerQuestion(question, binding.firstOptionButton.text.toString()) }
    binding.firstOptionButton.isEnabled = true
    binding.secondOptionButton.text = options[1]
    binding.secondOptionButton.setOnClickListener { viewModel.answerQuestion(question, binding.secondOptionButton.text.toString()) }
    binding.secondOptionButton.isEnabled = true

    Glide.with(binding.cocktailImageView).load(question.imageUrl).into(binding.cocktailImageView)
    binding.questionResultImageView.visibility = View.GONE
  }

  private fun showAnsweredQuestion(question: Question) {
    binding.firstOptionButton.setOnClickListener(null)
    binding.firstOptionButton.isEnabled = false
    binding.secondOptionButton.setOnClickListener(null)
    binding.secondOptionButton.isEnabled = false
    if (question.isAnsweredCorrectly) {
      binding.questionResultImageView.setImageResource(R.drawable.ic_check_24dp)
    } else {
      binding.questionResultImageView.setImageResource(R.drawable.ic_error_24dp)
    }
    binding.questionResultImageView.visibility = View.VISIBLE
  }

  private fun showError(show: Boolean) {
    if (show) {
      binding.errorContainer.root.visibility = View.VISIBLE
      binding.errorContainer.retryButton.setOnClickListener { viewModel.initGame() }
    } else {
      binding.errorContainer.root.visibility = View.GONE
    }
  }

  private fun showLoading(show: Boolean) {
    binding.loadingContainer.root.visibility = if (show) View.VISIBLE else View.GONE
    binding.mainGroup.visibility = View.GONE
    binding.noResultsTextView.visibility = View.GONE
    binding.questionResultImageView.visibility = View.GONE
  }

  private fun showGameOver(show: Boolean) {
    if (show) {
      binding.gameOverContainer.root.visibility = View.VISIBLE
      binding.mainGroup.visibility = View.GONE
      binding.gameOverContainer.restartButton.setOnClickListener { viewModel.initGame() }
      binding.questionResultImageView.visibility = View.GONE
    } else {
      binding.gameOverContainer.root.visibility = View.GONE
    }
  }
}
