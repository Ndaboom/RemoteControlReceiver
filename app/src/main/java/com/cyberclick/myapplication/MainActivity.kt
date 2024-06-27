package com.cyberclick.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.ByteString
import android.util.Log
import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.Toast
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var statusText: TextView
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)

        webSocketClient = WebSocketClient(
            url = "ws://192.168.1.72:3000",
            onMessageReceived = { message -> handleCommand(message, this@MainActivity) },
            updateStatus = { status -> updateStatus(status) }
        )
        webSocketClient.start()
    }

    private fun updateStatus(status: String) {
        mainHandler.post {
            statusText.text = "WebSocket Status: $status"
        }
    }

    private fun handleCommand(command: String,context: Context ) {
        val json = JSONObject(command)
        when (json.getString("type")) {
            "click" -> {
                val x = json.getInt("x")
                val y = json.getInt("y")
                // Show a toast with the coordinates
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Click at: ($x, $y)", Toast.LENGTH_SHORT).show()
                }

            }
            "swipe" -> {
                val x = json.getInt("x")
                val y = json.getInt("y")
                // Show a toast with the coordinates
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Swipe to: ($x, $y)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}