package com.cyberclick.myapplication

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect

@RequiresApi(Build.VERSION_CODES.N)
class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val eventType = event.eventType
        val source = event.source ?: return

        when (eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                // Handle click event
                val nodeInfo = event.source
                if (nodeInfo != null) {
                    val bounds = android.graphics.Rect()
                    nodeInfo.getBoundsInScreen(bounds)
                    val x = bounds.centerX()
                    val y = bounds.centerY()
                    showToast("Clicked at: ($x, $y)")
                    performClick(x, y)
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                // Handle scroll event (if needed)
            }
            // Add other event types as needed
        }
    }

    override fun onInterrupt() {
        Log.d("AccessibilityService", "onInterrupt")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    private fun performClick(x: Int, y: Int) {
//        // Perform click action here using AccessibilityNodeInfo
//        val rootInActiveWindow = rootInActiveWindow ?: return
//        val nodeInfo = findClickableNode(rootInActiveWindow, x, y)
//        nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//    }

    private fun findClickableNode(root: AccessibilityNodeInfo, x: Int, y: Int): AccessibilityNodeInfo? {
        // Implement logic to find a clickable node at the specified coordinates
        // Example: Traverse the view hierarchy to find a clickable node near (x, y)
        return root.findAccessibilityNodeInfosByText("Click Me").firstOrNull()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        showToast("Accessibility service connected")
    }

    private fun performClick(x: Int, y: Int) {
        // Simulate click action using AccessibilityService
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
    }

    private fun performSwipe(x: Int, y: Int) {
        // Simulate swipe action using AccessibilityService
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
            lineTo(x.toFloat() + 100, y.toFloat() + 100)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 500))
            .build()
        dispatchGesture(gesture, null, null)
    }
}
