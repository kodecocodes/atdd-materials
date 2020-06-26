package com.raywenderlich.codingcompanionfinder

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object CommonTestDataUtil {

  fun dispatch(request: RecordedRequest): MockResponse? {
    return when (request.path) {
      "/animals?limit=20&location=30318" -> {
        MockResponse().setResponseCode(200).setBody(readFile("search_30318.json")
        )
      }
      else -> {
        MockResponse().setResponseCode(404).setBody("{}")
      }
    }
  }

  @Throws(IOException::class)
  private fun readFile(jsonFileName: String): String {
    val inputStream = this::class.java.getResourceAsStream("/assets/$jsonFileName")
        ?: throw NullPointerException(
            "Have you added the local resource correctly?, "
                + "Hint: name it as: " + jsonFileName
        )
    val stringBuilder = StringBuilder()
    var inputStreamReader: InputStreamReader? = null
    try {
      inputStreamReader = InputStreamReader(inputStream)
      val bufferedReader = BufferedReader(inputStreamReader)
      var character: Int = bufferedReader.read()
      while (character != -1) {
        stringBuilder.append(character.toChar())
        character = bufferedReader.read()
      }
    } catch (exception: IOException) {
      exception.printStackTrace()
    } finally {
      inputStream.close()
      inputStreamReader?.close()
    }
    return stringBuilder.toString()
  }
}
