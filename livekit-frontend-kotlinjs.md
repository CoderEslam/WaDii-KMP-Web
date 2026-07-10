# LiveKit Frontend Integration Guide (Kotlin/JS)

Audience: web frontend team building the browser client in Kotlin/JS. Goal: join the same
LiveKit 1:1 calls the mobile app (Compose Multiplatform) already supports, using the same
backend token endpoint and the same websocket signaling protocol — so a mobile user and a web
user can call each other with no backend changes.

---

## 1. Concepts

- **Room** — an isolated call session; both participants join the same room to be connected.
- **Identity (String)** — how a participant is identified inside a room. This project uses the
  user's numeric id, stringified (`"3"`, not `3`).
- **Access Token (JWT)** — a short-lived, scoped credential minted by the backend, handed to the
  client, and passed to `Room.connect(url, token)`. The web client never talks to the LiveKit
  server directly for token minting — only the backend has the API secret.
- **LiveKit server URL** — an explicit `wss://…` address the JS SDK connects its
  websocket/media transport to. Comes back from the token endpoint, not hardcoded.

The web client's job is: fetch a token from the backend, connect to the LiveKit room with it,
publish the local mic/camera, and render whatever tracks the other participant publishes. Call
*signaling* (ring/accept/reject/end) is a separate concern — see §5 — and reuses the app's
existing chat websocket protocol unchanged.

---

## 2. Dependency: `livekit-client` via Kotlin/JS

LiveKit's official web SDK is the npm package `livekit-client` (plain JS/TS, no Kotlin wrapper
maintained by LiveKit). Pull it in as an npm dependency in the Kotlin/JS module's
`build.gradle.kts`:

```kotlin
kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        jsMain.dependencies {
            implementation(npm("livekit-client", "2.9.4")) // check npmjs.com for latest 2.x
        }
    }
}
```

Kotlin/JS resolves the npm package but does **not** generate Kotlin types for it automatically.
Two ways to call it from Kotlin:

1. **Hand-written `external` declarations** (recommended — small surface area, only declare what
   you use). See §4 for the minimal set this integration needs.
2. **Dukat-generated wrappers** — Dukat is unmaintained/removed from newer Kotlin releases; don't
   depend on it for a new integration.

```kotlin
@JsModule("livekit-client")
@JsNonModule
external object LiveKitClient {
    class Room {
        fun connect(url: String, token: String, options: dynamic = definedExternally): Promise<Unit>
        fun disconnect(stopTracks: Boolean = definedExternally): Promise<Unit>
        val localParticipant: LocalParticipant
        val remoteParticipants: dynamic // Map<String, RemoteParticipant> at runtime
        fun on(event: String, callback: (dynamic) -> Unit): Room
    }

    class LocalParticipant {
        fun setMicrophoneEnabled(enabled: Boolean): Promise<dynamic>
        fun setCameraEnabled(enabled: Boolean): Promise<dynamic>
    }

    class RemoteParticipant {
        val identity: String
    }

    class RemoteTrackPublication
    class Track {
        fun attach(): HTMLMediaElement
        fun attach(element: HTMLMediaElement): HTMLMediaElement
        fun detach()
    }
}
```

`dynamic` is used deliberately for event payloads and options objects — the JS SDK's event
payloads vary by event name, and re-declaring every options bag up front isn't worth it for a
first integration. Tighten individual call sites with proper `external interface`s as the
integration grows.

---

## 3. Fetching a token: reuse the existing `/livekit/token` endpoint

This is the **same endpoint the mobile app calls** — no new backend work, no new request/response
shape. Full URL: `{BASE_URL}/livekit/token` (see the backend's `livekit-backend.md` for the
endpoint contract and security rules).

```kotlin
@Serializable
data class LiveKitTokenRequest(
    val roomName: String = "",
    val identity: String = "",
    val participantName: String = ""
)

@Serializable
data class LiveKitTokenResponse(
    val url: String = "",
    val token: String = ""
)
```

Fetch it with `ktor-client-js` (already the natural choice if the rest of this Kotlin/JS app
uses Ktor for HTTP) or the browser `fetch` API directly:

```kotlin
suspend fun fetchLiveKitToken(
    httpClient: HttpClient,
    authToken: String,
    request: LiveKitTokenRequest
): LiveKitTokenResponse =
    httpClient.post("$BASE_URL/livekit/token") {
        header("Authorization", "Bearer $authToken")
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()
```

`identity` **must** equal the authenticated user's own id (stringified) — the backend rejects
mismatches (see backend doc §6). `roomName` must follow the exact convention in §4 below.

---

## 4. Room naming — must match the mobile client exactly

Both platforms derive the room name the same way: both participants' numeric user ids, sorted
ascending, joined with `_`. Port this helper as-is so a mobile↔web call lands in the same room:

```kotlin
object CallChannel {
    fun name(userId: Int, contactId: Int): String =
        listOf(userId, contactId).sorted().joinToString("_")
}
```

A call between user `7` and user `3` always uses room name `"3_7"`, regardless of who called
whom or which platform either side is on.

---

## 5. Signaling: reuse the existing chat websocket, unchanged

Ringing/accept/reject/end does **not** go through LiveKit — it rides the same websocket the chat
feature already uses:

```
ws://{IP}:8080/web-socket/{userId}?token={jwt}
```

Frames are JSON shaped `{"event": "<SocketEvent>", "data": <CallSignal>}`:

```kotlin
enum class SocketEvent { MESSAGE, PRESENCE, CALL_INVITE, CALL_ACCEPT, CALL_REJECT, CALL_END }

@Serializable
data class CallSignal(
    val channelName: String = "",
    val callType: String? = "VIDEO",       // "VIDEO" or "AUDIO"
    val fromUserId: Int = 0,
    val toUserId: Int = 0,
    val fromUserName: String = "",
    val fromUserImage: String? = null,
    val provider: String? = "LIVEKIT"
)

@Serializable
data class SocketResponse(val event: String, val data: JsonElement)
```

Flow for a web-initiated call:

1. Caller opens the websocket, sends `CALL_INVITE` with `channelName = CallChannel.name(me, them)`
   and `callType`/`provider` set. Unchanged wire format — the callee's mobile app parses it as-is.
2. Callee sends `CALL_ACCEPT` back over the same socket.
3. **Only now** does either side call `POST /livekit/token` (§3) and connect to the LiveKit room
   (§6). Don't fetch a token before the call is accepted — tokens are short-lived (10 min TTL, see
   backend doc §4) and this project's convention is to mint one fresh per call, right after accept.
4. `CALL_END`/`CALL_REJECT` tear down the app-level call state on both sides; disconnect the
   LiveKit room independently (§6) when either fires.

The backend's relay logic is provider-agnostic — it forwards `CallSignal` opaquely regardless of
which platform sent it, so no backend changes are needed to support a web caller/callee.

---

## 6. Joining the room and publishing local media

```kotlin
suspend fun joinCall(url: String, token: String, withVideo: Boolean): LiveKitClient.Room {
    val room = LiveKitClient.Room()

    room.on("participantConnected") { participant -> /* update UI: remote user joined */ }
    room.on("participantDisconnected") { participant -> /* end call UI */ }
    room.on("trackSubscribed") { event ->
        val track = event.track as LiveKitClient.Track
        val participant = event.participant as LiveKitClient.RemoteParticipant
        attachRemoteTrack(track, participant.identity)
    }

    room.connect(url, token).await()
    room.localParticipant.setMicrophoneEnabled(true).await()
    if (withVideo) room.localParticipant.setCameraEnabled(true).await()

    return room
}
```

Attaching remote tracks to the DOM:

```kotlin
fun attachRemoteTrack(track: LiveKitClient.Track, identity: String) {
    val container = document.getElementById("remote-video-$identity") as? HTMLDivElement
        ?: return
    val element = track.attach() // creates and returns a <video> or <audio> element
    container.appendChild(element)
}
```

Local preview works the same way — call `.attach()` on the local participant's published camera
track and mount the returned `<video>` element muted (see §7).

Leaving the call:

```kotlin
suspend fun leaveCall(room: LiveKitClient.Room) {
    room.disconnect(true).await()
}
```

---

## 7. Browser-specific gotchas

These don't exist on mobile and will bite you if skipped:

- **Secure context required.** `getUserMedia` (mic/camera capture) and WebRTC only work over
  `https://` or `http://localhost` — plain `http://<IP>` in dev will silently fail to get camera/
  mic access. Use `localhost`/a tunnel (ngrok, etc.) for local dev against a non-localhost backend.
- **Autoplay-with-sound is blocked by default.** Attach remote video elements with `muted = true`
  and `autoplay = true` first so the browser allows autoplay, then unmute in response to a real
  user gesture (e.g. a "tap to unmute" button) if you need audio to start automatically. Audio-only
  remote tracks hit the same restriction — some browsers require a user gesture before any audio
  plays at all.
- **Permission prompts are per-origin**, not per-session like a mobile OS permission dialog —
  users can permanently deny mic/camera for your origin, and there's no in-app path to
  re-prompt; you can only detect denial (`getUserMedia` rejecting) and tell the user to fix it in
  browser site settings.
- **Camera/mic device changes** (unplugging a USB webcam, switching a Bluetooth headset) aren't
  auto-recovered by LiveKit — the JS SDK exposes `Room.switchActiveDevice(...)` for pickers, but
  a simple 1:1 call integration can skip this for v1 and rely on the browser's default device.
- **Tab backgrounding/minimizing** can throttle timers in some browsers, but WebRTC media itself
  keeps flowing — no special handling needed for background tabs during an active call.

---

## 8. Quick manual test

1. Open the web app in two browser tabs (or one browser + one mobile device), logged in as two
   different users who are contacts of each other.
2. From one side, trigger `CALL_INVITE` (voice or video) — confirm the other side receives a
   ringing UI over the existing chat websocket.
3. Accept — confirm both sides call `POST /livekit/token`, each gets a token with `identity`
   equal to their own user id and the same `roomName`.
4. Confirm both sides connect (`room.connect` resolves), local mic/camera publish without
   throwing, and each side's `trackSubscribed` fires for the other's track.
5. Confirm audio is audible and (for video calls) the remote video renders — check the browser
   console for autoplay-block warnings if video/audio doesn't render (see §7).
6. End the call from either side — confirm `CALL_END` fires over the websocket and both sides
   call `room.disconnect()`.
