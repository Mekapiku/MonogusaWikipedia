package info.mekapiku.android.monogusawikipedia.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import info.mekapiku.android.monogusawikipedia.R
import info.mekapiku.android.monogusawikipedia.bean.PageData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

/**
 * Created by mitsuyasu on 2014/09/08.
 */
class WikipediaPageParser(private val context: Context, private val listener: PageParserListener) {
    private lateinit var loader: WikipediaLoader

    fun loadUrl(url: String) {
        loader = WikipediaLoader()
        loader.execute(url)
        listener.onStartParse()
    }

    fun cancel() {
        loader.cancel(true)
    }

    private fun clipWebPageBody(document: Document) {
        val pageData = PageData()
        pageData.url = document.baseUri()

        val title = document.title().split(context.getString(R.string.wikipedia_title_pivot))[0]
        pageData.title = title

        val body = document.getElementById(context.getString(R.string.wikipedia_body_id))
        pageData.speechData = title + "\n" + body.text()
        listener.onFinishParse(pageData)
    }

    interface PageParserListener : EventListener {
        fun onStartParse()
        fun onFinishParse(pageData: PageData)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class WikipediaLoader : AsyncTask<String, Int, Document>() {

        override fun doInBackground(vararg urls: String): Document {
            return Jsoup.connect(urls[0])
                    .userAgent(context.getString(R.string.user_agent_ie11))
                    .followRedirects(true)
                    .get()
        }

        override fun onPostExecute(result: Document) {
            clipWebPageBody(result)
        }

    }
}
