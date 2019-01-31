package com.freshworks.yagc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.freshworks.yagc.Model.AllRepo
import com.freshworks.yagc.Model.Repository
import com.freshworks.yagc.Model.User
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var apiServices = ApiClient.client.create(ApiInterface::class.java)
        val call = apiServices.getPublicRepos(1.toString())

        call.enqueue(object : Callback<AllRepo> {
            override fun onFailure(call: Call<AllRepo>, t: Throwable) {
                toast("KEEP TRYING")
            }

            override fun onResponse(call: Call<AllRepo>, response: Response<AllRepo>) {

                var list = response.body()!!.listofRepo

                for(i in list)
                {
                    toast(i.full_name)
                }

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })


    }
}
