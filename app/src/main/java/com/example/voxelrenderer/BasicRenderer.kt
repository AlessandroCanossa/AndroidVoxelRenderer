package com.example.voxelrenderer

import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

open class BasicRenderer(r: Float, g: Float, b: Float, a: Float) : GLSurfaceView.Renderer {
    protected val TAG = javaClass.simpleName

    private var mClearScreen = arrayOf(r, g, b, a)
    private var mCurrentScreen = Point(0, 0)
    private lateinit var mContext: Context
    val context: Context
        get() = mContext
    private lateinit var mSurface: GLSurfaceView
    val surface: GLSurfaceView
        get() = mSurface


    constructor() : this(0.0f, 0.0f, 0.0f)

    constructor(r: Float, g: Float, b: Float) : this(r, g, b, 1.0f)

    open fun setContextAndSurface(context: Context, surface: GLSurfaceView) {
        this.mContext = context
        this.mSurface = surface
    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        gl.glClearColor(mClearScreen[0], mClearScreen[1], mClearScreen[2], mClearScreen[3])
        Log.v(TAG, "onSurfaceCreated " + Thread.currentThread().name)
        Log.v(TAG, gl.glGetString(gl.GL_VERSION))
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceChanged " + Thread.currentThread().name)
        gl.glViewport(0, 0, width, height)
        mCurrentScreen.x = width
        mCurrentScreen.y = height
    }

    override fun onDrawFrame(gl10: GL10?) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT)
    }
}
