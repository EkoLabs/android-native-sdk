package com.eko.sdk

import org.json.JSONArray
import org.json.JSONObject

/**
 * Listener interface for receiving events and errors from an eko video
 */
interface IEkoPlayerListener {
    /**
     * The eko player triggers a number of events.
     * The app can listen to these events by providing the event name in the load call.
     * This function will be called whenever an event passed in to load() is triggered.
     * See also [Core Events doc](https://developer.eko.com/docs/embedding/dev.html#Core-Events)
     * @see EkoPlayerOptions.events
     */
    fun onEvent(event: String, args: JSONArray?)

    /**
     * Called whenever an error occurs.
     * This could happen in the loading process (if an invalid project id was given or we fail to open the link to the video),
     * or if an event is passed in with malformed data (missing an event name, etc).
     */
    fun onError(error: Error)
}