package com.cyberclick.myapplication
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

class WebSocketClient(private val url: String, private val onMessageReceived: (String) -> Unit, private val updateStatus: (String) -> Unit) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun start() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, WebSocketListener())
        client.dispatcher.executorService.shutdown()
    }

    inner class WebSocketListener : okhttp3.WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            updateStatus("Connected")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            onMessageReceived(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            onMessageReceived(bytes.utf8())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            updateStatus("Closing")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            updateStatus("Error: ${t.message}")
        }
    }

    fun send(message: String) {
        webSocket.send(message)
    }
}