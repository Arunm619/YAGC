package com.freshworks.yagc.Rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        val BASE_URL = "https://api.github.com/"
        val CLIENT_ID = "645c12594e51c8e53c10"
        val CLIENT_SECRET = "8f9ead704b23bc4fddd84711c529efafed276ab8"
        var retrofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit!!
            }
    }

}
