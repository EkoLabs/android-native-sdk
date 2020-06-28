# android-native-sdk
A lightweight SDK that allows for easy integration of eko projects into an Android app

# API
## EkoPlayer
This is the view in which the eko player will reside. It will also forward any events from the player to the rest of the app.
### Static
#### EkoPlayer.clearData(context: Context)
Clears all the all EkoPlayer's Webview data, including: cache, cookies and javasctript storage.
NOTE: Since Webview data in Android is shared at the app level, calling this method
will clear the data for all of the app's Webviews.
### Properties
#### appName: String
App name is for analytics purposes. Will default to the bundle id if not set.
### Methods
#### init()
The EkoPlayerView can be initialized programmatically or included via the layout XML.
#### load(projectId: String, options: EkoOptions)
Will load and display an eko project. The EkoPlayer will display the loading animation while it prepares the project for playback if `showCover=true` in the options.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| projectId | `String` | The id of a project to display |
| options | `EkoOptions` | Options for project delivery. See [EkoOptions](#ekooptions) for more details. |
#### load(projectId: String)
Will load and display an eko project. The EkoPlayer will display the loading animation while it prepares the project for playback with default options.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| projectId | `String` | The id of a project to display |
#### play()
Will attempt to begin playing an eko project.
#### pause()
Will attempt to pause an eko project.
#### invoke(method: String, args: List\<Any>?)
Will call any player function defined on the developer site and return the response via callback function.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| method | `String` | The player method to call. |
| args | `List<Any>?` | Any arguments that should be passed into the method (must be serializable to json) |
#### setEkoPlayerListener(ekoPlayerListener: IEkoPlayerListener?)
Set a listener that listens to events configured in the options object when `load` was called. Also listens to any errors that might happen.
#### setEkoPlayerShareListener(ekoPlayerShareLister: IEkoPlayerShareListener?)
Set a listener to handle share intents. If set to `null`, the default Android share screen will be shown.
#### setEkoPlayerUrlListener(ekoPlayerUrlListener: IEkoPlayerUrlListener?)
Set a listener to handle link outs. If set to `null`, link outs will automatically open in the browser.

## IEkoPlayerListener
Listener interface for receiving events and errors from an eko project.
### Methods
#### onEvent(event:String, args: JSONArray?)
The eko player triggers a number of events. The app can listen to these events by providing the event name in the load call. This function will be called whenever an event passed in to `load()` is triggered.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| event | `String` | The name of the event fired. |
| args | `JSONArray?` | Any arguments that might have been passed along when the event was fired. |

#### onError(error: Error)
Called whenever an error occurs. This could happen in the loading process (if an invalid project id was given or we fail to open the link to the project), or if an event is passed in with malformed data (missing an event name, etc).

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| error | `Error` | An error with a description of the issue. |

## IEkoPlayerUrlListener
Listener interface for link out events.
### Methods
#### onUrlOpen(url: String)
There can be link outs from within an eko project. This function will be called whenever a link out is supposed to occur. The listener is responsible for opening the url.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| url | `String` | The url to open. |

## IEkoPlayerShareListener
Listener interface for share intents.
### Methods
#### onShare(url: String)
There can be share intents from within an eko project via share buttons or ekoshell. This function will be called whenever a share intent happened.

| Param           | Type           | Description  |
| :-------------: |:--------------:| :------------|
| url | `String` | The canonical url of the project. |

## EkoOptions
### Properties
#### params: Map<String, String> = { “autoplay”: true }
A list of embed params that will affect the delivery.
#### events: List\<String> = []
A list of events that should be forwarded to the app
#### cover: Class\<out View>? = EkoDefaultCover::class.java
A View to cover the loading of the eko project. Set to `null` to disable.
Will be initialized by the EkoPlayer.

# Default Player Events
#### eko.canplay
Triggered when the player has buffered enough media to begin playback. Only added if `showCover=true` and `autoplay=false`
#### eko.playing
Only added if `showCover=true` and `autoplay=true`
