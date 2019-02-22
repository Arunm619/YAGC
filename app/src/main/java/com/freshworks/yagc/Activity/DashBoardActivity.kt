package com.freshworks.yagc.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.freshworks.yagc.Adapter.FeedAdapter
import com.freshworks.yagc.Adapter.HttpDataHandler
import com.freshworks.yagc.Model.FeedsURL.FeedsUrlModel
import com.freshworks.yagc.Model.RSSFeed.AtomFeedModel
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dash_board.*
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder

class DashBoardActivity : AppCompatActivity() {


    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var credentials = ""
    var feedUrl = ""
    val RSS_TO_JSON_API = "https://feed2json.org/convert?url="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        //  Toast.makeText(this, " teh code token is ${ApiClient.token}", Toast.LENGTH_LONG).show()
        checkCredentialsOkay()

        val llm = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        rv_feed.layoutManager = llm

        getUserFeedLinks()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {

            R.id.goto_public_events -> {

                startActivity(Intent(this, PublicEventsActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkCredentialsOkay() {
        pref = getSharedPreferences(getString(R.string.NAME_SHARED_PREFERENCES), Context.MODE_PRIVATE)
        editor = pref.edit()
        credentials = pref.getString(getString(R.string.KEY_CREDENTIALS_TOKEN), getString(R.string.notset))
        if (credentials.equals(getString(R.string.notset))) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun displayFeed(feedUrl: String) {

        val loadAtomFeedAsync = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, String, String>() {

            override fun onPreExecute() {
                progressBar.visibility = View.VISIBLE
            }


            override fun onPostExecute(result: String?) {

                progressBar.visibility = View.INVISIBLE
                val AtomObject: AtomFeedModel

                if (result == null || result.isEmpty()) {
                    Toast.makeText(baseContext, "Try again result is null", Toast.LENGTH_LONG).show()

                    return


                }
                AtomObject = Gson().fromJson<AtomFeedModel>(result, AtomFeedModel::class.java)
                val adapter = FeedAdapter(AtomObject, baseContext)
                rv_feed.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: String?): String? {
                val result: String?
                val http = HttpDataHandler()
                result = http.getHttpDataHandler(params[0]!!)
                return result
            }
        }


        val url_get_data = StringBuilder(RSS_TO_JSON_API)
        url_get_data.append(feedUrl)
        loadAtomFeedAsync.execute(url_get_data.toString())

    }

    private fun getUserFeedLinks() {

        ApiClient.token = credentials

        val apiServices = ApiClient.client.create(ApiInterface::class.java)

        val callForFeedUrls = apiServices.getFeedLinks()


        Log.d("TESTING CODE TOKEN", ApiClient.token)

        callForFeedUrls.enqueue(object : retrofit2.Callback<FeedsUrlModel> {
            override fun onFailure(call: Call<FeedsUrlModel>, t: Throwable) {
                Toast.makeText(baseContext, "Not okay bro try again ${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<FeedsUrlModel>, response: Response<FeedsUrlModel>) {


                feedUrl = response.body()!!.current_user_url


                Toast.makeText(
                    baseContext,
                    "Cool passed  this is current user ${response.body()!!.current_user_actor_url}",
                    Toast.LENGTH_LONG
                ).show()

                displayFeed(feedUrl)
            }

        })

    }
}
