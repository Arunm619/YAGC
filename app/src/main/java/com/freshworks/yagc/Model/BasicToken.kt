package com.freshworks.yagc.Model

import com.google.gson.annotations.SerializedName
import java.util.*

class BasicToken {

    var id: Int = 0
    var url: String? = null
    var token: String? = null
    @SerializedName("created_at")
    private var createdAt: Date? = null
    @SerializedName("updated_at")
    private var updatedAt: Date? = null
     private var scopes: List<String>? = null


    fun generateFromOauthToken(oauthToken: AccessToken): BasicToken {
        val basicToken = BasicToken()
        basicToken.token = (oauthToken.accessToken)
        basicToken.scopes = arrayListOf(oauthToken.tokenType)
        return basicToken
    }
}
