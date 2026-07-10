# Agora Voice/Video Calling — Android & iOS (KMP) Implementation Spec

This document is a hand-off spec for implementing 1:1 voice/video calling on **Android and
iOS** using **Agora RTC**, so mobile clients interoperate with the WaDii backend and the
already-working Kotlin/JS (Compose for Web) client in this repo. The web implementation is
**done and in production use** — this doc describes its exact contract (data shapes, channel
naming, signaling events, REST endpoint) so a mobile port stays wire-compatible, plus concrete
native SDK guidance for Android and iOS.

The backend needs **no changes** — it's already implemented per `agora.md` in this repo (REST
token endpoint + WebSocket relay). This doc is client-only.

---

## 1. Agora RTC — background

Agora RTC is a managed SFU (selective forwarding unit) service for low-latency audio/video.
Key concepts used here:

- **App ID** — public identifier for the Agora project. Safe to ship in client binaries.
- **App Certificate** — secret, server-only, used to *sign* tokens. Never present on any client.
- **Token** — short-lived, signed credential (App ID + channel + uid + role + expiry) minted by
  our backend per join. Clients must fetch a fresh token per call.
- **Channel** — a named "room". Peers who join the same channel name can see/hear each other.
- **UID** — numeric identifier a client joins a channel as. We reuse the app's own integer user id.
- **Role** — `PUBLISHER` (can send + receive media) vs `AUDIENCE` (receive-only). This app always
  uses `PUBLISHER` for both call participants.
- **Engine / Client** — the SDK object you create once, join/leave channels with, and that fires
  events for remote users joining, publishing tracks, and leaving.

---

## 2. Product flow (identical on every platform)

1. Caller taps "call" on a contact. Channel name is computed **deterministically** so both
   peers land in the same channel regardless of who initiates:
   `channelName = listOf(myUserId, contactId).sorted().joinToString("_")` — e.g. users 3 and 7
   always get `"3_7"`.
2. Caller sends `CALL_INVITE` over the **existing authenticated WebSocket** (the same socket
   already used for chat, not a new connection) — this is just a ring, no Agora token involved
   yet.
3. Callee's client receives `CALL_INVITE`, shows an incoming-call UI (ringtone + accept/decline).
4. **On Accept**: callee sends `CALL_ACCEPT` back, then immediately requests an Agora token
   (`POST /agora/token`) and joins the Agora channel, publishing its local mic/camera tracks.
5. The **caller**, upon receiving `CALL_ACCEPT`, does the same: requests its own token and joins
   the same channel. (Both token requests happen close together, for the same channel, with two
   different uids — expected and fine.)
6. As each side's remote peer publishes tracks, the SDK fires a "user published" event; the
   local client subscribes to that specific track and renders/plays it.
7. Either side sends `CALL_END` (hang up) or `CALL_REJECT` (decline) over the same socket to tear
   down; on receipt (or on local hang-up), the client leaves the Agora channel and releases
   mic/camera tracks.

No call history is persisted; this is a pure real-time relay + short-lived token mint, matching
the web MVP.

---

## 3. Wire contracts to replicate exactly

These shapes/names are shared with the backend and the web client. Field names and enum values
are **case-sensitive** and must match exactly for cross-platform interop (web ⇄ Android ⇄ iOS all
share the same backend and channels).

### 3.1 REST — `POST {BASE_URL}/agora/token`

```
POST /agora/token
Authorization: Bearer <JWT>
Content-Type: application/json
```

Request:
```kotlin
@Serializable
data class AgoraTokenRequest(
    val channelName: String = "",
    val uid: Int = 0
)
```
- `uid` is always the **authenticated user's own id**.

Response (wrapped in the project's standard envelope):
```kotlin
@Serializable
data class BaseResponse<T>(
    val data: T,
    val message: String = "",
    val statusCode: Int = 0,
    val timestamp: String = ""
)

@Serializable
data class AgoraTokenResponse(
    val appId: String = "",
    val token: String = ""
)
```

### 3.2 WebSocket — reuse the existing chat socket

```
ws://{host}/web-socket/{userId}?token={jwt}
```

Do **not** open a second socket for calls if one is already open for chat — the web client keeps
two logical services (`ChatWebSocketService`, `CallSignalingService`) but they both point at this
same URL; the backend already supports multiple concurrent connections per user and relays to
whichever socket(s) send/receive. On mobile, a single existing chat socket connection can carry
both message types just as well — dispatch locally by `event`.

All frames are JSON text frames shaped as:
```json
{ "event": "CALL_INVITE", "data": { ... } }
```
```kotlin
@Serializable
data class SocketResponse(val event: String, val data: JsonElement)

enum class SocketEvent { MESSAGE, PRESENCE, CALL_INVITE, CALL_ACCEPT, CALL_REJECT, CALL_END }
```

`data` payload for all four `CALL_*` events:
```kotlin
@Serializable
data class CallSignal(
    val channelName: String = "",
    val callType: String = "VIDEO",   // "VIDEO" or "AUDIO"
    val fromUserId: Int = 0,
    val toUserId: Int = 0,
    val fromUserName: String = "",
    val fromUserImage: String? = null
)
```

Sending a frame: wrap as `{ "event": "<SocketEvent.name>", "data": <CallSignal> }` and send as a
single JSON text frame — see `CallSignalingService.kt` for the reference (Ktor `client.webSocket`)
implementation; a mobile client using OkHttp/URLSession WebSocket APIs just needs to reproduce the
same envelope.

### 3.3 Channel naming

```kotlin
object CallChannel {
    fun name(userId: Int, contactId: Int): String =
        listOf(userId, contactId).sorted().joinToString("_")
}
```
Reuse this exact algorithm — a mismatch would make a mobile caller land in a different channel
than a web callee for the same pair of users.

---

## 4. Client state machine to mirror (platform-agnostic — port as shared/common logic)

This is pure Kotlin/coroutines logic with no JS-specific dependency except calls into the Agora
client wrapper, so it is the part most worth sharing across platforms (e.g. in KMP `commonMain`)
rather than reimplementing per-platform.

```kotlin
enum class CallStatus { CALLING, RINGING, CONNECTING, CONNECTED, ENDED }

data class CallState(
    val status: CallStatus = CallStatus.CONNECTING,
    val isCaller: Boolean = false,
    val withVideo: Boolean = true,
    val remoteUserName: String = "",
    val remoteUserImage: String? = null,
    val micEnabled: Boolean = true,
    val cameraEnabled: Boolean = true,
    val remoteHasVideo: Boolean = false,
    val error: String = ""
    // local/remote video track handles are platform-specific — see §6
)

sealed class CallEvent {
    object ToggleMic : CallEvent()
    object ToggleCamera : CallEvent()
    object EndCall : CallEvent()
}
```

Reference control flow (see `CallViewModel.kt` for the full implementation):

- **Init**: if caller → send `CALL_INVITE` (status `CALLING`); if callee (opened after accepting)
  → immediately `joinAndPublish()` (status `CONNECTING`).
- **On `CALL_ACCEPT` received** (caller side only) → `joinAndPublish()`.
- **On `CALL_REJECT` received** → status `ENDED`, error "Call declined".
- **On `CALL_END` received** → leave Agora channel, status `ENDED`.
- **`joinAndPublish()`**: call `POST /agora/token` with `{channelName, uid = myUserId}` → on
  success, join the Agora channel with the returned `appId`/`token`, publish local mic (+camera if
  `withVideo`), set status `CONNECTED`. On failure, status `ENDED` with the error message.
- **On remote user published** (Agora SDK event) → subscribe to that user's track; if video, hold
  the render handle and mark `remoteHasVideo = true`; if audio, just start playback.
- **On remote user left** (Agora SDK event) → leave channel, status `ENDED`.
- **`ToggleMic`/`ToggleCamera`** → flip local track enabled state, mirror in `CallState`.
- **`EndCall`** → send `CALL_END` (unless triggered by a received `CALL_END`), leave Agora
  channel, status `ENDED`.
- **Dispose** → always leave the Agora channel / release engine resources, even if the call never
  connected (e.g. screen closed mid-ring).

Incoming-call UI (separate from the active-call screen) listens globally for `CALL_INVITE` while
idle, shows an accept/decline overlay with ringtone, and on Accept sends `CALL_ACCEPT` then
navigates into the call screen as callee (`isCaller = false`); on Decline sends `CALL_REJECT` and
dismisses. See `IncomingCallOverlay.kt` for reference behavior.

---

## 5. Reference: existing Web implementation (for parity, not to be ported literally)

- `data/agora/AgoraRtc.kt` — Kotlin/JS external bindings to the `agora-rtc-sdk-ng` npm package
  (`AgoraRTC.createClient`, `createMicrophoneAudioTrack`, `createCameraVideoTrack`, client
  `join`/`publish`/`subscribe`/`leave`/`on(...)`).
- `data/agora/AgoraCallClient.kt` — thin wrapper exposing `join()`, `subscribe()`,
  `setMicEnabled()`, `setCameraEnabled()`, `leave()`, plus `onUserPublished`/`onUserLeft`
  callbacks. This is the shape mobile platform clients should functionally match (see §6).
- Video is rendered by calling `.play(htmlElement)` on the local camera track / remote video
  track, bound to a Compose HTML `Div` ref.

Mobile SDKs differ in one structural way worth flagging up front: **the Agora Android/iOS native
SDKs don't have an explicit `subscribe()` call** the way the Web SDK does — by default, once you
join a channel and a remote user publishes, the native SDK auto-subscribes and simply fires a
"remote video decoded / first frame" style callback with a `uid` you then bind a render surface to.
So `onUserPublished(user, mediaType)` on Android/iOS becomes closer to "remote user joined /
remote video available for this uid," and there's no separate subscribe step to port — one fewer
concept to carry over.

---

## 6. Android implementation

### Dependency

Agora ships the Android SDK on Maven Central. Add to the Android target/module:
```kotlin
dependencies {
    implementation("io.agora.rtc:full-sdk:4.3.2") // check Agora console for current recommended version
}
```
(Package coordinates/version drift over time — confirm the current artifact name and latest 4.x
version in the Agora Android SDK Quickstart before pinning.)

### Permissions (`AndroidManifest.xml`)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<!-- Android 12+ if using Bluetooth headset routing -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```
Request `RECORD_AUDIO` (+ `CAMERA` if `withVideo`) as **runtime permissions** before calling
`joinChannel` — Agora will fail to publish audio/video without them granted first.

### Engine lifecycle
```kotlin
val engine: RtcEngine = RtcEngine.create(RtcEngineConfig().apply {
    mContext = context
    mAppId = appId
    mEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) { /* remote peer joined channel */ }
        override fun onUserOffline(uid: Int, reason: Int) { /* -> onRemoteLeft */ }
        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            /* state == REMOTE_VIDEO_STATE_STARTING/DECODING -> onUserPublished(uid, "video") */
        }
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            /* good signal to bind the remote SurfaceView for this uid */
        }
    }
})

engine.enableAudio()
if (withVideo) engine.enableVideo()
engine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER) // "publisher" equivalent

val options = ChannelMediaOptions().apply {
    clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
    channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
}
engine.joinChannel(token, channelName, uid, options)
```

### Video rendering
```kotlin
// Local preview
val localSurface = SurfaceView(context).also { RtcEngine.setupLocalVideo(VideoCanvas(it, VideoCanvas.RENDER_MODE_HIDDEN, uid)) }
// or: engine.setupLocalVideo(VideoCanvas(localSurface, VideoCanvas.RENDER_MODE_HIDDEN, 0))
engine.startPreview()

// Remote (on user joined / first frame decoded)
val remoteSurface = SurfaceView(context)
engine.setupRemoteVideo(VideoCanvas(remoteSurface, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid))
```

### Mute / toggle
```kotlin
engine.muteLocalAudioStream(!micEnabled)
engine.muteLocalVideoStream(!cameraEnabled) // or enableLocalVideo(false) to also stop the camera device
```

### Leave / teardown
```kotlin
engine.leaveChannel()
RtcEngine.destroy() // release when the call screen is fully done, not on every mute toggle
```

---

## 7. iOS implementation

### Dependency

Agora ships `AgoraRtcKit` via CocoaPods or Swift Package Manager:
```ruby
# Podfile
pod 'AgoraRtcEngine_iOS', '~> 4.3.2' # check Agora console for current recommended version
```
or SPM: add `https://github.com/AgoraIO/AgoraRtcEngine_iOS` as a package dependency.

If this call screen is built with a **Kotlin/Native iOS target consuming the shared KMP module**,
note that `AgoraRtcKit` is an Objective-C framework — calling it directly from Kotlin/Native
requires a cinterop `.def` binding, which is non-trivial to hand-maintain against Agora's ObjC
headers. The pragmatic path most teams take: keep the Agora engine + call UI **native Swift**
(using `AgoraRtcEngineKit` directly, per Apple/Agora conventions), and have Swift call into the
shared Kotlin module only for the platform-agnostic pieces — the `/agora/token` REST call
(`AgoraUseCase.getToken`), the WebSocket signaling (`CallSignalingController`/`CallSignal`), and
`CallChannel.name(...)`. That keeps the Agora SDK version/ObjC-interop concerns entirely on the
Swift side while still sharing the network/data contract layer.

### Permissions (`Info.plist`)
```xml
<key>NSMicrophoneUsageDescription</key>
<string>Needed for voice/video calls</string>
<key>NSCameraUsageDescription</key>
<string>Needed for video calls</string>
```

### Engine lifecycle
```swift
let engine = AgoraRtcEngineKit.sharedEngine(withAppId: appId, delegate: self)
engine.setChannelProfile(.communication)
engine.setClientRole(.broadcaster) // "publisher" equivalent
engine.enableAudio()
if withVideo { engine.enableVideo() }

engine.joinChannel(byToken: token, channelId: channelName, info: nil, uid: UInt(uid)) { channel, uid, elapsed in
    // joined
}
```

### Video rendering
```swift
// Local preview
let localView = UIView()
let localCanvas = AgoraRtcVideoCanvas()
localCanvas.view = localView
localCanvas.renderMode = .hidden
localCanvas.uid = 0 // 0 == local user
engine.setupLocalVideo(localCanvas)
engine.startPreview()

// Remote — on didJoinedOfUid delegate callback
func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
    let remoteView = UIView()
    let canvas = AgoraRtcVideoCanvas()
    canvas.uid = uid
    canvas.view = remoteView
    canvas.renderMode = .hidden
    engine.setupRemoteVideo(canvas)
    // bind remoteView into your call screen's remote video container
}

func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
    // -> onRemoteLeft equivalent
}
```

### Mute / toggle
```swift
engine.muteLocalAudioStream(!micEnabled)
engine.muteLocalVideoStream(!cameraEnabled)
```

### Leave / teardown
```swift
engine.leaveChannel(nil)
AgoraRtcEngineKit.destroy()
```

---

## 8. Suggested KMP boundary (if sharing one module across web/Android/iOS)

Keep these in `commonMain` — they have no platform dependency today (web already implements them
in plain Kotlin against `kotlinx.serialization`/`kotlinx.coroutines`):
- `AgoraTokenRequest`, `AgoraTokenResponse`, `BaseResponse`
- `CallSignal`, `SocketEvent`, `SocketResponse`, `IncomingCallSignal`, `CallChannel`
- `AgoraRepo`/`AgoraUseCase` (calls the existing `/agora/token` REST endpoint via `ktor-client`,
  which already has Android/iOS/JS engines)
- `CallSignalingController` (pure coroutines/Flow logic dispatching on `SocketEvent`)
- `CallViewModel`/`CallState`/`CallEvent`/`CallStatus` **if** the local/remote video handles in
  `CallState` are made an opaque `expect` type (see below) rather than the current JS-only
  `ICameraVideoTrack`/`IRemoteVideoTrack`.

Make platform-specific via `expect`/`actual` (mirroring the current `AgoraCallClient` role):
```kotlin
expect class AgoraCallClient() {
    var onUserPublished: ((remoteUid: Int, mediaType: String) -> Unit)?
    var onUserLeft: ((remoteUid: Int) -> Unit)?
    suspend fun join(appId: String, channel: String, token: String?, uid: Int, withVideo: Boolean)
    fun setMicEnabled(enabled: Boolean)
    fun setCameraEnabled(enabled: Boolean)
    suspend fun leave()
}
```
- `actual` for `jsMain` = today's `AgoraCallClient.kt` (adjust `onUserPublished` to plain `Int` uid
  instead of `IAgoraRTCRemoteUser`, since only Android/iOS need the numeric uid directly — the web
  actual can extract `.uid` internally).
- `actual` for `androidMain` = thin wrapper around `RtcEngine` per §6.
- `actual` for `iosMain` — only attempt this if the Agora iOS SDK is bound via cinterop; otherwise
  leave call UI native-Swift per §7 and don't force an `iosMain actual` that doesn't exist.

Video surface creation/binding (`SurfaceView` on Android, `UIView` on iOS, `HTMLElement` on Web)
is inherently platform-UI-specific and should stay in each platform's screen/view layer, exactly
as the current `CallScreen.kt` treats `localVideoTrack`/`remoteVideoTrack` as opaque objects it
hands to a platform ref/container — don't try to force this into `commonMain`.

---

## 9. Mobile-specific gaps not present in the web MVP

The web version's scope deliberately excludes these; mobile should evaluate them since they're
much more commonly load-bearing on mobile:

- **Backgrounded/killed app**: the web client only shows an incoming-call overlay while the tab is
  open. On mobile, a `CALL_INVITE` arriving while the app is backgrounded/killed needs a VoIP push
  (iOS: PushKit + CallKit) or a high-priority FCM data message (Android) to wake the app and show
  a native incoming-call UI — the current WebSocket-only signaling silently drops the invite if no
  socket is open, same as the backend spec (`agora.md` §3) describes. This needs new backend
  support (device push tokens, sending a push alongside/instead of the WS relay when the target
  has no open socket) if wanted — flag as a backend follow-up, not something to solve client-only.
- **Token expiry on long calls**: tokens are minted with a short expiry (~1h per `agora.md`); the
  web client doesn't renew mid-call. Mobile should decide whether to proactively re-fetch/renew
  before expiry (`RtcEngine`/`AgoraRtcEngineKit` both expose a "token about to expire" callback)
  if calls longer than the expiry window are expected.
- **Audio routing** (speaker/earpiece/bluetooth toggle) and **CallKit/ConnectionService**
  integration for native call UI (lock screen, system call log) are mobile-only concerns with no
  web equivalent — plan UI for these separately; Agora's SDK cooperates with both but doesn't
  provide them for you.

None of the above is required to reach parity with the current web MVP — call them out as
decisions, not blockers.

---

## 10. Alternative: embed the existing web call screen in a WebView (no native SDK)

Instead of §6/§7's native reimplementation, a mobile app can reuse the Kotlin/JS call screen
as-is by loading it in a WebView, deep-linked straight into an active call. This trades native
call quality/UX for zero SDK duplication — see the trade-offs at the end before choosing this
over §6/§7.

### Deep-link contract

The web app boots straight into `CallScreen` (bypassing login/home, no `Layout` chrome) when
loaded with a URL hash of the form:

```
https://<web-host>/index.html#call?channelName=3_7&remoteUserId=7&remoteUserName=John&remoteUserImage=abc.jpg&withVideo=true&isCaller=false&myUserId=3&myUserName=Jane&token=<JWT>
```

Hash (`#...`), not query string — the fragment never reaches the server, so this works on any
static host with zero rewrite/`historyApiFallback` config. Parsing is in
`CallDeepLink.kt`/`parseCallDeepLink`; wiring is in `App.kt` (`main()` seeds `AppState.token` /
`AppState.user` from the link before first composition, `App()` special-cases
`bootCallDeepLink` to render a bare `Navigator(CallScreen(...))`).

Required params: `channelName`, `remoteUserId`, `myUserId`, `token`. Optional: `remoteUserName`,
`remoteUserImage`, `withVideo` (`"true"`/absent), `isCaller` (`"true"`/absent), `myUserName`.

The mobile app builds this URL from data it already has locally (it's the same six fields
`CallScreen` always took — see §3/§4) plus the current session's JWT.

### Android — `WebView` wrapper

```kotlin
class CallActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    runOnUiThread { request.grant(request.resources) }
                }
            }
        }
        setContentView(webView)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 1
        )
        webView.loadUrl(buildCallUrl(/* channelName, remoteUserId, ..., token */))
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
```
Manifest permissions: same list as §6 (`INTERNET`, `RECORD_AUDIO`, `CAMERA`, etc.).

### iOS — `WKWebView` wrapper

```swift
class CallViewController: UIViewController, WKUIDelegate {
    private var webView: WKWebView!

    override func viewDidLoad() {
        super.viewDidLoad()
        let config = WKWebViewConfiguration()
        config.allowsInlineMediaPlayback = true
        config.mediaTypesRequiringUserActionForPlayback = []
        webView = WKWebView(frame: view.bounds, configuration: config)
        webView.uiDelegate = self
        view.addSubview(webView)
        webView.load(URLRequest(url: callUrl)) // built the same way as Android above
    }

    // iOS 15+: grant camera/mic to the WebView content
    func webView(_ webView: WKWebView, requestMediaCapturePermissionFor origin: WKSecurityOrigin,
                 initiatedByFrame frame: WKFrameInfo, type: WKMediaCaptureType,
                 decisionHandler: @escaping (WKPermissionDecision) -> Void) {
        decisionHandler(.grant)
    }
}
```
`Info.plist`: same `NSMicrophoneUsageDescription`/`NSCameraUsageDescription` as §7. Requires
iOS ≥ 14.3 for `getUserMedia` in `WKWebView` at all, iOS ≥ 15 for the permission delegate above
(older iOS auto-prompts the user instead).

### Trade-offs vs. §6/§7 native SDK

- **Faster to ship, zero SDK duplication** — reuses the already-working `agora-rtc-sdk-ng` JS
  code and `CallViewModel` logic verbatim; no native Agora integration to maintain per platform.
- **No CallKit/ConnectionService** — no lock-screen call UI, no system call log, no native
  ringtone/incoming-call handling outside the app.
- **No background/killed-app wake** — same VoIP-push gap as §9 applies, but worse: even a
  foregrounded-but-backgrounded WebView tab may throttle/suspend the JS runtime more
  aggressively than a native RTC engine service.
- **Weaker reliability/perf** — WebRTC-in-WebView has more device/OS-version variance than a
  native SDK; expect more support burden on low-end Android WebViews.
- **Token in the URL** — the JWT rides in the hash fragment (never logged server-side, but it
  does sit in the WebView's in-process URL and could surface in a device-connected remote
  debugger). Prefer minting a short-lived, call-scoped token for this URL rather than reusing a
  long-lived session JWT, if the backend is extended to support that.

Recommended use: an MVP/stopgap to unblock mobile calling quickly, not a permanent replacement
for §6/§7 if native call quality and system call UX end up mattering.
