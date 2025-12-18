package com.example.autoglm.data

data class Action(
    val action: String?,
    val location: List<Int>? = null,
    val content: String? = null
)
