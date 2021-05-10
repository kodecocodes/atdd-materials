package com.raywenderlich.android.cocktails.common.repository

import android.content.SharedPreferences
import com.raywenderlich.android.cocktails.common.network.CocktailsApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class RepositoryUnitTests {
  private lateinit var repository: CocktailsRepository
  @Mock
  private lateinit var api: CocktailsApi
  @Mock
  private lateinit var sharedPreferences: SharedPreferences
  @Mock
  private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

  @Before
  fun setup() {
    whenever(sharedPreferences.edit())
        .thenReturn(sharedPreferencesEditor)

    repository = CocktailsRepositoryImpl(api, sharedPreferences)
  }

  @Test
  fun saveScore_shouldSaveToSharedPreferences() {
    val score = 100
    repository.saveHighScore(score)

    inOrder(sharedPreferencesEditor) {
      verify(sharedPreferencesEditor).putInt(any(), eq(score))
      verify(sharedPreferencesEditor).apply()
    }
  }

  @Test
  fun getScore_shouldGetFromSharedPreferences() {
    repository.getHighScore()

    verify(sharedPreferences).getInt(any(), any())
  }

  @Test
  fun saveScore_shouldNotSaveToSharedPreferencesIfLower() {
    val previouslySavedHighScore = 100
    val newHighScore = 10
    val spyRepository = spy(repository)
    doReturn(previouslySavedHighScore)
        .whenever(spyRepository)
        .getHighScore()
    spyRepository.saveHighScore(newHighScore)
    verify(sharedPreferencesEditor, never())
        .putInt(any(), eq(newHighScore))
  }
}