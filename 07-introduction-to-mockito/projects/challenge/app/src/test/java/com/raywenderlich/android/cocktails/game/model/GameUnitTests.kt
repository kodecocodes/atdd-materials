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

package com.raywenderlich.android.cocktails.game.model

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class GameUnitTests {

  @Test
  fun whenGettingNextQuestion_shouldReturnIt() {
    val question1 = Question("CORRECT", "INCORRECT")
    val questions = listOf(question1)
    val game = Game(questions)

    val nextQuestion = game.nextQuestion()

    Assert.assertSame(question1, nextQuestion)
  }

  @Test
  fun whenGettingNextQuestion_withoutMoreQuestions_shouldReturnNull() {
    val question1 = Question("CORRECT", "INCORRECT")
    val questions = listOf(question1)
    val game = Game(questions)

    game.nextQuestion()
    val nextQuestion = game.nextQuestion()

    Assert.assertNull(nextQuestion)
  }

  @Test
  fun whenAnswering_shouldDelegateToQuestion() {
    val question = mock<Question>()
    val game = Game(listOf(question))

    game.answer(question, "OPTION")

    verify(question).answer(eq("OPTION"))
  }

  @Test
  fun whenAnsweringCorrectly_shouldIncrementCurrentScore() {
    val question = mock<Question>()
    whenever(question.answer(anyString())).thenReturn(true)
    val score = mock<Score>()
    val game = Game(listOf(question), score)

    game.answer(question, "OPTION")

    verify(score).increment()
  }

  @Test
  fun whenAnsweringIncorrectly_shouldNotIncrementCurrentScore() {
    val question = mock<Question>()
    whenever(question.answer(anyString())).thenReturn(false)
    val score = mock<Score>()
    val game = Game(listOf(question), score)

    game.answer(question, "OPTION")

    verify(score, never()).increment()
  }

  @Test
  fun whenAnsweringIncorrectlyThreeTimes_shouldFinishTheGame() {
    val question1 = mock<Question>()
    whenever(question1.answer(anyString())).thenReturn(false)
    val question2 = mock<Question>()
    whenever(question2.answer(anyString())).thenReturn(false)
    val question3 = mock<Question>()
    whenever(question3.answer(anyString())).thenReturn(false)
    val questions = listOf(question1, question2, question3)
    val game = Game(questions)

    game.answer(question1, "INCORRECT")
    game.answer(question2, "INCORRECT")
    game.answer(question3, "INCORRECT")

    Assert.assertTrue(game.isOver)
  }

  @Test
  fun whenAnsweringCorrectlyThreeTimesSequentially_shouldStartGivingDoubleScore() {
    val question1 = mock<Question>()
    whenever(question1.answer(anyString())).thenReturn(true)
    val question2 = mock<Question>()
    whenever(question2.answer(anyString())).thenReturn(true)
    val question3 = mock<Question>()
    whenever(question3.answer(anyString())).thenReturn(true)
    val question4 = mock<Question>()
    whenever(question4.answer(anyString())).thenReturn(true)
    val questions = listOf(question1, question2, question3, question4)
    val score = mock<Score>()
    val game = Game(questions, score)

    game.answer(question1, "CORRECT")
    game.answer(question2, "CORRECT")
    game.answer(question3, "CORRECT")
    game.answer(question4, "CORRECT")

    verify(score, times(1 + 1 + 1 + 2)).increment()
  }
}