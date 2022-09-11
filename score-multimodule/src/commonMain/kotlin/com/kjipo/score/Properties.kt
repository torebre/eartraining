package com.kjipo.score

class Properties : ElementWithProperties {
    private val properties = mutableMapOf<String, String>()

    override fun getProperty(key: String) = properties[key]

    override fun setProperty(key: String, value: String) {
        properties[key] = value
    }

}