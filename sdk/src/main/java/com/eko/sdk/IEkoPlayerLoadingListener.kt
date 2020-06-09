package com.eko.sdk

interface IEkoPlayerLoadingListener {
    /***
     * Any custom animation should start here.
     * This will be called when the eko player begins to load.
     */
    fun onLoadStart()

    /***
     * Any custom animation should end here.
     * This will be called when the eko player is ready to play.
     */
    fun onLoadFinish()
}