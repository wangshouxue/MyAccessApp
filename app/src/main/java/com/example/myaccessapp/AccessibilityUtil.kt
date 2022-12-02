package com.example.myaccessapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo

/**
 * @Description: 类作用描述
 */
object AccessibilityUtil {
    /**
     * Gesture手势实现滚动(Android7+)
     * 解决滚动距离不可控制问题
     * @param distanceX 向右滚动为负值 向左滚动为正值
     * @param distanceY 向下滚动为负值 向上滚动为正值
     */
    fun scrollByNode(
        service: AccessibilityService,
        nodeInfo: AccessibilityNodeInfo,
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            nodeInfo.getBoundsInScreen(rect)
            val point = Point((rect.left + rect.right) / 2, (rect.top + rect.bottom) /3*2)
            val builder = GestureDescription.Builder()
            val path = Path()
            path.moveTo(point.x.toFloat(), point.y.toFloat())
            path.lineTo(point.x.toFloat(), 100f)
            builder.addStroke(GestureDescription.StrokeDescription(path, 0L, 500L))
            val gesture = builder.build()
            return service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                }
            }, null)
        } else
            return false
    }

}