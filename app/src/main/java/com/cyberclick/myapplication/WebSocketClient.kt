package com.cyberclick.myapplication
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket

class WebSocketClient(
    private val url: String,
    private val onMessageReceived: (String) -> Unit,
    private val updateStatus: (String) -> Unit
) {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    inner class WebSocketListener : okhttp3.WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            updateStatus("Connected")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            onMessageReceived(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            updateStatus("Closing: $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            updateStatus("Error: ${t.message}")
        }
    }

    fun start() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, WebSocketListener())
        client.dispatcher.executorService.shutdown()
    }
}