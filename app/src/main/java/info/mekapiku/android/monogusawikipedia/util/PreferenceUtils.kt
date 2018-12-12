package info.mekapiku.android.monogusawikipedia.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by mitsuyasu on 2014/09/13.
 */
class PreferenceUtils private constructor(mContext: Context) {

    var ttsSpeed: Float
        get() = mSharedPref.getFloat(SPEECH_SPEED_KEY, 1.0f)
        set(speed) {
            mSharedPref.edit().putFloat(SPEECH_SPEED_KEY, speed).apply()
        }

    var ttsPitch: Float
        get() = mSharedPref.getFloat(SPEECH_PITCH_KEY, 1.0f)
        set(pitch) {
            mSharedPref.edit().putFloat(SPEECH_PITCH_KEY, pitch).apply()
        }

    init {
        mSharedPref = mContext.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
    }

    companion object {

        private const val PREF_KEY = "MonogusaWikipediaPref"
        private const val SPEECH_SPEED_KEY = "speech_key"
        private const val SPEECH_PITCH_KEY = "speech_pitch"

        @SuppressLint("StaticFieldLeak")
        private lateinit var mPrefUtils: PreferenceUtils
        private lateinit var mSharedPref: SharedPreferences

        operator fun get(context: Context): PreferenceUtils {
            mPrefUtils = PreferenceUtils(context)
            return mPrefUtils
        }
    }
}
