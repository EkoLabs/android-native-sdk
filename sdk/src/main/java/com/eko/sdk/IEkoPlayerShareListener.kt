package com.eko.sdk

/**
 * Listener interface for share events.
 */
interface IEkoPlayerShareListener {
    /**
     * There can be share intents from within an eko project via share buttons or ekoshell. This function will be called whenever a share intent happened.
     * @param url The canonical url of the project.
     */
    fun onShare(url: String)
}