package com.freshworks.yagc.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.freshworks.yagc.Model.AccessToken
import com.freshworks.yagc.Model.AuthRequestModel
import com.freshworks.yagc.Model.BasicToken
import com.freshworks.yagc.R
import com.freshworks.yagc.Rest.ApiClient
import com.freshworks.yagc.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        pref = getSharedPreferences(getString(R.string.NAME_SHARED_PREFERENCES), Context.MODE_PRIVATE)
        editor = pref.edit()


        var credentials = pref.getString(getString(R.string.KEY_CREDENTIALS_TOKEN), getString(R.string.notset))

        if (credentials != getString(R.string.notset)) {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        }


        btn_authorize.setOnClickListener {
            Authorize()
        }

        btn_login.setOnClickListener {

            val username = et_username.text.toString().trim()
            val password = et_password.text.toString().trim()
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(baseContext, "Credentials Error", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            // val username = "arunm619"
            //val pass = "alliswell200"
            val authRequestModel = AuthRequestModel.generate()
            val credentials_token = Credentials.basic(username, password)

            editor.putString(getString(R.string.KEY_CREDENTIALS_TOKEN), credentials_token)
            editor.apply()


            ApiClient.token = credentials_token

            val apiServices = ApiClient.client.create(ApiInterface::class.java)

            Log.d("TOKEN BUILD ", credentials_token)
            val callforBasicToken = apiServices.authorizations(authRequestModel)

            callforBasicToken.enqueue(object : Callback<BasicToken> {
                override fun onFailure(call: Call<BasicToken>, t: Throwable) {
                    Toast.makeText(baseContext, "failed ${t.message}", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<BasicToken>, response: Response<BasicToken>) {
                    // var r = response

                    Toast.makeText(baseContext, "passed ${response.body()!!.token}", Toast.LENGTH_LONG).show()
                    startActivity(Intent(baseContext, DashBoardActivity::class.java))
                    finish()
                }

            })


        }


    }


    private fun Authorize() {
        //opens Browser to show the authorization page and returns to the application with the callback code
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                ApiClient.authorizeURL +
                        "?client_id=${ApiClient.CLIENT_ID}" +
                        "&scope=user,repo,gist,notifications&redirect_uri=${ApiClient.REDIRECT_URL}"
            )
        )

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()

        if (intent != null) {
            val uri = intent.data

            if (uri != null && uri.toString().startsWith(ApiClient.REDIRECT_URL)) {
                val code = uri.getQueryParameter("code")
                val apiServices = ApiClient.client.create(ApiInterface::class.java)
                val callforToken =
                    apiServices.getAccessToken(ApiClient.CLIENT_ID, ApiClient.CLIENT_SECRET, code.toString())

                callforToken.enqueue(object : Callback<AccessToken> {
                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Toast.makeText(baseContext, "Not authorized", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

                        val accesstoken = response.body()?.accessToken
                        Toast.makeText(
                            baseContext,
                            "yay authorized with $accesstoken",
                            Toast.LENGTH_LONG
                        ).show()

                        editor.putString(getString(R.string.KEY_ACCESS_TOKEN), accesstoken)
                        editor.apply()


                        /*  var basicToken = BasicToken()
                          var AccessToken = response.body()!!
                          var basicTokenfromOauth = basicToken.generateFromOauthToken(AccessToken)

                          Log.d("BasicTOkenKey",basicTokenfromOauth.token)
  */
                        // startActivity(Intent(baseContext, DashBoardActivity::class.java))
                        //finish()

                        Log.d("Code", """$accesstoken ${response.body()!!.tokenType}""")
                    }

                })


            } else {

                Toast.makeText(baseContext, "Please Login", Toast.LENGTH_LONG).show()
                Log.d("Code", " ${pref.getString(getString(R.string.KEY_ACCESS_TOKEN), "SOrry bro try again")} ")


            }
        }
    }

}
