package com.freshworks.yagc.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.freshworks.yagc.Adapter.EventsAdapter
import com.freshworks.yagc.Model.EventsPublic.EventModel
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_public_events.*
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*


@SuppressLint("StaticFieldLeak")
var adapter: EventsAdapter? = null


class PublicEventsActivity : AppCompatActivity() {


    private var page = 1
    private var per_page = 10


    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var pastVisibleItem: Int = 0
    private var view_threshold: Int = 0
    private var previous_total: Int = 0
    private var pageNumberFromBundle: Int = 0
    private var ScrolViewPositionFromBundle: Int = 0
    var isLoading: Boolean = true
    var isLoadingTop: Boolean = false
    var lastVisiblePosition: Int = 0
    var topPage = -1
    var scrollviewpositionLocalHolder = -1

    lateinit var pref: SharedPreferences
    val llm = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

    val TAG = "Arun"

    var isFirstLoadComplete = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.freshworks.yagc.R.layout.activity_public_events)
        supportActionBar?.title = "Events"

        Log.d(TAG, "OnCreate Started ")

        if (savedInstanceState != null) {
            Log.d(TAG, "OnCreate - Saved Instance State is NOT NULL ")



            ScrolViewPositionFromBundle =
                savedInstanceState.getInt(getString(R.string.KEY_CURRENT_VISIBLE_SCROLL_POSITION))

            page = savedInstanceState.getInt(getString(R.string.KEY_CURRENT_VISIBLE_PAGE))

            Log.d(TAG, "Bundle : ")
            Log.d(TAG, "Page number =  $page")
            Log.d(TAG, "Scrollposition  = $ScrolViewPositionFromBundle")


            Log.d(TAG, "Scrollposition to be loaded in this page = $ScrolViewPositionFromBundle")
            scrollviewpositionLocalHolder = ScrolViewPositionFromBundle

        } else {
            Log.d(TAG, "Oncreate Started for First time")
            page = 1
            scrollviewpositionLocalHolder = -1
        }

        pref =
            getSharedPreferences(getString(com.freshworks.yagc.R.string.NAME_SHARED_PREFERENCES), Context.MODE_PRIVATE)
        checkCredentialsOkay()
        Log.d(TAG, "Credentials Okay")


        val apiServices = ApiClient.client.create(ApiInterface::class.java)
        progressBar.visibility = View.VISIBLE
        rv_events.setHasFixedSize(true)
        rv_events.layoutManager = llm

        Log.d(TAG, "Opening page $page ")

        callData(apiServices)

        Log.d(TAG, "Opened Page $page")

        rv_events.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = llm.childCount
                totalItemCount = llm.itemCount
                pastVisibleItem = llm.findFirstVisibleItemPosition()
                lastVisiblePosition = pastVisibleItem

                Log.d(TAG, "$lastVisiblePosition")

                if (dy > 0) {
                    //scrolling up
                    if (isLoading) {
                        if (totalItemCount > previous_total) {
                            isLoading = false
                            previous_total = totalItemCount
                        }
                    }

                    if (!isLoading && (totalItemCount - visibleItemCount) <= pastVisibleItem + view_threshold) {
                        page++
                        performPagination(apiServices)
                        isLoading = true
                    }
                } else {
                    //scrolling down


                    Log.d(TAG, "Scrolling down")
                    if (isFirstLoadComplete && scrollviewpositionLocalHolder != -1) {
                        Log.d(TAG, "Local Holder $scrollviewpositionLocalHolder and First load is completed ")

                        if (!isLoadingTop) {
                            Log.d(TAG, "isLoading Top = $isLoadingTop")

                            val pagetoBeLoaded = findPageTobeLoaded(scrollviewpositionLocalHolder)
                            Log.d(
                                TAG,
                                "Page to be loaded = $pagetoBeLoaded Last visible position = $lastVisiblePosition"
                            )

                            if (lastVisiblePosition % 10 <= 2 && pagetoBeLoaded > 0) {
                                isLoadingTop = true
                                performPaginationforAddingatFront(apiServices, pagetoBeLoaded)
                            } else {
                                Log.d(TAG, "You are at top page")
                            }
                        }

                    }
                    else{


                    }


                }

            }
        })
    }

    private fun findPageTobeLoaded(position: Int): Int {

        return (position / 10)

    }


    /*
    * callData fetches the data from Github events End Point and creates the adapter and feeds the data.
    * */
    private fun callData(apiServices: ApiInterface) {
        val callForPublicEvents = apiServices.getPublicEvent(true, page = page, per_page = per_page)

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<EventModel>>,
                response: Response<ArrayList<EventModel>>
            ) {
                Toast.makeText(baseContext, "Page $page is loading", Toast.LENGTH_LONG).show()
                val data = response.body()
                if (data == null) {
                    Toast.makeText(baseContext, "API rate limit EXCEEDED", Toast.LENGTH_LONG).show()
                    isFirstLoadComplete = true

                } else {
                    adapter = EventsAdapter(data, baseContext)
                    rv_events.adapter = adapter
                    adapter?.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    isLoading = true

                    Handler().postDelayed(
                        {
                            rv_events.smoothSnapToPosition((ScrolViewPositionFromBundle % per_page))
                            isFirstLoadComplete = true
                        },
                        400
                    )


                }


            }


        })

    }

    private fun performPaginationforAddingatFront(apiServices: ApiInterface?, pagetoBeLoaded: Int) {
        progressBar.visibility = View.VISIBLE


        val callForPublicEvents = apiServices!!.getPublicEvent(true, page = pagetoBeLoaded, per_page = per_page)


        //        Toast.makeText(baseContext, "${callForPublicEvents.request().url()}", Toast.LENGTH_LONG).show()
        Log.d(TAG, "Performing top pagintaion for $pagetoBeLoaded")
        Log.d(TAG, "Link = ${callForPublicEvents.request().url()}")

        //Toast.makeText(baseContext, "page  $pagetoBeLoaded is loading..", Toast.LENGTH_LONG).show()

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()
                isLoadingTop = false
            }

            override fun onResponse(
                call: Call<ArrayList<EventModel>>,
                response: Response<ArrayList<EventModel>>
            ) {
                // Toast.makeText(baseContext, "lol da ${response.body()}", Toast.LENGTH_LONG).show()
                val data = response.body()

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    for (i in 0 until data?.size!!)
                        adapter?.addatFirst(data[i])

                    adapter?.notifyDataSetChanged()
                    progressBar.visibility = View.GONE


                } else {
                    Toast.makeText(
                        baseContext,
                        "Reached End!! ${response.errorBody()!!.string()}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    progressBar.visibility = View.GONE

                    Handler().postDelayed(
                        { rv_events.smoothSnapToPosition((scrollviewpositionLocalHolder % 10) + 10) },
                        2000
                    )


                }

                isLoadingTop = false


            }


        })

    }


    //for storing page number and scroll view position closed
    override fun onSaveInstanceState(outState: Bundle?) {


        Log.d(TAG, "Rotated: ")
        if (scrollviewpositionLocalHolder == -1) {

            Log.d(TAG, "FIRST TIME as VARIBALE  is -1")

            val localCurrentVisiblePage = (lastVisiblePosition / per_page) + 1
            Log.d(TAG, "Current Visible Page = $localCurrentVisiblePage")
            outState?.putInt(getString(R.string.KEY_CURRENT_VISIBLE_PAGE), localCurrentVisiblePage)

            val localCurrentVisibleScrollPosition = lastVisiblePosition % per_page
            Log.d(TAG, "Current Visible Scroll Position saved = $localCurrentVisibleScrollPosition")
            outState?.putInt(getString(R.string.KEY_CURRENT_VISIBLE_SCROLL_POSITION), localCurrentVisibleScrollPosition)

        } else {


            Log.d(TAG, "Variable value is $scrollviewpositionLocalHolder last visible position is $lastVisiblePosition")
            val localCurrentVisibleScrollPosition = lastVisiblePosition + (scrollviewpositionLocalHolder / 10) * 10
            Log.d(TAG, "Current Visible Scroll Position saved = $localCurrentVisibleScrollPosition")
            outState?.putInt(getString(R.string.KEY_CURRENT_VISIBLE_SCROLL_POSITION), localCurrentVisibleScrollPosition)


            val localCurrentVisiblePage = ((localCurrentVisibleScrollPosition) / per_page) + 1
            Log.d(TAG, "Current Visible Page = $localCurrentVisiblePage")
            outState?.putInt(getString(R.string.KEY_CURRENT_VISIBLE_PAGE), localCurrentVisiblePage)


        }

        super.onSaveInstanceState(outState)
    }


    /*
    * performPagination at the bottom
    * */
    fun performPagination(apiServices: ApiInterface) {
        progressBar.visibility = View.VISIBLE
        val callForPublicEvents = apiServices.getPublicEvent(true, page = page, per_page = per_page)
        Log.d(TAG, "Bottom Pagination called with page = $page ")

        // Toast.makeText(baseContext, "${callForPublicEvents.request().url()}", Toast.LENGTH_LONG).show()
        Log.d(TAG, callForPublicEvents.request().url().toString())
        Toast.makeText(baseContext, "Page $page is loading..", Toast.LENGTH_LONG).show()

        callForPublicEvents.enqueue(object : retrofit2.Callback<ArrayList<EventModel>> {
            override fun onFailure(call: Call<ArrayList<EventModel>>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.stackTrace}", Toast.LENGTH_LONG).show()

            }

            override fun onResponse(
                call: Call<ArrayList<EventModel>>,
                response: Response<ArrayList<EventModel>>
            ) {
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

            }


        })

    }


    private fun checkCredentialsOkay() {
        var credentials = pref.getString(getString(R.string.KEY_CREDENTIALS_TOKEN), getString(R.string.notset))
        if (credentials.equals(getString(R.string.notset))) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        ApiClient.token = credentials
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

        llm.startSmoothScroll(smoothScroller)
    }
}

