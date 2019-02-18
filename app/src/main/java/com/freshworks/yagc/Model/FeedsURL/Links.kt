package com.freshworks.yagc.Model.FeedsURL

data class Links(
    val current_user: CurrentUser,
    val current_user_actor: CurrentUserActor,
    val current_user_organization: CurrentUserOrganization,
    val current_user_organizations: List<Any>,
    val current_user_public: CurrentUserPublic,
    val security_advisories: SecurityAdvisories,
    val timeline: Timeline,
    val user: User
)