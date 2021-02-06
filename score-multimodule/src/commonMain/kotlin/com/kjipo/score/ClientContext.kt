package com.kjipo.score

import kotlinx.serialization.Serializable

@Serializable
class ClientContext(val highlightMapping: Map<String, Collection<String>>)
