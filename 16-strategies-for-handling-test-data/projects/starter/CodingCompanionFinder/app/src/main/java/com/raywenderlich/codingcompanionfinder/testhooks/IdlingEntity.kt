package com.raywenderlich.codingcompanionfinder.testhooks

import java.io.Serializable

data class IdlingEntity(
    var incrementValue: Int = 0,
    var resetValue: Boolean = false
) : Serializable
