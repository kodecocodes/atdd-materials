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
        MockResponse().setResponseCode(200).setBody(
            readFile("search_30318.json")
        )
      }
      "/animals?limit=20&location=90210" -> {
        MockResponse().setResponseCode(200).setBody("{\"animals\": []}")
      }
      else -> {
        MockResponse().setResponseCode(404).setBody("{}")
      }
    }
  }

  @Throws(IOException::class)
  fun readFile(jsonFileName: String): String {
    val inputStream = this::class.java
      .getResourceAsStream("/assets/$jsonFileName") ?:
    this::class.java
      .getResourceAsStream("/$jsonFileName")
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
