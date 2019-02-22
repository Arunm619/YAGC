package com.freshworks.yagc.Activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import com.freshworks.yagc.Adapter.EventsAdapter
import com.freshworks.yagc.Model.EventsPublic.EventModel
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_public_events.*
import retrofit2.Call
import retrofit2.Response
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.util.ArrayList

private var page = 1
private var per_page = 10


private var visibleItemCount: Int = 0
private var totalItemCount: Int = 0
private var pastVisibleItems: Int = 0
private var view_threshold: Int = 10
private var previous_total: Int = 0
var isLoading: Boolean = true
@SuppressLint("StaticFieldLeak")
var adapter: EventsAdapter? = null


class PublicEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_events)


        previous_total = 0
        val apiServices = ApiClient.client.create(ApiInterface::class.java)

        progressBar.visibility = View.VISIBLE

        rv_events.setHasFixedSize(true)
        val llm = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        rv_events.layoutManager = llm

        callData(apiServices)


        rv_events.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = llm.childCount
                totalItemCount = llm.itemCount
                pastVisibleItems = llm.findFirstVisibleItemPosition()

                Log.d(
                    "Testing", "visible count = $visibleItemCount  \n" +"" +
                            "dy = $dy   "+
                            "total count =  $totalItemCount  " +
                            "pastvisible items = $pastVisibleItems" +"" +
                            "previous total = $previous_total  "+
                            "isLoading = $isLoading" +
                            "  " +
                            " viewThreshold = $view_threshold "
                )

                if (dy > 0) {

                    if (isLoading) {
                        if (totalItemCount > previous_total) {
                            isLoading = false
                            previous_total = totalItemCount
                        }
                        else
                        {
                            Log.d("Testing","Lol da what the fuck")
                        }
                    }

                    if (!isLoading && (totalItemCount - visibleItemCount) <= pastVisibleItems + view_threshold) {
                        Log.d(
                            "Testing",
                            "Inside Perform task ${!isLoading && (totalItemCount - visibleItemCount) <= pastVisibleItems + view_threshold}"
                        )
                        page++

                        performPagination(apiServices)
                        isLoading = true
                    }
                }
            }
        })

    }

    private fun callData(apiServices: ApiInterface) {
        val callForPublicEvents = apiServices.getPublicEvent(true, page = page, per_page = per_page+10)

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<EventModel>>, response: Response<ArrayList<EventModel>>) {
                Toast.makeText(baseContext, "First Page is loading", Toast.LENGTH_LONG).show()

                // Toast.makeText(baseContext, "lol da ${response.body()}", Toast.LENGTH_LONG).show()
                val data = response.body()
                //Log.d("Data ", data.toString())
                //Log.d("Data ", data?.size.toString())


                if (data == null) {
                    Toast.makeText(baseContext, "Data is Null try again", Toast.LENGTH_LONG).show()


                } else {
                    adapter = EventsAdapter(data!!, mContext = baseContext)
                    rv_events.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    isLoading = false
                }

            }


        })

    }


    fun performPagination(apiServices: ApiInterface) {

        progressBar.visibility = View.VISIBLE


        val callForPublicEvents = apiServices.getPublicEvent(true, page = page, per_page = per_page)


        Toast.makeText(baseContext, "${callForPublicEvents.request().url()}", Toast.LENGTH_LONG).show()
        Log.d("URL", callForPublicEvents.request().url().toString())
        Toast.makeText(baseContext, "Page $page is loading..", Toast.LENGTH_LONG).show()

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<EventModel>>, response: Response<ArrayList<EventModel>>) {
                // Toast.makeText(baseContext, "lol da ${response.body()}", Toast.LENGTH_LONG).show()
                val data = response.body()

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    for (i in 0 until data?.size!!)
                        adapter?.addEvent(data[i])

                    adapter?.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

                } else {
                    Toast.makeText(baseContext, "Reached End!! ${response.errorBody()!!.string()}", Toast.LENGTH_LONG)
                        .show()
                    progressBar.visibility = View.GONE


                }

                //Log.d("Data ", data.toString())
                //Log.d("Data ", data?.size.toString())


                // val adapter = EventsAdapter(data!!, mContext = baseContext)
                // rv_events.adapter = adapter


            }


        })

    }

    override fun onBackPressed() {
        super.onBackPressed()

        page = 1
        per_page = 10
        adapter!!.deleteEvents()

        Toast.makeText(this,"BYEBYEBYE",Toast.LENGTH_LONG).show()
        finish()

    }

    /*   override fun onDestroy() {
           super.onDestroy()
           adapter!!.deleteEvents()
           page = 1
           per_page = 10
           isLoading = false
       }*/
}

