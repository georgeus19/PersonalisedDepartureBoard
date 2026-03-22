package dev.hruby.personaliseddepartureboard.data.model

import java.time.LocalTime

data class Profile(
    val Name: String,
    val Stop: Stop,
    val Validity: Validity
)

data class Validity(
    val From: LocalTime,
    val To: LocalTime
)
