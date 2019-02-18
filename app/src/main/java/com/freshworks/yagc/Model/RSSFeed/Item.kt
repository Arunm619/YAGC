package com.freshworks.yagc.Model.RSSFeed

import android.provider.MediaStore

data class Item(
    val author: Author,
    val content_html: String,
    val date_published: String,
    val guid: String,
    val title: String,
    val url: String,
    val thumbnail :String
)