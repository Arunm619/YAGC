package com.freshworks.yagc

import android.util.Log
import com.freshworks.yagc.Model.EventsPublic.EventModel
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*
import kotlin.collections.ArrayList

class ServerHandler {
    private var apiServices: ApiInterface = ApiClient.client.create(ApiInterface::class.java)
    private var Events: ArrayList<EventModel>
    private var pageNumber = 1
    val per_page = 10
    var isLoadingVar: Boolean


    init {
        Events = ArrayList()
        isLoadingVar = false
    }


    fun isLoading(): Boolean {
        return isLoadingVar
    }

    interface SuccessListener {
        fun onSuccess(events: ArrayList<EventModel>)
    }

    fun getEvents(page: Int, result: SuccessListener) {
        isLoadingVar = true


        val callForPublicEvents = apiServices.getPublicEvent(true, page = page, per_page = per_page)
        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                isLoadingVar = false

            }

            override fun onResponse(call: Call<ArrayList<EventModel>>, response: Response<ArrayList<EventModel>>) {
                isLoadingVar = false
                val data = response.body()

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Events = data!!
                } else {
                    Events = ArrayList()
                }
                result.onSuccess(Events)
            }

        })


    }


}