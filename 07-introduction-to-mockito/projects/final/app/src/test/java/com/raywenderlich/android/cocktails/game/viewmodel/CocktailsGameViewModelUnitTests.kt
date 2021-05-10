package com.raywenderlich.android.cocktails.game.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.game.factory.CocktailsGameFactory
import com.raywenderlich.android.cocktails.game.model.Game
import com.raywenderlich.android.cocktails.game.model.Question
import com.raywenderlich.android.cocktails.game.model.Score
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class CocktailsGameViewModelUnitTests {
  // 1
  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()

  private lateinit var repository: CocktailsRepository
  private lateinit var factory: CocktailsGameFactory
  private lateinit var viewModel: CocktailsGameViewModel
  private lateinit var game: Game
  private lateinit var loadingObserver: Observer<Boolean>
  private lateinit var errorObserver: Observer<Boolean>
  private lateinit var scoreObserver: Observer<Score>
  private lateinit var questionObserver: Observer<Question>

  @Before
  fun setup() {
    repository = mock()
    factory = mock()
    viewModel = CocktailsGameViewModel(repository, factory)

    game = mock()

    loadingObserver = mock()
    errorObserver = mock()
    scoreObserver = mock()
    questionObserver = mock()
    viewModel.getLoading().observeForever(loadingObserver)
    viewModel.getScore().observeForever(scoreObserver)
    viewModel.getQuestion().observeForever(questionObserver)
    viewModel.getError().observeForever(errorObserver)
  }

  @Test
  fun init_shouldBuildGame() {
    viewModel.initGame()

    verify(factory).buildGame(any())
  }

  @Test
  fun init_shouldShowLoading() {
    viewModel.initGame()

    verify(loadingObserver).onChanged(eq(true))
  }

  @Test
  fun init_shouldHideError() {
    viewModel.initGame()

    verify(errorObserver).onChanged(eq(false))
  }

  @Test
  fun init_shouldShowError_whenFactoryReturnsError() {
    setUpFactoryWithError()

    viewModel.initGame()

    verify(errorObserver).onChanged(eq(true))
  }

  @Test
  fun init_shouldHideLoading_whenFactoryReturnsError() {
    setUpFactoryWithError()

    viewModel.initGame()

    verify(loadingObserver).onChanged(eq(false))
  }

  @Test
  fun init_shouldHideError_whenFactoryReturnsSuccess() {
    setUpFactoryWithSuccessGame(game)

    viewModel.initGame()

    verify(errorObserver, times(2)).onChanged(eq(false))
  }

  @Test
  fun init_shouldHideLoading_whenFactoryReturnsSuccess() {
    setUpFactoryWithSuccessGame(game)

    viewModel.initGame()

    verify(loadingObserver).onChanged(eq(false))
  }

  @Test
  fun init_shouldShowScore_whenFactoryReturnsSuccess() {
    val score = mock<Score>()
    whenever(game.score).thenReturn(score)
    setUpFactoryWithSuccessGame(game)

    viewModel.initGame()

    verify(scoreObserver).onChanged(eq(score))
  }

  @Test
  fun init_shouldShowFirstQuestion_whenFactoryReturnsSuccess() {
    val question = mock<Question>()
    whenever(game.nextQuestion()).thenReturn(question)
    setUpFactoryWithSuccessGame(game)

    viewModel.initGame()

    verify(questionObserver).onChanged(eq(question))
  }

  @Test
  fun nextQuestion_shouldShowQuestion() {
    val question1 = mock<Question>()
    val question2 = mock<Question>()
    whenever(game.nextQuestion())
        .thenReturn(question1)
        .thenReturn(question2)
    setUpFactoryWithSuccessGame(game)
    viewModel.initGame()

    viewModel.nextQuestion()

    verify(questionObserver).onChanged(eq(question2))
  }

  @Test
  fun answerQuestion_shouldDelegateToGame_saveHighScore_showQuestionAndScore() {
    val score = mock<Score>()
    val question = mock<Question>()
    whenever(game.score).thenReturn(score)
    setUpFactoryWithSuccessGame(game)
    viewModel.initGame()

    viewModel.answerQuestion(question, "VALUE")

    inOrder(game, repository, questionObserver, scoreObserver) {
      verify(game).answer(eq(question), eq("VALUE"))
      verify(repository).saveHighScore(any())
      verify(scoreObserver).onChanged(eq(score))
      verify(questionObserver).onChanged(eq(question))
    }
  }

  private fun setUpFactoryWithSuccessGame(game: Game) {
    doAnswer {
      val callback: CocktailsGameFactory.Callback = it.getArgument(0)
      callback.onSuccess(game)
    }.whenever(factory).buildGame(any())
  }

  private fun setUpFactoryWithError() {
    doAnswer {
      val callback: CocktailsGameFactory.Callback = it.getArgument(0)
      callback.onError()
    }.whenever(factory).buildGame(any())
  }
}