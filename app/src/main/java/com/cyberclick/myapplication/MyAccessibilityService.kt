package com.cyberclick.myapplication

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import org.json.JSONObject
import android.app.Instrumentation
import android.os.SystemClock
import android.view.MotionEvent


class MyAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var commandReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        commandReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val command = it.getStringExtra("command")
                    handleCommand(command)
                }
            }
        }
        val filter = IntentFilter("com.cyberclick.myapplication.COMMAND")
        registerReceiver(commandReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(commandReceiver)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if needed
    }

    override fun onInterrupt() {
        // Handle interruptions
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCommand(command: String?) {
        command?.let {
            val json = JSONObject(command)
            when (json.getString("type")) {
                "click" -> {
                    val x = json.getDouble("x")
                    val y = json.getDouble("y")
                    showToast("Click at: ($x, $y)")
                    performClick(x, y)
                }
                "swipe" -> {
                    val startX = json.getInt("startX")
                    val startY = json.getInt("startY")
                    val endX = json.getInt("endX")
                    val endY = json.getInt("endY")
                    showToast("Swipe from: ($startX, $startY) to ($endX, $endY)")
                    performSwipe(startX, startY, endX, endY)
                }
            }
        }
    }

    private fun performClick(x: Double, y: Double) {
        Log.d("MyAccessibilityService", "Performing click at: ($x, $y)")
        // Show a toast with the coordinates
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "Click at: ($x, $y)", Toast.LENGTH_SHORT).show()
        }
        clickAtCoordinates(x, y)
    }

    fun performSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        Log.d("MyAccessibilityService", "Performing swipe from: ($startX, $startY) to ($endX, $endY)")
        val swipePath = Path().apply {
            moveTo(startX.toFloat(), startY.toFloat())
            lineTo(endX.toFloat(), endY.toFloat())
        }
        val gestureDescription = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(swipePath, 0, 500))
        }.build()
        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("MyAccessibilityService", "Swipe gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("MyAccessibilityService", "Swipe gesture cancelled")
            }
        }, null)
    }

    private fun clickAtCoordinates(x: Double, y: Double) {
        val instrumentation = Instrumentation()

        // Simulate a touch event down
        val downTime = SystemClock.uptimeMillis()
        val eventDown = MotionEvent.obtain(
            downTime,
            downTime,
            MotionEvent.ACTION_DOWN,
            x.toFloat(),
            y.toFloat(),
            0
        )
        instrumentation.sendPointerSync(eventDown)

        // Simulate a touch event up
        val eventUp = MotionEvent.obtain(
            downTime,
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_UP,
            x.toFloat(),
            y.toFloat(),
            0
        )
        instrumentation.sendPointerSync(eventUp)
    }
}