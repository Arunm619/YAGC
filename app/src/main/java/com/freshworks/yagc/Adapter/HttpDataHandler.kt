package com.freshworks.yagc.Adapter

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class HttpDataHandler {
    var stream: String = ""
    fun getHttpDataHandler(_url: String): String? {
        try {
            val url: URL = URL(_url)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
                val r: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                var sb = StringBuilder()
                var line: String? = null
                line = r.readLine()
                while (line != null) {
                    sb.append(line)
                    line = r.readLine()
                }
                stream = sb.toString()
                urlConnection.disconnect()

            }
        } catch (ex: Exception) {
            return null
        }
        return stream
    }
}