package com.freshworks.yagc.Model.RSSFeed

data class AtomFeedModel(
    val home_page_url: String,
    val items: List<Item>,
    val title: String,
    val version: String
)