package com.kjipo.attemptprocessor

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test


class ProcessAttemptTest {


    // TODO Got the problem mention here when using test annotation: https://youtrack.jetbrains.com/issue/CMP-2610. Using Gradle 8.14.1
//    @Test
    fun processAttemptTest() {
        val inputAttemptProcessor = InputAttemptProcessor()


        // TODO Parse data
        val attempt  = Json.decodeFromString<InputAttempt>(attemptData)



    }



}