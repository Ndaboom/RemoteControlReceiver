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
import android.widget.Button
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
        val buttonTest: Button = findViewById(R.id.buttonTest)
        // Set a click listener on the button
        buttonTest.setOnClickListener {
            // Log a message when the button is clicked
            Log.d("ButtonTest", "Button clicked")
        }

        webSocketClient = WebSocketClient(
            url = "ws://192.168.1.67:3000",
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
                val x = json.getDouble("x")
                val y = json.getDouble("y")
                // Show a toast with the coordinates
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Click at: ($x, $y)", Toast.LENGTH_SHORT).show()
                    performClick(x, y)
                }

            }
            "swipe" -> {
                val x = json.getDouble("x")
                val y = json.getDouble("y")
                // Show a toast with the coordinates
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Swipe to: ($x, $y)", Toast.LENGTH_SHORT).show()
                    performSwipe(x, y, 3)
                }
            }
        }
    }

    private fun performClick(x: Double, y: Double) {
        val command = "input tap $x $y"
        Runtime.getRuntime().exec(command)
    }

    private fun performSwipe(x: Double, y: Double, duration: Int) {
        val command = "input swipe $x $y $x $y $duration"
        Runtime.getRuntime().exec(command)
    }
}