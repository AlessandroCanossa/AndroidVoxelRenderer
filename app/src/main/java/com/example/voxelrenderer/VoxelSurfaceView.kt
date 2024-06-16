package com.example.voxelrenderer

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.voxelrenderer.utils.Mesh

private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

@SuppressLint("ClickableViewAccessibility", "ViewConstructor")
class VoxelSurfaceView(context: Context, mesh: Mesh) : GLSurfaceView(context) {
    private val renderer = VoxelRenderer(mesh)

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            renderer.mScaleFactor *= detector.scaleFactor
            renderer.mScaleFactor = renderer.mScaleFactor.coerceIn(0.5f, 10.0f)
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (distanceX == 0f) return false

            renderer.mRotation -= distanceX * TOUCH_SCALE_FACTOR
            if (renderer.mRotation > 180f) renderer.mRotation = -180f
            else if (renderer.mRotation < -180f) renderer.mRotation = 180f

            requestRender()
            return true
        }
    }
    private val gestureDetector = GestureDetector(context, gestureListener)

    init {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val reqGlesVersion = activityManager.deviceConfigurationInfo.reqGlEsVersion

        val supported = if (reqGlesVersion >= 0x30000) {
            3
        } else if (reqGlesVersion >= 0x20000) {
            2
        } else {
            1
        }

        setEGLContextClientVersion(supported)
        preserveEGLContextOnPause = true

        debugFlags = DEBUG_CHECK_GL_ERROR or DEBUG_LOG_GL_CALLS

        renderer.setContextAndSurface(context, this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        if (scaleDetector.isInProgress) {
            requestRender()
            return true
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onPause() {
        super.onPause()
        Log.v("VoxelSurface", "On Pause")
    }

    override fun onResume() {
        super.onResume()
        Log.v("VoxelSurface", "On Resume")
    }
}