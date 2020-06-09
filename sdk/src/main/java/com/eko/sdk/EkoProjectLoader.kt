package com.eko.sdk

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URLEncoder

class EkoProjectLoader {
    private val projectIDEndpoint: String = "https://sdks.eko.com/api/v1/projects/"
    private var projectId: String
    private var requestQueue: RequestQueue

    constructor(projectId: String, context: Context) {
        this.projectId = projectId
        this.requestQueue = Volley.newRequestQueue(context);
    }

    private fun buildEmbedUrl(json: JSONObject, config: EkoPlayerOptions): String {
        if (!json.has("url")) {
            throw EkoPlayerError(EkoPlayerError.TYPE.MALFORMED_RESPONSE, "URL not found - Missing embed url in response")
        }

        var url = "${json.getString("embedUrl")}/?embedapi=1.0"
        config.params.forEach { entry ->
            url += "&${entry.key}=${entry.value}"
        }
        if (config.events.isNotEmpty()) {
            url += "&events=${config.events.joinToString(",") {s -> URLEncoder.encode(s, "UTF-8") }}"
        }

        return url
    }

    fun getProjectEmbedUrl(
        config: EkoPlayerOptions,
        successListener: (url: String, metadata: JSONObject?) -> Unit,
        errorListener: (error: EkoPlayerError) -> Unit
    ) {
        val urlString = projectIDEndpoint + projectId
        val request = StringRequest(Request.Method.GET, urlString,
            Response.Listener { response ->
                println(response)
                try {
                    val json = JSONObject(response)
                    successListener(buildEmbedUrl(json, config), json.optJSONObject("metadata"))
                } catch (error: EkoPlayerError) {
                    errorListener(error)
                }
            },
            Response.ErrorListener { error ->
                if (error.networkResponse == null) {
                     errorListener(EkoPlayerError(EkoPlayerError.TYPE.REQUEST_ERROR, error.localizedMessage))
                } else {
                    errorListener(
                        EkoPlayerError(
                            EkoPlayerError.TYPE.STATUS_CODE,
                            "Request failed with status code - ${error.networkResponse.statusCode}. Potentially invalid project id."
                        )
                    )
                }
            })
        requestQueue.add(request)
    }
}