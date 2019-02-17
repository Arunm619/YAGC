package com.freshworks.yagc.Model

import com.freshworks.yagc.Rest.ApiClient
import com.google.gson.annotations.SerializedName
import java.util.*

class AuthRequestModel {

    private var scopes: List<String>? = null
    var note: String? = null
        private set
    var noteUrl: String? = null
        private set
    @SerializedName("client_id")
    var clientId: String? = null
        private set
    @SerializedName("client_secret")
    var clientSecret: String? = null
        private set

    companion object {

        fun generate(): AuthRequestModel {
            val model = AuthRequestModel()
          //  model.scopes = Arrays.asList("user", "repo", "gist", "notifications")
            model.note = "dummy note"
            model.clientId = ApiClient.CLIENT_ID
            model.clientSecret =ApiClient.CLIENT_SECRET
            //model.noteUrl = ApiClient.REDIRECT_URL
            return model
        }
    }
}
