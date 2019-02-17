package com.freshworks.yagc.Activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.freshworks.yagc.Model.AccessToken
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //open github link

        btn_authorize.setOnClickListener {

        }


/*
        val apiServices = ApiClient.client.create(ApiInterface::class.java)
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

        })*/

    }




}
