package com.eko.sdk

import org.json.JSONArray
import org.json.JSONObject

interface IEkoPlayerListener {
    /**
     * Any project can define custom events. The app can listen to these events by providing
     * the event name in the configuration object. This function will be called whenever a
     * custom event defined in the config object is triggered.
     */
    fun onEvent(event: String, args: JSONArray?)

    /**
     * Called whenever the player triggers an 'error' event
     */
    fun onError(error: Error)
}