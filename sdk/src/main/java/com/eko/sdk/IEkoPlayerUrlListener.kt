package com.eko.sdk

/**
 * Listener interface for link out events
 */
interface IEkoPlayerUrlListener {
    /**
     * There can be link outs from within an eko video.
     * This function will be called whenever a link out is supposed to occur.
     * The listener is responsible for opening the url.
     * @param url The url to open.
     */
    fun onUrlOpen(url: String)
}