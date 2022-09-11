package com.kjipo.score

interface ElementWithProperties {

    fun getProperty(key: String): String?

    fun setProperty(key: String, value: String)

}