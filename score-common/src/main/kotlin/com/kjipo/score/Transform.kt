package com.kjipo.score

import kotlinx.serialization.Serializable


// TODO There is an issue with serializing sealed classes in the JS part of the serialization library. Try again with sealed class when the issue is resolved

//@Serializable
//sealed class Transform {
//
//    @Serializable
//    data class Translation(
//            val xShift: Int, val yShift: Int): Transform()
//
//}



@Serializable
data class Translation(
        val xShift: Int, val yShift: Int)