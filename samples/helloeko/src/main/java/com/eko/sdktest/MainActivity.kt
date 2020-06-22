package com.eko.sdktest

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.eko.sdk.*
import org.json.JSONArray

class MainActivity : AppCompatActivity(), IEkoPlayerListener, IEkoPlayerUrlListener,
    IEkoPlayerShareListener {
    private lateinit var projectIdTextView: TextView
    private lateinit var customEventsTextView: TextView
    private lateinit var eventsTextView: TextView
    private lateinit var loadingTextView: TextView
    private lateinit var envTextView: TextView
    private lateinit var paramsTextView: TextView
    private lateinit var customCoverCheck: CheckBox
    private lateinit var ekoPlayer: EkoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        projectIdTextView = findViewById(R.id.projectIdTextView)
        customEventsTextView = findViewById(R.id.customEventsTextView)
        loadingTextView = findViewById(R.id.loadingText)
        eventsTextView = findViewById(R.id.eventsTextView)
        envTextView = findViewById(R.id.envTextView)
        paramsTextView = findViewById(R.id.paramsTextView)
        customCoverCheck = findViewById(R.id.customCoverCheck)
        eventsTextView.movementMethod = ScrollingMovementMethod()

        ekoPlayer = findViewById(R.id.ekoplayer)
        ekoPlayer.setEkoPlayerListener(this)
        if (savedInstanceState != null) {
            ekoPlayer.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        ekoPlayer.saveState(outState)
    }

    override fun onEvent(event: String, args: JSONArray?) {
        eventsTextView.append("received event: $event\r\n")
        if (event == "eko.canplay") {
            loadingTextView.visibility = View.INVISIBLE
        } else if (event === "metadata") {
            val metadata = args?.getJSONObject(0)
            eventsTextView.append("${metadata.toString()}\r\n")
        }
    }

    override fun onError(error: Error) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
        loadingTextView.visibility = View.INVISIBLE
    }

    override fun onUrlOpen(url: String) {
        Toast.makeText(this, "URL: $url", Toast.LENGTH_LONG).show()
    }

    override fun onShare(url: String) {
        Toast.makeText(this, "Shared: $url", Toast.LENGTH_LONG).show()
    }

    fun loadProject(view: View) {
        val projectId = projectIdTextView.text
        val customEvents = customEventsTextView.text
        if (projectId.isNotBlank()) {
            val configuration = EkoPlayerOptions()
            if (customEvents.isNotBlank()) {
                configuration.events = customEvents.split(", ") + "eko.canplay"
            } else {
                configuration.events = listOf("eko.canplay")
            }
            val params = HashMap<String, String>()
            val paramsStringPairs = paramsTextView.text.split(",")
            paramsStringPairs.forEach { paramsPairString ->
                val paramsPair = paramsPairString.split("=")
                if (paramsPair.size == 2) {
                    params[paramsPair[0]] = paramsPair[1]
                }
            }
            if (params.isNotEmpty()) {
                configuration.params = params
            }
            configuration.environment = envTextView.text.toString()
            if (customCoverCheck.isChecked) {
                val customCover = View(this)
                customCover.setBackgroundColor(Color.BLUE)
                configuration.customCover = customCover
            }
            ekoPlayer.load(projectId.toString(), configuration)
            loadingTextView.visibility = View.VISIBLE
            eventsTextView.text = ""
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun clearLog(view: View) {
        eventsTextView.text = ""
    }

    fun play(view: View) {
        ekoPlayer.play()
    }

    fun pause(view: View) {
        ekoPlayer.pause()
    }

    fun increasePlayerSize(view: View) {
        ekoPlayer.layoutParams.height += 10
        ekoPlayer.requestLayout()
    }

    fun decreasePlayerSize(view: View) {
        ekoPlayer.layoutParams.height -= 10
        ekoPlayer.requestLayout()
    }

    fun handleUrlsChecked(view: View) {
        val isChecked = (view as CheckBox).isChecked
        if (isChecked) {
            ekoPlayer.setEkoPlayerUrlListener(this)
        } else {
            ekoPlayer.setEkoPlayerUrlListener(null)
        }
    }

    fun handleShareChecked(view: View) {
        val isChecked = (view as CheckBox).isChecked
        if (isChecked) {
            ekoPlayer.setEkoPlayerShareListener(this)
        } else {
            ekoPlayer.setEkoPlayerShareListener(null)
        }
    }
}
