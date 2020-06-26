package com.raywenderlich.codingcompanionfinder.data

import com.github.javafaker.Faker
import com.raywenderlich.codingcompanionfinder.data.AddressData.atlantaAddress
import com.raywenderlich.codingcompanionfinder.data.AddressData.fakerAddress
import com.raywenderlich.codingcompanionfinder.data.BreedsData.fakerBreed
import com.raywenderlich.codingcompanionfinder.data.BreedsData.shihTzu
import com.raywenderlich.codingcompanionfinder.data.ContactsData.atlantaCodingShelter
import com.raywenderlich.codingcompanionfinder.data.ContactsData.fakerShelter
import com.raywenderlich.codingcompanionfinder.models.Address
import com.raywenderlich.codingcompanionfinder.models.Animal
import com.raywenderlich.codingcompanionfinder.models.Breeds
import com.raywenderlich.codingcompanionfinder.models.Contact

val faker = Faker()

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

  val fakerAddress = Address(
    faker.address().streetAddress(),
    faker.address().secondaryAddress(),
    faker.address().city(),
    faker.address().state(),
    faker.address().zipCode(),
    faker.address().country()
  )
}

object BreedsData {
  val shihTzu = Breeds("shih tzu", "", false, false)
  val fakerBreed = Breeds(faker.cat().breed(), faker.dog().breed(), faker.bool().bool(), faker.bool().bool())
}

object ContactsData {
  val atlantaCodingShelter = Contact(
    phone = "404-867-5309",
    email = "coding.companion@razware.com",
    address = atlantaAddress
  )

  val fakerShelter = Contact(
    faker.phoneNumber().cellPhone(),
    faker.internet().emailAddress(),
    fakerAddress
  )
}
