package com.freshworks.yagc.Rest

import com.freshworks.yagc.Utils.AppConfig
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient


class ApiClient {
    companion object {
        val AUTHORIZE_BASE_URL = "https://github.com/"
        val BASE_URL = "https://api.github.com/"
        val CLIENT_ID = "5d18241c432e99e2bad8"
        val CLIENT_SECRET = "af15d009cc9f649f63a553a554cd4a6d4df6a5c4"
        val REDIRECT_URL = "github://callback"
        val authorizeURL = "https://github.com/login/oauth/authorize"
        var retrofit: Retrofit? = null
        var mydummytokrn = "47eb3d4a1b638623c4cd87fa89c72fdd402a73cb"
        var token: String = ""


        val httpClient = OkHttpClient.Builder()
/*
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("parameter", "value").build();
                return chain.proceed(request);
            }
        })*/


        val client: Retrofit
            get() {
                val timeOut = 32 * 1000
                val httpClient = OkHttpClient.Builder()

                httpClient
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request = chain.request().newBuilder().addHeader("Authorization", token).build()
                            return chain.proceed(request)
                        }
                    })



                if (retrofit == null) {


                    /*  if (token != "")
                          httpClient.addInterceptor(object : Interceptor {
                              override fun intercept(chain: Interceptor.Chain): Response {
                                  val request: Request =
                                      chain.request().newBuilder().addHeader("Authorization", token).build()
                                  return chain.proceed(request)

                              }


                          })*/

                    retrofit = Retrofit.Builder()
                        .baseUrl(this.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())

                        .client(httpClient.build())
                        .build()
                }
                return retrofit!!
            }
    }

}
