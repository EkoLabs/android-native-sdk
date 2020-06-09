package com.eko.sdktest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.eko.sdk.*
import com.eko.sdktest.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), IEkoPlayerListener, IEkoPlayerUrlListener {
    private lateinit var projectIdTextView: TextView
    private lateinit var customEventsTextView: TextView
    private lateinit var eventsTextView: TextView
    private lateinit var loadingTextView: TextView
    private lateinit var customCoverCheck: CheckBox
    private lateinit var ekoPlayer: EkoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        projectIdTextView = findViewById(R.id.projectIdTextView)
        customEventsTextView = findViewById(R.id.customEventsTextView)
        loadingTextView = findViewById(R.id.loadingText)
        eventsTextView = findViewById(R.id.eventsTextView)
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

    override fun onOpenUrl(url: String) {
        Toast.makeText(this, "URL: $url", Toast.LENGTH_LONG).show()
    }

    fun loadProject(view: View) {
        val projectId = projectIdTextView.text
        val customEvents = customEventsTextView.text
        if (projectId.isNotBlank()) {
            val configuration = EkoPlayerConfiguration()
            configuration.events = customEvents?.split(", ")!! + "eko.canplay"
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
}
