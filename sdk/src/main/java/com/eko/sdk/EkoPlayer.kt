package com.eko.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import org.json.JSONArray

/**
 * EkoPlayer will load, play and interact easily with eko videos
 */
class EkoPlayer : FrameLayout {
    private lateinit var webView: WebView
    private var eventsListener: IEkoPlayerListener? = null
    private var urlListener: IEkoPlayerUrlListener? = null
    private var coverShown = false

    /**
     * App name is for analytics purposes. Will default to the bundle id if not set.
     */
    var appName: String = ""
        set(value) {
            field = value
            setUa()
        }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    fun saveState(outState: Bundle?) {
        webView.saveState(outState)
    }

    fun restoreState(inState: Bundle) {
        webView.restoreState(inState)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        WebView.setWebContentsDebuggingEnabled(true)
        webView = WebView(context)
        webView.visibility = View.GONE
        webView.webViewClient = WebViewClient()
        this.addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setupWebview()
        appName = context.packageName
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        webView.settings.let {
            it?.javaScriptEnabled = true
            it?.builtInZoomControls = false
            it?.domStorageEnabled = true
            it?.allowFileAccess = true
            it?.blockNetworkImage = false
            it?.blockNetworkLoads = false
            it?.loadsImagesAutomatically = true
            it?.mediaPlaybackRequiresUserGesture = false
        }
        webView.addJavascriptInterface(
            JSBridge { type, args -> onJSEvent(type, args) },
            "nativeSdk"
        )
    }

    private fun onJSEvent(type: String, args: JSONArray?) {
        if (coverShown && type == "eko.canplay") {
            removeCover()
        } else if (type == "eko.urls.openinparent") {
            val url = args?.getJSONObject(0)?.getString("url")
                ?: return // event is malformed so we return
            if (urlListener != null) {
                urlListener!!.onOpenUrl(url)
            } else { // No url listener was given so we open the browser
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            return
        }

        eventsListener?.onEvent(type, args)
    }

    private fun addCover() {
        if (coverShown) {
            return
        }
        val coverContainer = FrameLayout(context)
        coverContainer.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)
        coverContainer.setBackgroundColor(Color.BLACK)
        val spinnerSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics)
                .toInt()
        val spinner = ProgressBar(context)
        spinner.layoutParams = LayoutParams(spinnerSize, spinnerSize, Gravity.CENTER)
        spinner.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        coverContainer.addView(spinner)
        addView(coverContainer)
    }

    private fun removeCover() {
        if (coverShown) {
            removeViewAt(1)
            coverShown = false
        }
    }

    private fun setUa() {
        val sdkVersion = BuildConfig.VERSION_NAME
        val appVersion =
            this.context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val sdkUa = " - ekoNativeSDK/$sdkVersion - $appName/$appVersion"
        webView.settings?.userAgentString = webView.settings?.userAgentString + sdkUa
    }

    //PUBLIC

    /**
     * Set a listener that listens to events configured in the options object when [load] was called.
     * Also listens to any errors that might happen.
     * @see IEkoPlayerListener
     */
    fun setEkoPlayerListener(ekoPlayerListener: IEkoPlayerListener?) {
        this.eventsListener = ekoPlayerListener
    }

    /**
     * Set a listener to handle link outs.
     * If set to `null`, link outs will automatically open in the browser.
     * @see IEkoPlayerUrlListener
     */
    fun setEkoPlayerUrlListener(ekoPlayerUrlListener: IEkoPlayerUrlListener?) {
        this.urlListener = ekoPlayerUrlListener
    }

    /**
     * Will load and display an eko video. The EkoPlayer will display the loading animation while it prepares the project for playback
     * with default options
     * @param projectId The ID of the project to display
     */
    fun load(projectId: String) {
        load(projectId, EkoPlayerOptions())
    }

    /**
     * Will load and display an eko video. The EkoPlayer will display the loading animation while it prepares the project for playback
     * if `showCover=true` in the options.
     * @param projectId The ID of the project to display
     * @param options The options for loading the project
     * @see EkoPlayerOptions
     */
    fun load(projectId: String, options: EkoPlayerOptions) {
        setUa()
        val projectLoader = EkoProjectLoader(projectId, context)
        if (options.showCover) {
            if (!options.events.contains("eko.canplay")) {
                options.events += "eko.canplay"
            }
            if (options.customCover != null) {
                addView(options.customCover)
            } else {
                addCover()
            }
            coverShown = true
        }
        if (!options.events.contains("urls.openinparent")) {
            options.events += "urls.openinparent"
        }
        webView.visibility = View.VISIBLE
        projectLoader.getProjectEmbedUrl(options,
            { url, metadata ->
                eventsListener?.onEvent("metadata", JSONArray(arrayOf(metadata)))
                webView.loadUrl(url)
            },
            { error ->
                println(error)
                webView.visibility = View.GONE
                removeCover()
                eventsListener?.onError(error)
            })
    }

    /**
     * Will attempt to begin playing an eko video.
     */
    fun play() {
        invoke("eko.play")
    }

    /**
     * Will attempt to pause an eko video.
     */
    fun pause() {
        invoke("eko.pause")
    }

    /**
     * Will call any player function defined on the [developer site](https://developer.eko.com/api)
     * @param method The player method to call
     * @param args Any arguments that should be passed into the method (must be serializable to json)
     */
    fun invoke(method: String, args: List<Any>? = null) {
        var argsStr = "[]"
        if (args != null) {
            argsStr = JSONArray(args).toString()
        }
        val jsString = """
        window.postMessage({
            type: "$method",
            args: $argsStr
        }, '*');
        """.trimIndent()
        webView.evaluateJavascript(jsString, null)
    }
}
