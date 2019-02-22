package com.freshworks.yagc.Model.EventsPublic

data class Commit(
    val author: Author,
    val distinct: Boolean,
    val message: String,
    val sha: String,
    val url: String
)