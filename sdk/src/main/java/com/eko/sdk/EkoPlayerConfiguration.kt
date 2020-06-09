package com.eko.sdk

import android.view.View

class EkoPlayerConfiguration {
    var params: Map<String, String> = mapOf(
        "cover" to "false",
        "autoplay" to "true"
    )
    var events: List<String> = ArrayList()
    var showCover = true
    var customCover: View? = null
}