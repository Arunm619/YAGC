package com.freshworks.yagc.Rest

import com.freshworks.yagc.Model.AllRepo
import com.freshworks.yagc.Model.Repository
import com.freshworks.yagc.Model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface{
    //val authenticate = "/users/arunm619?client_id=${ApiClient.CLIENT_ID}&client_secret=${ApiClient.CLIENT_SECRET}"

    @GET("users/")
    fun getAuthenticated(@Query("client_id") clientID : String
                         ,@Query("client_secret") clientSecret : String ) : Call<User>


    @GET("repositories?")
    fun getPublicRepos(@Query("since") since : String) : Call<AllRepo>

}
