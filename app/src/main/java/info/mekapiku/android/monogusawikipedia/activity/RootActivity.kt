package info.mekapiku.android.monogusawikipedia.activity

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import info.mekapiku.android.monogusawikipedia.R
import info.mekapiku.android.monogusawikipedia.bean.PageData
import info.mekapiku.android.monogusawikipedia.util.PreferenceUtils
import info.mekapiku.android.monogusawikipedia.util.WikipediaPageParser
import java.util.*


class RootActivity : Activity() {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var webPageParser: WikipediaPageParser
    private lateinit var handler: Handler
    private lateinit var progressDialog: ProgressDialog
    private lateinit var goWikiButton: Button
    private lateinit var speechSpeedSeekBar: SeekBar
    private lateinit var prefUtils: PreferenceUtils

    private var ttsSpeed = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        init()
    }

    private fun init() {

        prefUtils = PreferenceUtils[this]
        ttsSpeed = prefUtils.ttsSpeed

        handler = Handler()

        webPageParser = WikipediaPageParser(this, object : WikipediaPageParser.PageParserListener {

            override fun onStartParse() {
                textToSpeech.stop()

                progressDialog = ProgressDialog(this@RootActivity)
                progressDialog.setTitle(getString(R.string.progress_dialog_title))
                progressDialog.setMessage(getString(R.string.progress_dialog_value))
                progressDialog.isIndeterminate = false
                progressDialog.setOnCancelListener {
                    webPageParser.cancel()
                    textToSpeech.stop()
                }
                progressDialog.show()
            }

            override fun onFinishParse(pageData: PageData) {
                goWikiButton.text = pageData.title
                talkText(pageData.speechData)
            }
        })

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (TextToSpeech.SUCCESS == status) {
                val locale = Locale.JAPANESE
                if (textToSpeech.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.language = locale
                    textToSpeech.setSpeechRate(ttsSpeed)
                } else {
                    Log.d(RootActivity::class.java.simpleName, getString(R.string.error_tts_set_locale))
                }
            } else {
                Log.d(RootActivity::class.java.simpleName, getString(R.string.error_tts_on_init))
            }
        })

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(s: String) {
                progressDialog.dismiss()
            }

            override fun onDone(utteranceId: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(s: String) {
                handler.post { goWikiButton.text = getString(R.string.go_wiki_button_error_text) }
                Log.d(RootActivity::class.java.simpleName, s)
            }
        })

        goWikiButton = findViewById(R.id.go_wiki_button)

        run {
            // Go Wiki
            goWikiButton.setOnClickListener { webPageParser.loadUrl(WIKI_RAND_URL) }
        }

        speechSpeedSeekBar = findViewById(R.id.speech_speed_seek_bar)
        run {
            // Speech Speed
            speechSpeedSeekBar.progress = (ttsSpeed * 100).toInt()
            speechSpeedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    ttsSpeed = seekBar.progress.toFloat() / 100.0f
                    textToSpeech.setSpeechRate(ttsSpeed)
                }
            })
        }
    }

    private fun talkText(text: String) {
        if (text.isNotEmpty()) {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }

            // start speech
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UNIQUE_ID)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
        prefUtils.ttsSpeed = ttsSpeed
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.root, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        private const val UNIQUE_ID = "hAdZYnxUyVu4HTgx"
        private const val WIKI_RAND_URL = "https://ja.wikipedia.org/wiki/Special:Randompage"
    }
}
