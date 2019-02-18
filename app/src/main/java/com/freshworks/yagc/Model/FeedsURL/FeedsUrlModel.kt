package com.freshworks.yagc.Model.FeedsURL

data class FeedsUrlModel(
    val _links: Links,
    val current_user_actor_url: String,
    val current_user_organization_url: String,
    val current_user_organization_urls: List<Any>,
    val current_user_public_url: String,
    val current_user_url: String,
    val security_advisories_url: String,
    val timeline_url: String,
    val user_url: String
)