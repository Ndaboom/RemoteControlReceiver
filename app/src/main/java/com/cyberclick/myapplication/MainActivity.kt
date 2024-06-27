package com.cyberclick.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var webSocketClient: WebSocketClient
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)

        webSocketClient = WebSocketClient(
            url = "ws://192.168.1.72:3000",
            onMessageReceived = { message -> handleCommand(message, this) },
            updateStatus = { status -> updateStatus(status) }
        )
        webSocketClient.start()
    }

    private fun updateStatus(status: String) {
        mainHandler.post {
            statusText.text = "WebSocket Status: $status"
        }
    }

    private fun handleCommand(command: String, context: Context) {
        val intent = Intent("com.cyberclick.myapplication.COMMAND").apply {
            putExtra("command", command)
        }
        context.sendBroadcast(intent)
    }
}