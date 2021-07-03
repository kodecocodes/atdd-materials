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
package com.raywenderlich.codingcompanionfinder.data

import com.github.javafaker.Faker
import com.raywenderlich.codingcompanionfinder.data.AddressData.atlantaAddress
import com.raywenderlich.codingcompanionfinder.data.BreedsData.shihTzu
import com.raywenderlich.codingcompanionfinder.data.ContactsData.atlantaCodingShelter
import com.raywenderlich.codingcompanionfinder.models.Address
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.raywenderlich.codingcompanionfinder.models.Breeds
import com.raywenderlich.codingcompanionfinder.models.Contact

val faker = Faker()

val fakerAddress = Address(
  faker.address().streetAddress(),
  faker.address().secondaryAddress(),
  faker.address().city(),
  faker.address().state(),
  faker.address().zipCode(),
  faker.address().country()
)

val fakerBreed = Breeds(faker.cat().breed(),
  faker.dog().breed(), faker.bool().bool(), faker.bool().bool())

val fakerShelter = Contact(
  faker.phoneNumber().cellPhone(),
  faker.internet().emailAddress(),
  fakerAddress
)

val fakerAnimal = Animal(
  faker.number().digits(3).toInt(),
  fakerShelter,
  faker.number().digit(),
  faker.commerce().productName(),
  arrayListOf(),
  fakerBreed,
  faker.name().name(),
  faker.dog().gender(),
  faker.chuckNorris().fact()
)

object AnimalData {
  val atlantaShihTzuNamedSpike = Animal(
    22,
    atlantaCodingShelter,
    "5",
    "small",
    arrayListOf(),
    shihTzu,
    "Spike",
    "male",
    "A sweet little guy with spikey teeth!"
  )
}

object AddressData {
  val atlantaAddress = Address(
    "",
    "",
    "Atlanta",
    "GA",
    "30303",
    "USA"
  )
}

object BreedsData {
  val shihTzu = Breeds("shih tzu", "", false, false)
}

object ContactsData {
  val atlantaCodingShelter = Contact(
    phone = "404-867-5309",
    email = "coding.companion@razware.com",
    address = atlantaAddress
  )
}
