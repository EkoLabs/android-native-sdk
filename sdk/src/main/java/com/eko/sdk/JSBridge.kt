package com.eko.sdk

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject

class JSBridge(private var onEvent: (String, JSONArray?) -> Unit) {
    @JavascriptInterface
    fun postMessage(data: String) {
        try {
            val dataObj = JSONObject(data)
            Handler(Looper.getMainLooper()).post { ->
                onEvent(dataObj.getString("type"), dataObj.optJSONArray("args"))
            }
        } catch (error: Error) {
            Log.e("EkoPlayer", "Failed to parse message from project", error)
        }
    }
}