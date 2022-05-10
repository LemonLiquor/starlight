package zhu.moon.requests

import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


object Requests {
    fun request(urlStr: String?): InputStream? {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)")
            connection.inputStream
        } catch (e: Exception) {
            null
        }
    }
}