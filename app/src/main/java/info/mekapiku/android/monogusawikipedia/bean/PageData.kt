package info.mekapiku.android.monogusawikipedia.bean

import java.io.Serializable

/**
 * Created by mitsuyasu on 2014/09/08.
 */
class PageData : Serializable {
    lateinit var url: String
    lateinit var title: String
    lateinit var speechData: String
}
