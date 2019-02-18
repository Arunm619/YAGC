package com.freshworks.yagc.Rest

import android.support.annotation.NonNull
import com.freshworks.yagc.Model.*
import com.freshworks.yagc.Model.FeedsURL.FeedsUrlModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    //val authenticate = "/users/arunm619?client_id=${ApiClient.CLIENT_ID}&client_secret=${ApiClient.CLIENT_SECRET}"

    @GET("users/")
    fun getAuthenticated(
        @Query("client_id") clientID: String
        , @Query("client_secret") clientSecret: String
    ): Call<User>


    @GET("repositories")
    fun getPublicRepos(@Query("since") since: String): Call<AllRepo>



    @GET("feeds")
    fun getFeedLinks() : Call<FeedsUrlModel>

    @POST("authorizations")
    @Headers("Accept: application/json")
    fun authorizations(
        @NonNull @Body authRequestModel: AuthRequestModel
    ): Call<BasicToken>


    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    fun getAccessToken(
        @Field("client_id") clientID: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Call<AccessToken>
}
