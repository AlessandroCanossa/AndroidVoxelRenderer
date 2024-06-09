package com.example.voxelrenderer

import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

class VoxelRenderer : GLSurfaceView.Renderer {
    private val TAG = javaClass.simpleName
    private val currentScreen = Point(0, 0)
    private lateinit var context: Context
    private lateinit var surface: GLSurfaceView


    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Log.v(TAG, "Surface created")
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        currentScreen.set(width, height)
        Log.v(TAG, "Surface changed")

    }

    override fun onDrawFrame(gl: GL10?) {
        TODO("Not yet implemented")
    }

    fun setContextAndSurface(context: Context, surface: GLSurfaceView) {
        this.context = context
        this.surface = surface
    }
}