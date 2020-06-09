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
     * If `true`, the EkoPlayer will display a cover to hide the project's loading.
     * Set to `false` if you already cover the loading in your app and want to save on resources.
     */
    var showCover = true

    /**
     * Set a custom cover to be shown while loading.
     * EkoPlayer will handle adding and removing the view from the view tree.
     * Please make sure the view's visibility is set to `VISIBLE`
     */
    var customCover: View? = null
}