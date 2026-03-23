package com.kjipo.soundfontparser


class SoundfontParseException : Exception {

    constructor(message: String) : super(message)

    constructor(errors: List<String>) : super(errors.joinToString("\n"))

    constructor(message: String, errors: List<String>) : super(message + "\n" + errors.joinToString("\n"))

}