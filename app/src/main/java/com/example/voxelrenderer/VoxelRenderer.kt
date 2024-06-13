package com.example.voxelrenderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.voxelrenderer.utils.Mesh
import com.example.voxelrenderer.utils.Shader
import com.example.voxelrenderer.utils.VlyLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

class VoxelRenderer : BasicRenderer() {
    private lateinit var mShader: Shader

    private var mVPLoc: Int = 0
    private var mColorLoc: Int = 0

    private val mViewM = FloatArray(16)
    private val mProjM = FloatArray(16)
    private val mTemp = FloatArray(16)

    private lateinit var mMeshes: List<Mesh>

    private var mPrevTime = System.nanoTime()
    private var mNumFrame = 0

    private var mScaleFactor = 1.0f
    private var mRotation = 0.0f

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (detector.scaleFactor == 1f) return false
            mScaleFactor *= detector.scaleFactor
            Log.v(TAG, "Scale factor: $mScaleFactor")
            return true
        }
    }
    lateinit var scaleDetector: ScaleGestureDetector

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (distanceX == 0f) return false

            mRotation += distanceX
            Log.v(TAG, "Rotation: $mRotation")
            return true
        }
    }
    lateinit var gestureDetector: GestureDetector

    init {
        Matrix.setIdentityM(mViewM, 0)
        Matrix.setIdentityM(mProjM, 0)
    }

    override fun setContextAndSurface(context: Context, surface: GLSurfaceView) {
        super.setContextAndSurface(context, surface)
        scaleDetector = ScaleGestureDetector(this.context, scaleListener)
        gestureDetector = GestureDetector(this.context, gestureListener)
    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl10, config)

        mShader = Shader(
            context.assets.open("shader.vert"),
            context.assets.open("shader.frag")
        )

        val loader = VlyLoader(context.assets.open("simple.vly"))
        loader.load()

        mMeshes = loader.parse().onEach { it.setupMesh() }

        mVPLoc = mShader.getUniformLocation("VP")
        mColorLoc = mShader.getUniformLocation("color")

        gl.glEnable(gl.GL_DEPTH_TEST)
//        gl.glEnable(gl.GL_CULL_FACE)
//        gl.glCullFace(gl.GL_BACK)
//        gl.glFrontFace(gl.GL_CCW)
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl10, width, height)
        val h = if (height == 0) 1 else height
        val aspect = (width.toFloat()) / (h.toFloat())

        Matrix.perspectiveM(mProjM, 0, 45f, aspect, 0.1f, 100f)
        Matrix.setLookAtM(
            mViewM, 0,
            0f, 0f, 5f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )
    }

    override fun onDrawFrame(gl10: GL10?) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT or gl.GL_DEPTH_BUFFER_BIT)

        countFPS()

        Matrix.multiplyMM(mTemp, 0, mProjM, 0, mViewM, 0)

        mShader.use {
            mMeshes.forEach { mesh ->
                mesh.draw { color ->
                    gl.glUniformMatrix4fv(mVPLoc, 1, false, mTemp, 0)
                    gl.glUniform3f(mColorLoc, color.r, color.g, color.b)
                }
            }
        }
    }

    private fun countFPS() {
        val currentTime = System.nanoTime()
        mNumFrame++
        if (currentTime - mPrevTime >= 1_000_000_000) {
            Log.v(TAG, "FPS: $mNumFrame")
            mNumFrame = 0
            mPrevTime = currentTime
        }
    }

}