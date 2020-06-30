package com.eko.sdk

import android.view.View

/**
 * EkoPlayerOptions class for customizing the eko experience
 */
class EkoPlayerOptions {
    /**
     * Map of params to pass to the embed API.
     * default: `{cover: false, autoplay: true}`
     */
    var params: Map<String, String> = mapOf(
        "cover" to "false",
        "autoplay" to "true"
    )

    /**
     * List of event that will be forwarded to the IEkoPlayerListener.
     * please see [Core Events doc](https://developer.eko.com/docs/embedding/dev.html#Core-Events)
     * and [Custom Events doc](https://developer.eko.com/docs/embedding/dev.html#Custom-Events) for reference
     * @see IEkoPlayerListener
     */
    var events: List<String> = ArrayList()

    /**
     * A View class to cover the loading of the eko project. Set to `null` to disable.
     * Will be initialized by the EkoPlayer.
     */
    var cover: Class<out View>? = EkoDefaultCover::class.java

    var environment: String? = null
}