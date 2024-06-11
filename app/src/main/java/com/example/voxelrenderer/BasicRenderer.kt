package com.example.voxelrenderer

import android.content.Context
import android.graphics.Point
import android.opengl.GLES32 as gl
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class BasicRenderer(r: Float, g: Float, b: Float, a: Float) : GLSurfaceView.Renderer {
    private var clearScreen = arrayOf(r, g, b, a)
    protected var currentScreen = Point(0, 0)
    protected val TAG = javaClass.simpleName
    private lateinit var _context: Context
    val context: Context
        get() = _context
    private lateinit var _surface: GLSurfaceView
    val surface: GLSurfaceView
        get() = _surface


    constructor() : this(0.0f, 0.0f, 0.0f)

    constructor(r: Float, g: Float, b: Float) : this(r, g, b, 1.0f)

    open fun setContextAndSurface(context: Context, surface: GLSurfaceView) {
        this._context = context
        this._surface = surface
    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        gl.glClearColor(clearScreen[0], clearScreen[1], clearScreen[2], clearScreen[3])
        Log.v(TAG, "onSurfaceCreated " + Thread.currentThread().name)
        Log.v(TAG, gl.glGetString(gl.GL_VERSION))
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceChanged " + Thread.currentThread().name)
        gl.glViewport(0, 0, width, height)
        currentScreen.x = width
        currentScreen.y = height
    }

    override fun onDrawFrame(gl10: GL10?) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT)
    }
}
