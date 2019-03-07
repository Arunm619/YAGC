package com.freshworks.yagc.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.freshworks.yagc.Adapter.EventsAdapter
import com.freshworks.yagc.Model.EventsPublic.EventModel
import com.freshworks.yagc.ServerHandler
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_public_events.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@SuppressLint("StaticFieldLeak")
var adapter: EventsAdapter? = null


class PublicEventsActivity : AppCompatActivity() {

    lateinit var pref: SharedPreferences
    var currentActivePagenumber = 1
    var startPage = 1


    val TAG = "Arun"
    var serverHandler: ServerHandler? = null
    var firstLaunch: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.freshworks.yagc.R.layout.activity_public_events)
        supportActionBar?.title = "Events"

        Log.d(TAG, "OnCreate Started ")
        pref =
            getSharedPreferences(getString(com.freshworks.yagc.R.string.NAME_SHARED_PREFERENCES), Context.MODE_PRIVATE)
        checkCredentialsOkay()
        Log.d(TAG, "Credentials Okay")


        progressBar.visibility = View.VISIBLE

        rv_events.setHasFixedSize(true)
        val llm = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        rv_events.layoutManager = llm



        if (savedInstanceState != null) {
            Log.d(TAG, " Bundle is NOT NULL ")
            currentActivePagenumber =
                savedInstanceState.getInt("page")
            startPage = currentActivePagenumber

            Log.d(TAG, "Current active page saved = $currentActivePagenumber")
        } else {
            currentActivePagenumber = 1
            startPage = 1
        }



        loadFirstPage()




        rv_events.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                //for loading at the bottom
                if (dy > 0) {
                    //checking if the rv has reached the end of the list
                    val visibleItemCount = llm.childCount
                    val totalItemCount = llm.itemCount
                    val pastVisibleItems = llm.findFirstVisibleItemPosition()
                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        if (!serverHandler!!.isLoading()) {
                            Log.d(TAG, "Loading page ${currentActivePagenumber + 1} at bottom")
                            serverHandler?.getEvents(
                                page = currentActivePagenumber + 1,
                                result = object : ServerHandler.SuccessListener {
                                    override fun onSuccess(events: ArrayList<EventModel>) {
                                        currentActivePagenumber++
                                        for (i in events)
                                            adapter?.addEvent(i)
                                        adapter?.notifyDataSetChanged()
                                    }
                                })
                        } else {
                            Toast.makeText(baseContext, "Wait its loading", Toast.LENGTH_SHORT).show()
                        }

                    }
                } else {


                    //for loading at the top
                    if ((llm.findFirstVisibleItemPosition() <= 2)) {

                        if (!firstLaunch && !serverHandler!!.isLoading()) {
                            if (startPage > 1) {


                                Log.d(TAG, "Loading page ${startPage - 1} at Top")

                                serverHandler?.getEvents(
                                    page = startPage - 1,
                                    result = object : ServerHandler.SuccessListener {
                                        override fun onSuccess(events: ArrayList<EventModel>) {
                                            startPage--

                                            for (i in events)
                                                adapter?.addatFirst(i)

                                            adapter?.notifyDataSetChanged()
                                        }
                                    })
                            } else {
                                Toast.makeText(baseContext, "You are at top page", Toast.LENGTH_SHORT).show()

                            }
                        } else {
                            firstLaunch = false
                        }


                    }


                }


            }


        })


    }

    private fun loadFirstPage() {

        Log.d(TAG, "Loading page $currentActivePagenumber")

        serverHandler = ServerHandler()
        serverHandler?.getEvents(currentActivePagenumber, object : ServerHandler.SuccessListener {
            override fun onSuccess(events: ArrayList<EventModel>) {
                adapter = EventsAdapter(events, baseContext)
                rv_events.adapter = adapter
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle?) {

        Log.d(TAG, "rotated so saving current page number as $currentActivePagenumber")
        outState?.putInt("page", currentActivePagenumber)

        super.onSaveInstanceState(outState)
    }


    private fun checkCredentialsOkay() {
        val credentials = pref.getString(getString(R.string.KEY_CREDENTIALS_TOKEN), getString(R.string.notset))
        if (credentials.equals(getString(R.string.notset))) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        ApiClient.token = credentials
    }


    private fun callData(apiServices: ApiInterface) {
        val callForPublicEvents = apiServices.getPublicEvent(true, page = 1, per_page = 10)

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<EventModel>>,
                response: Response<ArrayList<EventModel>>
            ) {
                Toast.makeText(baseContext, "Page 1 is loading", Toast.LENGTH_LONG).show()
                val data = response.body()
                if (data == null) {
                    Toast.makeText(baseContext, "API rate limit EXCEEDED", Toast.LENGTH_LONG).show()


                } else {
                    adapter = EventsAdapter(data, baseContext)
                    rv_events.adapter = adapter
                    adapter?.notifyDataSetChanged()
                    progressBar.visibility = View.GONE


                }


            }


        })

    }

    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_ANY) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int {
                return snapMode
            }

            override fun getHorizontalSnapPreference(): Int {
                return snapMode
            }
        }
        smoothScroller.targetPosition = position

        Log.d(TAG, "Scrolled to Position $position ")

        rv_events.layoutManager?.startSmoothScroll(smoothScroller)
    }
}

