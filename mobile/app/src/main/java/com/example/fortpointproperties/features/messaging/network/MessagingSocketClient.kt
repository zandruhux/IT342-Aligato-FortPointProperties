package com.example.fortpointproperties.features.messaging.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.fortpointproperties.features.messaging.data.model.MessagingSocketEvent
import com.example.fortpointproperties.shared.network.ApiConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MessagingSocketClient(
    private val onConnectionChanged: (Boolean) -> Unit,
    private val onEvent: (MessagingSocketEvent) -> Unit,
    private val onError: (String) -> Unit,
    private val tokenProvider: () -> String?,
) {
    private val listenerId = UUID.randomUUID().toString()

    fun connect() {
        val token = tokenProvider()?.takeIf { it.isNotBlank() } ?: return
        synchronized(lock) {
            listeners[listenerId] = SocketListener(onConnectionChanged, onEvent, onError)
            pendingToken = token
            manuallyClosed = false

            if (webSocket != null) {
                Log.d(
                    TAG,
                    "Socket connect reused. webSocketOpen=$webSocketOpen sockJsOpen=$sockJsOpen stompConnected=$stompConnected subscribed=$subscribed"
                )
                notifyConnection(listenerId, stompConnected)
                return
            }

            handshakeRetryCount = 0
            openSocketLocked(token)
        }
    }

    fun disconnect() {
        synchronized(lock) {
            listeners.remove(listenerId)
            Log.d(TAG, "Messaging screen listener removed. activeListeners=${listeners.size}")

            if (listeners.isNotEmpty()) {
                return
            }

            mainHandler.removeCallbacks(disconnectRunnable)
            mainHandler.postDelayed(disconnectRunnable, DISCONNECT_GRACE_MS)
        }
    }

    private data class SocketListener(
        val onConnectionChanged: (Boolean) -> Unit,
        val onEvent: (MessagingSocketEvent) -> Unit,
        val onError: (String) -> Unit,
    )

    companion object {
        private const val TAG = "MessagingSocket"
        private const val USER_MESSAGES_DESTINATION = "/user/queue/messages"
        private const val SUBSCRIPTION_ID = "sub-user-messages"
        private const val HANDSHAKE_TIMEOUT_MS = 7000L
        private const val DISCONNECT_GRACE_MS = 1200L
        private const val MAX_HANDSHAKE_RETRIES = 1

        private val lock = Any()
        private val gson = Gson()
        private val mainHandler = Handler(Looper.getMainLooper())
        private val okHttpClient = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        private val listeners = linkedMapOf<String, SocketListener>()
        private var webSocket: WebSocket? = null
        private var manuallyClosed = false
        private var webSocketOpen = false
        private var sockJsOpen = false
        private var stompConnected = false
        private var subscribed = false
        private var pendingToken: String? = null
        private var handshakeRetryCount = 0
        private var waitingForStompConnected = false

        private val sockJsOpenTimeoutRunnable = Runnable {
            synchronized(lock) {
                if (webSocket != null && webSocketOpen && !sockJsOpen) {
                    retryHandshakeLocked("SockJS open frame timeout.")
                }
            }
        }

        private val stompConnectedTimeoutRunnable = Runnable {
            synchronized(lock) {
                if (webSocket != null && sockJsOpen && !stompConnected && waitingForStompConnected) {
                    retryHandshakeLocked("STOMP CONNECTED timeout.")
                }
            }
        }

        private val disconnectRunnable = Runnable {
            synchronized(lock) {
                if (listeners.isEmpty()) {
                    closeSocketLocked(manual = true, reason = "No messaging screens active.")
                }
            }
        }

        private fun openSocketLocked(token: String) {
            mainHandler.removeCallbacks(disconnectRunnable)
            resetConnectionFlagsLocked()
            pendingToken = token
            manuallyClosed = false

            val socketUrl = buildSocketUrl(ApiConfig.BASE_URL)
            Log.d(TAG, "Connecting to messaging socket: $socketUrl")
            val request = Request.Builder()
                .url(socketUrl)
                .build()

            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    synchronized(lock) {
                        webSocketOpen = true
                        Log.d(TAG, "WebSocket opened; waiting for SockJS open frame.")
                        mainHandler.removeCallbacks(sockJsOpenTimeoutRunnable)
                        mainHandler.postDelayed(sockJsOpenTimeoutRunnable, HANDSHAKE_TIMEOUT_MS)
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleIncomingPayload(webSocket, text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    handleIncomingPayload(webSocket, bytes.utf8())
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    synchronized(lock) {
                        Log.d(TAG, "Socket closed. code=$code reason=$reason")
                        if (this@Companion.webSocket === webSocket) {
                            this@Companion.webSocket = null
                        }
                        resetConnectionFlagsLocked()
                        notifyConnectionChanged(false)
                        if (!manuallyClosed && listeners.isNotEmpty()) {
                            scheduleReconnectLocked()
                        }
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                    synchronized(lock) {
                        Log.w(TAG, "Socket error: ${t.message}", t)
                        if (this@Companion.webSocket === webSocket) {
                            this@Companion.webSocket = null
                        }
                        resetConnectionFlagsLocked()
                        notifyConnectionChanged(false)
                        notifyError(t.message ?: "Messaging connection failed.")
                        if (!manuallyClosed && listeners.isNotEmpty()) {
                            scheduleReconnectLocked()
                        }
                    }
                }
            })
        }

        private fun handleIncomingPayload(webSocket: WebSocket, payload: String) {
            Log.d(TAG, "SockJS frame received. kind=${payload.firstOrNull()} length=${payload.length}")
            synchronized(lock) {
                when {
                    payload == "o" -> {
                        sockJsOpen = true
                        Log.d(TAG, "SockJS open frame received.")
                        mainHandler.removeCallbacks(sockJsOpenTimeoutRunnable)
                        val token = pendingToken ?: return
                        sendFrame(webSocket, buildConnectFrame(token))
                        waitingForStompConnected = true
                        Log.d(TAG, "STOMP CONNECT sent.")
                        mainHandler.removeCallbacks(stompConnectedTimeoutRunnable)
                        mainHandler.postDelayed(stompConnectedTimeoutRunnable, HANDSHAKE_TIMEOUT_MS)
                    }

                    payload == "h" -> {
                        Log.d(TAG, "SockJS heartbeat received.")
                    }

                    payload.startsWith("a") -> {
                        handleSockJsMessages(webSocket, payload)
                    }

                    payload.startsWith("c") -> {
                        Log.d(TAG, "SockJS close frame received: $payload")
                        notifyError("Messaging socket closed by server.")
                    }

                    else -> {
                        handleIncomingFrames(webSocket, payload)
                    }
                }
            }
        }

        private fun handleSockJsMessages(webSocket: WebSocket, payload: String) {
            try {
                val frames = gson.fromJson(payload.drop(1), Array<String>::class.java)
                frames?.forEach { frame ->
                    Log.d(TAG, "Unwrapped STOMP frame command=${frame.lineSequence().firstOrNull()?.trim()}")
                    handleIncomingFrames(webSocket, frame)
                }
            } catch (error: Exception) {
                Log.w(TAG, "Failed to parse SockJS frame: ${error.message}")
                notifyError("Unable to parse messaging socket frame.")
            }
        }

        private fun handleIncomingFrames(webSocket: WebSocket, payload: String) {
            payload.split('\u0000')
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { frame ->
                    val command = frame.lineSequence().firstOrNull()?.trim().orEmpty()
                    val body = frame.substringAfter("\n\n", "").trim('\u0000')

                    when (command) {
                        "CONNECTED" -> {
                            stompConnected = true
                            waitingForStompConnected = false
                            handshakeRetryCount = 0
                            mainHandler.removeCallbacks(stompConnectedTimeoutRunnable)
                            Log.d(TAG, "STOMP CONNECTED received.")
                            notifyConnectionChanged(true)

                            if (!subscribed) {
                                sendFrame(webSocket, buildSubscribeFrame(USER_MESSAGES_DESTINATION, SUBSCRIPTION_ID))
                                subscribed = true
                                Log.d(TAG, "SUBSCRIBE sent to $USER_MESSAGES_DESTINATION.")
                            }
                        }

                        "MESSAGE" -> handleSocketMessage(body)
                        "ERROR" -> {
                            Log.w(TAG, "STOMP error frame received: $body")
                            notifyError(body.ifBlank { "Messaging connection error." })
                        }
                    }
                }
        }

        private fun handleSocketMessage(body: String) {
            if (body.isBlank()) {
                return
            }

            try {
                val event = gson.fromJson(body, MessagingSocketEvent::class.java)
                if (event != null) {
                    Log.d(TAG, "Parsed event type=${event.type} conversationId=${event.conversationId}")
                    notifyEvent(event)
                }
        } catch (error: JsonSyntaxException) {
            Log.w(TAG, "Malformed socket JSON: ${error.message}. bodyLength=${body.length}")
        } catch (error: Exception) {
            Log.w(TAG, "Failed to parse socket message: ${error.message}. bodyLength=${body.length}")
        }
        }

        private fun retryHandshakeLocked(reason: String) {
            Log.w(TAG, "$reason webSocketOpen=$webSocketOpen sockJsOpen=$sockJsOpen stompConnected=$stompConnected")
            if (handshakeRetryCount >= MAX_HANDSHAKE_RETRIES) {
                notifyError(reason)
                closeSocketLocked(manual = false, reason = reason)
                return
            }

            handshakeRetryCount += 1
            val token = pendingToken
            Log.d(TAG, "Retry triggered for messaging socket. attempt=$handshakeRetryCount")
            closeSocketLocked(manual = false, reason = reason)
            if (!token.isNullOrBlank() && listeners.isNotEmpty()) {
                openSocketLocked(token)
            }
        }

        private fun closeSocketLocked(manual: Boolean, reason: String) {
            manuallyClosed = manual
            mainHandler.removeCallbacks(sockJsOpenTimeoutRunnable)
            mainHandler.removeCallbacks(stompConnectedTimeoutRunnable)
            val socket = webSocket
            webSocket = null
            resetConnectionFlagsLocked()
            Log.d(TAG, "Disconnecting messaging socket. reason=$reason")
            socket?.close(1000, reason)
            notifyConnectionChanged(false)
        }

        private fun resetConnectionFlagsLocked() {
            webSocketOpen = false
            sockJsOpen = false
            stompConnected = false
            subscribed = false
            waitingForStompConnected = false
        }

        private fun buildConnectFrame(token: String): String {
            return buildString {
                append("CONNECT\n")
                append("accept-version:1.2\n")
                append("heart-beat:10000,10000\n")
                append("Authorization:Bearer ").append(token).append('\n')
                append('\n')
                append('\u0000')
            }
        }

        private fun buildSubscribeFrame(destination: String, subscriptionId: String): String {
            return buildString {
                append("SUBSCRIBE\n")
                append("id:").append(subscriptionId).append('\n')
                append("destination:").append(destination).append('\n')
                append('\n')
                append('\u0000')
            }
        }

        private fun sendFrame(webSocket: WebSocket, frame: String) {
            // SockJS transports STOMP frames inside a JSON array instead of as raw WebSocket text.
            webSocket.send(gson.toJson(listOf(frame)))
        }

        private fun buildSocketUrl(baseUrl: String): String {
            val trimmed = baseUrl.trim().trimEnd('/')
            val socketBase = when {
                trimmed.startsWith("https://", ignoreCase = true) ->
                    trimmed.replaceFirst("https://", "wss://", ignoreCase = true)
                trimmed.startsWith("http://", ignoreCase = true) ->
                    trimmed.replaceFirst("http://", "ws://", ignoreCase = true)
                trimmed.startsWith("ws://", ignoreCase = true) || trimmed.startsWith("wss://", ignoreCase = true) ->
                    trimmed
                else -> "ws://$trimmed"
            }
            val serverId = Random.nextInt(0, 1000).toString().padStart(3, '0')
            val sessionId = "android-${UUID.randomUUID().toString().replace("-", "")}"
            // Spring SockJS native WebSocket transport requires /ws/{serverId}/{sessionId}/websocket.
            return "$socketBase/ws/$serverId/$sessionId/websocket"
        }

        private fun scheduleReconnectLocked() {
            val token = pendingToken
            mainHandler.postDelayed({
                synchronized(lock) {
                    if (!manuallyClosed && webSocket == null && !token.isNullOrBlank() && listeners.isNotEmpty()) {
                        Log.d(TAG, "Reconnecting messaging socket.")
                        openSocketLocked(token)
                    }
                }
            }, 5000)
        }

        private fun notifyConnection(listenerId: String, connected: Boolean) {
            val listener = listeners[listenerId] ?: return
            mainHandler.post {
                listener.onConnectionChanged(connected)
            }
        }

        private fun notifyConnectionChanged(connected: Boolean) {
            val snapshot = listeners.values.toList()
            mainHandler.post {
                snapshot.forEach { it.onConnectionChanged(connected) }
            }
        }

        private fun notifyEvent(event: MessagingSocketEvent) {
            val snapshot = listeners.values.toList()
            mainHandler.post {
                snapshot.forEach { it.onEvent(event) }
            }
        }

        private fun notifyError(message: String) {
            val snapshot = listeners.values.toList()
            mainHandler.post {
                snapshot.forEach { it.onError(message) }
            }
        }
    }
}
