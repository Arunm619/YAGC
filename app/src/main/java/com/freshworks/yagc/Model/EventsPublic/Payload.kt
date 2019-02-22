package com.freshworks.yagc.Model.EventsPublic

data class Payload(
    val before: String,
    val commits: List<Commit>,
    val distinct_size: Int,
    val head: String,
    val push_id: Long,
    val ref: String,
    val size: Int
)